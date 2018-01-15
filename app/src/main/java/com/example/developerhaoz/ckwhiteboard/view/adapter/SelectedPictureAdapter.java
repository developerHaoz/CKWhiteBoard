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
import com.example.developerhaoz.ckwhiteboard.bean.PictureBean;
import com.example.developerhaoz.ckwhiteboard.common.util.Check;
import com.example.developerhaoz.ckwhiteboard.view.activity.DetailPhotoActivity;
import com.example.developerhaoz.ckwhiteboard.view.widget.CheckView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 选择导入导出图片的 Adapter
 *
 * Created by developerHaoz on 2017/7/17.
 */

public class SelectedPictureAdapter extends RecyclerView.Adapter<SelectedPictureAdapter.SelectedPictureViewHolder> {

    private List<PictureBean> mPictureBeanList;
    private WeakReference<Activity> mActivityWeakReference;
    private OnSelectedCallback mOnSelectedCallback;
    public HashMap<Integer, Boolean> mCheckMap = new HashMap<>();
    private HashMap<Integer, Integer> mReflectMap = new HashMap<>();
    public static List<PictureInt> mCheckIdList = new ArrayList<>();
    public static List<String> mCheckPathList = new ArrayList<>();

    public SelectedPictureAdapter(Activity activity, List<PictureBean> photoUrlList){
        this.mActivityWeakReference = new WeakReference<>(activity);
        this.mPictureBeanList = photoUrlList;
    }

    @Override
    public SelectedPictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_photo_selected_picture, null);
        return new SelectedPictureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SelectedPictureViewHolder holder, final int position) {
        final String photoUrl = mPictureBeanList.get(position).getPicturePath();
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
                DetailPhotoActivity.startActivity(mPictureBeanList.get(position).getPicturePath(), activity);
            }
        });

        holder.mIvPhoto.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        holder.mCvCheckable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureBean pictureBean = mPictureBeanList.get(position);
                PictureInt pictureInt = new PictureInt(pictureBean.getId());
                if (!mCheckMap.containsKey(position)){
                    mCheckMap.put(position, true);
                    mCheckIdList.add(pictureInt);
                    mReflectMap.put(position, mCheckIdList.size() - 1);
                    mCheckPathList.add(pictureBean.getPicturePath());
                    holder.mCvCheckable.setChecked(true);
                }else{
                    mCheckMap.remove(position);
                    try{
                        mCheckIdList.remove(mReflectMap.get(position));
                        mCheckPathList.remove(position);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    holder.mCvCheckable.setChecked(false);
                }
            }
        });
    }

    public void setOnLongListener(OnSelectedCallback callback){
        this.mOnSelectedCallback = callback;
    }

    @Override
    public int getItemCount() {
        if(!Check.isEmpty(mPictureBeanList)){
            return mPictureBeanList.size();
        }
        return 0;
    }

    static class SelectedPictureViewHolder extends RecyclerView.ViewHolder{

        private ImageView mIvPhoto;
        private CheckView mCvCheckable;

        public SelectedPictureViewHolder(View itemView) {
            super(itemView);
            mIvPhoto = (ImageView) itemView.findViewById(R.id.selected_picture_iv_photo);
            mCvCheckable = (CheckView) itemView.findViewById(R.id.selected_picture_cv_check);
        }
    }

    public interface OnSelectedCallback{
        void onLongClick();
    }

   public static class PictureInt{
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public PictureInt(int id) {
            this.id = id;
        }

        int id;
    }
}
