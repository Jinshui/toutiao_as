package com.yingshi.toutiao;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.yingshi.toutiao.model.News;
import com.yingshi.toutiao.view.CustomizeImageView;

public class MainPhotoFragment extends Fragment{
	private static final String tag = "TT-MainPhotoFragment";
	private News mNews;
	private CustomizeImageView mImageView;
	
	public MainPhotoFragment(){
	}
	
	public MainPhotoFragment(News news){
		mNews = news;
	}
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mImageView = (CustomizeImageView) inflater.inflate(R.layout.view_news_list_photo_page, container, false);
		return mImageView;
	}
	
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			mNews = savedInstanceState.getParcelable("mNews");
		}
		updateUI();
	}
	
	private  void updateUI(){
		Log.d(tag, "updateUI() with news " + (mNews!=null?mNews.get_id():""));
		if(mNews == null)
			return;
		if(mNews.getPhotoUrls().size() > 0){
			mImageView.loadImage(mNews.getPhotoUrls().get(0));
			mImageView.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					Intent showNewsDetailIntent = new Intent();
					showNewsDetailIntent.putExtra(Constants.INTENT_EXTRA_NEWS, mNews);
					showNewsDetailIntent.setClass(getActivity(), NewsDetailActivity.class);
					getActivity().startActivity(showNewsDetailIntent);
				}
			});
		}
	}
	
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable("mNews", mNews);
	}
}
