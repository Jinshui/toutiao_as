package com.yingshi.toutiao;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.yingshi.toutiao.actions.AbstractAction.ActionError;
import com.yingshi.toutiao.actions.AbstractAction.UICallBack;
import com.yingshi.toutiao.actions.GetSpecialAction;
import com.yingshi.toutiao.actions.GetSpecialNewsAction;
import com.yingshi.toutiao.model.News;
import com.yingshi.toutiao.model.Pagination;
import com.yingshi.toutiao.model.Special;
import com.yingshi.toutiao.view.CustomizeImageView;
import com.yingshi.toutiao.view.SpecialHeader;
import com.yingshi.toutiao.view.ptr.HeaderLoadingSupportPTRListFragment;
import com.yingshi.toutiao.view.ptr.PTRListAdapter;

public class SpecialNewsFragment extends HeaderLoadingSupportPTRListFragment{
	private final static String tag = "TT-SpecialPageFragment";
	
	private String mSpecialName;
	private SpecialHeader mSpecialHeader;
	private SpecialNewsArrayAdapter mSpecialNewsArrayAdapter;
	private GetSpecialNewsAction mGetSpecialNewsAction;
	private int mAsyncTaskCount = 0;

	public SpecialNewsFragment() {
	}
	
	public SpecialNewsFragment(String specialName) {
		Log.d(tag, specialName + " new SpecialPageFragment");
		mSpecialName = specialName;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(tag, mSpecialName + " onCreate");
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mSpecialName = savedInstanceState.getString("mSpecialName");
		}
	}
	
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		loadPage(true);
	}
	
	private void loadPage(boolean showLoadingView){
		if(showLoadingView)
			showLoadingView();
		mAsyncTaskCount = 2;
		new GetSpecialAction(getActivity(), mSpecialName).execute(new UICallBack<Pagination<Special>>(){
			public void onSuccess(Pagination<Special> specials) {
				if( ! specials.getItems().isEmpty() ){
					Special special = specials.getItems().get(0);
					if(special.getPhotoUrls().size() > 0)
						mSpecialHeader.getHeaderImageView().loadImage(special.getPhotoUrls().get(0));
					mSpecialHeader.getSummaryView().setText(special.getSummary());
				}
				if(--mAsyncTaskCount == 0){
					showListView();
				}
				refreshComplete();
			}
			public void onFailure(ActionError error) {
				if(--mAsyncTaskCount == 0){
					showListView();
				}
				refreshComplete();
			}
		});
		
		mGetSpecialNewsAction = new GetSpecialNewsAction(getActivity(), mSpecialName, 1, 20);
		mGetSpecialNewsAction.execute(new UICallBack<Pagination<News>>(){
			public void onSuccess(Pagination<News> newsList) {
				if(mSpecialNewsArrayAdapter == null){
					mSpecialNewsArrayAdapter = new SpecialNewsArrayAdapter(getActivity(), R.layout.view_special_list_item, newsList.getItems());
					setAdapter(mSpecialNewsArrayAdapter);
				}else{
					mSpecialNewsArrayAdapter.clear();
					mSpecialNewsArrayAdapter.addMore(newsList.getItems());
				}
				if(--mAsyncTaskCount == 0){
					showListView();
				}
				refreshComplete();
			}
			public void onFailure(ActionError error) {
				mGetSpecialNewsAction = (GetSpecialNewsAction)mGetSpecialNewsAction.cloneCurrentPageAction();
				Toast.makeText(getActivity(), R.string.load_special_fails, Toast.LENGTH_SHORT).show();
				//TODO: Show failure
				if(--mAsyncTaskCount == 0){
					showListView();
				}
				refreshComplete();
			}
		});
	}

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(tag, mSpecialName + " onSaveInstanceState");
		outState.putString("mSpecialName", mSpecialName);
	}


	public ViewHolder createHeaderView(LayoutInflater inflater){
		ViewHolder holder = new ViewHolder();
		mSpecialHeader = (SpecialHeader)inflater.inflate(R.layout.view_special_list_header, null);
		holder.headerView = mSpecialHeader;
		holder.height = LinearLayout.LayoutParams.WRAP_CONTENT;
		return holder;
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		loadPage(false);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		mGetSpecialNewsAction = (GetSpecialNewsAction)mGetSpecialNewsAction.getNextPageAction();
		mGetSpecialNewsAction.execute(new UICallBack<Pagination<News>>(){
			public void onSuccess(Pagination<News> newsList) {
				if(mSpecialNewsArrayAdapter == null){
					mSpecialNewsArrayAdapter = new SpecialNewsArrayAdapter(getActivity(), R.layout.view_special_list_item, newsList.getItems());
					setAdapter(mSpecialNewsArrayAdapter);
				}else{
					mSpecialNewsArrayAdapter.addMore(newsList.getItems());
				}
				if(--mAsyncTaskCount == 0){
					showListView();
				}
				refreshComplete();
			}
			public void onFailure(ActionError error) {
				mGetSpecialNewsAction = (GetSpecialNewsAction)mGetSpecialNewsAction.getPreviousPageAction();
				Toast.makeText(getActivity(), R.string.load_special_fails, Toast.LENGTH_SHORT).show();
				//TODO: Show failure
				if(--mAsyncTaskCount == 0){
					showListView();
				}
				refreshComplete();
			}
		});
	}

	protected class SpecialNewsArrayAdapter extends PTRListAdapter<News> {
        private LayoutInflater mInflater;
        public SpecialNewsArrayAdapter(Context context, int res, List<News> items) {
            super(context, res, items);
            mInflater = LayoutInflater.from(context);
        }

		public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate( R.layout.view_special_list_item, null);
                holder = new ViewHolder();
                holder.newsThumbnail = (CustomizeImageView) convertView.findViewById(R.id.id_special_thumbnail);
                holder.newsTitle = (TextView) convertView.findViewById(R.id.id_special_title);
                holder.newsSummary = (TextView)  convertView.findViewById(R.id.id_special_summary);
                holder.newsVideoSign = convertView.findViewById(R.id.id_special_video_sign);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
        	final News news = getItem(position);
            if(!TextUtils.isEmpty(news.getName())){
                holder.newsTitle.setText(news.getName());
            }
            if( news.getThumbnailUrls().size() > 0){
        		holder.newsThumbnail.loadImage(news.getThumbnailUrls().get(0));
            }

            if(!TextUtils.isEmpty(news.getSummary())){
                holder.newsSummary.setText(news.getSummary());
            }
            holder.newsVideoSign.setVisibility( news.isHasVideo() ? View.VISIBLE : View.INVISIBLE);
            convertView.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					Intent showNewsDetailIntent = new Intent();
					showNewsDetailIntent.setClass(getContext(), NewsDetailActivity.class);
					showNewsDetailIntent.putExtra(Constants.INTENT_EXTRA_NEWS, news);
					getActivity().startActivity(showNewsDetailIntent);
				}
            });
            return convertView;
        }

        class ViewHolder {
        	CustomizeImageView newsThumbnail;
            TextView  newsTitle;
            TextView  newsSummary;
            View newsVideoSign;
        }
    }
}