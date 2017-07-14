package com.example.developerhaoz.ckwhiteboard.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.developerhaoz.ckwhiteboard.R;
import com.example.developerhaoz.ckwhiteboard.common.img.CommonImageLoader;
import com.example.developerhaoz.ckwhiteboard.common.util.SavePictureUtil;
import com.example.developerhaoz.ckwhiteboard.common.util.TeamManager;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 用于预览所生成图片的 Activity
 * <p>
 * Created by developerHaoz on 2017/7/12.
 */

public class PreviewActivity extends AppCompatActivity {

    private static final String TAG = "PreviewActivity";

    @BindView(R.id.preview_ll)
    LinearLayout mLl;
    @BindView(R.id.preview_iv_team_avatar)
    ImageView mIvTeamAvatar;
    @BindView(R.id.preview_tv_team_name)
    TextView mTvTeamName;
    @BindView(R.id.preview_tv_team_introduce)
    TextView mTvTeamIntroduce;
    @BindView(R.id.preview_tv_evaluation)
    TextView mTvEvaluation;
    @BindView(R.id.preview_iv_signature)
    ImageView mIvSignature;
    @BindView(R.id.preview_tv_time)
    TextView mTvTime;
    @BindView(R.id.preview_tv_count_down)
    TextView mTvCountDown;

    private static final String KEY_IMAGE_URL = "imageUrl";
    private static final String KEY_EVALUATION = "evaluation";
    @BindView(R.id.preview_toolbar)
    RelativeLayout mToolbar;


    public static void startActivity(String imageUrl, String evaluation, Context context) {
        Intent intent = new Intent(context, PreviewActivity.class);
        intent.putExtra(KEY_IMAGE_URL, imageUrl);
        intent.putExtra(KEY_EVALUATION, evaluation);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        ButterKnife.bind(this);
        initView();
    }

    /**
     * 初始化界面
     */
    private void initView() {

        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra(KEY_IMAGE_URL);
        String evaluation = "评语：" + intent.getStringExtra(KEY_EVALUATION);
        CommonImageLoader.getInstance().displayImage(imageUrl, mIvSignature);
        mTvEvaluation.setText(evaluation);

        // 初始化团队信息
        TeamManager teamManager = TeamManager.getInstance(this);
        String teamName = teamManager.getTeamName();
        String teamIntroduce = "团队简介：" + teamManager.getTeamIntroduce();
        String teamLogoUrl = teamManager.getTeamLogoUrl();
        mTvTeamName.setText(teamName);

        mTvTeamIntroduce.setText(teamIntroduce);
        mTvEvaluation.setText(evaluation);
        CommonImageLoader.getInstance().displayImage(teamLogoUrl, mIvTeamAvatar);

        // 初始化时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = simpleDateFormat.format(new Date());
        mTvTime.setText(dateString);
    }

    private static final String PATH_SAVE_PICUTRE = "/signaturePath/";


    @OnClick(R.id.preview_tv_count_down)
    public void onViewClicked() {
        mToolbar.setVisibility(View.GONE);
        Bitmap bitmap = Bitmap.createBitmap(getResources().getDisplayMetrics().widthPixels
                , getResources().getDisplayMetrics().heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        mLl.draw(canvas);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + PATH_SAVE_PICUTRE
                + System.currentTimeMillis() + ".png";
        SavePictureUtil.savePicByPNG(bitmap, path);
    }
}
























