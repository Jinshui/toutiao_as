package com.yingshi.toutiao.storage.adapters;

import android.database.sqlite.SQLiteDatabase;

public class HeadNewsDBAdapter extends NewsDBAdapter {
    static final String tag = "TT-FavoritesDBAdapter";
	private static final String DB_TABLE_HEADNEWS = "headnews";
	
    public HeadNewsDBAdapter(SQLiteDatabase database) {
    	super(DB_TABLE_HEADNEWS, database);
    }
}
