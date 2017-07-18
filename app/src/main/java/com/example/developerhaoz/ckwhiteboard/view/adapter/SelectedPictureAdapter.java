package com.example.developerhaoz.ckwhiteboard.view.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.developerhaoz.ckwhiteboard.R;
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
    private OnSelectedCallback mOnSelectedCallback;

    /**
     * 标识 ImageButton 是否被勾选
     */
    private boolean isCheck;

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
    public void onBindViewHolder(final SelectedPictureViewHolder holder, final int position) {
        final String photoUrl = mPhotoUrlList.get(position);
        Glide.with(mActivityWeakReference.get())
                .load(photoUrl)
                .asBitmap()
                .override(208, 208)
                .error(R.drawable.selected_image)
                .placeholder(R.drawable.selected_image)
                .dontAnimate()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mIvPhoto);

        holder.mIvPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Activity activity = mActivityWeakReference.get();
                DetailPhotoActivity.startActivity(mPhotoUrlList.get(position), activity);
            }
        });

        holder.mIbCheckable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCheck) {
                    holder.mIbCheckable.setImageResource(R.drawable.pictures_selected);
                    isCheck = true;
                }else {
                    holder.mIbCheckable.setImageResource(R.drawable.picture_unselected);
                    isCheck = false;
                }
            }
        });

        holder.mIvPhoto.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.mIbCheckable.setVisibility(View.VISIBLE);
//                mOnSelectedCallback.onLongClick();
                return true;
            }
        });
    }

    public void setOnLongListener(OnSelectedCallback callback){
        this.mOnSelectedCallback = callback;
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
        private ImageButton mIbCheckable;

        public SelectedPictureViewHolder(View itemView) {
            super(itemView);
            mIvPhoto = (ImageView) itemView.findViewById(R.id.selected_picture_iv_photo);
            mIbCheckable = (ImageButton) itemView.findViewById(R.id.selected_picture_ib_checkable);
        }
    }

    public interface OnSelectedCallback{
        void onLongClick();
    }
}
