package com.example.developerhaoz.ckwhiteboard.common.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 密码的管理类
 *
 * Created by developerHaoz on 2017/7/15.
 */

public class PasswordManager {

    private static final String SP_NAME = "password_manager";
    private static final String KEY_SETTINGS_PASSWORD = "settings_password";
    private static final String KEY_PICTURE_PASSWORD = "picture_password";
    private static final String KEY_COMMON_PASSWORD = "common_password";
    private static final String COMMON_PASSWORD = "admin";

    private volatile static PasswordManager sInstance;
    private Context mContext;
    private SharedPreferences mSharedPreferences;

    private String mSettingsPassword;
    private String mPicturePassword;
    private String mCommonPassword;

    private PasswordManager(Context context){
        this.mContext = context.getApplicationContext();
    }

    public static PasswordManager getInstance(Context context){
        if (sInstance == null){
            synchronized (PasswordManager.class){
                if (sInstance == null){
                    sInstance = new PasswordManager(context);
                }
            }
        }
        return sInstance;
    }

    private SharedPreferences getSharedPreferences(){
        if(mSharedPreferences == null){
            mSharedPreferences = mContext.getSharedPreferences(SP_NAME, Context.MODE_APPEND);
        }
        return mSharedPreferences;
    }

    public void setSettingsPassword(String settingsPassword){
        this.mSettingsPassword = settingsPassword;
        getSharedPreferences().edit().putString(KEY_SETTINGS_PASSWORD, settingsPassword).apply();
    }

    public String getSettingsPassword(){
        if(Check.isEmpty(mSettingsPassword)){
            this.mSettingsPassword = getSharedPreferences().getString(KEY_SETTINGS_PASSWORD, "");
        }
        return mSettingsPassword;
    }

    public void setPicturePassword(String picturePassword) {
        this.mPicturePassword = picturePassword;
        getSharedPreferences().edit().putString(KEY_PICTURE_PASSWORD, picturePassword).apply();
    }

    public String getPicturePassword(){
        if(Check.isEmpty(mPicturePassword)){
            mPicturePassword = getSharedPreferences().getString(KEY_PICTURE_PASSWORD, "");
        }
        return mPicturePassword;
    }

    public void setCommonPassword(String commonPassword){
        this.mCommonPassword = commonPassword;
        getSharedPreferences().edit().putString(KEY_COMMON_PASSWORD, commonPassword).apply();
    }

    public String getCommonPassword(){
        if(Check.isEmpty(mCommonPassword)){
            mCommonPassword = getSharedPreferences().getString(KEY_COMMON_PASSWORD, COMMON_PASSWORD);
        }
        return mCommonPassword;
    }
}
