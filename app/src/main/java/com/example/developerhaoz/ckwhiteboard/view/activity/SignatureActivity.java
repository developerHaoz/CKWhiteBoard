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

import com.example.developerhaoz.ckwhiteboard.R;
import com.example.developerhaoz.ckwhiteboard.doodle.Doodle;

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
        mIvConfirm.setVisibility(View.GONE);
        initView();
    }

    private void initView() {
        mDoodle.setSize(dip2px(5));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDoodle.onTouchEvent(event);
    }

    @OnClick({R.id.toolbar_iv_back,
            R.id.signature_tv_evaluation,
            R.id.signature_btn_reevaluate,
            R.id.signature_btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toolbar_iv_back:
                finish();
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
                            new String[]{"项目很好，团队很棒", "项目不错，加油努力", "同学们仍需努力，做的更好"
                            }, 0,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which + 1) {
                                        case 1:
                                            mTvEvaluation.setText("项目很好，团队很棒");
                                            break;
                                        case 2:
                                            mTvEvaluation.setText("项目不错，加油努力");
                                            break;
                                        case 3:
                                            mTvEvaluation.setText("同学们仍需努力，做的更好");
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
