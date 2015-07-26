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

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class HttpRequestHandler {
    static final String tag = "TT-RequestHandler";
    static final int DEFAULT_CONN_TIMEOUT = 10 * 1000;
    static final int DEFAULT_SO_TIMEOUT = 30 * 1000;
    private DefaultHttpClient client = null;
    private Context mContext = null;

    public interface Callback {
        boolean handleResponse(HttpResponse resp);
    }

    private volatile static HttpRequestHandler handler;

    public static HttpRequestHandler getInstance(Context context) {
        if (handler == null)
            handler = new HttpRequestHandler(context);
        return handler;
    }

    private HttpRequestHandler(Context context) {
        mContext = context;
        HttpParams httpParameters = new BasicHttpParams();
        httpParameters.setLongParameter(ConnManagerPNames.TIMEOUT, DEFAULT_CONN_TIMEOUT);

        SchemeRegistry sr = new SchemeRegistry();
        sr.register(new Scheme("http", new PlainSocketFactory(), 8080));
        sr.register(new Scheme("http", new PlainSocketFactory(), 80));
        SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
        socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        sr.register(new Scheme("https", socketFactory, 443));

        ClientConnectionManager connMgr = new ThreadSafeClientConnManager(httpParameters, sr);
        client = new DefaultHttpClient(connMgr, httpParameters);
    }

    /**
     * processRequest - starts a web service request asynchronously, the
     * callback will be called when the request is processed
     *
     * @param req
     *            - the request to start
     * @param callback
     *            - callback which will be called when response is available
     */
    public void processRequest(HttpRequest req, Callback callback) {
        new Thread(new Transaction(req, callback)).start();
    }

    /**
     * processRequest - starts a web service synchronous request
     *
     * @param req - the request to start
     */
    public synchronized HttpResponse processRequest(HttpRequest req){
        return doHttpRequest(req);
    }

    /**
     * processRequest - starts a web service synchronous request
     *
     * @param req - the request to start
     */
    public synchronized void processDownloadRequest(HttpRequest req){
        doHttpRequest(req);
    }

    private class Transaction implements Runnable {
        private HttpRequest request;
        private Callback callback;

        public Transaction(HttpRequest req, Callback callback) {
            this.request = req;
            this.callback = callback;
        }

        public void run() {
            if(request == null)
                return;
            HttpResponse resp = null;
            try {
                resp = doHttpRequest(request);
            } finally {
                if (callback != null){
                    callback.handleResponse(resp);
                }
            }
        }
    }

    private HttpResponse doHttpRequest(HttpRequest req) {
        HttpUriRequest httpReq = null;
        try {
            if (req.hasData()) {
                HttpPost postReq = new HttpPost(req.getUrl());
                postReq.setEntity(req.getRequestEntity());
                httpReq = postReq;
            } else {
                httpReq = new HttpGet(req.getUrl());
            }
            Log.d(tag, new String(req.getData()));
            Log.d(tag, httpReq.getMethod() + " : " + req.getUrl());
            //set default http parameters
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, DEFAULT_CONN_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParameters, DEFAULT_SO_TIMEOUT);
            client.setParams(httpParameters);
            //set http parameter for the method, this will overwrite the default params above
            if(!req.getHttpParams().isEmpty()){
                Iterator<String> keys = req.getHttpParams().keySet().iterator();
                while(keys.hasNext()) {
                    String key = keys.next();
                    client.getParams().setParameter(key, req.getHttpParams().get(key));
                }
            }

            byte[] respData = null;
            if( ! isNetworkAvailable(mContext)){
                Log.d(tag, "No data connection.");
                return req.createResponse(HttpResponse.NETWORK_DISCONNECTED, respData);
            }

            //execute the http request
            org.apache.http.HttpResponse resp = client.execute(httpReq);
            HttpEntity respEntity = resp.getEntity();
            int statusCode = resp.getStatusLine().getStatusCode();

            if (respEntity != null) {
                respData = EntityUtils.toByteArray(respEntity);
                respData = respData.length != 0 ? respData : null;
            }
            Log.d(tag, String.format("Got Http Response : %d", statusCode));
            return req.createResponse(statusCode, respData);
        } catch (InterruptedIOException e) {
            Log.e(tag, "Failed to read response", e);
            return req.createResponse(HttpResponse.NETWORK_TIMEOUT, e);
        } catch (IOException e) {
            Log.e(tag, "Failed to process http request", e);
            return req.createResponse(HttpResponse.NETWORK_ERROR, e);
        } catch (Throwable e) {
            Log.e(tag, "Failed to process http request", e);
            return req.createResponse(HttpResponse.UNKNOWN_ERROR, e);
        }
    }
    
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null)
            return info.isConnected();
        return false;
    }
}
