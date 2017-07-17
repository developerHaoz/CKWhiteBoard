package com.example.developerhaoz.ckwhiteboard.view.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.developerhaoz.ckwhiteboard.R;
import com.example.developerhaoz.ckwhiteboard.common.img.CommonImageLoader;
import com.example.developerhaoz.ckwhiteboard.common.util.Check;
import com.example.developerhaoz.ckwhiteboard.view.activity.DetailPhotoActivity;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 主界面的 Adapter
 *
 * Created by developerHaoz on 2017/7/10.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

    private List<String> mPhotoUrlList;
    private WeakReference<Activity> mWeakReference;

    public MainAdapter(List<String> photoUrlList, Activity activity) {
        mPhotoUrlList = photoUrlList;
        mWeakReference = new WeakReference<>(activity);
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_photo, null);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        final String imageUrl = mPhotoUrlList.get(position);
        CommonImageLoader.getInstance().displayImage(imageUrl, holder.mIvPhoto);
        Glide.with(mWeakReference.get())
                .load(imageUrl)
                .asBitmap()
                .override(248, 248)
                .error(R.drawable.selected_image)
                .placeholder(R.drawable.selected_image)
                .dontAnimate()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mIvPhoto);

        holder.mIvPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = mWeakReference.get();
                DetailPhotoActivity.startActivity(imageUrl, activity);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(!Check.isEmpty(mPhotoUrlList)){
            return mPhotoUrlList.size();
        }
        return 0;
    }

    static class MainViewHolder extends RecyclerView.ViewHolder{

        private ImageView mIvPhoto;

         MainViewHolder(View itemView) {
            super(itemView);
            mIvPhoto = (ImageView) itemView.findViewById(R.id.item_main_photo);
        }
    }

}
