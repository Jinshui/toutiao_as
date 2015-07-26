package com.yingshi.toutiao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.yingshi.toutiao.actions.ParallelTask;
import com.yingshi.toutiao.social.AccountInfo;
import com.yingshi.toutiao.util.PreferenceUtil;

public class WelcomeActivity extends Activity {
	private TouTiaoApp mApp;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_welcome);
		mApp = (TouTiaoApp)getApplication();
		loadAccountInfo();
		initialize();
	}
	
	private void loadAccountInfo(){
		new ParallelTask<AccountInfo>() {
			protected AccountInfo doInBackground(Void... params) {
				long expiresIn = Long.parseLong(PreferenceUtil.getString(getApplicationContext(), Constants.USER_TOKEN_EXPIRES_IN, "0"));
				if(expiresIn > System.currentTimeMillis()){
					//the session is still valid
					AccountInfo account = new AccountInfo();
					account.setExpiresIn(expiresIn);
					account.setOpenId(PreferenceUtil.getString(getApplicationContext(), Constants.USER_OPEN_ID, null));
					account.setToken(PreferenceUtil.getString(getApplicationContext(), Constants.USER_ACCESS_TOKEN, null));
					account.setProvider(PreferenceUtil.getString(getApplicationContext(), Constants.USER_PROVIDER, null));
					account.setUserName(PreferenceUtil.getString(getApplicationContext(), Constants.USER_NAME, null));
					account.setPhotoUrl(PreferenceUtil.getString(getApplicationContext(), Constants.USER_PHOTO_URL, null));
					account.setPhotoBase64(PreferenceUtil.getString(getApplicationContext(), Constants.USER_PHOTO_BASE64, null));
					mApp.setUserInfo(account);
					return account;
				}else{
					//session has expired
					PreferenceUtil.removeKey(getApplicationContext(), Constants.USER_TOKEN_EXPIRES_IN);
					PreferenceUtil.removeKey(getApplicationContext(), Constants.USER_OPEN_ID);
					PreferenceUtil.removeKey(getApplicationContext(), Constants.USER_ACCESS_TOKEN);
					PreferenceUtil.removeKey(getApplicationContext(), Constants.USER_PROVIDER);
					PreferenceUtil.removeKey(getApplicationContext(), Constants.USER_NAME);
					PreferenceUtil.removeKey(getApplicationContext(), Constants.USER_PHOTO_URL);
					PreferenceUtil.removeKey(getApplicationContext(), Constants.USER_PHOTO_BASE64);
					return null;
				}
			}
		}.execute();
	}
	
	private void initialize(){
		new ParallelTask<Void>() {
			protected Void doInBackground(Void... params) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
				}
				return null;
			}
			
			protected void onPostExecute(Void result){
				if(mApp.getUserInfo() == null){
					startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
				}else{
					startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
				}
				finish();
			}
		}.execute();
	}
}
