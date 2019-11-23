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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.emanu.diplomskiadmin.DB.Exhibition;
import com.example.emanu.diplomskiadmin.DB.Picture;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;


public class ExhContentActivity extends AppCompatActivity {

    private ExhContentFragment mExhContentFragment;
    private LoadingFragment mLoadingFragment;
    private Exhibition mCurrentExhibition;
    private List<Picture> mAllPicturesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exh_content);

        //postavljanje custom toolbar-a
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mCurrentExhibition = ExhibitionsActivity.sSelectedExhibition;
        mExhContentFragment = new ExhContentFragment();
        mLoadingFragment = new LoadingFragment();
        mLoadingFragment.setLoadingText(LoadingFragment.LOADING);

        //postavljanje fragmenta za cuvanje slike
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentFrameExhibition, mLoadingFragment);
        fragmentTransaction.commit();

        //load all images in gallery
        //those images that are set to the exhibition, highlight them

        restAllPictures();

        changeActivityLabelFont();
    }

    public void restAllPictures(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest arrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                LibraryActivity.URL + "pictures",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        String json = response.toString();
                        Type type = new TypeToken<List<Picture>>() {}.getType();

                        mAllPicturesList = new Gson().fromJson(json, type);
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
        mExhContentFragment.setmAllPicturesList(mAllPicturesList);
        mExhContentFragment.setmCurrentExhibition(mCurrentExhibition);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentFrameExhibition, mExhContentFragment);
        fragmentTransaction.commit();
    }

    public void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ExhContentActivity.this);
        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //pokreni download ponovo
                restAllPictures();
            }
        });
        builder.setNegativeButton("Abort", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //vracanje na home activity
                finish();
            }
        });
        builder.setMessage(LibraryActivity.DOWNLOADING_FAIL);
        builder.create().show();
    }

    public void showFailedDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ExhContentActivity.this);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.setMessage(SavingActivity.UPLOADING_FAIL);
        builder.create().show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accept_menu_item2:
                restUpdate();
                return true;
            case R.id.delete_menu_item:
                restDelete();
                return true;
        }

        return false;
    }

    public void restUpdate(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                LibraryActivity.URL + "updateExh",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Intent intent = new Intent(ExhContentActivity.this, ExhibitionsActivity.class);
                        startActivity(intent);
                        ExhContentActivity.this.finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showFailedDialog();
                    }
                }
        ){
            @Override
            public byte[] getBody() throws AuthFailureError {
                return new Gson().toJson(mExhContentFragment.mCurrentExhibition).getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        requestQueue.add(stringRequest);
    }

    public void restDelete(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                LibraryActivity.URL + "deleteExh",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Intent intent = new Intent(ExhContentActivity.this, ExhibitionsActivity.class);
                        startActivity(intent);
                        ExhContentActivity.this.finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showFailedDialog();
                    }
                }
        ){
            @Override
            public byte[] getBody() throws AuthFailureError {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", mExhContentFragment.mCurrentExhibition.getId());
                } catch (JSONException e) { e.printStackTrace(); }

                return jsonObject.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_accept_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void changeActivityLabelFont(){
        Typeface typeface = ResourcesCompat.getFont(this, R.font.montserrat_medium);

        TextView textView = new TextView(this);
        textView.setText(mCurrentExhibition.getName());
        textView.setTypeface(typeface);
        textView.setTextSize(17);
        textView.setTextColor(Color.WHITE);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(textView);
    }

    public void onClickBack(View view) {
        finish();
    }
}
