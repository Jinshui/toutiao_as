package com.yingshi.toutiao.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yingshi.toutiao.R;
import com.yingshi.toutiao.model.Comment;

public class CommentListRow extends LinearLayout{
    private CustomizeImageView mAuthorImg;
    private TextView mAuthorTxt;
    private TextView mCommentText;
    
    public CommentListRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_comment_list_item, this, true);
        mAuthorImg = (CustomizeImageView)findViewById(R.id.id_comment_author_thumbnail);
        mAuthorTxt = (TextView)findViewById(R.id.id_comment_author);
        mCommentText = (TextView)findViewById(R.id.id_comment_text);
    }

    public void setProduct(final Comment comment){
        if(comment == null)
            return;
        mAuthorImg.loadRoundImageWithDefault(comment.getPhotoUrl(), R.drawable.list_item_thumbnail, 20);
        mAuthorTxt.setText(comment.getName());
        mCommentText.setText(comment.getContent());
    }
}
