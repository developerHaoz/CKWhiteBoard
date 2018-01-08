package com.example.developerhaoz.ckwhiteboard.common;

import com.example.developerhaoz.ckwhiteboard.common.util.Check;
import com.example.developerhaoz.ckwhiteboard.common.util.PasswordManager;

import org.litepal.LitePalApplication;

/**
 * 白板应用的 Application
 *
 * Created by developerHaoz on 2017/7/15.
 */

public class WhiteBoardApplication extends LitePalApplication {

    private  static WhiteBoardApplication sInstance;
    private static final String COMMON_PASSWORD = "admin";

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        if(Check.isEmpty(PasswordManager.getInstance(this).getCommonPassword())){
            PasswordManager.getInstance(this).setCommonPassword(COMMON_PASSWORD);
        }
    }

    public static WhiteBoardApplication getInstance(){
        return sInstance;
    }
}




