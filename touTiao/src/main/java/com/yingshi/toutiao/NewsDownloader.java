package com.yingshi.toutiao;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import android.util.Log;
import android.widget.Toast;

import com.yingshi.toutiao.actions.AbstractAction.ActionError;
import com.yingshi.toutiao.actions.AbstractAction.BackgroundCallBack;
import com.yingshi.toutiao.actions.AbstractAction.UICallBack;
import com.yingshi.toutiao.actions.GetCategoryAction;
import com.yingshi.toutiao.actions.GetFocusAction;
import com.yingshi.toutiao.actions.GetNewsAction;
import com.yingshi.toutiao.actions.ParallelTask;
import com.yingshi.toutiao.model.Category;
import com.yingshi.toutiao.model.News;
import com.yingshi.toutiao.model.Pagination;

public class NewsDownloader{
    private static final String tag = "TT-NewsDownloader";
    private static final int CORE_POOL_SIZE = 2;
    private static final int MAXIMUM_POOL_SIZE = 4;
    private static final int KEEP_ALIVE = 1;
    /**
     * An {@link Executor} that can be used to execute tasks in parallel.
     */
    private ThreadPoolExecutor mExecutor;
	private TouTiaoApp mApp;
	private int mTaskCount = 0;
	private static NewsDownloader mInstance;
	private NewsDownloader(TouTiaoApp app){
		mApp = app;
	}
	
	public static NewsDownloader getInstance(TouTiaoApp mApp){
		if(mInstance == null){
			mInstance = new NewsDownloader(mApp);
		}
		return mInstance;
	}
	
	private void prepareDownload(){
		BlockingQueue<Runnable> poolWorkQueue = new LinkedBlockingQueue<Runnable>(20);
		ThreadFactory threadFactory = new ThreadFactory() {
	        private final AtomicInteger mCount = new AtomicInteger(1);
	        public Thread newThread(Runnable r) {
	            return new Thread(r, "NewsDownloader #" + mCount.getAndIncrement());
	        }
	    };
		mExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
                TimeUnit.SECONDS, poolWorkQueue, threadFactory);
	}
	
	public void startDownlaod() {
		Log.i(tag, "startDownlaod");
		if(mTaskCount > 0){
			Toast.makeText(mApp, R.string.downloading, Toast.LENGTH_SHORT).show();
			return;
		}
		Toast.makeText(mApp, R.string.download_started, Toast.LENGTH_SHORT).show();
		prepareDownload();
		mTaskCount ++;
		GetCategoryAction getCategoryAction = new GetCategoryAction(mApp, 1, 100);
		getCategoryAction.executeOnExecutor(
			new BackgroundCallBack<Pagination<Category>>(){
				public void onSuccess(Pagination<Category> result) {
					Log.d(tag, "downloaded " + result.getItems().size() + " categories from server");
					mApp.getCategoryDAO().delete();
					mApp.getCategoryDAO().save(result.getItems());
				}
			},
			new UICallBack<Pagination<Category>>(){
				public void onSuccess(Pagination<Category> result) {
					downloadNews(result.getItems());
					afterTaskDone();
				}
				public void onFailure(ActionError error) {
					Log.d(tag, "Failed to download categories from server");
					loadCategoryFromDB();
					afterTaskDone();
				}
			},
			mExecutor
		);
	}
	
	private void loadCategoryFromDB(){
		Log.d(tag, "loadCategoryFromDB");
		mTaskCount ++;
		new ParallelTask<List<Category>>() {
			protected List<Category> doInBackground(Void... params) {
				return mApp.getCategoryDAO().getAll(null);
			}
			public void onPostExecute(List<Category> newsList){
				Log.d(tag, "Found " + newsList.size() + " categories from database.");
				downloadNews(newsList);
				afterTaskDone();
			}
		}.executeOnExecutor(mExecutor);
	}
	
	private void downloadNews(List<Category> categories){
		Log.d(tag, "downloadNews...");
		if(categories == null || categories.isEmpty()){
			return;
		}
		for(final Category category : categories){
			mTaskCount ++;
			new GetFocusAction(mApp, category.getName()).executeOnExecutor(new BackgroundCallBack<List<News>>(){
				public void onSuccess(List<News> newsList) {
					Log.d(tag, "downloaded " + newsList.size() + " news for category " + category.getName());
					for(News news : newsList){
						news.setFocus(true);
					}
					if("头条".equals(category.getName())){
						mApp.getHeadNewsDAO().deleteHeadFocus();//先删除旧的
						mApp.getHeadNewsDAO().save(newsList);//后保存新的
					}else{
						mApp.getNewsDAO().deleteFocusByCategory(category.getName());//先删除旧的
						mApp.getNewsDAO().save(newsList);//后保存新的
					}
				}
			},
			new UICallBack<List<News>>(){
				public void onSuccess(List<News> result) {
					afterTaskDone();
				}
				public void onFailure(ActionError error) {
					afterTaskDone();
				}
			},
			mExecutor);

			mTaskCount ++;
			new GetNewsAction(mApp, category.getName(), 1, Constants.PAGE_SIZE).executeOnExecutor(
				new BackgroundCallBack<Pagination<News>>(){
					public void onSuccess(Pagination<News> newsPage) {
						Log.d(tag, "downloaded " + newsPage.getItems().size() + " news for category " + category.getName());
						if("头条".equals(category.getName())){
							mApp.getHeadNewsDAO().deleteHeadNews();//先删除旧的
							mApp.getHeadNewsDAO().save(newsPage.getItems());//后保存新的
						}else{
							mApp.getNewsDAO().deleteNewsByCategory(category.getName());//先删除旧的
							mApp.getNewsDAO().save(newsPage.getItems());//后保存新的
						}
					}
				},
				new UICallBack<Pagination<News>>(){
					public void onSuccess(Pagination<News> result) {
						afterTaskDone();
					}
					public void onFailure(ActionError error) {
						afterTaskDone();
					}
				},
				mExecutor
			);
		}
	}
	
	private void afterTaskDone(){
		mTaskCount --;
		if(mTaskCount == 0){
			Toast.makeText(mApp, R.string.download_complete, Toast.LENGTH_SHORT).show();
			Log.d(tag, "shutdown executor service...");
			mExecutor.shutdownNow();
		}
	}
}
