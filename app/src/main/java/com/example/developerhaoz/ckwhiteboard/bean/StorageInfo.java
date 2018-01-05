package com.example.developerhaoz.ckwhiteboard.bean;

/**
 * @author Haoz
 * @date 2018/1/5.
 */
public class StorageInfo {
    public String path;
    public String state;
    public boolean isRemoveable;

    public StorageInfo(String path) {
        this.path = path;
    }

    public boolean isMounted() {
        return "mounted".equals(state);
    }
}
