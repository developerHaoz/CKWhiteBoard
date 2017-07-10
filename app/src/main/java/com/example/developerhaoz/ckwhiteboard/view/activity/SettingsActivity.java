package com.example.developerhaoz.ckwhiteboard.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.developerhaoz.ckwhiteboard.R;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 设置团队信息的 Activity
 * <p>
 * Created by developerHaoz on 2017/7/10.
 */

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.settings_et_team_name)
    EditText mEtTeamName;
    @BindView(R.id.settings_et_team_introduce)
    EditText mEtTeamIntroduce;
    @BindView(R.id.settings_iv_choose_logo)
    ImageView mIvTeamLogo;
    @BindView(R.id.settings_et_password)
    EditText mEtPassword;

    private static final int REQUEST_CODE_CHOOSE = 1;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.settings_iv_choose_logo)
    public void onViewClicked() {
        Matisse.from(SettingsActivity.this)
                .choose(MimeType.allOf())
                .countable(true)
                .maxSelectable(9)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .forResult(REQUEST_CODE_CHOOSE);

//        Matisse.from(SettingsActivity.this)
//                .choose(MimeType.allOf())
//                .countable(true)
//                .maxSelectable(1)
//                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.photo_grid_size))
//                .thumbnailScale(0.85f)
//                .imageEngine(new GlideEngine())
//                .forResult(REQUEST_CODE_CHOOSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_CHOOSE){
            Uri imageUri = Matisse.obtainResult(data).get(0);
            Glide.with(SettingsActivity.this)
                    .load(imageUri)
                    .into(mIvTeamLogo);
        }
    }
}














