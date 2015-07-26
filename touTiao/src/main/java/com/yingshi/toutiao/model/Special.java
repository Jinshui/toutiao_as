package com.yingshi.toutiao.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Special extends BaseModel{
	private String summary;
	private List<String> photoUrls; 
	
	public Special() {
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
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

    public static Special fromJSON(JSONObject json) throws JSONException{
        if(json == null)
            throw new IllegalArgumentException("JSONObject is null");
        Special special = new Special();
        if(json.has("Id"))
        	special.setId(json.getString("Id"));
        if(json.has("Dy"))
        	special.setSummary(json.getString("Dy"));
        if(json.has("BigUrl")){
        	Object obj = json.get("BigUrl");
        	if(obj instanceof JSONArray){
        		JSONArray array = (JSONArray)obj;
        		for(int i=0; i<array.length(); i++){
        			JSONObject jo = array.getJSONObject(i);
        			if(jo.has("image")){
        				special.getPhotoUrls().add(jo.getString("image"));
        			}
        		}
        	}
        }
        return special;
    }
}
