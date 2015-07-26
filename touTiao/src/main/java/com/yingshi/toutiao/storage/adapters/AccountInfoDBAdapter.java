/*
 * Copyright (C) 2011, Motorola Mobility, Inc,
 * All Rights Reserved.
 * Motorola Confidential Restricted.
 *
 * Modification History:
 **********************************************************
 * Date           Author         Comments
 * 12-Apr-2011    Jinshui Tang   Created file
 **********************************************************
 */
package com.yingshi.toutiao.storage.adapters;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yingshi.toutiao.social.AccountInfo;

public class AccountInfoDBAdapter extends BaseAdapter<AccountInfo> {

	public static final String DB_TABLE_NEWS = "accounts";
    private static String[] ACCOUNTS_COLUMNS = { "_id", "provider", "userName", "photoUrl", 
    	"openId", "token", "lastLogin", "expiresIn"};

    public AccountInfoDBAdapter(SQLiteDatabase database) {
    	super(DB_TABLE_NEWS, ACCOUNTS_COLUMNS, database);
    }

    public ContentValues toContentValues(AccountInfo account) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("provider", account.getProvider());
        initialValues.put("userName", account.getUserName());
        initialValues.put("photoUrl", account.getPhotoUrl());
        initialValues.put("openId", account.getOpenId());
        initialValues.put("token", account.getToken());
        initialValues.put("lastLogin", account.getLastLogin());
        initialValues.put("expiresIn", account.getExpiresIn());
        return initialValues;
    }
    
    
    public AccountInfo toObject(Cursor cursor) {
    	AccountInfo account = new AccountInfo();
    	account.set_id(cursor.getLong(cursor.getColumnIndex("_id")));
    	account.setProvider(cursor.getString(cursor.getColumnIndex("provider")));
    	account.setUserName(cursor.getString(cursor.getColumnIndex("userName")));
    	account.setPhotoUrl(cursor.getString(cursor.getColumnIndex("photoUrl")));
    	account.setOpenId(cursor.getString(cursor.getColumnIndex("openId")));
    	account.setToken(cursor.getString(cursor.getColumnIndex("token")));
    	account.setLastLogin(cursor.getLong((cursor.getColumnIndex("lastLogin"))));
    	account.setExpiresIn(cursor.getLong(cursor.getColumnIndex("expiresIn")));
        return account;
    }
}
