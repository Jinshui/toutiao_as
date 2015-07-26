package com.yingshi.toutiao.social;

public interface SocialResponseListener {
	public void onLoginSuccess(AccountInfo accountInfo);
	public void onFailure(String error);
	public void onGetAccountInfo(AccountInfo accountInfo);
}
