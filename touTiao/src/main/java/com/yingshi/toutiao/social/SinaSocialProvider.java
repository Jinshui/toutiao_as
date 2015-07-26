package com.yingshi.toutiao.social;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.User;
import com.yingshi.toutiao.Constants;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

public class SinaSocialProvider implements ISocialProvider{
	private static final String tag = "TT-SinaSocialProvider";
	public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    public static final String SCOPE = 
            "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";
	private Activity mActivity;
	private Oauth2AccessToken mAccessToken;
	
	
	public SinaSocialProvider(Activity activity){
		mActivity = activity;
	}
	
	
	public void login(final SocialResponseListener callback) {
		WeiboAuth mWeiboAuth = new WeiboAuth(mActivity, Constants.APP_WEIBO_KEY, REDIRECT_URL, SCOPE);
		mWeiboAuth.anthorize(new WeiboAuthListener() {
			public void onComplete(Bundle values) {
				mAccessToken = Oauth2AccessToken.parseAccessToken(values); // 从 Bundle 中解析 Token
				Log.d(tag, mAccessToken.toString());
				if (mAccessToken.isSessionValid()) {
					AccountInfo accountInfo = new AccountInfo();
					accountInfo.setProvider(Constants.SINA);
					accountInfo.setExpiresIn(mAccessToken.getExpiresTime());
					accountInfo.setOpenId(mAccessToken.getUid());
					accountInfo.setToken(mAccessToken.getToken());
					if(callback != null){
						callback.onLoginSuccess(accountInfo);
					}
				} else {
					// 当您注册的应用程序签名不正确时,就会收到错误Code,请确保签名正确
					if(callback != null){
						callback.onFailure(values.getString("code"));
					}
				}
			}
			
			public void onCancel() {
				
			}

			public void onWeiboException(WeiboException arg0) {
				Log.e(tag, "Failed to login sina weibo", arg0);
				if(callback != null){
					callback.onFailure(arg0.getMessage());
				}
			}
		});
	}
	
	public void getAccountInfo(final SocialResponseListener callback){
		UsersAPI mUsersAPI = new UsersAPI(mAccessToken);
		long uid = Long.parseLong(mAccessToken.getUid());
		mUsersAPI.show(uid, new RequestListener() {
			public void onComplete(String response) {
				Log.d(tag, "Got user info: " + response);
				if (!TextUtils.isEmpty(response)) {
					User user = User.parse(response);
					if(callback != null){
						AccountInfo accountInfo = new AccountInfo();
						accountInfo.setUserName(user.name);
						accountInfo.setPhotoUrl(user.avatar_large);
						callback.onGetAccountInfo(accountInfo);
					}
				}
			}
			
			public void onWeiboException(WeiboException arg0) {
				Log.e(tag, "Failed to get user info.", arg0);
				if(callback != null){
					callback.onFailure(arg0.getMessage());
				}
			}
		});
	}
}
