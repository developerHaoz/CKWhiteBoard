package com.example.developerhaoz.ckwhiteboard.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.developerhaoz.ckwhiteboard.R;
import com.example.developerhaoz.ckwhiteboard.test.Doodle;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 用于签名的 Activity
 * <p>
 * Created by developerHaoz on 2017/7/10.
 */

public class SignatureActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_iv_back)
    ImageView mIvBack;
    @BindView(R.id.toolbar_iv_confirm)
    ImageView mIvConfirm;
    @BindView(R.id.signature_tv_evaluation)
    TextView mTvEvaluation;
    @BindView(R.id.signature_doodle)
    Doodle mDoodle;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SignatureActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.toolbar_iv_back, R.id.toolbar_iv_confirm, R.id.signature_tv_evaluation})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toolbar_iv_back:
                finish();
                break;
            case R.id.toolbar_iv_confirm:
                Toast.makeText(this, "You click confirm", Toast.LENGTH_SHORT).show();
                break;
            case R.id.signature_tv_evaluation:
                break;
        }
    }
}
