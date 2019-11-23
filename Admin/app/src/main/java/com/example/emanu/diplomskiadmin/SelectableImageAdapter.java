package com.example.emanu.diplomskiadmin;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.emanu.diplomskiadmin.DB.Picture;

import java.util.List;

/**
 * Created by emanu on 9/7/2018.
 */

public class SelectableImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<Picture> mImagesList;
    private List<Picture> mExhibitionPicturesList;
    private int height;
    private int width;

    public SelectableImageAdapter(Context mContext, List<Picture> mImagesList, List<Picture> mExhibitionPicturesList, int height, int width) {
        this.mContext = mContext;
        this.mImagesList = mImagesList;
        this.mExhibitionPicturesList = mExhibitionPicturesList;
        this.height = height;
        this.width = width;
    }

    @Override
    public int getCount() {
        if(mImagesList != null)
            return mImagesList.size();
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        Picture currentPicture = mImagesList.get(position);
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(width / 3, width / 3));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            //highlight pictures set on exhibition
            for (Picture exhibitionPicture : mExhibitionPicturesList) {
                if(currentPicture.getId().equals(exhibitionPicture.getId()))
                    imageView.setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
            }
        } else {
            imageView = (ImageView) convertView;

//            for (Picture exhibitionPicture : mExhibitionPicturesList) {
//                if(currentPicture.getId().equals(exhibitionPicture.getId()))
//                    imageView.setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
//            }
        }

        //Glide biblioteka za brzo ucitavanje i prikaz slika
        Glide.with(mContext)
                .load(currentPicture.getPictureBlob()) // Uri/String of the picture
                .into(imageView);

        return imageView;
    }
}
