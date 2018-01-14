package com.example.developerhaoz.ckwhiteboard.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.developerhaoz.ckwhiteboard.R;
import com.example.developerhaoz.ckwhiteboard.bean.PictureBean;
import com.example.developerhaoz.ckwhiteboard.common.img.dialog.CommonDialogFragment;
import com.example.developerhaoz.ckwhiteboard.common.img.dialog.DialogFragmentHelper;
import com.example.developerhaoz.ckwhiteboard.common.img.dialog.IDialogResultListener;
import com.example.developerhaoz.ckwhiteboard.doodle.Doodle;
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

    private static final int REQUEST_CODE_CHOOSE = 1;

    @BindView(R.id.selected_picture_iv_back)
    ImageView mIvBack;
    @BindView(R.id.selected_picture_iv_import)
    ImageView mIvImportPicture;
    @BindView(R.id.selected_picture_iv_delete)
    ImageView mIvDelete;
    @BindView(R.id.selected_picture_rv_show_photo_wall)
    RecyclerView mRvShowPhotoWall;
    @BindView(R.id.selected_picture_rl)
    RelativeLayout mRl;
    @BindView(R.id.progressBar)
    ProgressBar mProgress;

    /**
     * 保存所有图片的 path
     */
    private List<String> mPhotoUrlList = new ArrayList<>();

    private DialogFragment mDialogFragment;
    private SelectedPictureAdapter mAdapter;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SelectedPictureActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_picture);
        ButterKnife.bind(this);
        /**
         * 数据库用的是郭霖的 LitePal，以下是获取所有图片实体类的方法
         */
        List<PictureBean> pictureBeanList = DataSupport.findAll(PictureBean.class);
        for (int i = 0; i < pictureBeanList.size(); i++) {
            mPhotoUrlList.add(pictureBeanList.get(i).getPicturePath());
        }
        initPhotoWall(mPhotoUrlList);
    }

    private void initPhotoWall(List<String> photoUrlList) {
        mAdapter = new SelectedPictureAdapter(this, photoUrlList);
        mRvShowPhotoWall.setLayoutManager(new GridLayoutManager(this, 4));
        mRvShowPhotoWall.setAdapter(mAdapter);
    }

    @OnClick({R.id.selected_picture_iv_back, R.id.selected_picture_ll_import, R.id.selected_picture_ll_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.selected_picture_iv_back:
                finish();
                break;
            case R.id.selected_picture_ll_import:
                Matisse.from(SelectedPictureActivity.this)
                        .choose(MimeType.allOf())
                        .countable(true)
                        .maxSelectable(20)
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .imageEngine(new GlideEngine())
                        .forResult(REQUEST_CODE_CHOOSE);
                break;
            case R.id.selected_picture_ll_delete:
                DialogFragmentHelper.showConfirmDialog(getSupportFragmentManager(), "是否删除所有图片？", new IDialogResultListener<Integer>() {
                    @Override
                    public void onDataResult(Integer result) {
                        if(result == RESULT_OK){
                            Doodle.deleteAllFiles();
                            DataSupport.deleteAll(PictureBean.class);
                            mPhotoUrlList.clear();
                            mAdapter.notifyDataSetChanged();
                            showToast("图片删除成功");
                        }else{
                            showToast("删除图片已取消");
                        }
                    }
                }, true, new CommonDialogFragment.OnDialogCancelListener() {
                    @Override
                    public void onCancel() {
                        showToast("删除图片已取消");
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    @NonNull
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CHOOSE) {
            if (data != null) {
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

}
