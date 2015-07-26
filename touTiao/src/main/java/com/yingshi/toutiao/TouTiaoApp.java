package com.yingshi.toutiao;

import android.app.Application;
import android.util.Log;

import com.yingshi.toutiao.social.AccountInfo;
import com.yingshi.toutiao.storage.CategoryDAO;
import com.yingshi.toutiao.storage.DatabaseHelper;
import com.yingshi.toutiao.storage.FavoritesDAO;
import com.yingshi.toutiao.storage.HeadNewsDAO;
import com.yingshi.toutiao.storage.NewsDAO;

public class TouTiaoApp extends Application {
    private static final String tag = "TT-TouTiaoApp";
    
    private NewsDAO mNewsDao;
    private HeadNewsDAO mHeadNewsDAO;
    private FavoritesDAO mFavoritesDAO;
    private CategoryDAO mCategoryDAO;
    private DatabaseHelper mDatabaseHelper;
    private AccountInfo mUserInfo;
    private String mCachePath;
    
    public void onCreate() {
        Log.i(tag, "影视头条App is initializing");
        super.onCreate();
        mDatabaseHelper = new DatabaseHelper(this);
        mNewsDao = new NewsDAO(mDatabaseHelper.getWritableDatabase());
        mHeadNewsDAO = new HeadNewsDAO(mDatabaseHelper.getWritableDatabase());
        mFavoritesDAO = new FavoritesDAO(mDatabaseHelper.getWritableDatabase());
        mCategoryDAO = new CategoryDAO(mDatabaseHelper.getWritableDatabase());
        mCachePath = getCacheDir().getAbsolutePath() + Constants.CACHE_DIR;
    }
    
    public NewsDAO getNewsDAO() {
        return mNewsDao;
    }

    public HeadNewsDAO getHeadNewsDAO() {
        return mHeadNewsDAO;
    }

    public FavoritesDAO getFavoritesDAO() {
        return mFavoritesDAO;
    }

    public CategoryDAO getCategoryDAO(){
    	return mCategoryDAO;
    }
    
    public void setUserInfo(AccountInfo userInfo){
    	this.mUserInfo = userInfo;
    }
    
    public AccountInfo getUserInfo(){
    	return mUserInfo;
    }
    
    public String getCachePath(){
    	return mCachePath;
    }
}
