package com.example.developerhaoz.ckwhiteboard.view.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
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
    @BindView(R.id.signature_btn_reevaluate)
    Button mBtnReevaluate;
    @BindView(R.id.signature_btn_confirm)
    Button mBtnConfirm;

    private AlertDialog mEvaluationDialog;


    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SignatureActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mDoodle.setSize(dip2px(5));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDoodle.onTouchEvent(event);
    }

    @OnClick({R.id.toolbar_iv_back, R.id.toolbar_iv_confirm, R.id.signature_tv_evaluation
            , R.id.signature_btn_reevaluate, R.id.signature_btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toolbar_iv_back:
                finish();
                break;
            case R.id.toolbar_iv_confirm:
                Toast.makeText(this, "You click confirm", Toast.LENGTH_SHORT).show();
                break;
            case R.id.signature_tv_evaluation:
                showEvaluationDialog();
                break;
            case R.id.signature_btn_reevaluate:
                mDoodle.reset();
                break;
            case R.id.signature_btn_confirm:
                doConfirm();
                break;
            default:
                break;
        }
    }

    /**
     * 确定保存图片
     */
    private void doConfirm() {
        String path = mDoodle.saveBitmap(mDoodle);
        Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
        String evaluation = mTvEvaluation.getText().toString();
        PreviewActivity.startActivity(path, evaluation, SignatureActivity.this);
    }

    /**
     * 显示选择评语的对话框
     */
    private void showEvaluationDialog() {
        if (mEvaluationDialog == null) {
            mEvaluationDialog = new AlertDialog.Builder(this)
                    .setTitle("请选择评语")
                    .setSingleChoiceItems(
                            new String[]{"项目很好继续加油", "项目技术含量很高", "团队人员都很出色"
                            }, 0,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which + 1) {
                                        case 1:
                                            mTvEvaluation.setText("项目很好继续加油");
                                            break;
                                        case 2:
                                            mTvEvaluation.setText("项目技术含量很高");
                                            break;
                                        case 3:
                                            mTvEvaluation.setText("团队人员都很出色");
                                            break;
                                        default:
                                            break;
                                    }
                                    dialog.dismiss();
                                }
                            }).create();
        }
        mEvaluationDialog.show();
    }

    private int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
