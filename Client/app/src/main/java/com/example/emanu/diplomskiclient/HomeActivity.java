package com.example.emanu.diplomskiclient;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.emanu.diplomskiclient.DB.Exhibition;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.example.emanu.diplomskiclient.LoadingScanActivity.URL_;

public class HomeActivity extends AppCompatActivity {

    private Spinner mSpinner;
    private ArrayAdapter<String> mAdapter;
    private List<String> mLanguagesList;
    protected static String sLanguage;
    private Button mScanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mScanButton = findViewById(R.id.buttonScan);
        mScanButton.setEnabled(false);

        getRestLanguages();
    }

    public void getRestLanguages(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest objectRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL_ + "languages",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        String json = response.toString();
                        Type type = new TypeToken<List<String>>() {}.getType();

                        mLanguagesList = new Gson().fromJson(json, type);
                        initSpinner();
                        mScanButton.setEnabled(true);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("REST", error.getMessage());
                        showAlertDialog();
                    }
                }
        );

        requestQueue.add(objectRequest);
    }

    public void initSpinner(){
        mSpinner = findViewById(R.id.languageSpinner);
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mLanguagesList);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                sLanguage = item;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        mSpinner.setSelection(0);
    }

    public void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getRestLanguages();
            }
        });
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setMessage("Communication with server failed!");
        builder.create().show();
    }

    public void onClickScan(View view) {
        Intent intent = new Intent(this, LoadingScanActivity.class);
        startActivity(intent);
    }

}
