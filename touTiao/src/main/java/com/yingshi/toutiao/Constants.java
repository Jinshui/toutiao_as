package com.yingshi.toutiao;



public class Constants {//159357
	public static final String APP_WEIXIN_ID = "wxc0ff6c151ffb2eab";
	public static final String APP_TENCENT_ID = "1103289406";
	public static final String APP_WEIBO_KEY = "3491377561"; //ce23d9dacb21705e8ac3e82cc18a145b
	
	public static final String CACHE_DIR = "/toutiao";
	
	public static final String UPLOAD_ADDRESS = "http://115.28.85.247/upload";
	public static final String SERVER_ADDRESS = "http://115.28.85.247/nosession";
//	public static final String SERVER_ADDRESS = "http://192.168.204.72:8080/my-webapp/service";
//	public static final String SERVER_ADDRESS = "http://192.168.1.103:8080/my-webapp/service";
	
	//Intents
	public static final String INTENT_EXTRA_NEWS = "news";
//	public static final String INTENT_EXTRA_NEWS_ID = "news_id";
//	public static final String INTENT_EXTRA_NEWS_CATEGORY = "news_category";
	public static final String INTENT_ACTION_PHOTO_UPDATED = "photo_updated";
	
	//Preference keys
	public static final String USER_OPEN_ID = "user.openid";
	public static final String USER_ACCESS_TOKEN = "user.token";
	public static final String USER_TOKEN_EXPIRES_IN = "user.token.expires.in";
	public static final String USER_PHOTO_URL = "user.photo.url";
	public static final String USER_PHOTO_BASE64 = "user.photo.base64";
	public static final String USER_NAME = "user.name";
	public static final String USER_PROVIDER = "user.provider";
	public static final String PUSH_STATUS = "push.status";
	
	//Pagination
	public static final int PAGE_SIZE = 20;
	
	//Login
	public static final String SINA = "sina";
	public static final String WEIXIN = "weixin";
	public static final String QQ = "qq";
}
