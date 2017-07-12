package com.example.developerhaoz.ckwhiteboard;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.developerhaoz.ckwhiteboard.common.img.CommonImageLoader;
import com.example.developerhaoz.ckwhiteboard.common.util.TeamManager;
import com.example.developerhaoz.ckwhiteboard.view.activity.SettingsActivity;
import com.example.developerhaoz.ckwhiteboard.view.activity.SignatureActivity;
import com.example.developerhaoz.ckwhiteboard.view.adapter.MainAdapter;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CHOOSE = 1;

    @BindView(R.id.main_iv_team_avatar)
    ImageView mIvTeamAvatar;
    @BindView(R.id.main_tv_team_name)
    TextView mTvTeamName;
    @BindView(R.id.main_tv_team_introduce)
    TextView mTvTeamIntroduce;
    @BindView(R.id.main_rv_photo_wall)
    RecyclerView mRvPhotoWall;
    @BindView(R.id.main_iv_photo)
    ImageView mIvPhoto;
    @BindView(R.id.main_iv_settings)
    ImageView mIvSettings;
    @BindView(R.id.main_iv_great)
    ImageView mIvGreat;

    private List<String> mPhotoUrlList;

    public static void startActivity(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initiView();
    }

    /**
     * 初始化界面
     */
    private void initiView() {
        TeamManager teamManager = TeamManager.getInstance(this);
        String teamName= teamManager.getTeamName();
        String teamIntroduce = teamManager.getTeamIntroduce();
        String teamLogoUrl = teamManager.getTeamLogoUrl();

        mTvTeamName.setText(teamName);
        mTvTeamIntroduce.setText(teamIntroduce);
        CommonImageLoader.getInstance().displayImage(teamLogoUrl, mIvTeamAvatar);

        mPhotoUrlList = new ArrayList<>();
        initPhotoUrlList();
        mRvPhotoWall.setLayoutManager(new GridLayoutManager(this, 3));
        mRvPhotoWall.setAdapter(new MainAdapter(mPhotoUrlList));
    }

    private void initPhotoUrlList() {

        String imageUrl = "http://ww3.sinaimg.cn/large/7a8aed7bgw1eswencfur6j20hq0qodhs.jpg";
        for (int i = 0; i < 15; i++) {
            mPhotoUrlList.add(imageUrl);
        }
    }

    @OnClick({R.id.main_iv_photo, R.id.main_iv_settings, R.id.main_iv_great})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.main_iv_photo:
                Matisse.from(MainActivity.this)
                        .choose(MimeType.allOf())
                        .countable(true)
                        .maxSelectable(9)
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .imageEngine(new GlideEngine())
                        .forResult(REQUEST_CODE_CHOOSE);
                break;
            case R.id.main_iv_settings:
                SettingsActivity.startActivity(this);
                break;
            case R.id.main_iv_great:
                SignatureActivity.startActivity(this);
                break;
        }
    }
}
