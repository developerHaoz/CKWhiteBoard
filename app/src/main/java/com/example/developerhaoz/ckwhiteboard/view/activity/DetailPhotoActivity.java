package com.example.developerhaoz.ckwhiteboard.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.developerhaoz.ckwhiteboard.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 显示图片细节的 Activity
 * <p>
 * Created by developerHaoz on 2017/7/15.
 */

public class DetailPhotoActivity extends AppCompatActivity {

    @BindView(R.id.detail_photo_pv_show_photo)
    PhotoView mPvShowPhoto;

    private static final String IMAGE_URL = "imageUrl";

    public static void startActivity(String imageUrl, Context context){
        Intent intent = new Intent(context, DetailPhotoActivity.class);
        intent.putExtra(IMAGE_URL, imageUrl);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_photo);
        ButterKnife.bind(this);
        initView();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra(IMAGE_URL);
        Glide.with(this)
                .load(imageUrl)
                .asBitmap()
                .error(R.mipmap.ic_launcher_round)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(0.5f)
                .into(mPvShowPhoto);

        mPvShowPhoto.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float v, float v1) {
                finish();
            }

            @Override
            public void onOutsidePhotoTap() {

            }
        });
    }
}
