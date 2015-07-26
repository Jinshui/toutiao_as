package com.yingshi.toutiao.view.ptr;

import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.yingshi.toutiao.R;

public abstract class AbstractPTRFragment<T extends Parcelable, I extends Parcelable> extends Fragment implements OnRefreshListener2<ListView> {
	private final static String tag = "TT-AbstractPTRFragment";
	public static class ViewHolder{
		public View headerView;
		public int height;
	}
	private final static long REFRESH_INTERVAL = 600000; 
	
	private T mData;
	private long mLastRefreshTime;
	private PTRListAdapter<I> mListAdapter;
	
	private CustomizedPTRListView mListView;
	private View mProgressBar;
	private boolean mIsLoadingPage = false;

	public AbstractPTRFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mData = savedInstanceState.getParcelable("mData");
			mLastRefreshTime = savedInstanceState.getLong("mRefreshTime");
		}
	}

	public void onSaveInstanceState(Bundle outState) {
		outState.putLong("mRefreshTime", mLastRefreshTime);
		outState.putParcelable("mData", mData);
	}

	public void onResume() {
		super.onResume();
		doResume();
	}
	
	/**
	 * Override this method to do your stuff while resuming
	 */
	protected void doResume(){
		if (mData == null || System.currentTimeMillis() - mLastRefreshTime > REFRESH_INTERVAL) {
			loadPage();
		} else {
			showContent(mData);
		}
	}
	/**
	 * set the refresh mode: START or END
	 * @param mode
	 */
	public void setMode(Mode mode){
		mListView.setMode(mode);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		loadMore();
	}

	public void onPullUpToRefresh(final PullToRefreshBase<ListView> refreshView) {
		loadMore();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.view_ptr_list_layout, null);
		mListView = (CustomizedPTRListView) layout.findViewById(R.id.id_content);
		ViewHolder holder = createHeader(inflater);
		if(holder != null && holder.headerView != null)
			mListView.addViewToListHeader(holder.headerView, holder.height);
		mListView.setOnRefreshListener(this);
		mProgressBar = layout.findViewById(R.id.id_loading);
		//hide both before any data is available
		mListView.setVisibility(View.GONE);
		mProgressBar.setVisibility(View.GONE);
		return layout;
	}
	
	/**
	 * Overrite this method to create your customized view that you want to add to the header of the pull-to-refresh list view
	 */
	public abstract ViewHolder createHeader(LayoutInflater inflater);
	public abstract PTRListAdapter<I> onDataChanged(T data); 
	public abstract T loadData(); 
	public abstract List<I> loadMoreList(); 
	
	public T getData(){
		return mData;
	}
	
	private void loadMore() {
		new LoadMoreTask(mListView).execute();
	}

	protected void loadPage() {
		if (!mIsLoadingPage) {
			mIsLoadingPage = true;
			showLoadingbar();
			new LoadPageTask().execute();
		}
	}

	protected final void showLoadingbar() {
		mProgressBar.setVisibility(View.VISIBLE);
		mListView.setVisibility(View.GONE);
	}

	protected final void showContent(T data) {
		mProgressBar.setVisibility(View.GONE);
		mListView.setVisibility(View.VISIBLE);
		if(isResumed()){
			mListView.setAdapter(mListAdapter = onDataChanged(data));
		}
	}

	
	private class LoadPageTask extends AsyncTask<Void, Void, T> {
		protected T doInBackground(Void... params) {
			Log.d(tag, "started loading data");
			return loadData();
		}
		
		protected void onPostExecute(T result) {
			Log.d(tag, "finished loading page");
			mIsLoadingPage = false;
			mLastRefreshTime = System.currentTimeMillis();
			mData = result;
			showContent(result);
		}
	}

	private class LoadMoreTask extends AsyncTask<Void, Void, List<I>> {

		private PullToRefreshBase<?> mListView;

		public LoadMoreTask(PullToRefreshBase<?> refreshedView) {
			mListView = refreshedView;
		}

		@Override
		protected List<I> doInBackground(Void... params) {
			return loadMoreList();
		}

		@Override
		protected void onPostExecute(List<I> result) {
			// Do some stuff here
			// Call onRefreshComplete when the list has been refreshed.
//			mListView.onRefreshComplete();
//			if(result != null)
//				mListAdapter.addObjects(result);
		}
	}
}