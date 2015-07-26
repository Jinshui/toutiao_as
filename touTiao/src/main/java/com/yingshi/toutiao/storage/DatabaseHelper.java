package com.yingshi.toutiao.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	static final String tag = "TT-DatabaseHelper";

	private static final String CREATE_DATABASE_SQL = "create table %s("
			+ "_id integer primary key autoincrement,"
			+ "id text not null," // id returned from server
			+ "name text not null,"
			+ "summary text null,"
			+ "content text null,"
			+ "time integer null,"
			+ "category text null,"
			+ "contact text null,"
			+ "likes integer null,"
			+ "isSpecial integer null,"
			+ "specialName text null,"
			+ "hasVideo integer null,"
			+ "videoUrl text null,"
			+ "videoPhotoUrl text null,"
			+ "author text null,"
			+ "photoUrl text null,"
			+ "thumbnailUrl text null,"
			+ "videoPhotoFilePath text null,"
			+ "photoFilePath text null,"
			+ "thumbnailFilePath text null,"
			+ "isFocus integer null,"
			+ "isUserCache integer null"
			+ ");";
	private static final String CREATE_DATABASE_NEWS = String.format(CREATE_DATABASE_SQL, "news");
	private static final String CREATE_DATABASE_NEWS_UNIQUE_INDEX = "CREATE UNIQUE INDEX news_u1 ON news(id)";
	private static final String CREATE_DATABASE_HEADNEWS = String.format(CREATE_DATABASE_SQL, "headnews");
	private static final String CREATE_DATABASE_HEADNEWS_UNIQUE_INDEX = "CREATE UNIQUE INDEX headnews_u1 ON headnews(id)";
	private static final String CREATE_DATABASE_FAVORITES = String.format(CREATE_DATABASE_SQL, "favorites");
	private static final String CREATE_DATABASE_FAVORITES_UNIQUE_INDEX = "CREATE UNIQUE INDEX favorites_u1 ON favorites(id)";

	private static final String CREATE_DATABASE_CATEGORY = "create table categories("
			+ "_id integer primary key autoincrement,"
			+ "id text not null," // id returned from server
			+ "name text not null"
			+ ");";

	private static final String CREATE_DATABASE_ACCOUNTS = "create table accounts("
			+ "_id integer primary key autoincrement,"
			+ "provider text null,"
			+ "userName text null,"
			+ "photoUrl text null,"
			+ "openId text null,"
			+ "token text null,"
			+ "lastLogin integer null,"
			+ "expiresIn integer null"
			+ ");";

	public static final String DB_NAME = "news.db";
	public static final int DATABASE_VERSION = 1;

	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_DATABASE_NEWS);
		db.execSQL(CREATE_DATABASE_NEWS_UNIQUE_INDEX);
		db.execSQL(CREATE_DATABASE_HEADNEWS);
		db.execSQL(CREATE_DATABASE_HEADNEWS_UNIQUE_INDEX);
		db.execSQL(CREATE_DATABASE_FAVORITES);
		db.execSQL(CREATE_DATABASE_FAVORITES_UNIQUE_INDEX);
		db.execSQL(CREATE_DATABASE_CATEGORY);
		db.execSQL(CREATE_DATABASE_ACCOUNTS);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(tag, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", this will destroy all old data");
	}
}
