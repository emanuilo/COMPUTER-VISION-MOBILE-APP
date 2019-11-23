package com.example.emanu.diplomskiclient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class FullScreenPictureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_picture);

        Bundle extras = getIntent().getExtras();
        String url = extras.getString(GalleryActivity.FULL_SCREEN_IMAGE);
        ImageView imageView = findViewById(R.id.fullScreenImageView);

        Glide.with(this)
                .load(url) // Uri/String of the picture
                .into(imageView);
    }
}
