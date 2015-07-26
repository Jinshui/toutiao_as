package com.yingshi.toutiao.storage.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.yingshi.toutiao.model.BaseModel;

public abstract class BaseAdapter<T extends BaseModel> {
    static final String tag = "TT-BaseAdapter";
    private SQLiteDatabase mDb;
    private String mTableName;
    private String[] mColumnNames;
	public BaseAdapter(String tableName, String[] columnNames, SQLiteDatabase database){
		mDb = database;
		mColumnNames = columnNames;
		mTableName = tableName;
	}
	
	public abstract ContentValues toContentValues(T object); 
	public abstract T toObject(Cursor cursor);
	
	public String getTableName(){
		return mTableName;
	};
	
	public String[] getColumnNames(){
		return mColumnNames;
	};
	
	public SQLiteDatabase getDatabase(){
		return mDb;
	}
	
	public long insert(T object) {
		Log.d(tag, "insert: table=" + mTableName +", id=" + object.getId());
        return mDb.insert(mTableName, null, toContentValues(object));
	}
	
	public int delete(long rowId){
		Log.d(tag, "delete: table=" + mTableName +", _id=" + rowId);
		return mDb.delete(mTableName, "_id=" + rowId, null);
	}
	
	public int delete(String query){
		Log.d(tag, "delete: table=" + mTableName +", query={" + query + "}");
		return mDb.delete(mTableName, query, null);
	}
	
    public int update(T object) {
		Log.d(tag, "update: table=" + mTableName +", _id=" + object.get_id());
        return getDatabase().update(mTableName, toContentValues(object), "_id=" + object.get_id(), null);
    }
	
    public List<T> fetchAll(String orderByColumn) {
		Log.d(tag, "fetchAll: table=" + mTableName +", orderByColumn=" + orderByColumn);
        return toObjectList(mDb.query(mTableName, mColumnNames, null, null, null, null, orderByColumn, null));
    }
	
    public List<T> fetchAll(String query, String orderByColumn) {
		Log.d(tag, "fetchAll: table=" + mTableName +", query="+ query +", orderByColumn=" + orderByColumn);
        return toObjectList(mDb.query(mTableName, mColumnNames, query, null, null, null, orderByColumn, null));
    }
    
    public List<T> fetchPage(int pageSize, int pageIndex, String query, String orderBy){
		Log.d(tag, "fetchPage: table=" + mTableName +", pageSize="+ pageSize +", pageIndex=" + pageIndex +", query="+ query +", orderBy=" + orderBy);
    	String columns = "";
    	for(int i=0; i<mColumnNames.length; i++){
    		columns = columns + mColumnNames[i];
    		if(i != mColumnNames.length - 1)
    			columns = columns + ", ";
    	}
    	String sql = "select " + columns + " from " + mTableName;
    	if(query != null){
    		sql = sql + " where " + query;
    	}
    	if(orderBy != null){
    		sql = sql + " order by " + orderBy;
    	}
    	sql = sql + " limit " + pageSize + " offset " + (pageIndex - 1)*pageSize;
    	Log.d(tag, "fetchPage : " + sql);
    	return toObjectList(getDatabase().rawQuery(sql, null));
    }
    
	public T fetchOneById(long rowId){
		Log.d(tag, "fetchOneById: table=" + mTableName +", _id=" + rowId);
		Cursor cursor = mDb.query(true, mTableName, mColumnNames, "_id="+rowId, null, null, null, null, null);
        if (cursor != null) {
            try{
            	if(cursor.moveToFirst()){
            		T object = toObject(cursor);
            		Log.d(tag, "found object: _id=" + rowId);
            		return object;
            	}
            } finally {
                cursor.close();
            }
        }
		Log.d(tag, "found no object: _id=" + rowId);
        return null;
	}
	
	public int count(){
		return count(null);
	}
	
	public int count(String query){
    	String sql = "select count(*) as count from " + mTableName;
    	if(query != null){
    		sql = sql + " where " + query;
    	}
    	Cursor cursor = getDatabase().rawQuery(sql, null);
    	try{
	    	if(cursor != null && cursor.moveToFirst()){
	    		return cursor.getInt(cursor.getColumnIndex("count"));
	    	}
    	}finally{
    		cursor.close();
    	}
    	return 0;
    }
    
    protected List<T> toObjectList(Cursor cursor) {
        List<T> objList = new ArrayList<T>();
        try{
	        if (cursor.moveToFirst()) {
	            do {
	            	objList.add(toObject(cursor));
	            } while (cursor.moveToNext());
	        }
        } finally {
            cursor.close();
        }
        return objList;
    }
}
