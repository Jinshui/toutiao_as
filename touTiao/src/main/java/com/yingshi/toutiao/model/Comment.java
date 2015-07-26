package com.yingshi.toutiao.model;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.yingshi.toutiao.util.Utils;


public class Comment extends BaseModel {
	private String id;
	private String name;
	private String content;
	private long time;
	private String photoUrl;

    public static Comment fromJSON(JSONObject json) throws JSONException{
        if(json == null)
            throw new IllegalArgumentException("JSONObject is null");
        Comment comment = new Comment();
        if(json.has("Id"))
        	comment.setId(json.getString("Id"));
        if(json.has("Name"))
        	comment.setName(Utils.getDecodedValue(json, "Name"));
        if(json.has("Time"))
        {
        	Date date = Utils.parseDate("yyyy-MM-dd HH:mm", json.getString("Time"));
        	comment.setTime(date == null ? 0 : date.getTime());
        }
        if(json.has("Content"))
        	comment.setContent(Utils.getDecodedValue(json, "Content"));
        if(json.has("PicUrl"))
        	comment.setPhotoUrl(json.getString("PicUrl"));
        return comment;
    }
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long createTime) {
		this.time = createTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}
}
