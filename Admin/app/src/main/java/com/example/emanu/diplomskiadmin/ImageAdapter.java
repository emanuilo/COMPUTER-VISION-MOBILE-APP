package com.example.emanu.diplomskiadmin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.emanu.diplomskiadmin.DB.RelatedPicture;

import java.util.List;

/**
 * Created by emanu on 8/21/2018.
 */

public class ImageAdapter<T> extends BaseAdapter {

    private Context mContext;
    private List<T> mImagesList;
    private int height;
    private int width;

    public ImageAdapter(Context mContext, List<T> mImagesList, int height, int width) {
        this.mContext = mContext;
        this.mImagesList = mImagesList;
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
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(width / 3, width / 3));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        //Glide biblioteka za brzo ucitavanje i prikaz slika

        Glide.with(mContext)
                .load(mImagesList.get(position)) // Uri/String of the picture
                .into(imageView);

        return imageView;
    }
}
