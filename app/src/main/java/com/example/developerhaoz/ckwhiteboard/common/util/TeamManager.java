package com.example.developerhaoz.ckwhiteboard.common.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 保存团队的信息
 *
 * Created by developerHaoz on 2017/7/12.
 */

public class TeamManager {

    private static final String SP_NAME = "team_manager";
    private static final String KEY_TEAM_NAME = "teamName";
    private static final String KEY_TEAM_INTRODUCE = "teamIntroduce";
    private static final String KEY_TEAM_LOGO_URL = "teamLogoUrl";
    private static final String KEY_TEAM_PASSWORD = "teamPassword";

    private volatile static TeamManager sInstance;
    private Context mContext;
    private SharedPreferences mSharedPreferences;

    private String teamName;
    private String teamIntroduce;
    private String teamLogoUrl;
    private String teamPassword;

    private TeamManager(Context context){
        this.mContext = context.getApplicationContext();
    }

    public static TeamManager getInstance(Context context){
        if (sInstance == null){
            synchronized (TeamManager.class){
                if (sInstance == null){
                    sInstance = new TeamManager(context);
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

    public void setTeamName(String teamName){
        this.teamName = teamName;
        getSharedPreferences().edit().putString(KEY_TEAM_NAME, teamName).apply();
    }

    public String getTeamName(){
        if(Check.isEmpty(teamName)){
            teamName = getSharedPreferences().getString(KEY_TEAM_NAME, "");
        }
        return teamName;
    }

    public void setTeamIntroduce(String teamIntroduce){
        this.teamIntroduce = teamIntroduce;
        getSharedPreferences().edit().putString(KEY_TEAM_INTRODUCE, teamIntroduce).apply();
    }

    public String getTeamIntroduce(){
        if(Check.isEmpty(teamIntroduce)){
            teamIntroduce = getSharedPreferences().getString(KEY_TEAM_INTRODUCE, "");
        }
        return teamIntroduce;
    }

    public void setTeamLogoUrl(String teamLogoUrl){
        this.teamLogoUrl = teamLogoUrl;
        getSharedPreferences().edit().putString(KEY_TEAM_LOGO_URL, teamLogoUrl).apply();
    }

    public String getTeamLogoUrl(){
        if(Check.isEmpty(teamLogoUrl)){
            teamLogoUrl = getSharedPreferences().getString(KEY_TEAM_LOGO_URL, "");
        }
        return teamLogoUrl;
    }

    public void setTeamPassword(String teamPassword){
        this.teamPassword = teamPassword;
        getSharedPreferences().edit().putString(KEY_TEAM_PASSWORD, teamPassword).apply();
    }

    public String getTeamPassword(){
        if(Check.isEmpty(teamPassword)){
            teamPassword = getSharedPreferences().getString(KEY_TEAM_PASSWORD, "");
        }
        return teamPassword;
    }



}



















