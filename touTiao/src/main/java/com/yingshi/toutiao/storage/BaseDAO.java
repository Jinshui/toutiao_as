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

import com.yingshi.toutiao.model.BaseModel;
import com.yingshi.toutiao.storage.adapters.BaseAdapter;

public class BaseDAO<T extends BaseModel> {
    static final String tag = "TT-NewsDAO";
    BaseAdapter<T> mDbAdapter;

    public BaseDAO(BaseAdapter<T> adapter) {
        mDbAdapter = adapter;
    }
    
    public BaseAdapter<T> getDbAdapter(){
    	return mDbAdapter;
    }

    public int count(){
    	return mDbAdapter.count();
    }

    public int count(String query){
    	return mDbAdapter.count(query);
    }
    
    public T save(T object){
    	object.set_id(mDbAdapter.insert(object));
        return object;
    }

    public void save(List<T> objects){
    	if(objects == null || objects.isEmpty())
    		return;
    	mDbAdapter.getDatabase().beginTransaction();
    	try {
			for(T object : objects)
				object.set_id(mDbAdapter.insert(object));
			mDbAdapter.getDatabase().setTransactionSuccessful();
    	} finally {
    		mDbAdapter.getDatabase().endTransaction();
    	}
    }

    public void delete(long _id) {
    	mDbAdapter.delete(_id);
    }
    
    public void delete(){
    	mDbAdapter.delete(null);
    }
    
    public void delete(String query){
    	mDbAdapter.delete(query);
    }
    
    public void update(T object){
        mDbAdapter.update(object);;
    }

    public T get(long _id){
    	return mDbAdapter.fetchOneById(_id);
    }

    public List<T> getAll(String orderBy) {
        return mDbAdapter.fetchAll(orderBy);
    }

    public List<T> search(String query, String orderBy) {
        return mDbAdapter.fetchAll(orderBy);
    }
}
