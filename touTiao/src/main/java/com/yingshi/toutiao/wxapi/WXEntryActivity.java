package com.yingshi.toutiao.wxapi;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yingshi.toutiao.Constants;
import com.yingshi.toutiao.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{

	private final static String tag = "TT-WXEntryActivity";
	private IWXAPI mWxApi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mWxApi = WXAPIFactory.createWXAPI(this, Constants.APP_WEIXIN_ID, false);
		mWxApi.handleIntent(getIntent(), this);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onReq(BaseReq arg0) {
		Log.d(tag, "BaseReq: " +arg0.openId);
		this.finish();
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.d(tag, "resp.errCode:" + resp.errCode + ",resp.errStr:" + resp.errStr);
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			Toast.makeText(this, R.string.share_weichat_success, Toast.LENGTH_SHORT).show();
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			// 分享取消
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			Toast.makeText(this, R.string.share_weichat_denied, Toast.LENGTH_SHORT).show();
			break;
		}
		this.finish();
	}
}
