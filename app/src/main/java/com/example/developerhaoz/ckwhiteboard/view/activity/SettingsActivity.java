package com.example.developerhaoz.ckwhiteboard.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.developerhaoz.ckwhiteboard.MainActivity;
import com.example.developerhaoz.ckwhiteboard.R;
import com.example.developerhaoz.ckwhiteboard.common.util.TeamManager;
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
    ImageView mIvChooseLogo;
    @BindView(R.id.settings_et_password)
    EditText mEtPassword;
    @BindView(R.id.settings_iv_team_logo)
    ImageView mIvTeamLogo;
    @BindView(R.id.toolbar_iv_back)
    ImageView mIvBack;
    @BindView(R.id.toolbar_iv_confirm)
    ImageView mIvConfirm;
    @BindView(R.id.settings_tv_team_logo)
    TextView mSettingsTvTeamLogo;

    private static final int REQUEST_CODE_CHOOSE = 1;
    private TeamManager teamManager;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        mIvConfirm.setVisibility(View.VISIBLE);
        teamManager = TeamManager.getInstance(this);

    }

    /**
     * 保存团队的信息
     */
    private void saveTeamInfo(TeamManager teamManager) {
        String teamName = mEtTeamName.getText().toString();
        String teamIntroduce = mEtTeamIntroduce.getText().toString();
        String teamPassword = mEtPassword.getText().toString();
        teamManager.setTeamName(teamName);
        teamManager.setTeamIntroduce(teamIntroduce);
        teamManager.setTeamPassword(teamPassword);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE) {
            mIvChooseLogo.setVisibility(View.GONE);
            mIvTeamLogo.setVisibility(View.VISIBLE);
            Uri imageUri = Matisse.obtainResult(data).get(0);
            Glide.with(SettingsActivity.this)
                    .load(imageUri)
                    .into(mIvTeamLogo);
            teamManager.setTeamLogoUrl(String.valueOf(imageUri));
        }
    }

    @OnClick({R.id.toolbar_iv_back, R.id.toolbar_iv_confirm, R.id.settings_iv_choose_logo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toolbar_iv_back:
                finish();
                break;
            case R.id.toolbar_iv_confirm:
                TeamManager teamManager = TeamManager.getInstance(this);
                saveTeamInfo(teamManager);
                finish();
                MainActivity.startActivity(this);
                break;
            case R.id.settings_iv_choose_logo:
                Matisse.from(SettingsActivity.this)
                        .choose(MimeType.allOf())
                        .countable(true)
                        .maxSelectable(9)
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .imageEngine(new GlideEngine())
                        .forResult(REQUEST_CODE_CHOOSE);
                break;
        }
    }
}














