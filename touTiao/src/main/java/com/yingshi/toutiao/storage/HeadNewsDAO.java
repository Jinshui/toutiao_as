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

import java.util.List;

import android.database.sqlite.SQLiteDatabase;

import com.yingshi.toutiao.model.News;
import com.yingshi.toutiao.storage.adapters.HeadNewsDBAdapter;

public class HeadNewsDAO extends BaseDAO<News>{
    static final String tag = "TT-HeadNewsDAO";
    public HeadNewsDAO(SQLiteDatabase mDb) {
        super(new HeadNewsDBAdapter(mDb));
    }
    
    public int deleteHeadNews(){
    	return getDbAdapter().delete("isFocus=0");
    }
    
    public int deleteHeadFocus(){
    	return getDbAdapter().delete("isFocus=1");
    }
    
    public List<News> findHeadNews(){
    	return getDbAdapter().fetchAll("isFocus=0", null);
    }
    
    public List<News> findHeadFocus(){
    	return getDbAdapter().fetchAll("isFocus=1", null);
    }
}
