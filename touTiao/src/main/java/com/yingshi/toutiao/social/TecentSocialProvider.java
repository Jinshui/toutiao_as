package com.yingshi.toutiao.social;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.yingshi.toutiao.Constants;

public class TecentSocialProvider implements ISocialProvider{
	private static final String tag = "TT-TecentSocialProvider";
    public static QQAuth mQQAuth;
	private Tencent mTencent;
	private UserInfo mInfo = null;
	private Activity mActivity;
	
	
	public TecentSocialProvider(Activity activity){
		mActivity = activity;
		initTencent();
	}
	
	private void initTencent(){
		mQQAuth = QQAuth.createInstance(Constants.APP_TENCENT_ID, mActivity.getApplicationContext());
		mTencent = Tencent.createInstance(Constants.APP_TENCENT_ID, mActivity);
//		String expiresIn = PreferenceUtil.getString(this, TENCENT_EXPIRES_IN, "0");
//		long seconds = ( Long.parseLong(expiresIn) - System.currentTimeMillis() ) / 1000;
//		if(seconds > 0){
//			String openId = PreferenceUtil.getString(this, TENCENT_OPEN_ID, null);
//			String token = PreferenceUtil.getString(this, TENCENT_ACCESS_TOKEN, null);
//			mTencent.setOpenId(openId);
//			mTencent.setAccessToken(token, String.valueOf(seconds));
//		}
	}
	
	public void login(final SocialResponseListener callback) {
		if (!mQQAuth.isSessionValid()) {
			IUiListener listener = new IUiListener() {
				public void onCancel() {

				}

				public void onComplete(Object arg0) {
					JSONObject response = (JSONObject) arg0;
					try {
						Log.d(tag, response.toString(4));
						AccountInfo account = new AccountInfo();
						account.setProvider(Constants.QQ);
						account.setOpenId(response.getString("openid"));
						account.setToken(response.getString("access_token"));
						account.setExpiresIn(System.currentTimeMillis() + response.getLong("expires_in") * 1000);
						if(callback != null){
							callback.onLoginSuccess(account);
						}
					} catch (JSONException e) {
						if(callback != null){
							callback.onFailure(e.getMessage());
						}
					}
				}

				public void onError(UiError arg0) {
					if(callback != null){
						callback.onFailure(arg0.errorMessage);
					}
				}
			};
			// mQQAuth.login(this, "all", listener);
			mTencent.loginWithOEM(mActivity, "all", listener,"10000144","10000144","xxxx");
		}
	}
	
	public void getAccountInfo(final SocialResponseListener callback){
		if (mQQAuth != null && mQQAuth.isSessionValid()) {
			IUiListener listener = new IUiListener() {
				public void onError(UiError arg0) {
					Log.e(tag, "Failed load user info: " + arg0.errorDetail);
					if(callback != null){
						callback.onFailure(arg0.errorMessage);
					}
				}
				public void onComplete(final Object arg0) {
					try{
						JSONObject response = (JSONObject) arg0;
						Log.d(tag, response.toString(4));
						AccountInfo account = new AccountInfo();
						account.setUserName(response.getString("nickname"));
						account.setPhotoUrl(response.getString("figureurl_qq_2"));
						if(callback != null){
							callback.onGetAccountInfo(account);
						}
					} catch (JSONException e) {
						Log.e(tag, "Failed parse user info", e);
						if(callback != null){
							callback.onFailure(e.getMessage());
						}
					}
				}
				public void onCancel() {
				}
			};
			mInfo = new UserInfo(mActivity, mQQAuth.getQQToken());
			mInfo.getUserInfo(listener);
		}
	}
}
