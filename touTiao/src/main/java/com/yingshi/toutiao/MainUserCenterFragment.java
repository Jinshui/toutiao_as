package com.yingshi.toutiao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.Base64;
import com.yingshi.toutiao.actions.ParallelTask;
import com.yingshi.toutiao.util.PreferenceUtil;
import com.yingshi.toutiao.util.Utils;
import com.yingshi.toutiao.view.CustomizeImageView;

public class MainUserCenterFragment extends Fragment
{
	private final static String tag = "TT-UserCenterFragment";
	private TouTiaoApp mApp;
	private SlidingMenu mSlidingMenu;
	private View mView;
	private ImageButton mReturnBtn;
	private CustomizeImageView mUserPhoto;
	private TextView mUserName;
	private View mBtnFavorites;
	private View mBtnDownloads;
	private View mBtnPush;
	private View mBtnClearCache;
	private ImageView mPushImageView;
	
	class PhotoUpdateBroadcastReceiver extends BroadcastReceiver{
		public void onReceive(Context context, Intent intent) {
			if(Constants.INTENT_ACTION_PHOTO_UPDATED.equals(intent.getAction())){
				Log.d(tag, "Received intent: " + Constants.INTENT_ACTION_PHOTO_UPDATED);
				loadUserInfo();
			}
		}
	}

	private PhotoUpdateBroadcastReceiver mReceiver;
	
	public MainUserCenterFragment(){}
	
	public MainUserCenterFragment(SlidingMenu slidingMenu){
		mSlidingMenu = slidingMenu;
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mReceiver = new PhotoUpdateBroadcastReceiver();
		mApp = (TouTiaoApp)(getActivity().getApplication());
	}
	
	public void onResume(){
		super.onResume();
		IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_PHOTO_UPDATED);
		getActivity().registerReceiver(mReceiver, filter);
	}
	
	public void onPause(){
		super.onPause();
		getActivity().unregisterReceiver(mReceiver);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		mView = inflater.inflate(R.layout.view_user_center, container, false);
		mReturnBtn = (ImageButton)mView.findViewById(R.id.id_btn_back_to_toutiao);
		mUserPhoto = (CustomizeImageView)mView.findViewById(R.id.id_user_profile_photo);
		mUserName = (TextView)mView.findViewById(R.id.id_user_name);
		mBtnFavorites = mView.findViewById(R.id.id_favorites);
		mBtnDownloads = mView.findViewById(R.id.id_downloads);
		mBtnPush = mView.findViewById(R.id.id_push);
		mBtnClearCache = mView.findViewById(R.id.id_clear_cache);
		mPushImageView = (ImageView)mView.findViewById(R.id.id_push_img);
		addListener();
		loadUserInfo();
		updatePushButton();
		return mView;
	}
	
	private void loadUserInfo(){
		if(mApp.getUserInfo()!=null){
			mUserName.setText(mApp.getUserInfo().getUserName());
			if(mApp.getUserInfo().getPhotoBase64() != null){
				mUserPhoto.loadImage(Base64.decode(mApp.getUserInfo().getPhotoBase64(), Base64.DEFAULT));
			}else if(mApp.getUserInfo().getPhotoUrl()!=null){
				mUserPhoto.loadImage(mApp.getUserInfo().getPhotoUrl());
			}
		}
	}
	
	private void updatePushButton(){
		new ParallelTask<Boolean>() {
			protected Boolean doInBackground(Void... params) {
				return PreferenceUtil.getInt(getActivity(), Constants.PUSH_STATUS, 1) == 1;
			}
			protected void onPostExecute(Boolean enabled){
				if(enabled){
					mPushImageView.setBackgroundResource(R.drawable.gerenzhongxin_tuisong);
				}else{
					mPushImageView.setBackgroundResource(R.drawable.push_off);
				}
			}
		}.execute();
	}
	
	private void addListener(){
		mUserPhoto.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if(mApp.getUserInfo() == null){
					getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
				}
			}});
		mReturnBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				mSlidingMenu.showContent();
			}});
		mBtnFavorites.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), FavoritesActivity.class);
				startActivity(intent);
			}});
		mBtnDownloads.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				NewsDownloader.getInstance((TouTiaoApp)getActivity().getApplication()).startDownlaod();
			}});
		mBtnClearCache.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				new ParallelTask<Void>() {
					protected Void doInBackground(Void... params) {
						Utils.removeFile(mApp.getCachePath());
						return null;
					}
					protected void onPostExecute(Void result){
						Toast.makeText(mApp, R.string.clear_cache_complete, Toast.LENGTH_SHORT).show();
					}
				}.execute();
			}});
		mBtnPush.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				new ParallelTask<Boolean>() {
					protected Boolean doInBackground(Void... params) {
						if(PreferenceUtil.getInt(getActivity(), Constants.PUSH_STATUS, 1) == 0){
							PreferenceUtil.saveInt(getActivity(), Constants.PUSH_STATUS, 1);
							return true;
						}else{
							PreferenceUtil.saveInt(getActivity(), Constants.PUSH_STATUS, 0);
							return false;
						}
					}
					protected void onPostExecute(Boolean result){
						if(result){
							mPushImageView.setBackgroundResource(R.drawable.gerenzhongxin_tuisong);
							Toast.makeText(getActivity(), R.string.push_on, Toast.LENGTH_SHORT).show();
						}else{
							mPushImageView.setBackgroundResource(R.drawable.push_off);
							Toast.makeText(getActivity(), R.string.push_off, Toast.LENGTH_SHORT).show();
						}
					}
				}.execute();
			}});
	}
}
