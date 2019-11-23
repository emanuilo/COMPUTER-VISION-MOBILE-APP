package com.example.emanu.diplomskiadmin;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by emanu on 8/25/2018.
 */

public class LoadingFragment extends Fragment {
    public static final String LOADING = "Loading...";
    public static final String UPLOADING = "Uploading...";

    private String mText;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading, container, false);

        TextView textView = view.findViewById(R.id.textViewLoading);
        textView.setText(mText);

        return view;
    }

    public void setLoadingText(String text){
        mText = text;
    }
}
