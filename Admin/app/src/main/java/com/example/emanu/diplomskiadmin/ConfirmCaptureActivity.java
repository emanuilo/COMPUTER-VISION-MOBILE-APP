package com.example.emanu.diplomskiadmin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class ConfirmCaptureActivity extends AppCompatActivity {

    protected static Bitmap sPhoto;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_capture);

        Mat mat = CaptureActivity.sCroppedPhoto;
        sPhoto = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, sPhoto);

        mImageView = findViewById(R.id.imageView);
        mImageView.setImageBitmap(sPhoto);
    }

    public void onClickConfirm(View view) {
        Intent intent = new Intent(this, SavingActivity.class);
        startActivity(intent);
    }

    public void onClickCancel(View view) {
        Intent intent = new Intent(this, CaptureActivity.class);
        startActivity(intent);
    }
}
