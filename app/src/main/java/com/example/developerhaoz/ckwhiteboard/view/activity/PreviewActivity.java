package com.example.developerhaoz.ckwhiteboard.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.developerhaoz.ckwhiteboard.MainActivity;
import com.example.developerhaoz.ckwhiteboard.R;
import com.example.developerhaoz.ckwhiteboard.bean.PictureBean;
import com.example.developerhaoz.ckwhiteboard.common.img.CommonImageLoader;
import com.example.developerhaoz.ckwhiteboard.common.util.SavePictureUtil;
import com.example.developerhaoz.ckwhiteboard.common.util.TeamManager;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 用于预览所生成图片的 Activity
 *
 * Created by developerHaoz on 2017/7/12.
 */

public class PreviewActivity extends AppCompatActivity {

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
    @BindView(R.id.preview_toolbar)
    RelativeLayout mToolbar;

    private static final String KEY_IMAGE_URL = "imageUrl";
    private static final String KEY_EVALUATION = "evaluation";

    /**
     * 进行倒计时保存图片
     */
    private CountDownTimer timer = new CountDownTimer(5000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            String timeUnit = " S";
            mTvCountDown.setText(millisUntilFinished / 1000 + timeUnit);
        }

        @Override
        public void onFinish() {
            mToolbar.setVisibility(View.GONE);
            String path = saveCurrentView();
            saveToDatabase(path);
            MainActivity.startActivity(PreviewActivity.this);
            finish();
        }
    };

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
        timer.start();
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

    /**
     * 将当前界面的保存为图片，并返回图片的路径
     *
     * @return 图片的路径
     */
    @NonNull
    private String saveCurrentView() {
        Bitmap bitmap = Bitmap.createBitmap(getResources().getDisplayMetrics().widthPixels
                , getResources().getDisplayMetrics().heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        mLl.draw(canvas);

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/doodle/" + System.currentTimeMillis() + ".png";
        SavePictureUtil.savePicByPNG(bitmap, path);
        return path;
    }

    /**
     * 将图片路径保存在数据库中
     *
     * @param path
     */
    private void saveToDatabase(String path) {
        PictureBean pictureBean = new PictureBean();
        pictureBean.setPicturePath(path);
        pictureBean.save();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}
























