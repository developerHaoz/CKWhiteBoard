package com.example.developerhaoz.ckwhiteboard.view.activity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.example.developerhaoz.ckwhiteboard.view.adapter.SelectedPictureAdapter;
import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.partition.Partition;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import org.litepal.crud.DataSupport;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
                    UsbFile[] files = root.listFiles();
                    Toast.makeText(this, "1111", Toast.LENGTH_SHORT).show();
                    // 在 U 盘中创建文件夹
                    UsbFile newDir = root.createDirectory("her");

                    Toast.makeText(this, "2222", Toast.LENGTH_SHORT).show();
                    // 以下方法能够将 test.txt 写入 U 盘
                    UsbFile file = newDir.createFile("test.jpg");
                    List<PictureBean> pictureBeen = DataSupport.findAll(PictureBean.class);
                    Bitmap bitmap = BitmapFactory.decodeFile(pictureBeen.get(0).getPicturePath());
                    try {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.write(out.toByteArray());
                        out.flush();
                        out.close();
//                        OutputStream os = new UsbFileOutputStream(file);
//                        FileOutputStream fos = (FileOutputStream) os;
//                        if (null != fos) {
//                            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
//                            fos.flush();
//                            fos.close();
//                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

//                    OutputStream os  = new UsbFileOutputStream(file);
//                    os.write("hello worldss".getBytes());
//                    os.close();
//                    List<PictureBean> pictureBeen = DataSupport.findAll(PictureBean.class);
//
//                    if (!Check.isEmpty(pictureBeen)) {
//                        for (PictureBean pictureBean : pictureBeen) {
//                            mPhotoUrlList.add(pictureBean.getPicturePath());
//                        }
//                        Collections.reverse(mPhotoUrlList);
//                    } else {
//                        showToastMsg("当前没有图片");
//                    }
//                    Bitmap bitmap = BitmapFactory.decodeFile(pictureBeen.get(0).getPicturePath());
//                    SavePictureUtil.savePicByPNG(bitmap, "/storage/emulated/0/hero5555");
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

    public static List<String> getAllExterSdcardPath() {
        List<String> SdList = new ArrayList<String>();

        String firstPath = Environment.getExternalStorageDirectory().getPath();

        try {
            Runtime runtime = Runtime.getRuntime();
            // 运行mount命令，获取命令的输出，得到系统中挂载的所有目录
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                Log.d("", line);
                // 将常见的linux分区过滤掉

                if (line.contains("proc") || line.contains("tmpfs") || line.contains("media") || line.contains("asec") || line.contains("secure") || line.contains("system") || line.contains("cache")
                        || line.contains("sys") || line.contains("data") || line.contains("shell") || line.contains("root") || line.contains("acct") || line.contains("misc") || line.contains("obb")) {
                    continue;
                }

                // 下面这些分区是我们需要的
                if (line.contains("fat") || line.contains("fuse") || (line.contains("ntfs"))) {
                    // 将mount命令获取的列表分割，items[0]为设备名，items[1]为挂载路径
                    String items[] = line.split(" ");
                    if (items != null && items.length > 1) {
                        String path = items[1].toLowerCase(Locale.getDefault());
                        // 添加一些判断，确保是sd卡，如果是otg等挂载方式，可以具体分析并添加判断条件
                        if (path != null && !SdList.contains(path) && path.contains("sd"))
                            SdList.add(items[1]);
                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (!SdList.contains(firstPath)) {
            SdList.add(firstPath);
        }

        return SdList;
    }


}
