package com.example.developerhaoz.ckwhiteboard.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import com.example.developerhaoz.ckwhiteboard.R;
import com.example.developerhaoz.ckwhiteboard.common.util.SavePictureUtil;
import com.example.developerhaoz.ckwhiteboard.common.util.UsbFileEvent;
import com.github.mjdev.libaums.fs.UsbFile;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

public class ProgressActivity extends AppCompatActivity {

    private RelativeLayout MRl;

    public static void startActivity(Context context){
        Intent intent = new Intent(context, ProgressActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UsbFileEvent event) {
        UsbFile usbFile = event.getUsbFile();
        try {
            SavePictureUtil.savePictureToUsb(this, usbFile);
            finish();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ;
}
