/*
 * Copyright (C) 2011, Motorola Mobility, Inc,
 * All Rights Reserved.
 * Motorola Confidential Restricted.
 *
 * Modification History:
 **********************************************************
 * Date           Author         Comments
 * 12-Apr-2011    Jinshui Tang   Created file
 **********************************************************
 */
package com.yingshi.toutiao.http;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;

public class HttpRequest {
    //how many times we've retried this request
    protected byte mRetryCount = 0;
    //max number of times we'll retry the request, negative number means infinite
    protected byte mMaxRetries = 0;
    protected byte[] mData = null;
    protected long id; // correlate with the response
    private String mUrl;
    private Map<String, Object> httpParams;

    public HttpRequest(String url, byte[] data) {
        this(System.currentTimeMillis(), url, data);
    }

    public HttpRequest(long id, String url, byte[] data) {
        this.id = id;
        this.mData = data;
        this.mUrl = url;
        this.httpParams = new HashMap<String, Object>();
    }

    public boolean shouldRetry() {
        return mMaxRetries < 0 || mRetryCount < mMaxRetries;
    }

    public void upRetryCount() {
        mRetryCount++;
    }

    public HttpEntity getRequestEntity() {
        ByteArrayEntity reqEntity = new ByteArrayEntity(mData);
        reqEntity.setContentType("application/octet-stream");
        return reqEntity;
    }

    public byte[] getData(){
    	return mData;
    }
    
    /**
     * hasData() - used to determine if this request is a GET or a POST.
     *
     * @return true - request will be treated as a POST
     * @return false - request will be treated as a GET
     */
    public boolean hasData() {
        return (mData != null && mData.length > 0);
    }

    /**
     * isSecure() - whether this request needs to be done over SSL
     *
     * @return true - request will use SSL
     * @return false - request will not use SSL
     */
    public boolean isSecure() {
        return false;
    }

    /**
     * getBodySize() - returns the size of the body so we can put that in the
     * query string params
     *
     * @return int - size of body in bytes
     */
    public long getBodySize() {
        return mData != null ? mData.length : 0;
    }

    public String getUrl() {
        return mUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setHttpParams(Map<String, Object> params) {
        if(params != null && !params.isEmpty())
            httpParams.putAll(params);
    }

    public void setHttpParam(String key, Object value) {
        httpParams.put(key, value);
    }

    public Map<String, Object> getHttpParams() {
        return httpParams;
    }

    public HttpResponse createResponse(int statusCode, byte[] data) {
        return new HttpResponse(getId(), statusCode, data);
    }

    public HttpResponse createResponse(int statusCode, Throwable error) {
        return new HttpResponse(getId(), statusCode, error);
    }
}