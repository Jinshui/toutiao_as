package com.yingshi.toutiao.model;

import android.os.Parcel;
import android.os.Parcelable;

public class BaseModel implements Parcelable{

    public static final Parcelable.Creator<BaseModel> CREATOR
            = new Parcelable.Creator<BaseModel>() {
        public BaseModel createFromParcel(Parcel in) {
            return new BaseModel(in);
        }

        public BaseModel[] newArray(int size) {
            return new BaseModel[size];
        }
    };
    
	//used by client only
	private long _id;
	//server id
	private String id;

	public BaseModel(){}
	
	public BaseModel(Parcel in){
		_id = in.readLong();
		id = in.readString();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(_id);
		dest.writeString(id);
	}

	public long get_id() {
		return _id;
	}

	public void set_id(long _id) {
		this._id = _id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
