package com.yingshi.toutiao;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.yingshi.toutiao.actions.AbstractAction.ActionError;
import com.yingshi.toutiao.actions.AbstractAction.BackgroundCallBack;
import com.yingshi.toutiao.actions.AbstractAction.UICallBack;
import com.yingshi.toutiao.actions.GetCategoryAction;
import com.yingshi.toutiao.actions.ParallelTask;
import com.yingshi.toutiao.model.Category;
import com.yingshi.toutiao.model.Pagination;
import com.yingshi.toutiao.storage.CategoryDAO;
import com.yingshi.toutiao.view.HeaderView;

public class MainActivity extends SlidingFragmentActivity
{
	public static final String INTENT_EXTRA_SHOW_USER_CENTER = "show_user_center";
	private List<RelativeLayout> mTabs = new ArrayList<RelativeLayout>();
	private TabHost mTabHost;
	private HorizontalScrollView mTabScrollView ;
	private ViewPager mViewPager;
	private View mLoadingView;
	private MainUserCenterFragment mUserCenterFragment;
	private CategoryDAO mcategoryDAO;
	
	private TabHost.TabContentFactory mEmptyTabContentFactory = new TabHost.TabContentFactory(){
		public View createTabContent(String tag) {
			return new TextView(MainActivity.this);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_news);
		// 初始化ContentView
		initContentView();
		// 初始化SlideMenu
		initUserCenterMenu();
		mcategoryDAO = ((TouTiaoApp)getApplication()).getCategoryDAO();
		if(getIntent().getBooleanExtra(INTENT_EXTRA_SHOW_USER_CENTER, false))
			getSlidingMenu().showMenu();
	}

	private void initContentView()
	{
		final HeaderView headerView = (HeaderView)findViewById(R.id.id_main_header);
	    mTabScrollView = (HorizontalScrollView)findViewById(R.id.tabs_scrollView);
		mTabHost = (TabHost) findViewById(R.id.tabhost);
		mTabHost.setup();
		mLoadingView = findViewById(R.id.id_category_loading);
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		//Header view
		headerView.setLeftImage(R.drawable.settings, new OnClickListener(){
			public void onClick(View v) {
				getSlidingMenu().showMenu();
			}});
		headerView.setRightImage(R.drawable.sousuo_sousuo, new OnClickListener(){
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, SearchActivity.class));
			}});
		headerView.setTitle(R.string.title_toutiao);
		loadCategoryFromServer();
	}
	
	private void loadCategoryFromServer(){
		mLoadingView.setVisibility(View.VISIBLE);
		mViewPager.setVisibility(View.GONE);
		GetCategoryAction getCategoryAction = new GetCategoryAction(this, 1, 100);
		getCategoryAction.execute(
			new BackgroundCallBack<Pagination<Category>>(){
				public void onSuccess(Pagination<Category> result) {
					mcategoryDAO.delete();
					mcategoryDAO.save(result.getItems());
				}
			},
			new UICallBack<Pagination<Category>>(){
				public void onSuccess(Pagination<Category> result) {
					updateUI(result.getItems());
				}
				public void onFailure(ActionError error) {
					loadCategoryFromDB();
				}
			}
		);
	}
	
	private void loadCategoryFromDB(){
		new ParallelTask<List<Category>>() {
			protected List<Category> doInBackground(Void... params) {
				return mcategoryDAO.getAll(null);
			}
			public void onPostExecute(List<Category> newsList){
				updateUI(newsList);
			}
		}.execute();
	}
	
	private void updateUI(final List<Category> categories){
		mLoadingView.setVisibility(View.GONE);
		mViewPager.setVisibility(View.VISIBLE);
		if(categories == null || categories.isEmpty()){
			Toast.makeText(MainActivity.this, R.string.load_failed, Toast.LENGTH_SHORT).show();
			return;
		}
		
		for (Category category : categories) {
			RelativeLayout tabIndicator = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.view_news_tab_widget, null);
			TextView tvTab = (TextView) tabIndicator.findViewById(R.id.tv_title);
			tvTab.setText(category.getName());
			mTabs.add(tabIndicator);
			mTabHost.addTab(mTabHost.newTabSpec(category.getName()).setIndicator(tabIndicator).setContent(mEmptyTabContentFactory));
		}
		//点击tabhost中的tab时，要切换下面的viewPager
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				for (int i = 0; i < categories.size(); i++) {
					if (categories.get(i).getName().equals(tabId)) {
						mViewPager.setCurrentItem(i);
					}
				}
			}
		});
		
		//Pager
		mViewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager(), categories));
		//切换Pager时,要将当前对应的Tab滚动到可见位置
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			public void onPageSelected(int position) {
				mTabHost.setCurrentTab(position);
				int moveLeft = (int) mTabs.get(position).getLeft() - (int) mTabs.get(1).getLeft();
				mTabScrollView.smoothScrollTo(moveLeft, 0);
				highlightTab(position);
			}
		});
		highlightTab(0);
	}
	
	private void highlightTab(int position){
		for(int i=0; i<mTabs.size(); i++){
			TextView tvTab = (TextView) mTabs.get(i).findViewById(R.id.tv_title);
			if(i == position){
				tvTab.setTextColor(Color.RED);
			}else{
				tvTab.setTextColor(Color.BLACK);
			}
		}
	}

	private void initUserCenterMenu()
	{
		SlidingMenu menu = getSlidingMenu();
		mUserCenterFragment = new MainUserCenterFragment(menu);
		setBehindContentView(R.layout.view_user_center_frame);
		getSupportFragmentManager().beginTransaction().replace(R.id.id_left_menu_frame, mUserCenterFragment).commit();
		menu.setMode(SlidingMenu.LEFT);
		// 设置触摸屏幕的模式
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		menu.setShadowWidth(0);
		menu.setShadowDrawable(R.drawable.shadow);
		// 设置滑动菜单视图的宽度
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		// 设置渐入渐出效果的值
		menu.setFadeDegree(0.35f);
		// menu.setBehindScrollScale(1.0f);
		menu.setSecondaryShadowDrawable(R.drawable.shadow);
	}

	/**
	 * A simple pager adapter that represents 5 {@link SlidePageFragment}
	 * objects, in sequence.
	 */
	private class MainPagerAdapter extends FragmentStatePagerAdapter {
		public static final String tag = "TT-MainPagerAdapter";
		List<Category> mCategories;
		public MainPagerAdapter(FragmentManager fm, List<Category> categories) {
			super(fm);
			mCategories = categories;
		}

		@Override
		public Fragment getItem(int position) {
			Log.d(tag, ": getItem: " + position);
			MainFragment fragment = new MainFragment();
			Bundle bundle = new Bundle();
			bundle.putString("categoryName", mCategories.get(position).getName());
			fragment.setArguments(bundle);
			return fragment;
		}

		@Override
		public int getCount() {
			return mCategories.size();
		}
	}
}
