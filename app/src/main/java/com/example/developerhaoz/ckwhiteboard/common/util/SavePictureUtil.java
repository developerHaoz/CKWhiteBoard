package com.example.developerhaoz.ckwhiteboard.common.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.developerhaoz.ckwhiteboard.bean.PictureBean;
import com.example.developerhaoz.ckwhiteboard.common.WhiteBoardApplication;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileOutputStream;
import org.litepal.crud.DataSupport;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

/**
 * 将当前界面保存为图片的工具类
 *
 * Created by developerHaoz on 2017/7/14.
 */

public class SavePictureUtil {

    private static final String PATH_SAVE_PICUTRE = "/signaturePath/";
    private DialogFragment mDialogFragment;

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

    /**
     * 将图片保存在 U 盘中
     *
     * @param newDir 保存的文件夹名称
     * @throws IOException
     */
    public static void savePictureToUsb(AppCompatActivity context, UsbFile newDir) throws IOException{
        List<PictureBean> pictureBeen = DataSupport.findAll(PictureBean.class);
        for (int i = 0; i < pictureBeen.size(); i++) {
            UsbFile file = newDir.createFile("ck" + System.currentTimeMillis() + ".jpg");
            Bitmap bitmap = BitmapFactory.decodeFile(getRealPathFromUri(WhiteBoardApplication.getInstance(), Uri.parse(pictureBeen.get(i).getPicturePath())));
            OutputStream outputStream = new UsbFileOutputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            outputStream.write(out.toByteArray());
            out.flush();
            out.close();
            outputStream.flush();
            outputStream.close();
            file.flush();
            file.close();
        }
    }

    private static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            if (contentUri == null) {
                return "";
            }
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static String getExternalStorageDirectory(){
        String dir = new String();
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                // System.out.println(line);
                if (line.contains("secure")) continue;
                if (line.contains("asec")) continue;

                if (line.contains("fat")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        dir = dir.concat(columns[1] );
                        break;
                    }
                } else if (line.contains("fuse")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        dir = dir.concat(columns[1]);
                        break;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dir;
    }


}

