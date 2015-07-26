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

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.yingshi.toutiao.model.News;

public class NewsDBAdapter extends BaseAdapter<News> {
    static final String tag = "TT-NewsDBAdapter";
    private static final String DB_TABLE_NEWS = "news";
    private static String[] NEWS_COLUMNS = { "_id", "id", "name", "summary", 
    	"content", "time", "category", "contact", "likes", "isSpecial", 
    	"specialName", "hasVideo", "videoUrl", "videoPhotoUrl", "author", 
    	"photoUrl", "thumbnailUrl", "videoPhotoFilePath", "photoFilePath", 
    	"thumbnailFilePath","isFocus", "isUserCache"};

    public NewsDBAdapter(SQLiteDatabase database) {
    	super(DB_TABLE_NEWS, NEWS_COLUMNS, database);
    }
    
    public NewsDBAdapter(String dbName, SQLiteDatabase database) {
    	super(dbName, NEWS_COLUMNS, database);
    }

    public long insert(News news) {
    	News existingNews = fetchOneByNewsId(news.getId());
    	if(existingNews != null){
    		Log.d(tag, "Found existing record in " + getTableName() + ": " + existingNews.get_id());
    		return existingNews.get_id();
    	}
		return super.insert(news);
    }
    
    public ContentValues toContentValues(News news) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("id", news.getId());
        initialValues.put("name", news.getName());
        initialValues.put("summary", news.getSummary());
        initialValues.put("content", news.getContent());
        initialValues.put("time", news.getTime());
        initialValues.put("category", news.getCategory());
        initialValues.put("contact", news.getContact());
        initialValues.put("likes", news.getLikes());
        initialValues.put("isSpecial", news.isSpecial());
        initialValues.put("specialName", news.getSpecialName());
        initialValues.put("hasVideo", news.isHasVideo());
        initialValues.put("videoUrl", news.getVideoUrl());
        initialValues.put("videoPhotoUrl", news.getVideoPhotoUrl());
        initialValues.put("author", news.getAuthor());
        String photoUrl = "";
        for(int i=0; i<news.getPhotoUrls().size(); i++){
        	if(i > 0)
        		photoUrl += ";;;";
        	photoUrl += news.getPhotoUrls().get(i);
        }
        initialValues.put("photoUrl", photoUrl);
        String thumbnailUrl = "";
        for(int i=0; i<news.getThumbnailUrls().size(); i++){
        	if(i > 0)
        		thumbnailUrl += ";;;";
        	thumbnailUrl += news.getThumbnailUrls().get(i);
        }
        initialValues.put("thumbnailUrl", thumbnailUrl);
        initialValues.put("videoPhotoFilePath", news.getVideoPhotoFilePath());
        initialValues.put("photoFilePath", news.getPhotoFilePath());
        initialValues.put("thumbnailFilePath", news.getThumbnailFilePath());
        initialValues.put("isFocus", news.isFocus());
        initialValues.put("isUserCache", news.isUserCache());
        return initialValues;
    }
    
    
	public News fetchOneByNewsId(String newsId){
		Log.d(tag, "fetchOneByNewsId: newsId=" + newsId);
		Cursor cursor = getDatabase().query(true, getTableName(), getColumnNames(), "id='"+newsId+"'", null, null, null, null, null);
        if (cursor != null) {
            try{
            	if(cursor.moveToFirst())
            		return toObject(cursor);
            } finally {
                cursor.close();
            }
        }
        return null;
	}
    
    public News toObject(Cursor cursor) {
    	News news = new News();
        news.set_id(cursor.getLong(cursor.getColumnIndex("_id")));
        news.setId(cursor.getString(cursor.getColumnIndex("id")));
        news.setName(cursor.getString(cursor.getColumnIndex("name")));
        news.setSummary(cursor.getString(cursor.getColumnIndex("summary")));
        news.setTime(cursor.getLong(cursor.getColumnIndex("time")));
        news.setContent(cursor.getString(cursor.getColumnIndex("content")));
        news.setCategory(cursor.getString(cursor.getColumnIndex("category")));
        news.setContact(cursor.getString(cursor.getColumnIndex("contact")));
        news.setLikes(cursor.getInt(cursor.getColumnIndex("likes")));
        news.setSpecial(cursor.getInt(cursor.getColumnIndex("isSpecial")) > 0);
        news.setSpecialName(cursor.getString(cursor.getColumnIndex("specialName")));
        news.setHasVideo(cursor.getInt(cursor.getColumnIndex("hasVideo")) > 0);
        news.setVideoUrl(cursor.getString(cursor.getColumnIndex("videoUrl")));
        news.setVideoPhotoUrl(cursor.getString(cursor.getColumnIndex("videoPhotoUrl")));
        news.setAuthor(cursor.getString(cursor.getColumnIndex("author")));
        String photoUrl = cursor.getString(cursor.getColumnIndex("photoUrl"));
        if(!TextUtils.isEmpty(photoUrl)){
        	String[] urls = photoUrl.split(";;;");
        	List<String> urlList = new ArrayList<String>();
        	for(int i = 0; urls!=null && i<urls.length; i++){
        		if(!TextUtils.isEmpty(urls[i]))
        			urlList.add(urls[i]);
        	}
            news.setPhotoUrls(urlList);
        }
        String thumbnailUrl = cursor.getString(cursor.getColumnIndex("thumbnailUrl"));
        if(!TextUtils.isEmpty(thumbnailUrl)){
        	String[] urls = thumbnailUrl.split(";;;");
        	List<String> urlList = new ArrayList<String>();
        	for(int i = 0; urls!=null && i<urls.length; i++){
        		if(!TextUtils.isEmpty(urls[i]))
        			urlList.add(urls[i]);
        	}
            news.setThumbnailUrls(urlList);
        }
        news.setVideoPhotoFilePath(cursor.getString(cursor.getColumnIndex("videoPhotoFilePath")));
        news.setPhotoFilePath(cursor.getString(cursor.getColumnIndex("photoFilePath")));
        news.setThumbnailFilePath(cursor.getString(cursor.getColumnIndex("thumbnailFilePath")));
        news.setFocus(cursor.getInt(cursor.getColumnIndex("isFocus")) > 0);
        news.setUserCache(cursor.getInt(cursor.getColumnIndex("isUserCache")) > 0);
        return news;
    }
}
