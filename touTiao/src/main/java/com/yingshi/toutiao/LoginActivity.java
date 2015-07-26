package com.yingshi.toutiao;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.yingshi.toutiao.actions.ParallelTask;
import com.yingshi.toutiao.social.AccountInfo;
import com.yingshi.toutiao.social.ISocialProvider;
import com.yingshi.toutiao.social.SinaSocialProvider;
import com.yingshi.toutiao.social.SocialResponseListener;
import com.yingshi.toutiao.social.TecentSocialProvider;
import com.yingshi.toutiao.social.WeixinSocialProvider;
import com.yingshi.toutiao.util.PreferenceUtil;

@SuppressLint("ShowToast")
public class LoginActivity extends Activity implements SocialResponseListener{
	private static final String tag = "TT-LoginActivity";
	public static final String INTENT_EXTRA_GO_NEWS_LIST = "LOGIN-FROM-ACTIVITY";
	private ISocialProvider mAuthProvider;
	private TouTiaoApp mApp;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mApp = (TouTiaoApp)getApplication();
	}
	
	public void loginQQ(View view) {
		mAuthProvider = new TecentSocialProvider(this);
		mAuthProvider.login(this);
	}

	public void loginWeibo(View view) {
		mAuthProvider = new SinaSocialProvider(this);
		mAuthProvider.login(this);
	}

	public void loginWeixin(View view) {
		mAuthProvider = new WeixinSocialProvider(this);
		mAuthProvider.login(this);
	}

	public void enter(View view) {
		showHomeActivity();
	}

	public void onLoginSuccess(AccountInfo accountInfo) {
		Map<String, String> qqAuthInfo = new HashMap<String, String>();
		qqAuthInfo.put(Constants.USER_PROVIDER, accountInfo.getProvider());
		qqAuthInfo.put(Constants.USER_OPEN_ID, accountInfo.getOpenId());
		qqAuthInfo.put(Constants.USER_ACCESS_TOKEN, accountInfo.getToken());
		qqAuthInfo.put(Constants.USER_TOKEN_EXPIRES_IN, String.valueOf(accountInfo.getExpiresIn()));
		PreferenceUtil.saveStringMap(this, qqAuthInfo);
		mApp.setUserInfo(accountInfo);
		mAuthProvider.getAccountInfo(this);
		showHomeActivity();
	}

	public void onFailure(String error) {
		Toast.makeText(this, error, Toast.LENGTH_LONG).show();
	}
	
	public void onGetAccountInfo(final AccountInfo accountInfo){
		PreferenceUtil.saveString(this, Constants.USER_PHOTO_URL, accountInfo.getPhotoUrl());
		PreferenceUtil.saveString(this, Constants.USER_NAME, accountInfo.getUserName());
		sendBroadcast(new Intent(Constants.INTENT_ACTION_PHOTO_UPDATED));
		mApp.getUserInfo().setUserName(accountInfo.getUserName());
		mApp.getUserInfo().setPhotoUrl(accountInfo.getPhotoUrl());
		new ParallelTask<Void>() {
			protected Void doInBackground(Void... params) {
				InputStream is = null;
				ByteArrayOutputStream baos = null;
				try{
					is = (InputStream) new URL(accountInfo.getPhotoUrl()).getContent();
					baos = new ByteArrayOutputStream();
					int b = is.read();
					while(b!=-1){ baos.write(b); b = is.read(); }
					String base64String = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
					mApp.getUserInfo().setPhotoBase64(base64String);
					PreferenceUtil.saveString(LoginActivity.this, Constants.USER_PHOTO_BASE64, base64String);
				}catch(Exception e){
					Log.e(tag, "Error retrieving photo from " + accountInfo.getPhotoUrl(), e);
				}finally{
					if(is!=null) try{is.close();}catch(Exception e){}
					if(baos!=null) try{baos.close();}catch(Exception e){}
				}
				return null;
			}
		}.execute();
	}
	
	private void showHomeActivity(){
		if(getIntent().getBooleanExtra(INTENT_EXTRA_GO_NEWS_LIST, true)){
			Intent intent = new Intent();
			intent.setClass(this, MainActivity.class);
			intent.putExtra(MainActivity.INTENT_EXTRA_SHOW_USER_CENTER, true);
			startActivity(intent);
		}
		finish();
	}
}