package com.example.emanu.diplomskiclient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.emanu.diplomskiclient.DB.RelatedPicture;

import java.util.List;

/**
 * Created by emanu on 8/18/2018.
 */

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private List<RelatedPicture> mRelatedPictures;
    private int height;
    private int width;

    public ImageAdapter(Context mContext, List<RelatedPicture> mRelatedPictures, int height, int width) {
        this.mContext = mContext;
        this.mRelatedPictures = mRelatedPictures;
        this.height = height;
        this.width = width;
    }

    @Override
    public int getCount() {
        if(mRelatedPictures != null)
            return mRelatedPictures.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(width / 2, width / 2));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            imageView.setPadding(8, 8, 8, 8);

        } else {
            imageView = (ImageView) convertView;
        }

//        byte[] byteArray = mRelatedPictures.get(position).getPictureBlob();
//        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//        imageView.setImageBitmap(bitmap);

        Glide.with(mContext)
                .load(mRelatedPictures.get(position).getPictureBlob()) // Uri/String of the picture
                .into(imageView);

        return imageView;
    }
}
