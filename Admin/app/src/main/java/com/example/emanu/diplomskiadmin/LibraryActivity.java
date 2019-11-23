package com.example.emanu.diplomskiadmin;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.emanu.diplomskiadmin.DB.Picture;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LibraryActivity extends AppCompatActivity implements LibraryFragment.FragmentEditListener{

    public static final String URL = "http://192.168.0.18:8080/picture/";
    public static final String DOWNLOADING_FAIL = "Downloading failed!";
    private static final String ACTION_BAR_TITLE = "Library";

    private LibraryFragment mLibraryFragment;
    private LoadingFragment mLoadingFragment;
    private List<Picture> mPicturesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        //postavljanje custom toolbar-a
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mLibraryFragment = new LibraryFragment();
        mLoadingFragment = new LoadingFragment();
        mLoadingFragment.setLoadingText(LoadingFragment.LOADING);

        //postavljanje fragmenta za loading
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentFrameLibrary, mLoadingFragment);
        fragmentTransaction.commit();

        loadImages();

        changeActivityLabelFont();
    }

    public void loadImages(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest arrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL + "pictures",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        String json = response.toString();
                        Type type = new TypeToken<List<Picture>>() {}.getType();

                        mPicturesList = new Gson().fromJson(json, type);
                        onLoad();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showAlertDialog();
                    }
                }
        );

        requestQueue.add(arrayRequest);
    }

    public void onLoad(){
        //zamena fragmenata
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentFrameLibrary, mLibraryFragment);
        fragmentTransaction.commit();

        //pravljenje liste bytearraya od liste Picture objekata
        List<String> listURLPictures = extractURLFromObjects(mPicturesList);
        mLibraryFragment.setPicturesList(listURLPictures);

    }

    public List<String> extractURLFromObjects(List<Picture> listPictures){
        List<String> listURLPictures = new ArrayList<>();
        for(Picture picture : listPictures){
            listURLPictures.add(picture.getPictureBlob());
        }

        return listURLPictures;
    }

    public void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LibraryActivity.this);
        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //pokreni download ponovo
                loadImages();
            }
        });
        builder.setNegativeButton("Abort", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //vracanje na home activity
                Intent intent = new Intent(LibraryActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setMessage(DOWNLOADING_FAIL);
        builder.create().show();
    }

    public void changeActivityLabelFont(){
        Typeface typeface = ResourcesCompat.getFont(this, R.font.montserrat_medium);

        TextView textView = new TextView(this);
        textView.setText(ACTION_BAR_TITLE);
        textView.setTypeface(typeface);
        textView.setTextSize(19);
        textView.setTextColor(Color.WHITE);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(textView);
    }

    @Override
    public Object onClick(int position) {
        return mPicturesList.get(position);
    }

    public void onClickBack(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
