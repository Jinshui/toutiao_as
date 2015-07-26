package com.yingshi.toutiao.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Category extends BaseModel{
	private String id;
	private String name;

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

	public Category() {
	}
	
    public static Category fromJSON(JSONObject json) throws JSONException{
        if(json == null)
            throw new IllegalArgumentException("JSONObject is null");
        Category category = new Category();
        if(json.has("Id"))
        	category.setId(json.getString("Id"));
        if(json.has("Name"))
        	category.setName(json.getString("Name"));
        return category;
    }
}
