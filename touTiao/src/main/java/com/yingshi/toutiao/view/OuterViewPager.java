package com.yingshi.toutiao.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class OuterViewPager extends ViewPager {

	public OuterViewPager(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	public OuterViewPager(final Context context) {
		super(context);
	}

	@Override
	protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
		if (v != this && v instanceof ViewPager) {
			return true;
		}
		return super.canScroll(v, checkV, dx, x, y);
	}
}
