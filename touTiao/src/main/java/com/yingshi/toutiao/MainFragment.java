package com.yingshi.toutiao;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.yingshi.toutiao.actions.AbstractAction.ActionError;
import com.yingshi.toutiao.actions.AbstractAction.BackgroundCallBack;
import com.yingshi.toutiao.actions.AbstractAction.UICallBack;
import com.yingshi.toutiao.actions.ParallelTask;
import com.yingshi.toutiao.actions.GetFocusAction;
import com.yingshi.toutiao.actions.GetNewsAction;
import com.yingshi.toutiao.model.News;
import com.yingshi.toutiao.model.Pagination;
import com.yingshi.toutiao.storage.HeadNewsDAO;
import com.yingshi.toutiao.storage.NewsDAO;
import com.yingshi.toutiao.view.CustomizeImageView;
import com.yingshi.toutiao.view.PhotoPager;
import com.yingshi.toutiao.view.ptr.HeaderLoadingSupportPTRListFragment;
import com.yingshi.toutiao.view.ptr.PTRListAdapter;

public class MainFragment extends HeaderLoadingSupportPTRListFragment {
	private final static String tag = "TT-MainFragment";
	
	private String mCategory;
	private PhotoPager mPhotoPager;
	private PTRListAdapter<News> mNewsListAdapter;
	private List<News> mFocusNews;
	private NewsDAO mNewsDAO;
	private HeadNewsDAO mHeadNewsDAO;
	private GetNewsAction mGetnewsAction;
	private boolean mFocusLoadedFromServer = false;
	private boolean mNewsLoadedFromServer = false;
	
	private int mAsyncTaskCount = 0;
	
	public MainFragment() {
	}

	public void onAttach (Activity activity){
		Log.d(tag, mCategory +" onAttach()");
		super.onAttach(activity);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mCategory = savedInstanceState.getString("mCategory");
			mFocusLoadedFromServer = savedInstanceState.getBoolean("mFocusLoaded");
			mNewsLoadedFromServer = savedInstanceState.getBoolean("mNewsLoaded");
		}else{
			mCategory = getArguments().getString("categoryName");
		}
		Log.d(tag, mCategory +" onCreate()");
		mNewsDAO = ((TouTiaoApp)getActivity().getApplication()).getNewsDAO();
		mHeadNewsDAO = ((TouTiaoApp)getActivity().getApplication()).getHeadNewsDAO();
	}
	
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		Log.d(tag, mCategory +" onActivityCreated()");
		mAsyncTaskCount = 0;
		showLoadingView();
		if( !mFocusLoadedFromServer )
			loadFocusFromServer();
		else
			loadFocusFromDB();
		if( !mNewsLoadedFromServer )
			loadNewsFromServer();
		else
			loadNewsFromDB();
	}
	
	private void loadFocusFromServer(){
		mAsyncTaskCount ++;
		Log.d(tag, mCategory +" loadFocusFromServer(): tasks: " + mAsyncTaskCount);
		new GetFocusAction(getActivity(), mCategory).execute(new BackgroundCallBack<List<News>>(){
			public void onSuccess(List<News> newsList) {
				for(News news : newsList){
					news.setFocus(true);
				}
				if("头条".equals(mCategory)){
					mHeadNewsDAO.deleteHeadFocus();//先删除旧的
					mHeadNewsDAO.save(newsList);//后保存新的
				}else{
					mNewsDAO.deleteFocusByCategory(mCategory);//先删除旧的
					mNewsDAO.save(newsList);//后保存新的
				}
			}
			public void onFailure(ActionError error) {
			}
		},new UICallBack<List<News>>(){
			public void onSuccess(List<News> newsList) {
				mFocusLoadedFromServer = true;
				mFocusNews = newsList;
				if(isResumed()){ // show the pager only if the fragement is resumed
					mPhotoPager.getPhotoViewPager().setAdapter(new PhotoPagerAdapter(getChildFragmentManager(), newsList));
					updatePhotoPager(0);
					afterLoadReturned();
				}
			}
			public void onFailure(ActionError error) {
				loadFocusFromDB();
				afterLoadReturned();
			}
		});
	}
	
	private void loadNewsFromServer(){
		mAsyncTaskCount ++;
		Log.d(tag, mCategory +" loadNewsFromServer(): tasks: " + mAsyncTaskCount);
		mGetnewsAction = new GetNewsAction(getActivity(), mCategory, 1, Constants.PAGE_SIZE);
		mGetnewsAction.execute(
			new BackgroundCallBack<Pagination<News>>(){
				public void onSuccess(Pagination<News> newsPage) {
					if("头条".equals(mCategory)){
						mHeadNewsDAO.deleteHeadNews();//先删除旧的
						mHeadNewsDAO.save(newsPage.getItems());//后保存新的
					}else{
						mNewsDAO.deleteNewsByCategory(mCategory);//先删除旧的
						mNewsDAO.save(newsPage.getItems());//后保存新的
					}
				}
				public void onFailure(ActionError error) {}
			}, 
			new UICallBack<Pagination<News>>(){
				public void onSuccess(Pagination<News> newsList) {
					mNewsLoadedFromServer = true;
					if(isDetached() || getActivity() == null) //DO NOT update the view if this fragment is detached from the activity.
						return;
					if(mNewsListAdapter == null){
						mNewsListAdapter = new NewsArrayAdapter(getActivity(), R.layout.view_news_list_item, newsList.getItems());
						setAdapter(mNewsListAdapter);
					}else{
						if(newsList.getItems().isEmpty()){
							Toast.makeText(getActivity(), R.string.no_more_to_load, Toast.LENGTH_SHORT).show();
						}else{
							mNewsListAdapter.clear();
							mNewsListAdapter.addMore(newsList.getItems());
						}
					}
					afterLoadReturned();
				}
				public void onFailure(ActionError error) {
					loadNewsFromDB();
					afterLoadReturned();
				}
			}
		);
	}
	
	private void loadFocusFromDB(){
		mAsyncTaskCount ++;
		Log.d(tag, mCategory +" loadFocusFromDB(): tasks: " + mAsyncTaskCount);
		new ParallelTask<List<News>>() {
			protected List<News> doInBackground(Void... params) {
				if("头条".equals(mCategory)){
					return mHeadNewsDAO.findHeadFocus();
				}
				return mNewsDAO.findFocusByCategory(mCategory);
			}
			public void onPostExecute(List<News> newsList){
				mFocusNews = newsList;
				if(!mFocusNews.isEmpty())
					mFocusLoadedFromServer = true;
				if(isDetached()){ // This Fragment is currently invisible.
					return;
				}
				mPhotoPager.getPhotoViewPager().setAdapter(new PhotoPagerAdapter(getChildFragmentManager(), newsList));
				updatePhotoPager(0);
				afterLoadReturned();
			}
		}.execute();
	}
	
	private void loadNewsFromDB(){
		mAsyncTaskCount ++;
		Log.d(tag, mCategory +" loadNewsFromDB(): tasks: " + mAsyncTaskCount);
		new ParallelTask<List<News>>() {
			protected List<News> doInBackground(Void... params) {
				if("头条".equals(mCategory)){
					return mHeadNewsDAO.findHeadNews();
				}
				return mNewsDAO.findNewsByCategory(mCategory);
			}
			public void onPostExecute(List<News> newsList){
				if(!newsList.isEmpty())
					mNewsLoadedFromServer = true;
				//The pager is viewing another page now.
				if(getActivity() == null)
					return;
				
				if(mNewsListAdapter == null){
					mNewsListAdapter = new NewsArrayAdapter(getActivity(), R.layout.view_news_list_item, newsList);
					mGetnewsAction = new GetNewsAction(getActivity(), mCategory, newsList.size(), Constants.PAGE_SIZE);
					setAdapter(mNewsListAdapter);
				}else{
					mNewsListAdapter.clear();
					mNewsListAdapter.addMore(newsList);
				}
				afterLoadReturned();
			}
		}.execute();
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		loadFocusFromServer();
		loadNewsFromServer();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		mAsyncTaskCount ++;
		Log.d(tag, mCategory +" onPullUpToRefresh(): tasks: " + mAsyncTaskCount);
		mGetnewsAction = (GetNewsAction)mGetnewsAction.getNextPageAction();
		mGetnewsAction.execute(
			new BackgroundCallBack<Pagination<News>>(){
				public void onSuccess(Pagination<News> newsPage) {
					if("头条".equals(mCategory)){
						mHeadNewsDAO.save(newsPage.getItems());//保存新的
					}else{
						mNewsDAO.save(newsPage.getItems());//保存新的
					}
				}
				public void onFailure(ActionError error) {
				}
			},  
			new UICallBack<Pagination<News>>(){
				public void onSuccess(Pagination<News> newsList) {
					if(isDetached() || getActivity() == null) //DO NOT update the view if this fragment is detached from the activity.
						return;
					if(mNewsListAdapter == null){
						mNewsListAdapter = new NewsArrayAdapter(getActivity(), R.layout.view_news_list_item, newsList.getItems());
						setAdapter(mNewsListAdapter);
					}else{
						if(newsList.getItems().isEmpty()){
							Toast.makeText(getActivity(), R.string.no_more_to_load, Toast.LENGTH_SHORT).show();
						}else{
							mNewsListAdapter.addMore(newsList.getItems());
						}
					}
					afterLoadReturned();
				}
				public void onFailure(ActionError error) {
					Toast.makeText(getActivity(), R.string.load_failed, Toast.LENGTH_SHORT).show();
					mGetnewsAction = (GetNewsAction)mGetnewsAction.getPreviousPageAction();
					afterLoadReturned();
				}
			}
		);
	}
	
	private void afterLoadReturned(){
		mAsyncTaskCount --;
		Log.d(tag, mCategory +" afterLoadReturned(): tasks: " + mAsyncTaskCount);
		if(mAsyncTaskCount == 0){
			showListView();
		}
		refreshComplete();
	}
	
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(tag, mCategory + " onSaveInstanceState");
		outState.putString("mCategory", mCategory);
		outState.putBoolean("mFocusLoaded", mFocusLoadedFromServer);
		outState.putBoolean("mNewsLoaded", mNewsLoadedFromServer);
	}

	public ViewHolder createHeaderView(LayoutInflater inflater){
		Log.d(tag, mCategory + " createHeaderView");
		ViewHolder holder = new ViewHolder();
		mPhotoPager = (PhotoPager)inflater.inflate(R.layout.view_news_list_header, null);
		mPhotoPager.getPhotoViewPager().setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				int currentPosition = mPhotoPager.getPhotoViewPager().getCurrentItem();
				News news = mNewsListAdapter.getItem(currentPosition);
				showNews(getActivity(), news);
			}
		});
		mPhotoPager.getPhotoViewPager().setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			public void onPageSelected(int position) {
				updatePhotoPager(position);
			}
		});
		holder.headerView = mPhotoPager;
		holder.height = 300;
		return holder;
	}

	private void updatePhotoPager(int position){
		if(mFocusNews != null && mFocusNews.size() > 0){
			mPhotoPager.setVisibility(View.VISIBLE);
			mPhotoPager.getPhotoNumView().setText(String.format("%d/%d", position + 1, mFocusNews.size()));
			mPhotoPager.getPhotoDescriptionView().setText(mFocusNews.get(position).getName());
		}else{
			mPhotoPager.setVisibility(View.GONE);
		}
	}
	
	private static void showNews(Context context, News news){
		Intent showNewsDetailIntent = new Intent();
		if(news.isSpecial()){
			showNewsDetailIntent.putExtra(Constants.INTENT_EXTRA_NEWS, news);
			showNewsDetailIntent.setClass(context, SpecialNewsActivity.class);
		}else{
			showNewsDetailIntent.putExtra(Constants.INTENT_EXTRA_NEWS, news);
			showNewsDetailIntent.setClass(context, NewsDetailActivity.class);
		}
		context.startActivity(showNewsDetailIntent);
	}
	
	public static class NewsArrayAdapter extends PTRListAdapter<News> {
        private LayoutInflater mInflater;
        public NewsArrayAdapter(Context context, int res, List<News> items) {
            super(context, res, items);
            mInflater = LayoutInflater.from(context);
        }

		public View getView(final int position, View convertView,
                ViewGroup parent) {
        	final News news = getItem(position);
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate( R.layout.view_news_list_item, parent, false);
                holder = new ViewHolder();
                holder.newsThumbnail = (CustomizeImageView) convertView.findViewById(R.id.id_news_thumbnail);
                holder.newsTitle = (TextView) convertView.findViewById(R.id.id_news_title);
                holder.newsVideoSign = convertView.findViewById(R.id.id_news_video_sign);
                holder.newsSpecialSign = convertView.findViewById(R.id.id_news_special_sign);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if(!TextUtils.isEmpty(news.getName())){
                holder.newsTitle.setText(news.getName());
            }
            if( news.getThumbnailUrls().size() > 0){
        		holder.newsThumbnail.loadImage(news.getThumbnailUrls().get(0));
            }
            
            holder.newsVideoSign.setVisibility( news.isHasVideo() ? View.VISIBLE : View.INVISIBLE);
            holder.newsSpecialSign.setVisibility( news.isSpecial() ? View.VISIBLE : View.INVISIBLE);
            
            convertView.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					showNews(getContext(), news);
				}
            });
            
            return convertView;
        }

        class ViewHolder {
        	CustomizeImageView newsThumbnail;
            TextView  newsTitle;
            View newsVideoSign;
            View newsSpecialSign;
        }
    }
	
	private class PhotoPagerAdapter extends FragmentStatePagerAdapter {
		public static final String tag = "TT-PhotoPagerAdapter";
		List<News> news;
		public PhotoPagerAdapter(FragmentManager fm, List<News> news) {
			super(fm);
			this.news = news;
		}

		@Override
		public Fragment getItem(int position) {
			Log.d(tag, mCategory + ": getItem: " + position);
			return new MainPhotoFragment(news.get(position));
		}

		@Override
		public int getCount() {
			return news.size();
		}
	}
}