package com.yingshi.toutiao.actions;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

@SuppressLint("NewApi")
public abstract class ParallelTask<Result> extends AsyncTask<Void, Void, Result>{
	private static final String tag = "TT-ParallelTask";
    public void execute(){
    	try{
	        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
	            super.execute();
	        } else {
	        	super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	        }
    	}catch(Exception e){
    		Log.e(tag, "Failed to execute task", e);
    	}
    }
}
