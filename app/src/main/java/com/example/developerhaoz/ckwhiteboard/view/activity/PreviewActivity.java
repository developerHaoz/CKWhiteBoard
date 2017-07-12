package com.example.developerhaoz.ckwhiteboard.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.developerhaoz.ckwhiteboard.R;
import com.example.developerhaoz.ckwhiteboard.common.img.CommonImageLoader;
import com.example.developerhaoz.ckwhiteboard.common.util.TeamManager;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 用于预览所生成图片的 Activity
 * <p>
 * Created by developerHaoz on 2017/7/12.
 */

public class PreviewActivity extends AppCompatActivity {

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

    private static final String KEY_IMAGE_URL = "imageUrl";
    private static final String KEY_EVALUATION = "evaluation";

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
        String evaluation = intent.getStringExtra(KEY_EVALUATION);
        mTvEvaluation.setText(R.string.Evaluation + evaluation);
        CommonImageLoader.getInstance().displayImage(imageUrl, mIvSignature);

        // 初始化团队信息
        TeamManager teamManager = TeamManager.getInstance(this);
        String teamName = teamManager.getTeamName();
        String teamIntroduce = teamManager.getTeamIntroduce();
        String teamLogoUrl = teamManager.getTeamLogoUrl();
        mTvTeamName.setText(teamName);
        mTvEvaluation.setText(teamIntroduce);
        CommonImageLoader.getInstance().displayImage(teamLogoUrl, mIvTeamAvatar);

        // 初始化时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = simpleDateFormat.format(new Date());
        mTvTime.setText(dateString);
    }

}
























