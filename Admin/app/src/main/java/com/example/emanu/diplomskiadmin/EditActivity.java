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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.emanu.diplomskiadmin.DB.Content;
import com.example.emanu.diplomskiadmin.DB.Picture;
import com.example.emanu.diplomskiadmin.DB.RelatedPicture;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EditActivity extends AppCompatActivity {

    private static final String ACTION_BAR_TITLE = "Edit";
    private static final String DOWNLOADING_FAIL = "Downloading failed!";
    private static final String DELETING_FAIL = "Deleting failed!";
    private static final String UPDATING_FAIL = "Updating failed!";

    private LoadingFragment mLoadingFragment;
    private EditFragment mEditFragment;
    private Picture mPicture;
    private boolean isUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saving);
        Bundle extras = getIntent().getExtras();
        mPicture = (Picture) extras.getSerializable(LibraryFragment.PICTURE_EXTRA);

        //postavljanje custom toolbar-a
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mLoadingFragment = new LoadingFragment();
        mLoadingFragment.setLoadingText(LoadingFragment.LOADING);
        mEditFragment = new EditFragment();

        //postavljanje fragmenta za loading
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentFrame, mLoadingFragment);
        fragmentTransaction.commit();

        loadRelatedImages();

        changeActivityLabelFont();
    }

    public void onClickAddLang(View view) {
        final EditText editText = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(50, 5, 50, 10);
        editText.setLayoutParams(lp);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(editText);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Content newContent = new Content();
                newContent.setLanguage(editText.getText().toString());
                mEditFragment.mPicture.getContentList().add(newContent);
                //update liste jezika
                mEditFragment.mLanguages.add(editText.getText().toString());
                mEditFragment.mAdapter.notifyDataSetChanged();
                mEditFragment.mSpinner.setSelection(mEditFragment.mLanguages.size() - 1);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { }
        });
        builder.setMessage("Add new language");
        builder.create().show();

    }

    public void onClickAddPhotos(View view) {
        mEditFragment.onClickAddPhotos();
    }

    public void loadRelatedImages(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        Map<String, Integer> params = new HashMap();
        params.put("id", mPicture.getId());
        JSONObject jsonObject = new JSONObject(params);

        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                LibraryActivity.URL + "picture" + "?id=" + mPicture.getId(),
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String json = response.toString();
                        Type type = new TypeToken<Picture>() {}.getType();

                        //dohvatanje iste slike ali sa listom related pictures
                        mPicture = new Gson().fromJson(json, type);
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

        requestQueue.add(objectRequest);
    }

    public void onLoad(){
        //pravljenje liste url-ova od liste RelatedPicture objekata
        List<String> listURLRelatedPictures = extractURLFromObjects(mPicture.getRelatedPictures());
        mEditFragment.setListRelatedPictures(listURLRelatedPictures);
        mEditFragment.setPicture(mPicture);

        //zamena fragmenata
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentFrame, mEditFragment);
        fragmentTransaction.commit();
    }

    public void onResponseRest(boolean isSucceeded){
        if(isSucceeded){
            Intent intent = new Intent(this, LibraryActivity.class);
            startActivity(intent);
            //put this activity and all resources into the gc queue
            this.finish();
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
            builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //pokreni akciju ponovo
                    if(isUpdate)
                        restUpdate();
                    else
                        restDelete();
                }
            });
            builder.setNegativeButton("Abort", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //vracanje na home activity
                    Intent intent = new Intent(EditActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            builder.setMessage(isUpdate ? UPDATING_FAIL : DELETING_FAIL);
            builder.create().show();
        }
    }

    public void restDelete(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                LibraryActivity.URL + "delete",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        onResponseRest(true);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onResponseRest(false);
                    }
                }
        ){
            @Override
            public byte[] getBody() throws AuthFailureError {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", mPicture.getId());
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

    public void restUpdate(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                LibraryActivity.URL + "update",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        restDeleteRelatedPictures();
                        onResponseRest(true);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onResponseRest(false);
                    }
                }
        ){
            @Override
            public byte[] getBody() throws AuthFailureError {
                return new Gson().toJson(mEditFragment.mPicture).getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        requestQueue.add(stringRequest);
    }

    public void restDeleteRelatedPictures(){
        if(mEditFragment.removedRelatedPicturesIds.size() > 0){
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            StringRequest stringRequest = new StringRequest(
                    Request.Method.POST,
                    LibraryActivity.URL + "deleteRelated",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
//                            onResponseRest(true);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
//                            onResponseRest(false);
                        }
                    }
            ){
                @Override
                public byte[] getBody() throws AuthFailureError {
                    return new Gson().toJson(mEditFragment.removedRelatedPicturesIds).getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };

            requestQueue.add(stringRequest);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case  R.id.delete_menu_item:
                isUpdate = false;
                changeFragments(true);
                restDelete();
                return true;

            case  R.id.accept_menu_item2:
                isUpdate = true;
                changeFragments(true);
                mEditFragment.saveContent(mEditFragment.mLastPosition);
                restUpdate();
                return true;
        }

        return false;
    }

    public void changeFragments(boolean loading){
        //zamena fragmenata
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentFrame, loading ? mLoadingFragment : mEditFragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_accept_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public List<String> extractURLFromObjects(List<RelatedPicture> listRelatedPictures){
        List<String> listURLRelatedPictures = new ArrayList<>();
        for(RelatedPicture relatedPicture : listRelatedPictures){
            listURLRelatedPictures.add(relatedPicture.getPictureBlob());
        }

        return listURLRelatedPictures;
    }

    public void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //pokreni download ponovo
                loadRelatedImages();
            }
        });
        builder.setNegativeButton("Abort", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //vracanje na home activity
                Intent intent = new Intent(EditActivity.this, HomeActivity.class);
                startActivity(intent);
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

    //calling garbage collector to free memory earlier
    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }

    public void onClickBack(View view) {
        Intent intent = new Intent(this, LibraryActivity.class);
        startActivity(intent);
        finish();
    }
}
