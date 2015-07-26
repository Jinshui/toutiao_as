package com.yingshi.toutiao.social;

import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yingshi.toutiao.Constants;

import android.app.Activity;

public class WeixinSocialProvider implements ISocialProvider{
	private static final String tag = "TT-SinaSocialProvider";
	private Activity mActivity;
	private IWXAPI mWxapi = null;
	
	public WeixinSocialProvider(Activity activity){
		mActivity = activity;
        mWxapi = WXAPIFactory.createWXAPI(mActivity, Constants.APP_WEIXIN_ID, true);
	}
	
	
	public void login(final SocialResponseListener callback) {
		SendAuth.Req req = new SendAuth.Req();
		req.openId = Constants.APP_TENCENT_ID;
		req.scope = "snsapi_userinfo";
		req.state = Constants.APP_TENCENT_ID;
		mWxapi.sendReq(req);
	}
	
	public void getAccountInfo(final SocialResponseListener callback){
	}
}
