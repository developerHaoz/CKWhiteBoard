package com.example.developerhaoz.ckwhiteboard.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.developerhaoz.ckwhiteboard.R;
import com.example.developerhaoz.ckwhiteboard.bean.PictureBean;
import com.example.developerhaoz.ckwhiteboard.view.adapter.SelectedPictureAdapter;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 用于展示签名图片墙的 Activity
 * <p>
 * Created by developerHaoz on 2017/7/17.
 */

public class SelectedPictureActivity extends AppCompatActivity {

    private static final String TAG = "SelectedPictureActivity";
    private static final int REQUEST_CODE_CHOOSE = 1;

    @BindView(R.id.selected_picture_iv_back)
    ImageView mIvBack;
    @BindView(R.id.selected_picture_iv_import)
    ImageView mIvImportPicture;
    @BindView(R.id.selected_picture_iv_export)
    ImageView mIvExportPicture;
    @BindView(R.id.selected_picture_rv_show_photo_wall)
    RecyclerView mRvShowPhotoWall;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SelectedPictureActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_picture);
        ButterKnife.bind(this);

        List<PictureBean> pictureBeanList = DataSupport.findAll(PictureBean.class);
        List<String> photoUrlList = new ArrayList<>();

        for (int i = 0; i < pictureBeanList.size(); i++) {
            photoUrlList.add(pictureBeanList.get(i).getPicturePath());
        }

        SelectedPictureAdapter adapter = new SelectedPictureAdapter(this, photoUrlList);
        mRvShowPhotoWall.setLayoutManager(new GridLayoutManager(this, 4));
        mRvShowPhotoWall.setAdapter(adapter);
    }

    @OnClick({R.id.selected_picture_iv_back, R.id.selected_picture_iv_import, R.id.selected_picture_iv_export})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.selected_picture_iv_back:
                finish();
                break;
            case R.id.selected_picture_iv_import:
                Matisse.from(SelectedPictureActivity.this)
                        .choose(MimeType.allOf())
                        .countable(true)
                        .maxSelectable(20)
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .imageEngine(new GlideEngine())
                        .forResult(REQUEST_CODE_CHOOSE);
                break;
            case R.id.selected_picture_iv_export:
                Toast.makeText(this, "export picture", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CHOOSE) {
            List<Uri> imageUri = Matisse.obtainResult(data);
            List<PictureBean> pictureBeanList = new ArrayList<>();
            for (int i = 0; i < imageUri.size(); i++) {
                PictureBean pictureBean = new PictureBean();
                pictureBean.setPicturePath(String.valueOf(imageUri.get(i)));
                pictureBeanList.add(pictureBean);
            }
            DataSupport.saveAll(pictureBeanList);
            startActivity(this);
            finish();
        }
    }
}
