package com.example.emanu.diplomskiadmin;

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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.emanu.diplomskiadmin.DB.Exhibition;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.List;

public class ExhibitionsActivity extends AppCompatActivity {

    private static final String ACTION_BAR_TITLE = "Exhibitions";


    private ListView mListView;
    private MyListViewAdapter mAdapter;
    private List<Exhibition> mExhibitionsList;
    private Exhibition mNewExhibition;
    protected static Exhibition sSelectedExhibition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibitions);

        //postavljanje custom toolbar-a
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mListView = findViewById(R.id.listViewExh);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //prosledi objekat izlozbe
                sSelectedExhibition = mExhibitionsList.get(position);

                Intent intent = new Intent(ExhibitionsActivity.this, ExhContentActivity.class);
                startActivity(intent);
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(mAdapter.exhibitionSetOn != -1) {
                    mExhibitionsList.get(mAdapter.exhibitionSetOn).setSet(false);
                    restUpdate(mExhibitionsList.get(mAdapter.exhibitionSetOn));
                }

                mExhibitionsList.get(position).setSet(true);
                mAdapter.notifyDataSetChanged();

                restUpdate(mExhibitionsList.get(position));
                return true;
            }
        });
        //pozovi rest servis za izlozbe
        restExhibitions();

        changeActivityLabelFont();
    }

    public void restUpdate(final Exhibition exhibition){
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                LibraryActivity.URL + "updateExh",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

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
                return new Gson().toJson(exhibition).getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        requestQueue.add(stringRequest);
    }

    public void showFailedDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ExhibitionsActivity.this);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.setMessage(SavingActivity.UPLOADING_FAIL);
        builder.create().show();
    }

    public void restExhibitions(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest arrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                LibraryActivity.URL + "exhibitions",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        String json = response.toString();
                        Type type = new TypeToken<List<Exhibition>>() {}.getType();

                        mExhibitionsList = new Gson().fromJson(json, type);
                        onLoad();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showAlertDialog(LibraryActivity.DOWNLOADING_FAIL);
                    }
                }
        );
        arrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                HomeActivity.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(arrayRequest);
    }

    public void onLoad(){
        mAdapter = new MyListViewAdapter(mExhibitionsList, this);
        mListView.setAdapter(mAdapter);
    }

    public void showAlertDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(ExhibitionsActivity.this);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //vracanje na home activity
                Intent intent = new Intent(ExhibitionsActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setMessage(message);
        builder.create().show();
    }

    public void onClickAdd(){
        showAddExhibitionDialog();
    }

    public void showAddExhibitionDialog(){
        final EditText editText = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(50, 5, 50, 10);
        editText.setLayoutParams(lp);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(editText);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mNewExhibition = new Exhibition();
                mNewExhibition.setName(editText.getText().toString());
                mNewExhibition.setSet(false);

                restNewExhibition();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setMessage("Add new exhibition");
        builder.create().show();
    }

    public void restNewExhibition(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                LibraryActivity.URL + "saveExh",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response != null || !response.equals("")){
                            mNewExhibition.setId(Integer.parseInt(response));
                            mExhibitionsList.add(mNewExhibition);
                            mNewExhibition = null;
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showAlertDialog(SavingActivity.UPLOADING_FAIL);
                    }
                }
        ){
            @Override
            public byte[] getBody() throws AuthFailureError {
                return new Gson().toJson(mNewExhibition).getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_menu_item:
                onClickAdd();
                return true;
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return super.onCreateOptionsMenu(menu);
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

    public void onClickBack(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
