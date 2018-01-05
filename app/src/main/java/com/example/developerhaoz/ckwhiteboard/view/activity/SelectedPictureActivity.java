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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.developerhaoz.ckwhiteboard.R;
import com.example.developerhaoz.ckwhiteboard.bean.PictureBean;
import com.example.developerhaoz.ckwhiteboard.common.util.SavePictureUtil;
import com.example.developerhaoz.ckwhiteboard.view.adapter.SelectedPictureAdapter;
import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.partition.Partition;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import org.litepal.crud.DataSupport;

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
    private static final String TAG = "Selected3333";

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
                try {
                    UsbFile root = currentFs.getRootDirectory();
                    // 在 U 盘中创建文件夹
                    UsbFile newDir = root.createDirectory("heroHaoz");
                    SavePictureUtil.savePictureToUsb(newDir);
                    Toast.makeText(this, "写入成功", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    /**
     * OTG 广播注册
     */
    private void registerUDiskReceiver() {
        // 监听otg插入 拔出
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
            showToastMsg("请插入可用的U盘");
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
                            showToastMsg("没有插入U盘");
                        }
                    } else {
                        showToastMsg("未获取到U盘权限");
                    }
                    break;
                // 接收到 U 盘设备插入广播
                case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                    UsbDevice device_add = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device_add != null) {
                        //接收到U盘插入广播，尝试读取U盘设备数据
                        redUDiskDevsList();
                    }
                    gets(Uri.parse("ss"));
                    break;
                // 接收到U盘设设备拔出广播
                case UsbManager.ACTION_USB_DEVICE_DETACHED:
                    showToastMsg("U盘已拔出");
                    break;
                case Intent.ACTION_MEDIA_MOUNTED:
                    String usbPath = intent.getData().getPath();
                    Log.d(TAG, "mounted" + usbPath);
                    break;
            }
        }
    };

    private void gets(Uri uri) {

    }

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

}
