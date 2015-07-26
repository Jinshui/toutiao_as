package com.yingshi.toutiao.view.ptr;

import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

public class PTRListAdapter<T> extends ArrayAdapter<T>{
	private List<T> mObjects;
	public PTRListAdapter(Context context, int resource, List<T> objects) {
		super(context, resource, objects);
		mObjects = objects;
	}
	
	/**
	 * Override this because this is not available in SDK lower than 11;
	 */
    public void addMore(Collection<? extends T> collection) {
        mObjects.addAll(collection);
        notifyDataSetChanged();
    }
}
