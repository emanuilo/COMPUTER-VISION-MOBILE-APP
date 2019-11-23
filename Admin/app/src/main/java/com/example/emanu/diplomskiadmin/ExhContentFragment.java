package com.example.emanu.diplomskiadmin;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.emanu.diplomskiadmin.DB.Exhibition;
import com.example.emanu.diplomskiadmin.DB.Picture;

import java.util.List;

/**
 * Created by emanu on 9/7/2018.
 */

public class ExhContentFragment extends Fragment {

    private View mView;
    private Context mContext;
    private GridView mGridView;
    private SelectableImageAdapter mImageAdapter;
    private List<Picture> mAllPicturesList;
    protected Exhibition mCurrentExhibition;

    @Override
    public void onAttach(Context context) {
            super.onAttach(context);
            mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_library, container, false);

        //kreiranje image adaptera koji popunjava gridview
        mImageAdapter = new SelectableImageAdapter(mContext, mAllPicturesList, mCurrentExhibition.getPictures(), getScreenHeight(), getScreenWidth());
        mGridView = mView.findViewById(R.id.gridViewLibrary);
        mGridView.setAdapter(mImageAdapter);

        //postavljanje listenera za klik na sliku iz gridview-a
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView imageView = (ImageView) view;
                Picture picture = mAllPicturesList.get(position);
                ColorFilter colorFilter = imageView.getColorFilter();

                if(colorFilter == null){ //colorFilter == null => not selected
                    imageView.setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
                    mCurrentExhibition.getPictures().add(picture);
                }
                else{
                    imageView.setColorFilter(null);
                    removeFromExhList(picture.getId());
                }

                mImageAdapter.notifyDataSetChanged();
                mGridView.invalidateViews();
            }
        });


        return mView;
    }

    public void removeFromExhList(int id){
        List<Picture> exhPicturesList = mCurrentExhibition.getPictures();
        for(int i = 0; i < exhPicturesList.size(); i++){
            if(exhPicturesList.get(i).getId() == id){
                exhPicturesList.remove(i);
                return;
            }
        }
    }

    public void setmAllPicturesList(List<Picture> mAllPicturesList) {
        this.mAllPicturesList = mAllPicturesList;
    }

    public void setmCurrentExhibition(Exhibition mCurrentExhibition) {
        this.mCurrentExhibition = mCurrentExhibition;
    }

    public static int getScreenWidth(){
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(){
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
}
