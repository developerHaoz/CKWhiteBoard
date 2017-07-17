package com.example.developerhaoz.ckwhiteboard.common.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 将当前界面保存为图片的工具类
 *
 * Created by developerHaoz on 2017/7/14.
 */

public class SavePictureUtil {

    private static final String PATH_SAVE_PICUTRE = "/signaturePath/";

    /**
     * 将一个 Bitmap 保存在指定的路径中
     *
     * @param bitmap
     * @param filePath 指定的路径
     */
    public static void savePicByPNG(Bitmap bitmap, String filePath) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(filePath);
            if (null != fos) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String savePicture(View view){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + PATH_SAVE_PICUTRE
                + System.currentTimeMillis() + ".png";
        if(!new File(path).exists()){
            new File(path).getParentFile().mkdir();
        }
        SavePictureUtil.savePicByPNG(getBitmap(view), path);
        return path;
    }

    /**
     * 将一个 View 转换成 Bitmap
     *
     * @param view
     * @return
     */
    private static Bitmap getBitmap(View view){
        Bitmap bitmap = Bitmap.createBitmap(view.getLayoutParams().width, view.getLayoutParams().height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        view.draw(canvas);
        return bitmap;
    }
}

