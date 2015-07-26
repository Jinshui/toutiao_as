package com.yingshi.toutiao.view;

import com.yingshi.toutiao.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HeaderView extends LinearLayout{
	
	private ImageButton mLeftBtn;
	private ImageButton mRightBtn;
	private TextView mTitle;
	public HeaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.view_header_bar, this, true);
		mLeftBtn = (ImageButton)findViewById(R.id.id_btn_left);
		mRightBtn = (ImageButton)findViewById(R.id.id_btn_right);
		mTitle = (TextView)findViewById(R.id.id_txt_title);
	}
	
	public void setLeftImage(int resid, OnClickListener onClickListener){
		mLeftBtn.setVisibility(View.VISIBLE);
		mLeftBtn.setBackgroundResource(resid);
		mLeftBtn.setOnClickListener(onClickListener);
	}
	
	public void setRightImage(int resid, OnClickListener onClickListener){
		mRightBtn.setVisibility(View.VISIBLE);
		mRightBtn.setBackgroundResource(resid);
		mRightBtn.setOnClickListener(onClickListener);
	}
	
	public void setTitle(int resid){
		mTitle.setVisibility(View.VISIBLE);
		mTitle.setText(resid);
	}
}
