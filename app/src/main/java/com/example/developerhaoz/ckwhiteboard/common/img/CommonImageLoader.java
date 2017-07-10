package com.example.developerhaoz.ckwhiteboard.common.img;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.developerhaoz.ckwhiteboard.R;

/**
 * 通用的图片加载类
 *
 * Created by developerHaoz on 2017/7/10.
 */

public class CommonImageLoader {

    private static volatile CommonImageLoader INSTANCE;

    /**
     * 使用单例模式，节省资源
     * @return
     */
    public static CommonImageLoader getInstance(){
        if(INSTANCE == null){
            synchronized (CommonImageLoader.class){
                if (INSTANCE == null){
                    INSTANCE = new CommonImageLoader();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 用于单个图片的加载
     *
     * @param url 图片的 url
     * @param imageView 显示图片的控件
     */
    public void displayImage(String url, ImageView imageView){
        Glide.with(imageView.getContext())
                .load(url)
                .error(R.mipmap.ic_launcher_round)
                .placeholder(R.mipmap.ic_launcher_round)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

//    /**
//     * 传入图片的 Url，并将 Bitmap 进行回调
//     *
//     * @param context
//     * @param url
//     * @param listener
//     */
//    public void loadImage(Context context, String url, final GetImageListener listener){
//        GlideApp.with(context)
//                .asBitmap()
//                .load(url)
//                .into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
//                        listener.onSuccess(bitmap);
//                    }
//
//                    @Override
//                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
//                        super.onLoadFailed(errorDrawable);
//                        listener.onError(errorDrawable);
//                    }
//                });
//    }

//    /**
//     * 传入图片的 Url，并将 Bitmap 进行回调
//     *
//     * @param fragment
//     * @param url
//     * @param listener
//     */
//    public void loadImage(Fragment fragment, String url, final GetImageListener listener){
//        GlideApp.with(fragment)
//                .asBitmap()
//                .load(url)
//                .into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
//                        listener.onSuccess(bitmap);
//                    }
//
//                    @Override
//                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
//                        super.onLoadFailed(errorDrawable);
//                        listener.onError(errorDrawable);
//                    }
//                });
//    }

    public interface GetImageListener{
        void onSuccess(Bitmap bitmap);
        void onError(Drawable errorDrawable);
    }
}





















