package com.yingshi.toutiao;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.yingshi.toutiao.MainFragment.NewsArrayAdapter;
import com.yingshi.toutiao.actions.SearchAction;
import com.yingshi.toutiao.actions.AbstractAction.ActionError;
import com.yingshi.toutiao.actions.AbstractAction.BackgroundCallBack;
import com.yingshi.toutiao.actions.AbstractAction.UICallBack;
import com.yingshi.toutiao.model.News;
import com.yingshi.toutiao.model.Pagination;
import com.yingshi.toutiao.storage.NewsDAO;
import com.yingshi.toutiao.view.HeaderView;
import com.yingshi.toutiao.view.ptr.HeaderLoadingSupportPTRListFragment;
import com.yingshi.toutiao.view.ptr.PTRListAdapter;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class SearchActivity extends FragmentActivity {

	public static final String INTENT_EXTRA_SPECIAL_ID = "special_id";
	private EditText mKeywordText;
	private SearchFragment mSearchFragment;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		HeaderView headerView = (HeaderView) findViewById(R.id.id_search_header);
		headerView.setTitle(R.string.search_header_text);
		headerView.setLeftImage(R.drawable.fanhui, new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		mKeywordText = (EditText) findViewById(R.id.id_search_keyword_text);
		mSearchFragment = new SearchFragment();
		getSupportFragmentManager().beginTransaction().replace(R.id.id_search_result_list, mSearchFragment).commit();
	}

	public void doSearch(View view){
		String keyWord = mKeywordText.getText().toString();
		if(!TextUtils.isEmpty(keyWord)){
			mSearchFragment.doSearch(keyWord);
		}
	}
	
	
	class SearchFragment extends HeaderLoadingSupportPTRListFragment {
		private String mKeyword;
		private SearchAction mSearchAction;
		private NewsDAO mNewsDAO;
		private PTRListAdapter<News> mNewsListAdapter;
		private BackgroundCallBack<Pagination<News>> mSearchNewsListBackgroundCallback = new BackgroundCallBack<Pagination<News>>(){
			public void onSuccess(Pagination<News> newsPage) {
				mNewsDAO.save(newsPage.getItems());
			}
			public void onFailure(ActionError error) {}
		};
		
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mNewsDAO = ((TouTiaoApp)getActivity().getApplication()).getNewsDAO();
		}

		public void doSearch(String keyword){
			mKeyword = keyword;
			showLoadingView();
			mSearchAction = new SearchAction(getActivity(), mKeyword, 1, 20);
//			mSearchAction.execute(mSearchNewsListBackgroundCallback, searchUICallBack);
			mSearchAction.execute(new UICallBack<Pagination<News>>(){
				public void onSuccess(Pagination<News> newsList) {
					if(mNewsListAdapter == null){
						mNewsListAdapter = new NewsArrayAdapter(getActivity(), R.layout.view_news_list_item, newsList.getItems());
						setAdapter(mNewsListAdapter);
					}else{
						mNewsListAdapter.clear();
						mNewsListAdapter.addMore(newsList.getItems());
					}
					
					if(newsList.getItems().isEmpty()){
						Toast.makeText(getActivity(), R.string.search_no_result, Toast.LENGTH_SHORT).show();
					}
					
					showListView();
					refreshComplete();
				}
				public void onFailure(ActionError error) {
					Toast.makeText(getActivity(), R.string.load_complete, Toast.LENGTH_SHORT).show();
					showListView();
					refreshComplete();
				}
			});
		}
		
		public ViewHolder createHeaderView(LayoutInflater inflater){
			setMode(Mode.PULL_FROM_END);
			return null;
		}

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			mSearchAction = (SearchAction)mSearchAction.getNextPageAction();
//			mSearchAction.execute(mSearchNewsListBackgroundCallback, searchUICallBack);
			mSearchAction.execute(new UICallBack<Pagination<News>>(){
				public void onSuccess(Pagination<News> newsList) {
					if(mNewsListAdapter == null){
						mNewsListAdapter = new NewsArrayAdapter(getActivity(), R.layout.view_news_list_item, newsList.getItems());
						setAdapter(mNewsListAdapter);
					}else{
						mNewsListAdapter.addMore(newsList.getItems());
					}
					
					if(newsList.getItems().isEmpty()){
						Toast.makeText(getActivity(), R.string.load_complete, Toast.LENGTH_SHORT).show();
					}
					
					showListView();
					refreshComplete();
				}
				public void onFailure(ActionError error) {
					Toast.makeText(getActivity(), R.string.load_complete, Toast.LENGTH_SHORT).show();
					showListView();
					refreshComplete();
				}
			});
		}
	}
}