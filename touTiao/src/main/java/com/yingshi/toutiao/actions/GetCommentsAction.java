package com.yingshi.toutiao.actions;

import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.yingshi.toutiao.model.Comment;

public class GetCommentsAction extends PaginationAction<Comment> {
    private static final String tag = "TT-GetCommentsAction";

    //request keys
    private static final String NAME = "name";
    private static final String NEWS_ID = "newsid";
    //local variables
    private String mNewsId;
    private String mCategory;

    public GetCommentsAction(Context context, String newsId, String category, int pageIndex, int pageSize){
        super(context, pageIndex, pageSize);
        mNewsId = newsId;
        mCategory = category;
        mServiceId = SERVICE_ID_COMMENTS;
    }

    @Override
    public void addRequestParameters(JSONObject parameters) throws JSONException {
        super.addRequestParameters(parameters);
        parameters.put(NEWS_ID, mNewsId);
        try{
        	parameters.put(NAME, URLEncoder.encode(mCategory, "UTF-8"));
        }catch(Exception e){
        	Log.d(tag, "failed to add parameters", e);
        }
    }

    public GetCommentsAction cloneCurrentPageAction(){
        GetCommentsAction action = new GetCommentsAction(
                            mAppContext,
                            mNewsId,
                            mCategory,
                            getPageIndex(),
                            getPageSize()
                        );
        return action;
    }

    public Comment convertJsonToResult(JSONObject item) throws JSONException{
        return Comment.fromJSON(item);
    }
}
