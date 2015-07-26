/*
 * Copyright (C) 2011, Motorola Mobility, Inc,
 * All Rights Reserved.
 * Motorola Confidential Restricted.
 *
 * Modification History:
 **********************************************************
 * Date           Author         Comments
 * 11-12-2012    Jinshui Tang   Created file
 **********************************************************
 */
package com.yingshi.toutiao.http;


public class HttpResponse {
    public final static int NETWORK_ERROR = -1;
    public final static int NETWORK_TIMEOUT = -2;
    public final static int NETWORK_DISCONNECTED = -3;
    public final static int UNKNOWN_ERROR = -101;
    protected long id;
    protected byte[] mData = null;
    protected int mStatusCode;
    protected Throwable mError;

    public HttpResponse(long id, int statusCode, Throwable error){
        this.id = id;
        mStatusCode = statusCode;
        mError = error;
    }

    public HttpResponse(int statusCode, byte[] data) {
        this(0, statusCode, data);
    }

    public HttpResponse(long id, int statusCode, byte[] data) {
        this.id = id;
        mData = data;
        mStatusCode = statusCode;
    }

    public int getStatusCode() {
        return mStatusCode;
    }

    public Throwable getException() {
        return mError;
    }

    public void setException(Throwable error) {
        this.mError = error;
    }

    public byte[] getData() {
        return mData;
    }

    public String getAppError() {
        return null;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
