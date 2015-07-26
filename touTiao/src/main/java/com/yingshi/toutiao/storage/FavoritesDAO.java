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
import com.yingshi.toutiao.storage.adapters.FavoritesDBAdapter;

public class FavoritesDAO extends BaseDAO<News>{
    static final String tag = "TT-FavoritesDAO";
    public FavoritesDAO(SQLiteDatabase mDb) {
        super(new FavoritesDBAdapter(mDb));
    }
    
    public List<News> findFavorites(int pageSize, int pageIndex){
    	return ((FavoritesDBAdapter)getDbAdapter()).findFavorites(pageSize, pageIndex);
    }
    
    public int deleteFavorites(long _id){
    	return getDbAdapter().delete("_id="+_id);
    }
}
