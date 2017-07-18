package com.example.developerhaoz.ckwhiteboard.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.developerhaoz.ckwhiteboard.R;

import butterknife.ButterKnife;

/**
 * 用于测试 Activity
 * <p>
 * Created by developerHaoz on 2017/7/10.
 */

public class TestActivity extends AppCompatActivity {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
    }
}

























