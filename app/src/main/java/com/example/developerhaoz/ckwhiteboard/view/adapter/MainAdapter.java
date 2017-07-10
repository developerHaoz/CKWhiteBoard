package com.example.developerhaoz.ckwhiteboard.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.developerhaoz.ckwhiteboard.R;
import com.example.developerhaoz.ckwhiteboard.common.img.CommonImageLoader;
import com.example.developerhaoz.ckwhiteboard.common.util.Check;

import java.util.List;

/**
 * Created by developerHaoz on 2017/7/10.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

    private List<String> mPhotoUrlList;

    public MainAdapter(List<String> photoUrlList) {
        mPhotoUrlList = photoUrlList;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, null);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        CommonImageLoader.getInstance().displayImage(mPhotoUrlList.get(position), holder.mIvPhoto);
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

        public MainViewHolder(View itemView) {
            super(itemView);
            mIvPhoto = (ImageView) itemView.findViewById(R.id.item_main_photo);
        }
    }
}
