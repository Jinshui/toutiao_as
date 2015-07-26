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

import com.yingshi.toutiao.model.Category;

public class CategoryDBAdapter extends BaseAdapter<Category> {

	public static final String DB_TABLE_NEWS = "categories";
    private static String[] CATEGORY_COLUMNS = { "_id", "id", "name"};

    public CategoryDBAdapter(SQLiteDatabase database) {
    	super(DB_TABLE_NEWS, CATEGORY_COLUMNS, database);
    }

    public ContentValues toContentValues(Category category) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("id", category.getId());
        initialValues.put("name", category.getName());
        return initialValues;
    }
    
    
    public Category toObject(Cursor cursor) {
    	Category category = new Category();
    	category.set_id(cursor.getLong(cursor.getColumnIndex("_id")));
    	category.setId(cursor.getString(cursor.getColumnIndex("id")));
    	category.setName(cursor.getString(cursor.getColumnIndex("name")));
        return category;
    }
}
