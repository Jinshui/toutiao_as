package com.yingshi.toutiao.util;

import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PreferenceUtil {

    public static void saveStringMap(Context context, Map<String, String> map){
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = preference.edit();
        Iterator<Map.Entry<String, String>> entryIt = map.entrySet().iterator();
        while(entryIt.hasNext()){
        	Map.Entry<String, String> entry = entryIt.next();
        	editor.putString(entry.getKey(), entry.getValue());
        }
        editor.commit();
    }

    public static void saveString(Context context, String key, String value){
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = preference.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getString(Context context, String key, String defValue){
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        return preference.getString(key, defValue);
    }
    
    public static void removeKey(Context context, String key){
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = preference.edit();
        editor.remove(key);
        editor.commit();
    }

    public static void saveInt(Context context, String key, int value){
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = preference.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getInt(Context context, String key, int defValue){
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        return preference.getInt(key, defValue);
    }
}
