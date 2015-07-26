package com.yingshi.toutiao.view;

import com.yingshi.toutiao.R;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

public class PhotoPager extends FrameLayout {

	private ViewPager mViewPager;
	private TextView mPhotoNumView;
	private TextView mPhotoDescriptionView;
	
	public PhotoPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		mViewPager = (ViewPager)findViewById(R.id.id_photos_pager);
		mPhotoNumView = (TextView)findViewById(R.id.id_photo_number);
		mPhotoDescriptionView = (TextView)findViewById(R.id.id_photo_description);
	}

	
	public TextView getPhotoNumView(){
		return (TextView)findViewById(R.id.id_photo_number);
	}

	
	public TextView getPhotoDescriptionView(){
		return (TextView)findViewById(R.id.id_photo_description);
	}
	
	public ViewPager getPhotoViewPager(){
		return (ViewPager)findViewById(R.id.id_photos_pager);
	}
	
}
