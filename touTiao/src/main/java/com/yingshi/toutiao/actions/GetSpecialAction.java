package com.yingshi.toutiao.actions;

import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.yingshi.toutiao.model.Special;

public class GetSpecialAction extends PaginationAction<Special> {
    private static final String tag = "TT-GetSpecialAction";
    //request keys
    private static final String NAME = "name";
    //local variables
    private String mSpecialName;

    public GetSpecialAction(Context context, String specialName){
		super(context, 1, 100);
		mSpecialName = specialName;
        mServiceId = SERVICE_ID_SPECIAL_HEAD;
    }

    @Override
    public void addRequestParameters(JSONObject parameters) throws JSONException {
        super.addRequestParameters(parameters);
        try{
        	parameters.put(NAME, URLEncoder.encode(mSpecialName, "UTF-8"));
        }catch(Exception e){
        	Log.d(tag, "failed to add parameters", e);
        }
    }
	
	@Override
	public PaginationAction<Special> cloneCurrentPageAction() {
		return new GetSpecialAction(mAppContext, mSpecialName);
	}

	@Override
	public Special convertJsonToResult(JSONObject item) throws JSONException {
		return Special.fromJSON(item);
	}
}
