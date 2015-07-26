package com.yingshi.toutiao.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.yingshi.toutiao.util.Utils;


public class News extends BaseModel implements Parcelable{

    public static final Parcelable.Creator<News> CREATOR
            = new Parcelable.Creator<News>() {
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        public News[] newArray(int size) {
            return new News[size];
        }
    };
	
	private String name;
	private String summary;
	private long time;
	private String content;
	private String category;
	private String contact;
	private int likes;
	private boolean isSpecial;
	private String specialName;
	private boolean hasVideo;
	private String videoUrl;
	private String videoPhotoUrl;
	private String author;
	private List<String> photoUrls;
	private List<String> thumbnailUrls;
	
	//used by client only
	private String videoPhotoFilePath;
	private String photoFilePath;
	private String thumbnailFilePath;
	private boolean isFocus;
	private boolean isUserCache;

	public News(){}
    
    private News(Parcel in) {
    	super(in);
    	name = in.readString();
    	summary = in.readString();
    	time = in.readLong();
    	content = in.readString();
    	category = in.readString();
    	contact = in.readString();
    	likes = in.readInt();
    	isSpecial = (in.readInt() == 1);
    	specialName = in.readString();
    	hasVideo = (in.readInt() == 1);
    	videoUrl = in.readString();
    	videoPhotoUrl = in.readString();
    	author = in.readString();
    	photoUrls = in.createStringArrayList();
    	thumbnailUrls = in.createStringArrayList();
    	videoPhotoFilePath = in.readString();
    	photoFilePath = in.readString();
    	thumbnailFilePath = in.readString();
    	isFocus = (in.readInt() == 1);
    	isUserCache = (in.readInt() == 1);
    }
    
	
    public void writeToParcel(Parcel out, int flags) {
    	super.writeToParcel(out, flags);
        out.writeString(name);
        out.writeString(summary);
        out.writeLong(time);
        out.writeString(content);
        out.writeString(category);
        out.writeString(contact);
        out.writeInt(likes);
        out.writeInt(isSpecial ? 1 : 0);
        out.writeString(specialName);
        out.writeInt(hasVideo ? 1 : 0);
        out.writeString(videoUrl);
        out.writeString(videoPhotoUrl);
        out.writeString(author);
        out.writeStringList(photoUrls);
        out.writeStringList(thumbnailUrls);
        out.writeString(videoPhotoFilePath);
        out.writeString(photoFilePath);
        out.writeString(thumbnailFilePath);
        out.writeInt(isFocus ? 1 : 0);
        out.writeInt(isUserCache ? 1 : 0);
    }
	
	public int describeContents() {
		return 0;
	}
	
    public static News fromJSON(JSONObject json) throws JSONException{
        if(json == null)
            throw new IllegalArgumentException("JSONObject is null");
        News news = new News();
        if(json.has("Id"))
        	news.setId(json.getString("Id"));
        if(json.has("Dy"))
        	news.setSummary(json.getString("Dy"));
        if(json.has("NewsName"))
        	news.setName(json.getString("NewsName"));
        if(json.has("Time")){//"2014-09-16 00:16:09.0",
        	Date date = Utils.parseDate("yyyy-MM-dd HH:mm:ss.SSS", json.getString("Time"));
        	news.setTime(date == null ? 0 : date.getTime());
        }
        if(json.has("Content"))
        	news.setContent(json.getString("Content"));
        if(json.has("TypeName"))
        	news.setCategory(json.getString("TypeName"));
        if(json.has("LinkContent"))
        	news.setContact(json.getString("LinkContent"));
        if(json.has("Goods"))
        	news.setLikes(Integer.valueOf(json.getString("Goods")));
        if(json.has("IsZt"))
        	news.setSpecial(!"0".equals(json.getString("IsZt")));
        if(json.has("ZtName"))
        	news.setSpecialName(json.getString("ZtName"));
        if(json.has("IsSp"))
        	news.setHasVideo(!"0".equals(json.getString("IsSp")));
        if(json.has("Author"))
        	news.setAuthor(json.getString("Author"));
        if(json.has("BigUrl")){
        	Object obj = json.get("BigUrl");
        	if(obj instanceof JSONArray){
        		JSONArray array = (JSONArray)obj;
        		for(int i=0; i<array.length(); i++){
        			JSONObject jo = array.getJSONObject(i);
        			if(jo.has("image")){
        	        	news.getPhotoUrls().add(jo.getString("image"));
        			}
        		}
        	}
        }
        if(json.has("SmallUrl")){
        	Object obj = json.get("SmallUrl");
        	if(obj instanceof JSONArray){
        		JSONArray array = (JSONArray)obj;
        		for(int i=0; i<array.length(); i++){
                	news.getThumbnailUrls().add(array.getString(i));
        		}
        	}
        }
        if(json.has("VidelUrl"))
        	news.setVideoUrl(json.getString("VidelUrl"));
        if(json.has("VidelPicUrl"))
        	news.setVideoPhotoUrl(json.getString("VidelPicUrl"));
        return news;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public boolean isSpecial() {
		return isSpecial;
	}

	public void setSpecial(boolean isSpecial) {
		this.isSpecial = isSpecial;
	}

	public String getSpecialName() {
		return specialName;
	}

	public void setSpecialName(String specialName) {
		this.specialName = specialName;
	}

	public boolean isHasVideo() {
		return hasVideo;
	}

	public void setHasVideo(boolean hasVideo) {
		this.hasVideo = hasVideo;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public String getVideoPhotoUrl() {
		return videoPhotoUrl;
	}

	public void setVideoPhotoUrl(String videoPhotoUrl) {
		this.videoPhotoUrl = videoPhotoUrl;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public List<String> getPhotoUrls() {
		if(photoUrls == null){
			photoUrls = new ArrayList<String>();
		}
		return photoUrls;
	}

	public void setPhotoUrls(List<String> photoUrls) {
		this.photoUrls = photoUrls;
	}

	public List<String> getThumbnailUrls() {
		if(thumbnailUrls == null){
			thumbnailUrls = new ArrayList<String>();
		}
		return thumbnailUrls;
	}

	public void setThumbnailUrls(List<String> thumbnailUrls) {
		this.thumbnailUrls = thumbnailUrls;
	}

	public String getVideoPhotoFilePath() {
		return videoPhotoFilePath;
	}

	public void setVideoPhotoFilePath(String videoPhotoFilePath) {
		this.videoPhotoFilePath = videoPhotoFilePath;
	}

	public String getPhotoFilePath() {
		return photoFilePath;
	}

	public void setPhotoFilePath(String photoFilePath) {
		this.photoFilePath = photoFilePath;
	}

	public String getThumbnailFilePath() {
		return thumbnailFilePath;
	}

	public void setThumbnailFilePath(String thumbnailFilePath) {
		this.thumbnailFilePath = thumbnailFilePath;
	}

	public boolean isFocus() {
		return isFocus;
	}

	public void setFocus(boolean isFocus) {
		this.isFocus = isFocus;
	}

	public boolean isUserCache() {
		return isUserCache;
	}

	public void setUserCache(boolean isUserCache) {
		this.isUserCache = isUserCache;
	}
}
