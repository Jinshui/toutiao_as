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

import com.yingshi.toutiao.model.Category;
import com.yingshi.toutiao.storage.adapters.CategoryDBAdapter;

public class CategoryDAO extends BaseDAO<Category>{
    static final String tag = "TT-CategoryDAO";
    public CategoryDAO(SQLiteDatabase mDb) {
        super(new CategoryDBAdapter(mDb));
    }
}
