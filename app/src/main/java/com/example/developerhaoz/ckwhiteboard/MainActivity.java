package com.example.developerhaoz.ckwhiteboard;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.developerhaoz.ckwhiteboard.bean.PictureBean;
import com.example.developerhaoz.ckwhiteboard.common.img.CommonImageLoader;
import com.example.developerhaoz.ckwhiteboard.common.img.dialog.DialogFragmentHelper;
import com.example.developerhaoz.ckwhiteboard.common.img.dialog.IDialogResultListener;
import com.example.developerhaoz.ckwhiteboard.common.util.Check;
import com.example.developerhaoz.ckwhiteboard.common.util.PasswordManager;
import com.example.developerhaoz.ckwhiteboard.common.util.TeamManager;
import com.example.developerhaoz.ckwhiteboard.view.activity.SelectedPictureActivity;
import com.example.developerhaoz.ckwhiteboard.view.activity.SettingsActivity;
import com.example.developerhaoz.ckwhiteboard.view.activity.SignatureActivity;
import com.example.developerhaoz.ckwhiteboard.view.adapter.MainAdapter;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CHOOSE = 1;
    private static final int REQUEST_CODE_SELECTED = 2;

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
    private DialogFragment mDialogFragment;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        if (ContextCompat.checkSelfPermission(MainActivity.this
                , Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    /**
     * 初始化界面
     */
    private void initView() {

        TeamManager teamManager = TeamManager.getInstance(this);
        String teamName = teamManager.getTeamName();
        String teamIntroduce = teamManager.getTeamIntroduce();
        String teamLogoUrl = teamManager.getTeamLogoUrl();

        if (Check.isEmpty(teamName)) {
            mTvTeamName.setText(R.string.TeamName);
        } else {
            mTvTeamName.setText(teamName);
        }

        if (Check.isEmpty(teamIntroduce)) {
            mTvTeamIntroduce.setText(R.string.TeamIntroduceTemp);
        } else {
            String teamIntroduceStr = "团队简介：";
            mTvTeamIntroduce.setText(teamIntroduceStr + teamIntroduce);
        }

        CommonImageLoader.getInstance().displayImage(teamLogoUrl, mIvTeamAvatar);

        mPhotoUrlList = new ArrayList<>();
        initPhotoUrlList();
        mRvPhotoWall.setLayoutManager(new GridLayoutManager(this, 3));
        mRvPhotoWall.setAdapter(new MainAdapter(mPhotoUrlList, MainActivity.this));
    }

    private void initPhotoUrlList() {
        List<PictureBean> pictureBeen = DataSupport.findAll(PictureBean.class);
        if (!Check.isEmpty(pictureBeen)) {
            for (PictureBean pictureBean : pictureBeen) {
                mPhotoUrlList.add(pictureBean.getPicturePath());
            }
            Collections.reverse(mPhotoUrlList);
        } else {
            String imageUrl = "http://upload-images.jianshu.io/upload_images/4334738-118cfc403b6aca43.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240";
            for (int i = 0; i < 15; i++) {
                mPhotoUrlList.add(imageUrl);
            }
        }
    }

    @OnClick({R.id.main_iv_photo, R.id.main_iv_settings, R.id.main_iv_great})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.main_iv_photo:
                doSelectedPicture();
                break;
            case R.id.main_iv_settings:
                doSettings();
                break;
            case R.id.main_iv_great:
                SignatureActivity.startActivity(this);
                break;
        }
    }

    /**
     * 进入选择图片的界面
     */
    private void doSelectedPicture() {
        String title = "输入权限密码";
        DialogFragmentHelper.showPasswordInsertDialog(getSupportFragmentManager(), title, new IDialogResultListener<String>() {
            @Override
            public void onDataResult(String result) {
                String passwordInput = result;
                if (Check.isEmpty(result)){
                    String tips = "密码不能为空";
                    Toast.makeText(MainActivity.this, tips, Toast.LENGTH_SHORT).show();
                }else {
                    boolean isCorrect = confirmPassword(passwordInput);
                    if(isCorrect){
                        selectedPicture();
                    }
                }
            }
        }, false);
    }

    /**
     * 选择图片
     */
    private void selectedPicture() {
        Intent intent = new Intent(this, SelectedPictureActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SELECTED);
    }

    /**
     * 进入设置界面之前的密码判断
     */
    private void doSettings() {
        String title = "输入权限密码";
        DialogFragmentHelper.showPasswordInsertDialog(getSupportFragmentManager(), title, new IDialogResultListener<String>() {
            @Override
            public void onDataResult(String result) {
                String passwordInput = result;
                if (Check.isEmpty(result)){
                    String tips = "密码不能为空";
                    Toast.makeText(MainActivity.this, tips, Toast.LENGTH_SHORT).show();
                }else {
                    boolean isCorrect = confirmPassword(passwordInput);
                    if(isCorrect){
                        SettingsActivity.startActivity(MainActivity.this);
                    }
                }
            }
        }, false);
    }

    /**
     * 判断密码是否正确
     *
     * @param passwordInput 输入的密码
     */
    private boolean confirmPassword(String passwordInput) {
        String commonPassword = PasswordManager.getInstance(MainActivity.this).getCommonPassword();
        String teamPassword = TeamManager.getInstance(MainActivity.this).getTeamPassword();
        if(commonPassword.equals(passwordInput) || teamPassword.equals(passwordInput)){
            return true;
        }else {
            String passwordError = "密码错误";
            Toast.makeText(MainActivity.this, passwordError, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SELECTED){
            startActivity(this);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                }else {
                    Toast.makeText(this, "权限被拒绝", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
