package com.example.developerhaoz.ckwhiteboard.view.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.developerhaoz.ckwhiteboard.R;
import com.example.developerhaoz.ckwhiteboard.common.img.CommonImageLoader;
import com.example.developerhaoz.ckwhiteboard.common.util.Check;
import com.example.developerhaoz.ckwhiteboard.view.activity.DetailPhotoActivity;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 选择导入导出图片的 Adapter
 *
 * Created by developerHaoz on 2017/7/17.
 */

public class SelectedPictureAdapter extends RecyclerView.Adapter<SelectedPictureAdapter.SelectedPictureViewHolder> {

    private List<String> mPhotoUrlList;
    private WeakReference<Activity> mActivityWeakReference;

    public SelectedPictureAdapter(Activity activity, List<String> photoUrlList){
        this.mActivityWeakReference = new WeakReference<>(activity);
        this.mPhotoUrlList = photoUrlList;
    }

    @Override
    public SelectedPictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_photo_selected_picture, null);
        return new SelectedPictureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SelectedPictureViewHolder holder, final int position) {
        final String photoUrl = mPhotoUrlList.get(position);
        CommonImageLoader.getInstance().displayImage(photoUrl, holder.mIvPhoto);
        holder.mIvPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Activity activity = mActivityWeakReference.get();
                DetailPhotoActivity.startActivity(mPhotoUrlList.get(position), activity);
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

    static class SelectedPictureViewHolder extends RecyclerView.ViewHolder{

        private ImageView mIvPhoto;

         SelectedPictureViewHolder(View itemView) {
            super(itemView);
            mIvPhoto = (ImageView) itemView.findViewById(R.id.item_selected_picture_photo);
        }
    }
}
