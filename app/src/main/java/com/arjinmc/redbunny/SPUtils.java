package com.arjinmc.redbunny;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Eminem on 2016/10/27.
 */

public final class SPUtils {

    public static final String TB_CONFIG = "config";

    public static final String WECHAT = "wechat";
    public static final String QQ = "qq";

    public static void setStatus(Context context,String key, boolean status){
        if(context!=null){
            SharedPreferences sharedPreferences
                    = context.getSharedPreferences(TB_CONFIG,Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(key,status);
            editor.commit();
        }
    }

    public static boolean getStatus(Context context,String key){
        if(context!=null){
            SharedPreferences sharedPreferences
                    = context.getSharedPreferences(TB_CONFIG,Context.MODE_PRIVATE);
            return sharedPreferences.getBoolean(key,true);
        }
        return  true;
    }



}
