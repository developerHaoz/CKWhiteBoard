package com.example.developerhaoz.ckwhiteboard.view.activity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.developerhaoz.ckwhiteboard.R;
import com.example.developerhaoz.ckwhiteboard.bean.PictureBean;
import com.example.developerhaoz.ckwhiteboard.common.img.dialog.CommonDialogFragment;
import com.example.developerhaoz.ckwhiteboard.common.img.dialog.DialogFragmentHelper;
import com.example.developerhaoz.ckwhiteboard.common.img.dialog.IDialogResultListener;
import com.example.developerhaoz.ckwhiteboard.common.util.SavePictureUtil;
import com.example.developerhaoz.ckwhiteboard.common.util.UsbFileEvent;
import com.example.developerhaoz.ckwhiteboard.view.adapter.SelectedPictureAdapter;
import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.partition.Partition;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 用于展示签名图片墙的 Activity
 * <p>
 * Created by developerHaoz on 2017/7/17.
 */

public class SelectedPictureActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CHOOSE = 1;

    @BindView(R.id.selected_picture_iv_back)
    ImageView mIvBack;
    @BindView(R.id.selected_picture_iv_import)
    ImageView mIvImportPicture;
    @BindView(R.id.selected_picture_iv_export)
    ImageView mIvExportPicture;
    @BindView(R.id.selected_picture_rv_show_photo_wall)
    RecyclerView mRvShowPhotoWall;
    @BindView(R.id.selected_picture_rl)
    RelativeLayout mRl;
    @BindView(R.id.progressBar)
    ProgressBar mProgress;

    private FileSystem currentFs;
    /**
     * 当前 U 盘所在文件目录
     */
    private UsbFile cFolder;
    /**
     * 当前处接 U 盘列表
     */
    private UsbMassStorageDevice[] storageDevices;
    /**
     * 保存所有图片的 path
     */
    private List<String> mPhotoUrlList;

    /**
     * 自定义的广播
     */
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private DialogFragment mDialogFragment;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SelectedPictureActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_picture);
        ButterKnife.bind(this);
        registerUDiskReceiver();
        /**
         * 数据库用的是郭霖的 LitePal，以下是获取所有图片实体类的方法
         */
        List<PictureBean> pictureBeanList = DataSupport.findAll(PictureBean.class);
        List<String> photoUrlList = new ArrayList<>();
        for (int i = 0; i < pictureBeanList.size(); i++) {
            photoUrlList.add(pictureBeanList.get(i).getPicturePath());
        }
        initPhotoWall(photoUrlList);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    private void initPhotoWall(List<String> photoUrlList) {
        final SelectedPictureAdapter adapter = new SelectedPictureAdapter(this, photoUrlList);
        mRvShowPhotoWall.setLayoutManager(new GridLayoutManager(this, 4));
        adapter.setOnLongListener(new SelectedPictureAdapter.OnSelectedCallback() {
            @Override
            public void onLongClick() {
                adapter.notifyDataSetChanged();
            }
        });
        mRvShowPhotoWall.setAdapter(adapter);
    }

    @OnClick({R.id.selected_picture_iv_back, R.id.selected_picture_iv_import, R.id.selected_picture_iv_export})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.selected_picture_iv_back:
                finish();
                break;
            case R.id.selected_picture_iv_import:
                Matisse.from(SelectedPictureActivity.this)
                        .choose(MimeType.allOf())
                        .countable(true)
                        .maxSelectable(20)
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .imageEngine(new GlideEngine())
                        .forResult(REQUEST_CODE_CHOOSE);
                break;
            case R.id.selected_picture_iv_export:
                //设备管理器
                UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
                //获取U盘存储设备
                storageDevices = UsbMassStorageDevice.getMassStorageDevices(this);
                try {
                    if (storageDevices.length == 0) {
                        showToastMsg("请插入 U 盘");
                    } else {
                        final UsbFile root = currentFs.getRootDirectory();
                        DialogFragmentHelper.showConfirmDialog(getSupportFragmentManager(), "是否将图片导出到 U 盘",
                                new IDialogResultListener<Integer>() {
                                    @Override
                                    public void onDataResult(Integer result) {
                                        exportPictureToUsb(root);
                                    }
                                }, true, new CommonDialogFragment.OnDialogCancelListener() {
                                    @Override
                                    public void onCancel() {
                                        showToastMsg("导出图片已取消");
                                    }
                                });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private void exportPictureToUsb(final UsbFile root){
        mDialogFragment = DialogFragmentHelper.showProgress(getSupportFragmentManager(), "正在导出图片...", true,
                new CommonDialogFragment.OnDialogCancelListener() {
                    @Override
                    public void onCancel() {
                        showToastMsg("导出图片已取消");
                    }
                });
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final UsbFile newDir = root.createDirectory("CK" + System.currentTimeMillis());
                    SavePictureUtil.savePictureToUsb(SelectedPictureActivity.this, newDir);
                    EventBus.getDefault().post(new UsbFileEvent());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * OTG 广播注册
     */
    private void registerUDiskReceiver() {
        // 监听 otg 插入 拔出
        IntentFilter usbDeviceStateFilter = new IntentFilter();
        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        usbDeviceStateFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        registerReceiver(mOtgReceiver, usbDeviceStateFilter);
        // 注册监听自定义广播
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mOtgReceiver, filter);
    }

    private void readDevice(UsbMassStorageDevice device) {
        try {
            // 初始化
            device.init();
            // 设备分区
            Partition partition = device.getPartitions().get(0);
            // 文件系统
            currentFs = partition.getFileSystem();
            // 可以获取到设备的标识
            currentFs.getVolumeLabel();
            //设置当前文件对象为根目录
            cFolder = currentFs.getRootDirectory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * U 盘设备读取
     */
    private void redUDiskDevsList() {
        //设备管理器
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        //获取U盘存储设备
        storageDevices = UsbMassStorageDevice.getMassStorageDevices(this);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        //一般手机只有1个OTG插口
        for (UsbMassStorageDevice device : storageDevices) {
            //读取设备是否有权限
            if (usbManager.hasPermission(device.getUsbDevice())) {
                readDevice(device);
            } else {
                //没有权限，进行申请
                usbManager.requestPermission(device.getUsbDevice(), pendingIntent);
            }
        }
        if (storageDevices.length == 0) {
            showToastMsg("请插入可用的 U 盘");
        }
    }

    private void showToastMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * OTG 广播，监听 U 盘的插入及拔出
     */
    private BroadcastReceiver mOtgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                //接受到自定义广播
                case ACTION_USB_PERMISSION:
                    UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    //允许权限申请
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (usbDevice != null) {
                            //用户已授权，可以进行读取操作
                            readDevice(getUsbMass(usbDevice));
                        } else {
                            showToastMsg("没有插入 U 盘");
                        }
                    } else {
                        showToastMsg("未获取到 U 盘权限");
                    }
                    break;
                // 接收到 U 盘设备插入广播
                case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                    showToastMsg("U 盘已插入");
                    UsbDevice device_add = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device_add != null) {
                        //接收到 U 盘插入广播，尝试读取 U 盘设备数据
                        redUDiskDevsList();
                    }
                    break;
                // 接收到 U 盘设设备拔出广播
                case UsbManager.ACTION_USB_DEVICE_DETACHED:
                    showToastMsg("U 盘已拔出");
                    break;
            }
        }
    };

    private UsbMassStorageDevice getUsbMass(UsbDevice usbDevice) {
        for (UsbMassStorageDevice device : storageDevices) {
            if (usbDevice.equals(device.getUsbDevice())) {
                return device;
            }
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mOtgReceiver);
        EventBus.getDefault().unregister(this);
    }

    @Override
    @NonNull
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CHOOSE) {
            if (data != null) {
                List<Uri> imageUri = Matisse.obtainResult(data);
                List<PictureBean> pictureBeanList = new ArrayList<>();
                for (int i = 0; i < imageUri.size(); i++) {
                    PictureBean pictureBean = new PictureBean();
                    pictureBean.setPicturePath(String.valueOf(imageUri.get(i)));
                    pictureBeanList.add(pictureBean);
                }
                DataSupport.saveAll(pictureBeanList);
                startActivity(this);
                finish();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UsbFileEvent event) {
        if (mDialogFragment != null) {
            mDialogFragment.dismiss();
            showToastMsg("图片导入成功");
        }
    }

    ;

}
