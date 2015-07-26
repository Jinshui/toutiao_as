package com.yingshi.toutiao.view.ptr;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.R;

public class CustomizedPTRListView extends PullToRefreshListView {

	private LinearLayout mListViewHeader;

	public CustomizedPTRListView(Context context) {
		super(context);
	}

	public CustomizedPTRListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomizedPTRListView(Context context, Mode mode) {
		super(context, mode);
	}

	public CustomizedPTRListView(Context context, Mode mode, AnimationStyle style) {
		super(context, mode, style);
	}


	@SuppressWarnings("deprecation")
	protected void handleStyledAttributes(TypedArray a) {
		super.handleStyledAttributes(a);

		mListViewExtrasEnabled = a.getBoolean(R.styleable.PullToRefresh_ptrListViewExtrasEnabled, true);

		if (mListViewExtrasEnabled) {
			final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);

			// Create Loading Views ready for use later
			mListViewHeader = new LinearLayout(getContext());
			mListViewHeader.setOrientation(VERTICAL);
			mHeaderLoadingView = createLoadingLayout(getContext(), Mode.PULL_FROM_START, a);
			mHeaderLoadingView.setVisibility(View.GONE);
			mListViewHeader.addView(mHeaderLoadingView, LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			getRefreshableView().setDividerHeight(0);
			getRefreshableView().addHeaderView(mListViewHeader, null, false);

			mLvFooterLoadingFrame = new FrameLayout(getContext());
			mFooterLoadingView = createLoadingLayout(getContext(), Mode.PULL_FROM_END, a);
			mFooterLoadingView.setVisibility(View.GONE);
			mLvFooterLoadingFrame.addView(mFooterLoadingView, lp);

			/**
			 * If the value for Scrolling While Refreshing hasn't been
			 * explicitly set via XML, enable Scrolling While Refreshing.
			 */
			if (!a.hasValue(R.styleable.PullToRefresh_ptrScrollingWhileRefreshingEnabled)) {
				setScrollingWhileRefreshingEnabled(true);
			}
		}
	}
	
	public void addViewToListHeader(View view, int height){
		mListViewHeader.addView(view, LinearLayout.LayoutParams.FILL_PARENT, height);
	}
}
