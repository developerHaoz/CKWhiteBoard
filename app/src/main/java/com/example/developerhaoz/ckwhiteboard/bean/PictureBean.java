package com.example.developerhaoz.ckwhiteboard.bean;

import org.litepal.crud.DataSupport;

/**
 * 将签名的界面保存为一张图片的 Bean 类
 *
 * Created by developerHaoz on 2017/7/15.
 */

public class PictureBean extends DataSupport{

    private int id;
    private String picturePath;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

}
