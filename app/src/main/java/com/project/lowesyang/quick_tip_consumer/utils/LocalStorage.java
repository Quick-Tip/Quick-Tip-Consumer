package com.project.lowesyang.quick_tip_consumer.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LowesYang on 2017/7/8.
 */

public class LocalStorage {
    public static void setItem(Context context, String key, String val){
        SharedPreferences sp=context.getSharedPreferences(key,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        String target=null;
        if(val!=null) target=val;
        editor.putString(key,target);
        editor.commit();
    }

    public static Object getItem(Context context,String key){
        SharedPreferences sp=context.getSharedPreferences(key,Context.MODE_PRIVATE);
        Object res=null;
        try {
            res=sp.getString(key,null);
            if(res!=null) {
                res = new JSONObject(( String ) res);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }
}
