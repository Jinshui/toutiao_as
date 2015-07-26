package com.yingshi.toutiao.actions;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.yingshi.toutiao.model.News;

public class GetFocusAction extends AbstractAction<List<News>> {
    private static final String tag = "TT-GetFocusAction";
    //request keys
    private static final String NAME = "name";
    //local variables
    private String mCategory;

    public GetFocusAction(Context context, String category){
        super(context);
        mCategory = category;
        mServiceId = SERVICE_ID_FOCUS;
    }

    @Override
    public void addRequestParameters(JSONObject parameters) throws JSONException {
        try{
        	parameters.put(NAME, URLEncoder.encode(mCategory, "UTF-8"));
        }catch(Exception e){
        	Log.d(tag, "failed to add parameters", e);
        }
    }

	@Override
	protected List<News> createRespObject(JSONObject response) throws JSONException {
		List<News> mNewsList = new ArrayList<News>();
        if(response.has(RESP_LIST)){
            JSONArray items = response.getJSONArray(RESP_LIST);
            for(int i=0; i<items.length(); i++){
                JSONObject item = items.getJSONObject(i);
                News news = News.fromJSON(item);
                news.setFocus(true);
                mNewsList.add(news);
            }
        }
		return mNewsList;
	}
}
