package com.example.emanu.diplomskiclient;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.emanu.diplomskiclient.DB.Exhibition;
import com.example.emanu.diplomskiclient.DB.Picture;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.ORB;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class LoadingScanActivity extends AppCompatActivity {
    public static final String URL_ = "http://192.168.0.18:8080/picture/";

    protected static List<Picture> sPicturesList;
    private ORB mOrbDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_scan);

        if(OpenCVLoader.initDebug()){
//            Toast.makeText(getApplicationContext(), "OpenCV successfully loaded", Toast.LENGTH_SHORT).show();
            Log.d("OPENCV", "OpenCV successfully loaded");
        }
        else{
//            Toast.makeText(getApplicationContext(), "OpenCV not loaded", Toast.LENGTH_SHORT).show();
            Log.d("OPENCV", "OpenCV not loaded");
        }

        mOrbDetector = ORB.create();

        getRestData();
    }

    public void getRestData(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL_ + "setOnExh",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String json = response.toString();
                        Type type = new TypeToken<Exhibition>() {}.getType();

                        Exhibition exhibition = new Gson().fromJson(json, type);
                        sPicturesList = exhibition.getPictures();
                        new InitAsyncTask().execute();

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

    public void showAlertDialog(){

    }

    public void initializeImageMatching(){
        //feature detection for each Picture object
        for(int i = 0; i < sPicturesList.size(); i++){
            Picture currentPicture = sPicturesList.get(i);

            Bitmap bitmap = getBitmapFromURL(currentPicture.getPictureBlob());

            Mat imgObject = new Mat();
            Utils.bitmapToMat(bitmap, imgObject);  //converting bitmap to Mat object
            Imgproc.cvtColor(imgObject, imgObject, Imgproc.COLOR_RGB2GRAY);  //converting to grayscale
            imgObject.convertTo(imgObject, 0);  //converting image type

            //creation of keypoints, descriptors and feature detection
            MatOfKeyPoint keypointsObject = new MatOfKeyPoint();
            Mat descriptorsObject = new Mat();
            mOrbDetector.detectAndCompute(imgObject, new Mat(), keypointsObject, descriptorsObject);

            currentPicture.setImgObject(imgObject);
            currentPicture.setKeypointsObject(keypointsObject);
            currentPicture.setDescriptorsObject(descriptorsObject);
        }
    }

    public void onLoad(){
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
        finish();
    }

    private class InitAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            initializeImageMatching();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            onLoad();
        }
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

            return myBitmap;
        } catch (IOException e) { e.printStackTrace(); }

        return null;
    }
}
