package com.yingshi.toutiao.view;

import com.yingshi.toutiao.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class ShareNewsWidget extends LinearLayout{
	public ShareNewsWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.view_news_detail_share, this, true);
	}
}
