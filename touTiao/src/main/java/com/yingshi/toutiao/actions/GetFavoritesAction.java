package com.yingshi.toutiao.actions;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.yingshi.toutiao.TouTiaoApp;
import com.yingshi.toutiao.model.News;
import com.yingshi.toutiao.model.Pagination;
import com.yingshi.toutiao.storage.FavoritesDAO;

public class GetFavoritesAction extends PaginationAction<News> {
    private static final String tag = "TT-GetFavoritesAction";
    //request keys
    private static final String NAME = "name";
    //local variables
    private String mCategory;
    private FavoritesDAO mFavoritesDAO;

    public class DBBackgroundProcessor implements IBackgroundProcessor<Pagination<News>>{
		public com.yingshi.toutiao.actions.AbstractAction.ActionResult<Pagination<News>> doInBackground() {
//			Log.d(tag, "Loading favorites from database: total=" + getTotalCount() +", page="+getPageIndex()+", size="+getPageSize());
			List<News> newsList = mFavoritesDAO.findFavorites(getPageSize(), getPageIndex());
			Pagination<News> newsPagination = new Pagination<News>();
			newsPagination.setItems(newsList);
			newsPagination.setCurrentPage(getPageIndex());
			newsPagination.setPageSize(getPageSize());
			newsPagination.setTotalCounts(mFavoritesDAO.count());
			setTotalCount(newsPagination.getTotalCounts());
			return new ActionResult<Pagination<News>>(newsPagination);
		}
    }
    
    public GetFavoritesAction(Context context, int pageIndex, int pageSize){
        super(context, pageIndex, pageSize);
        mFavoritesDAO =((TouTiaoApp)context.getApplicationContext()).getFavoritesDAO();
        mServiceId = SERVICE_ID_NEWS;
        setBackgroundProcessor(new DBBackgroundProcessor());
    }

    @Override
    public void addRequestParameters(JSONObject parameters) throws JSONException {
        super.addRequestParameters(parameters);
        parameters.put(NAME, mCategory);
    }

    public GetFavoritesAction cloneCurrentPageAction(){
        GetFavoritesAction action = new GetFavoritesAction(
                            mAppContext,
                            getPageIndex(),
                            getPageSize()
                        );
        return action;
    }

    public News convertJsonToResult(JSONObject item) throws JSONException{
        return News.fromJSON(item);
    }
}
