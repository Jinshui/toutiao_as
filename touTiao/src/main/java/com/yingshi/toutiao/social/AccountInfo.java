package com.yingshi.toutiao.social;

import com.yingshi.toutiao.model.BaseModel;

public class AccountInfo extends BaseModel {
	private String provider;
	private String userName;
	private String photoUrl;
	private String photoBase64;
	private String openId;
	private String token;
	private long lastLogin;
	private long expiresIn;
	
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPhotoUrl() {
		return photoUrl;
	}
	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}
	public String getPhotoBase64(){
		return photoBase64;
	}
	public void setPhotoBase64(String photoBase64) {
		this.photoBase64 = photoBase64;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public long getLastLogin() {
		return lastLogin;
	}
	public void setLastLogin(long lastLogin) {
		this.lastLogin = lastLogin;
	}
	public long getExpiresIn() {
		return expiresIn;
	}
	public void setExpiresIn(long expiresIn) {
		this.expiresIn = expiresIn;
	}
}
