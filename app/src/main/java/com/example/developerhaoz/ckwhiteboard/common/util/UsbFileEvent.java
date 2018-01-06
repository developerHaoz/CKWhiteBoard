package com.example.developerhaoz.ckwhiteboard.common.util;

import com.github.mjdev.libaums.fs.UsbFile;

/**
 * @author Haoz
 * @date 2018/1/5.
 */
public class UsbFileEvent {

    private UsbFile mUsbFile;

    public UsbFileEvent(UsbFile file){
        this.mUsbFile = file;
    }

    public UsbFileEvent(){

    }

    public UsbFile getUsbFile() {
        return mUsbFile;
    }
}
