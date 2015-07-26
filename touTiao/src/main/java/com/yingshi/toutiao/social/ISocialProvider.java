package com.yingshi.toutiao.social;

public interface ISocialProvider {
	public void login(SocialResponseListener listener);
	public void getAccountInfo(SocialResponseListener callback);
}
