package com.yingshi.toutiao.view;

import java.util.List;

import com.yingshi.toutiao.R;
import com.yingshi.toutiao.actions.AbstractAction.ActionError;
import com.yingshi.toutiao.actions.AbstractAction.UICallBack;
import com.yingshi.toutiao.actions.PaginationAction;
import com.yingshi.toutiao.model.Pagination;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoadMoreView<T> extends LinearLayout{
    private LinearLayout mProgressView;
    private final TextView mStatusText;
    private OnMoreResultReturnListener<T> mListener;
    public static interface OnMoreResultReturnListener<T>{
        public void onMoreResultReturn(List<T> result);
    }
    private PaginationAction<T> mAction;
    public enum LoadStatus{
        IDLE,
        LOADING,
        NOMORE,
        FAILED;
    }
    private LoadStatus mStatus = LoadStatus.IDLE;
    public LoadMoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_load_comments, this, true);
        mProgressView = (LinearLayout)findViewById(R.id.progressView);
        mStatusText = (TextView)findViewById(R.id.load_status_view);
        setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                loadMore();
            }
        });
    }

    private void loadMore(){
        if(mAction == null || mStatus != LoadStatus.IDLE){
            return;
        }

        mStatusText.setVisibility(INVISIBLE);
        mProgressView.setVisibility(VISIBLE);
        mStatus = LoadStatus.LOADING;
        mAction.execute(new UICallBack<Pagination<T>>(){
            public void onSuccess(Pagination<T> result) {
                if(mListener!=null){
                    mListener.onMoreResultReturn(result.getItems());
                }
                onLoadMoreSuccess();
            }

            public void onFailure(ActionError error) {
                onLoadMoreFailure();
            }
        });
    }

    public void setOnMoreResultReturnListener(OnMoreResultReturnListener<T> listener){
        mListener = listener;
    }

    private void onLoadMoreSuccess(){
        if(mAction.hasMore()){
            mProgressView.setVisibility(INVISIBLE);
            mStatusText.setVisibility(VISIBLE);
            mStatusText.setText(R.string.press_to_load);
            //Always create a new Asyn task because an AsynTask can be executed ONLY ONCE
            mAction = mAction.getNextPageAction();
            mStatus = LoadStatus.IDLE;
        }else{
            mStatusText.setText(R.string.no_more_load);
            setVisibility(GONE);
            Toast.makeText(getContext(), R.string.no_more_load, Toast.LENGTH_SHORT).show();
            mStatus = LoadStatus.NOMORE;
        }
    }

    private void onLoadMoreFailure(){
        mStatusText.setVisibility(VISIBLE);
        mStatusText.setText(R.string.load_failed);
        mProgressView.setVisibility(INVISIBLE);
        mAction = mAction.getNextPageAction();
        //Always create a new Asyn task because an AsynTask can be executed ONLY ONCE
        mStatus = LoadStatus.IDLE;
    }

    public void setPaginationAction(PaginationAction<T> action){
        mAction = action;
    }
}
