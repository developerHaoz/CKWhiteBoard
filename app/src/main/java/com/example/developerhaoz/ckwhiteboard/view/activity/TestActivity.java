package com.example.developerhaoz.ckwhiteboard.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.developerhaoz.ckwhiteboard.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 用于测试 Activity
 * <p>
 * Created by developerHaoz on 2017/7/10.
 */

public class TestActivity extends AppCompatActivity {

    @BindView(R.id.test_iv_show_photo)
    ImageView mIvShowPhoto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        String imageUrl = "content://media/external/images/media/137";
        Glide.with(this)
                .load(imageUrl)
                .into(mIvShowPhoto);
    }
}

























