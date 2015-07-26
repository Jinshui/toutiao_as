package com.yingshi.toutiao.actions;

import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.yingshi.toutiao.actions.AbstractAction.ActionError;
import com.yingshi.toutiao.actions.AbstractAction.BackgroundCallBack;
import com.yingshi.toutiao.model.News;
import com.yingshi.toutiao.model.Pagination;

public class SearchAction extends PaginationAction<News> {
	static final String tag = "TT-SearchAction";

    //request keys
    private static final String NAME = "name";
    //local variables
    private String mKeyword;

    public SearchAction(Context context, String keyword, int pageIndex, int pageSize){
        super(context, pageIndex, pageSize);
        mKeyword = keyword;
        mServiceId = SERVICE_ID_SEARCH;
    }
    
    @Override
    public void addRequestParameters(JSONObject parameters) throws JSONException {
        super.addRequestParameters(parameters);
        try {
			parameters.put(NAME, URLEncoder.encode(mKeyword, "UTF-8"));
		} catch (Exception e) {
			Log.e(tag, "Unnable to add parameters ", e);
		}
    }

    public SearchAction cloneCurrentPageAction(){
        SearchAction action = new SearchAction(
                            mAppContext,
                            mKeyword,
                            getPageIndex(),
                            getPageSize()
                        );
        return action;
    }

    public News convertJsonToResult(JSONObject item) throws JSONException{
        return News.fromJSON(item);
    }
}
