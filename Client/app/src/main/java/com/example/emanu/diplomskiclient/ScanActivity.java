package com.example.emanu.diplomskiclient;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.QuickContactBadge;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.emanu.diplomskiclient.DB.Content;
import com.example.emanu.diplomskiclient.DB.Picture;
import com.example.emanu.diplomskiclient.DB.RelatedPicture;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.ORB;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



public class ScanActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{

    private static final String DEVELOPER_KEY = "AIzaSyDfwYGnOIYGGsyMg3mhUWBIN8it2S4dNXs";
    public static final String TITLE = "title";

    private JavaCameraView mJavaCameraView;
    private Mat mRgba; //red green blue alpha
    private Mat mGray;
    private ORB mOrbDetector;
    private DescriptorMatcher mMatcher;

    private CloudShape mCloudShape;
    private Button mVideoButton;
    private Button mPhotosButton;
    private Button mSearchButton;
    private Button mCancelButton;
    private TextView mTitleTextView;
    private TextView mArtistTextView;
    private TextView mDescriptionTextView;
    private String mVideoId;

    private List<Picture> mPicturesList;
    private Picture mCurrentPicture;
    private Content mCurrentContent;
    protected static List<RelatedPicture> sRelatedPictures;

    private boolean pass;
    private int counterPass;
    private float[] sceneCornersData;
    private Scalar mColor;
    private String mLanguage;
    private boolean process = true;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case BaseLoaderCallback.SUCCESS:
                    mJavaCameraView.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
            super.onManagerConnected(status);
        }
    };
    private ImageView mImageView;
    private int mFrameCnt = 1;
    private int mPictureCnt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        fullscreen();
        setContentView(R.layout.activity_scan);

        mLanguage = HomeActivity.sLanguage;
        mPicturesList = LoadingScanActivity.sPicturesList;

        mImageView = findViewById(R.id.imageViewScan);
        mVideoButton = findViewById(R.id.videoButton);
        mPhotosButton = findViewById(R.id.photosButton);
        mSearchButton = findViewById(R.id.searchButton);
        mCancelButton = findViewById(R.id.cancelButton);

        mTitleTextView = findViewById(R.id.title_);
        mArtistTextView = findViewById(R.id.artist);

        mDescriptionTextView = findViewById(R.id.description);

        //todo napravi konstante za ove vrednosti
        mDescriptionTextView.setWidth(getScreenHeight() - 80);
        mDescriptionTextView.setTranslationX(getScreenWidth() / 2 - 105);
        mTitleTextView.setTranslationX(getScreenWidth() / 2 - 225);
        mArtistTextView.setTranslationX(getScreenWidth() / 2 - 192);

        //todo ako je veci od x line count dodaj 'read more'
        int height = mDescriptionTextView.getHeight();
        int lineCount = mDescriptionTextView.getLineCount();
        //todo pomeri gornju ivicu opisa

        mCloudShape = findViewById(R.id.cloudShape);
        mCloudShape.setHeight(getScreenHeight());
        mCloudShape.setWidth(getScreenWidth());
        mCloudShape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentContent.getHtmlDescription() != null && mCurrentContent.getHtmlDescription().length() > 0){
                    Intent intent = new Intent(ScanActivity.this, DescriptionActivity.class);
                    intent.putExtra(DescriptionActivity.HTML_DESC, mCurrentContent.getHtmlDescription());
                    startActivity(intent);
                }
            }
        });

        ActivityCompat.requestPermissions(ScanActivity.this,
                new String[]{Manifest.permission.CAMERA},
                1);

        if(OpenCVLoader.initDebug()){
//            Toast.makeText(getApplicationContext(), "OpenCV successfully loaded", Toast.LENGTH_SHORT).show();
            Log.d("OPENCV", "OpenCV successfully loaded");
        }
        else{
//            Toast.makeText(getApplicationContext(), "OpenCV not loaded", Toast.LENGTH_SHORT).show();
            Log.d("OPENCV", "OpenCV not loaded");
        }

        mJavaCameraView = findViewById(R.id.javaCameraView);
        mJavaCameraView.setVisibility(SurfaceView.VISIBLE);
        mJavaCameraView.setCvCameraViewListener(this);

//        if(getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
//            showDialog();

    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);
        mOrbDetector = ORB.create();
        mMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
        mColor = new Scalar(0, 206, 255);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();

        Imgproc.cvtColor(mRgba, mGray, Imgproc.COLOR_RGB2GRAY);

        //frame count govori da li se svaki drugi ili svaki treci cetvrti frame obradjuje
        Mat ret = null;
        if(counterPass == mFrameCnt){
            ret = match();
            counterPass = 0;
        }
        else{
            counterPass++;
            if(sceneCornersData != null){
//                Imgproc.line(mRgba, new Point(sceneCornersData[0], sceneCornersData[1]),
//                        new Point(sceneCornersData[2], sceneCornersData[3]), mColor, 4);
//                Imgproc.line(mRgba, new Point(sceneCornersData[2], sceneCornersData[3]),
//                        new Point(sceneCornersData[4], sceneCornersData[5]), mColor, 4);
//                Imgproc.line(mRgba, new Point(sceneCornersData[4], sceneCornersData[5]),
//                        new Point(sceneCornersData[6], sceneCornersData[7]), mColor, 4);
//                Imgproc.line(mRgba, new Point(sceneCornersData[6], sceneCornersData[7]),
//                        new Point(sceneCornersData[0], sceneCornersData[1]), mColor, 4);
            }
            ret = mRgba;
        }


        return ret;
    }

    public Mat match(){
        if(mPicturesList == null)
            return mRgba;

        if(!process)
            return mRgba;

        //camera frame
        Mat imgScene = mGray;

        //creation of keypoints, descriptors and feature detection
        MatOfKeyPoint keypointsScene = new MatOfKeyPoint();
        Mat descriptorsScene = new Mat();
        mOrbDetector.detectAndCompute(imgScene, new Mat(), keypointsScene, descriptorsScene);

        if(descriptorsScene.empty()) //none features found
            return mRgba;

        for(int j = 0; j < mPicturesList.size(); j++){
            mCurrentPicture = mPicturesList.get((mPictureCnt + j) % mPicturesList.size());
            Mat imgObject = mCurrentPicture.getImgObject();
            Mat descriptorsObject = mCurrentPicture.getDescriptorsObject();
            MatOfKeyPoint keypointsObject = mCurrentPicture.getKeypointsObject();

            //matching features between imgScene and imgObject
            List<MatOfDMatch> knnMatches = new ArrayList<>();
            mMatcher.knnMatch(descriptorsObject, descriptorsScene, knnMatches, 2);

            //filtering good matches
            float ratioThresh = 0.6f;
            List<DMatch> listOfGoodMatches = new ArrayList<>();
            for(int i = 0; i < knnMatches.size(); i++){
                if(knnMatches.get(i).rows() > 1){
                    DMatch[] matches = knnMatches.get(i).toArray();
                    if(matches[0].distance < ratioThresh * matches[1].distance)
                        listOfGoodMatches.add(matches[0]);
                }
            }

            if(listOfGoodMatches.size() == 0)
                continue;

            MatOfDMatch goodMatches = new MatOfDMatch();
            goodMatches.fromList(listOfGoodMatches);

            //drawing matches
//            Mat imgMatches = new Mat();
//            Features2d.drawMatches(imgObject, keypointsObject, imgScene, keypointsScene, goodMatches, imgMatches, Scalar.all(-1),
//                    Scalar.all(-1), new MatOfByte(), Features2d.NOT_DRAW_SINGLE_POINTS);

            //localizing the object
            List<Point> obj = new ArrayList<>();
            List<Point> scene = new ArrayList<>();

            List<KeyPoint> listOfKeypointsObject = keypointsObject.toList();
            List<KeyPoint> listOfKeypointsScene = keypointsScene.toList();
            for(int i = 0; i < listOfGoodMatches.size(); i++){
                obj.add(listOfKeypointsObject.get(listOfGoodMatches.get(i).queryIdx).pt);
                scene.add(listOfKeypointsScene.get(listOfGoodMatches.get(i).trainIdx).pt);
            }

            MatOfPoint2f objMat = new MatOfPoint2f();
            MatOfPoint2f sceneMat = new MatOfPoint2f();
            objMat.fromList(obj);
            sceneMat.fromList(scene);
            double ransacReprojThreshold = 3.0;
            Mat H = Calib3d.findHomography(objMat, sceneMat, Calib3d.RANSAC, ransacReprojThreshold);

            if(H.empty())
                continue;

            //getting corners from imgObject
//            Mat objCorners = new Mat(4, 1, CvType.CV_32FC2);
//            Mat sceneCorners = new Mat();
//            float[] objCornersData = new float[(int) (objCorners.total() * objCorners.channels())];
//            objCorners.get(0, 0, objCornersData);
//            objCornersData[0] = 0;
//            objCornersData[1] = 0;
//            objCornersData[2] = imgObject.cols();
//            objCornersData[3] = 0;
//            objCornersData[4] = imgObject.cols();
//            objCornersData[5] = imgObject.rows();
//            objCornersData[6] = 0;
//            objCornersData[7] = imgObject.rows();
//            objCorners.put(0, 0, objCornersData);
//
//            Core.perspectiveTransform(objCorners, sceneCorners, H);
//            sceneCornersData = new float[(int) (sceneCorners.total() * sceneCorners.channels())];
//            sceneCorners.get(0, 0, sceneCornersData);

            //drawing lines between corners
//            Imgproc.line(mRgba, new Point(sceneCornersData[0], sceneCornersData[1]),
//                    new Point(sceneCornersData[2], sceneCornersData[3]), mColor, 4);
//            Imgproc.line(mRgba, new Point(sceneCornersData[2], sceneCornersData[3]),
//                    new Point(sceneCornersData[4], sceneCornersData[5]), mColor, 4);
//            Imgproc.line(mRgba, new Point(sceneCornersData[4], sceneCornersData[5]),
//                    new Point(sceneCornersData[6], sceneCornersData[7]), mColor, 4);
//            Imgproc.line(mRgba, new Point(sceneCornersData[6], sceneCornersData[7]),
//                    new Point(sceneCornersData[0], sceneCornersData[1]), mColor, 4);

            mPictureCnt = (mPictureCnt + j) % mPicturesList.size();
            mCurrentContent = findLanguage(mLanguage);

            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showContent();
                }
            });

            return mRgba;
        }

        //if for loop is over that means none pictures have been matched
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideContent();
            }
        });

        return mRgba;
    }

    public Content findLanguage(String language){
        for (Content content : mCurrentPicture.getContentList()){
            if (content.getLanguage().equalsIgnoreCase(language))
                return content;
        }

        return mCurrentPicture.getContentList().get(0); //nulti je podrazumevani
    }

    public void hideContent(){
        mCloudShape.setVisibility(View.GONE);
        mVideoButton.setVisibility(View.GONE);
        mPhotosButton.setVisibility(View.GONE);
        mSearchButton.setVisibility(View.VISIBLE);
        mCancelButton.setVisibility(View.GONE);

        mTitleTextView.setVisibility(View.GONE);
        mArtistTextView.setVisibility(View.GONE);
        mDescriptionTextView.setVisibility(View.GONE);
        sceneCornersData = null;

        //ubrzaj
        mFrameCnt = 3;
    }

    public void showContent(){
        mCloudShape.setVisibility(View.VISIBLE);
        mVideoButton.setVisibility(View.VISIBLE);
        mPhotosButton.setVisibility(View.VISIBLE);
        mSearchButton.setVisibility(View.GONE);
        mCancelButton.setVisibility(View.VISIBLE);

        mTitleTextView.setText(mCurrentContent.getTitle());
        mArtistTextView.setText(mCurrentContent.getArtist());
        mDescriptionTextView.setText(mCurrentContent.getDescription());
        mVideoId = mCurrentPicture.getVideoId();

        mTitleTextView.setVisibility(View.VISIBLE);
        mArtistTextView.setVisibility(View.VISIBLE);
        mDescriptionTextView.setVisibility(View.VISIBLE);

        //smanji obradu
        mFrameCnt = 6;
    }

    public void onClickVideoButton(View view) {
        Intent intent = YouTubeStandalonePlayer.createVideoIntent(this, DEVELOPER_KEY, mVideoId);
        startActivity(intent);
    }

    public void onClickPhotosButton(View view) {
        Intent intent = new Intent(this, GalleryActivity.class);
        intent.putExtra(TITLE, mCurrentContent.getArtist());
        sRelatedPictures = mCurrentPicture.getRelatedPictures();
        startActivity(intent);
    }

    public void onClickSearchButton(View view) {

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        showDialog();
        process = false;
    }

    public void onClickCancelButton(View view) {
        hideContent();
        mImageView.setVisibility(View.GONE);

        process = true;
    }

    public void showDialog(){


        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(50, 5, 50, 10);
        editText.setLayoutParams(lp);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(editText);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                onInputRegisterNumber(Integer.parseInt(editText.getText().toString()));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setMessage("Input register number");
        builder.create().show();
    }

    public void onInputRegisterNumber(int registerNumber){
        for (Picture picture : mPicturesList){
            if (picture.getRegisterNumber() == registerNumber){
                mCurrentPicture = picture;
                mCurrentContent = findLanguage(mLanguage);
                showContent();

                Glide.with(this)
                        .load(mCurrentPicture.getPictureBlob()) // Uri/String of the picture
                        .into(mImageView);

                mImageView.setVisibility(View.VISIBLE);

                return;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mJavaCameraView != null)
            mJavaCameraView.disableView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mJavaCameraView != null)
            mJavaCameraView.disableView();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(OpenCVLoader.initDebug()){
            Log.d("OPENCV", "OpenCV successfully loaded");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        else{
            Log.d("OPENCV", "OpenCV not loaded");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    // permission denied
                    Toast.makeText(ScanActivity.this, "Permission denied to use camera", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public static int getScreenWidth(){
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(){
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public void fullscreen(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

}
