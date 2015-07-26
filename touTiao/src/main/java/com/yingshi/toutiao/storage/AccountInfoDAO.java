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
package com.yingshi.toutiao.storage;

import android.database.sqlite.SQLiteDatabase;

import com.yingshi.toutiao.social.AccountInfo;
import com.yingshi.toutiao.storage.adapters.AccountInfoDBAdapter;

public class AccountInfoDAO extends BaseDAO<AccountInfo>{
    static final String tag = "TT-AccountInfoDAO";
    public AccountInfoDAO(SQLiteDatabase mDb) {
        super(new AccountInfoDBAdapter(mDb));
    }
}
