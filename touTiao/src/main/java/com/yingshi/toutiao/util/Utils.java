package com.yingshi.toutiao.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

public class Utils {
    private final static String tag = "TT-Utils";
    private static SimpleDateFormat sdf = null;
    
    public static String formatDate(String format, long date) {
    	return formatDate(format, new Date(date));
    }

    @SuppressLint("SimpleDateFormat")
	public static String formatDate(String format, Date date) {
        if (sdf == null)
            sdf = new SimpleDateFormat(format);
        else
            sdf.applyPattern(format);
        return sdf.format(date);
    }

    @SuppressLint("DefaultLocale")
	public static String formatShorterDate(Date date){
        Calendar formatDate = Calendar.getInstance();
        formatDate.setTime(date);

        Calendar endOfYesterday = Calendar.getInstance();
        endOfYesterday.set(Calendar.HOUR_OF_DAY, 0);
        endOfYesterday.set(Calendar.MINUTE, 0);
        endOfYesterday.set(Calendar.SECOND, 0);
        endOfYesterday.set(Calendar.MILLISECOND, 0);


        if(endOfYesterday.after(formatDate)){
            return formatDate("MMM dd", date);
        }else{
            return formatDate("h:mma", date).toLowerCase();
        }
    }
    
    @SuppressLint("SimpleDateFormat")
	public static Date parseDate(String format, String date){
        if(sdf == null)
            sdf = new SimpleDateFormat(format);
        else
            sdf.applyPattern(format);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            Log.e(tag, String.format("Invalid params : %s , %s", format , date), e);
            return null;
        }
    }
    
    public static String encode(String str, String charSet){
    	try {
    		if(str != null)
    			return URLEncoder.encode(str, charSet);
		} catch (UnsupportedEncodingException e) {
            Log.e(tag, String.format("Unsupported Encoding : %s , %s", str , charSet), e);
		}
    	return str;
    }

    public static String getDecodedValue(JSONObject json, String name) throws JSONException{
        String value = null;
        try {
            value = json.getString(name);
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new JSONException("Invalid json object. name:" + name + ", value:" + value);
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if(info != null)
            return info.isConnected();
        return false;
    }

    public static boolean isWIFIConnected(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (netInfo != null && netInfo.isConnected()) {
            Log.d(tag, String.format("TYPE_WIFI: %s, WIFI connected", netInfo.toString()));
            return true;
        }
        return false;
    }

    @SuppressLint("DefaultLocale")
	public static String capitalize(String source){
        if(source == null)
            return null;
        if(source.length() == 1)
            return source.toUpperCase();
        return Character.toUpperCase(source.charAt(0)) + source.substring(1).toLowerCase();
    }

    public static boolean mkdirs(String dir){
        if(TextUtils.isEmpty(dir))
            return false;
        File file = new File(dir);
        if(file.exists())
            return false;
        return file.mkdirs();
    }

    /**
     * Remove the multiples files specified in the files array. If the files contain
     * directory, it will remove it recursively.
     * @param files file paths to be removed
     */
    public static void removeFiles(String[] files){
        if(files == null)
            return;
        for(String file : files){
             removeFile(file);
        }
    }

    /**
     * Remove the file for the give filePath. If the the filePath is a directory,
     * it will remove it recursively.
     * @param filePath file path to be removed
     */
    public static boolean removeFile(String filePath){
        Log.d(tag, String.format("removeFile() : %s", filePath));
        if(filePath == null)
            return true;
        File file = new File(filePath);
        if(!file.exists()){
            Log.w(tag, String.format("File Not Exist : %s", filePath));
            return true;
        }

        if(file.isDirectory()){
            if(!filePath.endsWith(File.separator)){
                filePath = filePath + File.separator;
            }
            String[] children = file.list();
            for (int i=0; children!=null && i<children.length; i++) {
                boolean success = removeFile(filePath+children[i]);
                if (!success) {
                    return false;
                }
            }
        }
        return file.delete();
    }

    public static Properties readPropertiesFromFile(String filePath) {
        Log.i(tag, String.format("readPropertiesFromFile %s", filePath));
        Properties properties = new Properties();
        if(TextUtils.isEmpty(filePath)) {
            Log.i(tag, "Invalid file path");
            return properties;
        }
        File file = new File(filePath);
        //load the existing properties
        if (file.exists() && file.isFile()) {
            FileInputStream is = null;
            try {
                is = new FileInputStream(file);
                properties.load(is);
            } catch (FileNotFoundException e) {
                Log.e(tag, String.format("File not found %s", file.getAbsolutePath()), e);
            } catch (IOException e) {
                Log.e(tag, String.format("Error occured while loading properties file %s", file.getAbsolutePath()));
            } finally {
                try {
                    if (is != null)
                        is.close();
                } catch (Exception ex) {
                    Log.v(tag, ex.getMessage());
                }
            }
        }
        return properties;
    }

    public static void savePropertiesToFile(Properties properties, String filePath) {
        Log.i(tag, String.format("savePropertiesToFile %s", filePath));
        if(properties  == null) {
            Log.i(tag, "The properties to be saved is a null object");
            return;
        }
        OutputStream os = null;
        try {
            File file = new File(filePath);
            File parentFile = file.getParentFile();
            if(parentFile != null && !parentFile.exists())
                parentFile.mkdirs();
            os = new FileOutputStream(file);
            properties.store(os, "Auto-generated by Bug2Go at " + new Date().toString());
        } catch (Exception e) {
            Log.e(tag, String.format("Failed to save properties to file %s", filePath), e);
        } finally {
            try {
                if (os != null) {
                    os.close();
                    os = null;
                }
            } catch (Exception ex) {
                Log.e(tag, "Error closing Output Stream", ex);
            }
        }
    }

    public static void saveDataToFile(byte[] data, String filePath){
        Log.d(tag, String.format("saveDataToFile %s", filePath));
        if(data  == null) {
            Log.d(tag, "The data to be saved is a null object");
            return;
        }
        OutputStream os = null;
        try {
            File file = new File(filePath);
            File parentFile = file.getParentFile();
            if(parentFile != null && !parentFile.exists())
                parentFile.mkdirs();
            os = new FileOutputStream(file);
            os.write(data);
            os.flush();
        } catch (Exception e) {
            Log.e(tag, String.format("Failed to save data to file %s", filePath), e);
        } finally {
            try {
                if (os != null) {
                    os.close();
                    os = null;
                }
            } catch (Exception ex) {
                Log.e(tag, "Error closing Output Stream", ex);
            }
        }
    }

    /**
     * While calling this method, you must close the is yourself.
     * @param is
     * @param filePath
     */
    public static void saveDataToFile(InputStream is, String filePath) throws Exception{
// TODO Use Buffered streams
        Log.d(tag, String.format("saveDataToFile %s", filePath));
        if(is  == null || filePath == null) {
            Log.d(tag, "Invalid data or file path");
            return;
        }
        OutputStream os = null;
        try {
            File file = new File(filePath);
            File parentFile = file.getParentFile();
            if(parentFile != null && !parentFile.exists())
                parentFile.mkdirs();
            os = new FileOutputStream(file);
            byte[] data = new byte[1024];
            int read = is.read(data);
            while( read >= 0 ){
                os.write(data, 0, read);
                read = is.read(data);
            }
            os.flush();
        } finally {
            try {
                if (os != null) {
                    os.close();
                    os = null;
                }
            } catch (Exception ex) {
                Log.e(tag, "Error closing Output Stream", ex);
            }
        }
    }
    
    public static void saveDataToOutput(InputStream is, OutputStream os) throws Exception{
        Log.d(tag, String.format("saveDataToFile"));
        if(is  == null || os == null) {
            Log.d(tag, "Invalid data or file path");
            return;
        }
        try {
            byte[] data = new byte[1024];
            int read = is.read(data);
            while( read >= 0 ){
                os.write(data, 0, read);
                read = is.read(data);
            }
            os.flush();
        } finally {
            try {
                if (os != null) {
                    os.close();
                    os = null;
                }
            } catch (Exception ex) {
                Log.e(tag, "Error closing Output Stream", ex);
            }
        }
    }

    public static byte[] readDataFromFile(String filePath){
        ByteArrayOutputStream bArrayOut = null;
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);
            bArrayOut = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int length = is.read(data, 0, 1024);
            while(length != -1){
                bArrayOut.write(data, 0, length);
                length = is.read(data, 0, 1024);
            }
            return bArrayOut.toByteArray();
        } catch (IOException e) {
            Log.e(tag, "Error reading file : " + filePath, e);
        }finally{
            if(is != null) try{ is.close(); } catch(Exception e){}
            if(bArrayOut != null) try{ bArrayOut.close(); } catch(Exception e){}
        }
        return null;
    }

    /**
     * @param srcFile the source file, can not be a directory
     * @param dstFile the destination file, can be a directory.
     * @throws Exception
     */
    public static boolean copyFile(String srcFile, String dstFile) throws Exception{
// TODO Use Buffered streams
        if(srcFile == null || dstFile == null){
            return false;
        }
        File _srcFile = new File(srcFile);
        //does not support directories
        if(!_srcFile.exists() || _srcFile.isDirectory())
            return false;
        File _dstFile = new File(dstFile);
        if(_dstFile.exists() && _dstFile.isDirectory()){
            _dstFile = new File(dstFile + File.separator + _srcFile.getName());
        }
        InputStream is = new FileInputStream(_srcFile);
        try{
            saveDataToFile(is, _dstFile.getAbsolutePath());
            return true;
        }finally{
            try { is.close(); } catch(Exception e){}
        }
    }

    public static byte[] getFileContent(String file) throws IOException{
        if(file == null)
            throw new IllegalArgumentException("Invalid file path");
        ByteArrayOutputStream baos = null;
        InputStream is = null;
        try{
            is = new FileInputStream(new File(file));
            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read = is.read(buffer);
            while( read >= 0 ){
                baos.write(buffer, 0, read);
                read = is.read(buffer);
            }
            return baos.toByteArray();
        }finally{
            if(is != null) try { is.close(); } catch(Exception e){}
            if(baos != null) try { baos.close(); } catch(Exception e){}
        }
    }

    public static String getRealPathFromURI(Uri contentUri, Activity context) {
        String [] proj      = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
		Cursor cursor = context.managedQuery(contentUri, proj, null, null, null);
        if (cursor == null)
            return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String filePath = cursor.getString(column_index);
        if(!TextUtils.isEmpty(filePath) && filePath.startsWith("file://")){
            return filePath.substring("file://".length());
        }
        return filePath;
    }
}
