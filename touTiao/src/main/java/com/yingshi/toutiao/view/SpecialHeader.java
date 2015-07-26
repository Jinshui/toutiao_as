package com.yingshi.toutiao.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yingshi.toutiao.R;

public class SpecialHeader extends LinearLayout {

	public SpecialHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HeaderView getHeaderView(){
		return (HeaderView) findViewById(R.id.id_news_special_header);
	}
	
	public CustomizeImageView getHeaderImageView() {
		return (CustomizeImageView) findViewById(R.id.id_special_header_img);
	}

	public TextView getSummaryView() {
		return (TextView) findViewById(R.id.id_special_summary_text);
	}
}
