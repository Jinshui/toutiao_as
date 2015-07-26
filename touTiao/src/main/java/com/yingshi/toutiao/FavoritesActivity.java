package com.yingshi.toutiao;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.yingshi.toutiao.MainFragment.NewsArrayAdapter;
import com.yingshi.toutiao.actions.GetFavoritesAction;
import com.yingshi.toutiao.actions.AbstractAction.ActionError;
import com.yingshi.toutiao.actions.AbstractAction.UICallBack;
import com.yingshi.toutiao.model.News;
import com.yingshi.toutiao.model.Pagination;
import com.yingshi.toutiao.view.HeaderView;
import com.yingshi.toutiao.view.ptr.HeaderLoadingSupportPTRListFragment;
import com.yingshi.toutiao.view.ptr.PTRListAdapter;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class FavoritesActivity extends FragmentActivity
{
	public static final String tag = "TT-FavoritesActivity";

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_favorites);
		HeaderView headerView = (HeaderView) findViewById(R.id.id_favorites_header);
		headerView.setTitle(R.string.my_favorites);
		headerView.setLeftImage(R.drawable.fanhui, new OnClickListener(){
			public void onClick(View v) {
				finish();
			}
		});
		getSupportFragmentManager().beginTransaction().replace(R.id.id_favorites_list, new FavoritesFragment()).commit();
	}
	
	class FavoritesFragment extends HeaderLoadingSupportPTRListFragment {
		private GetFavoritesAction mGetFavoritesAction;
		private PTRListAdapter<News> mNewsListAdapter;
		private boolean mIsLoaded = false;
		
		public void onActivityCreated(Bundle savedInstanceState){
			super.onActivityCreated(savedInstanceState);
			if(!mIsLoaded){
				showLoadingView();
				mGetFavoritesAction  = new GetFavoritesAction(getActivity(), 1, 20);
				mGetFavoritesAction.execute(new UICallBack<Pagination<News>>(){
					public void onSuccess(Pagination<News> newsList) {
						mIsLoaded = true;
						if(getActivity() == null){ //Page has been detached
							return;
						}
						if(mNewsListAdapter == null){
							mNewsListAdapter = new NewsArrayAdapter(getActivity(), R.layout.view_news_list_item, newsList.getItems());
							setAdapter(mNewsListAdapter);
						}else{
							mNewsListAdapter.addMore(newsList.getItems());
						}
						
						if(newsList.getItems().isEmpty()){
							Toast.makeText(getActivity(), R.string.no_more_to_load, Toast.LENGTH_SHORT).show();
						}
						
						showListView();
						refreshComplete();
					}
					public void onFailure(ActionError error) {
						mGetFavoritesAction = (GetFavoritesAction)mGetFavoritesAction.cloneCurrentPageAction();
						//TODO: Show failure
						showListView();
						refreshComplete();
					}
				});
			}
		}
		
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		}

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			mGetFavoritesAction = (GetFavoritesAction)mGetFavoritesAction.getNextPageAction();
			mGetFavoritesAction.execute(new UICallBack<Pagination<News>>(){
				public void onSuccess(Pagination<News> newsList) {
					mIsLoaded = true;
					if(getActivity() == null){ //Page has been detached
						return;
					}
					if(mNewsListAdapter == null){
						mNewsListAdapter = new NewsArrayAdapter(getActivity(), R.layout.view_news_list_item, newsList.getItems());
						setAdapter(mNewsListAdapter);
					}else{
						mNewsListAdapter.addMore(newsList.getItems());
					}
					
					if(newsList.getItems().isEmpty()){
						Toast.makeText(getActivity(), R.string.no_more_to_load, Toast.LENGTH_SHORT).show();
					}
					
					showListView();
					refreshComplete();
				}
				public void onFailure(ActionError error) {
					mGetFavoritesAction = (GetFavoritesAction)mGetFavoritesAction.getPreviousPageAction();
					//TODO: Show failure
					showListView();
					refreshComplete();
				}
			});
		}
		public ViewHolder createHeaderView(LayoutInflater inflater) {
			setMode(Mode.PULL_FROM_END);
			return null;
		}
	}
}