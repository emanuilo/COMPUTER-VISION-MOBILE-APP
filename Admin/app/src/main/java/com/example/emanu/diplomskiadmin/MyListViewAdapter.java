package com.example.emanu.diplomskiadmin;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.emanu.diplomskiadmin.DB.Exhibition;

import java.util.List;

/**
 * Created by emanu on 9/7/2018.
 */

public class MyListViewAdapter extends BaseAdapter {

    private List<Exhibition> mExhibitionList;
    private Context mContext;
    private Typeface mTypeface;
    protected int exhibitionSetOn = -1;

    public MyListViewAdapter(List<Exhibition> mExhibitionList, Context mContext) {
        this.mExhibitionList = mExhibitionList;
        this.mContext = mContext;
        mTypeface = ResourcesCompat.getFont(mContext, R.font.montserrat_regular);
    }

    @Override
    public int getCount() {
        return mExhibitionList.size();
    }

    @Override
    public Object getItem(int position) {
        return mExhibitionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            textView = new TextView(mContext);
            textView.setTypeface(mTypeface);
            textView.setTextSize(convertDpToPixel(8, mContext));
            textView.setTextColor(Color.WHITE);
            textView.setPadding(convertDpToPixel(16, mContext), convertDpToPixel(13, mContext), 0, convertDpToPixel(13, mContext));
        } else {
            textView = (TextView) convertView;
            textView.setTextColor(Color.WHITE);
        }

        textView.setText(mExhibitionList.get(position).getName());
        if(mExhibitionList.get(position).isSet()) { //ako je izlozba u toku promeni boju teksta
            textView.setTextColor(ResourcesCompat.getColor(mContext.getResources(), R.color.blueFontColor, null));
            exhibitionSetOn = position;
        }
        return textView;
    }

    public static int convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int px = (int) (dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}
