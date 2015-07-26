package com.yingshi.toutiao.social;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.tencent.tauth.IRequestListener;
import com.tencent.utils.HttpUtils.HttpStatusException;
import com.tencent.utils.HttpUtils.NetworkUnavailableException;


public class BaseApiListener implements IRequestListener {
	private static Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	};
	
	@Override
	public void onComplete(final JSONObject response) {
		Message msg = new Message();
		Bundle params = new Bundle();
		params.putString("response", response.toString());
		params.putString("title", "onComplete");
		mHandler.sendMessage(msg);
	}

	@Override
	public void onIOException(final IOException e) {
		Message msg = new Message();
		Bundle params = new Bundle();
		params.putString("response", e.getMessage());
		params.putString("title", "onIOException");
		mHandler.sendMessage(msg);
	}

	@Override
	public void onMalformedURLException(final MalformedURLException e) {
		Message msg = new Message();
		Bundle params = new Bundle();
		params.putString("response", e.getMessage());
		params.putString("title", "onMalformedURLException");
		mHandler.sendMessage(msg);
	}

	@Override
	public void onJSONException(final JSONException e) {
		Message msg = new Message();
		Bundle params = new Bundle();
		params.putString("response", e.getMessage());
		params.putString("title", "onJSONException");
		mHandler.sendMessage(msg);
	}

	@Override
	public void onConnectTimeoutException(ConnectTimeoutException e) {
		Message msg = new Message();
		Bundle params = new Bundle();
		params.putString("response", e.getMessage());
		params.putString("title", "onConnectTimeoutException");
		mHandler.sendMessage(msg);
	}

	@Override
	public void onSocketTimeoutException(SocketTimeoutException e) {
		Message msg = new Message();
		Bundle params = new Bundle();
		params.putString("response", e.getMessage());
		params.putString("title", "onSocketTimeoutException");
		mHandler.sendMessage(msg);
	}

	@Override
	public void onUnknowException(Exception e) {
		Message msg = new Message();
		Bundle params = new Bundle();
		params.putString("response", e.getMessage());
		params.putString("title", "onUnknowException");
		mHandler.sendMessage(msg);
	}

	@Override
	public void onHttpStatusException(HttpStatusException arg0) {
		
	}

	@Override
	public void onNetworkUnavailableException(NetworkUnavailableException arg0) {
		
	}
}
