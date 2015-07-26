package com.yingshi.toutiao.actions;

import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.yingshi.toutiao.model.News;

public class GetNewsAction extends PaginationAction<News> {
    private static final String tag = "TT-GetNewsAction";
    //request keys
    private static final String NAME = "name";
    //local variables
    private String mCategory;

    public GetNewsAction(Context context, String category, int pageIndex, int pageSize){
        super(context, pageIndex, pageSize);
        mCategory = category;
        mServiceId = SERVICE_ID_NEWS;
    }

    @Override
    public void addRequestParameters(JSONObject parameters) throws JSONException {
        super.addRequestParameters(parameters);
        try{
        	parameters.put(NAME, URLEncoder.encode(mCategory, "UTF-8"));
        }catch(Exception e){
        	Log.d(tag, "failed to add parameters", e);
        }
    }

    public GetNewsAction cloneCurrentPageAction(){
        GetNewsAction action = new GetNewsAction(
                            mAppContext,
                            mCategory,
                            getPageIndex(),
                            getPageSize()
                        );
        return action;
    }

    public News convertJsonToResult(JSONObject item) throws JSONException{
        return News.fromJSON(item);
    }
}
