package com.yingshi.toutiao.storage.adapters;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;

import com.yingshi.toutiao.model.News;

public class FavoritesDBAdapter extends NewsDBAdapter {
    static final String tag = "TT-FavoritesDBAdapter";
	public static final String DB_TABLE_FAVORITES = "favorites";
	
    public FavoritesDBAdapter(SQLiteDatabase database) {
    	super(DB_TABLE_FAVORITES, database);
    }
    
    public List<News> findFavorites(int pageSize, int pageIndex){
    	return fetchPage(pageSize, pageIndex, null, "time");
    }
}
