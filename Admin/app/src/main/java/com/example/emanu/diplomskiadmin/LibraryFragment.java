package com.example.emanu.diplomskiadmin;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.emanu.diplomskiadmin.DB.Picture;
import com.example.emanu.diplomskiadmin.DB.RelatedPicture;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by emanu on 8/26/2018.
 */

public class LibraryFragment extends Fragment {

    public static final String PICTURE_EXTRA = "picture_extra";

    private Context mContext;
    private View mView;
    private List<String> mPicturesList;
    private ImageAdapter mImageAdapter;
    private GridView mGridView;
    private FragmentEditListener mListener;

    public interface FragmentEditListener {
        Object onClick(int position);
    }

    @Override
    public void onAttach(Context context) {
        if(context instanceof FragmentEditListener){
            super.onAttach(context);
            mContext = context;
            //prihvatanje listenera tj activity-a koji implementira interfejs
            mListener = (FragmentEditListener) context;
        }
        else {
            throw new ClassCastException(context.toString() + " must implement LibraryFragment.FragmentEditListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_library, container, false);

        //kreiranje image adaptera koji popunjava gridview
        mImageAdapter = new ImageAdapter(mContext, mPicturesList, getScreenHeight(), getScreenWidth());
        mGridView = mView.findViewById(R.id.gridViewLibrary);
        mGridView.setAdapter(mImageAdapter);

        //postavljanje listenera za klik na sliku iz gridview-a
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, EditActivity.class);
                Picture picture = (Picture) mListener.onClick(position);
                intent.putExtra(PICTURE_EXTRA, picture);
                startActivity(intent);
            }
        });


        return mView;
    }

    public void setPicturesList(List<String> mPicturesList) {
        this.mPicturesList = mPicturesList;
    }

    public static int getScreenWidth(){
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(){
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
}
