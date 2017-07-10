package com.example.developerhaoz.ckwhiteboard.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.developerhaoz.ckwhiteboard.R;

/**
 * 用于签名的 Activity
 *
 * Created by developerHaoz on 2017/7/10.
 */

public class SignatureActivity extends AppCompatActivity {

    public static void startActivity(Context context){
        Intent intent = new Intent(context, SignatureActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
    }
}
