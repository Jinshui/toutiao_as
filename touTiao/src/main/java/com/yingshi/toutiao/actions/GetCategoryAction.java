package com.yingshi.toutiao.actions;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.yingshi.toutiao.model.Category;

public class GetCategoryAction extends PaginationAction<Category>{
	public GetCategoryAction(Context context, int pageIndex, int pageSize) {
		super(context, pageIndex, pageSize);
		mServiceId = SERVICE_ID_CATEGORY;
	}

	public void addRequestParameters(JSONObject parameters) throws JSONException{
		super.addRequestParameters(parameters);
	}
	
	@Override
	public PaginationAction<Category> cloneCurrentPageAction() {
		return new GetCategoryAction(mAppContext, getPageIndex(), getPageSize());
	}

	@Override
	public Category convertJsonToResult(JSONObject item) throws JSONException {
		return Category.fromJSON(item);
	}
}
