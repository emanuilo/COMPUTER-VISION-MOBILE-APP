package com.example.emanu.diplomskiadmin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.emanu.diplomskiadmin.DB.Exhibition;

public class HomeActivity extends AppCompatActivity {
    public static final int MY_SOCKET_TIMEOUT_MS = 30000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void onClickCapture(View view) {
        Intent intent = new Intent(this, CaptureActivity.class);
        startActivity(intent);
    }

    public void onClickGallery(View view) {
        Intent intent = new Intent(this, LibraryActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClickExhibitions(View view) {
        Intent intent = new Intent(this, ExhibitionsActivity.class);
        startActivity(intent);
    }
}
