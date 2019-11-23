package com.example.emanu.diplomskiadmin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CaptureActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{

    private JavaCameraView mJavaCameraView;
    private Mat mRgba;
    private Mat mGray;
    private Mat mCanny;
    private Scalar mColor;
    private Point[] mContourPoints;

    private Mat mCapturedPhoto;
    protected static Mat sCroppedPhoto;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullscreen();
        setContentView(R.layout.activity_capture);

        ActivityCompat.requestPermissions(CaptureActivity.this,
                new String[]{Manifest.permission.CAMERA},
                1);

        if(OpenCVLoader.initDebug()){
            Toast.makeText(getApplicationContext(), "OpenCV successfully loaded", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(), "OpenCV not loaded", Toast.LENGTH_SHORT).show();
        }

        mJavaCameraView = findViewById(R.id.javaCameraView);
        mJavaCameraView.setVisibility(SurfaceView.VISIBLE);
        mJavaCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);
        mCanny = new Mat(height, width, CvType.CV_8UC1);
        mColor = new Scalar(0, 206, 255);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
        mCanny.release();
        if(mCapturedPhoto != null)
            mCapturedPhoto.release();
    }

    public void capturePhoto(View view) {
        perspectiveTransform();
        Intent intent = new Intent(this, ConfirmCaptureActivity.class);
        startActivity(intent);
    }

    public void perspectiveTransform(){
        //for some reason bl and br are rotated
        Point tl = mContourPoints[0];
        Point tr = mContourPoints[1];
        Point bl = mContourPoints[3];
        Point br = mContourPoints[2];

        //finding max distance for width
        int widthA = (int)Math.sqrt(Math.pow(tr.x - tl.x, 2.0) + Math.pow(tr.y - tl.y, 2.0));
        int widthB = (int)Math.sqrt(Math.pow(br.x - bl.x, 2.0) + Math.pow(br.y - bl.y, 2.0));
        int maxWidth = Math.max(widthA, widthB);

        //finding max distance for height
        int heightA = (int)Math.sqrt(Math.pow(tr.x - br.x, 2.0) + Math.pow(tr.y - br.y, 2.0));
        int heightB = (int)Math.sqrt(Math.pow(tl.x - bl.x, 2.0) + Math.pow(tl.y - bl.y, 2.0));
        int maxHeight = Math.max(heightA, heightB);

        //making destination points list for perspective transform
        List<Point> dstPointsList = new ArrayList<>();
        dstPointsList.add(new Point(0, 0)); //0
        dstPointsList.add(new Point(maxWidth - 1, 0)); //1
        dstPointsList.add(new Point(maxWidth - 1, maxHeight - 1)); //2
        dstPointsList.add(new Point(0, maxHeight - 1)); //3
        Mat dstPoints = Converters.vector_Point2f_to_Mat(dstPointsList);

        //just converting from array to list
        List<Point> srcPointsList = new ArrayList<>();
        srcPointsList.add(mContourPoints[0]);
        srcPointsList.add(mContourPoints[1]);
        srcPointsList.add(mContourPoints[2]);
        srcPointsList.add(mContourPoints[3]);
        Mat srcPoints = Converters.vector_Point2f_to_Mat(srcPointsList);

        //getting transformation matrix
        Mat M = Imgproc.getPerspectiveTransform(srcPoints, dstPoints);
        sCroppedPhoto = new Mat(maxHeight, maxWidth, CvType.CV_8UC1);
        Imgproc.warpPerspective(mCapturedPhoto, sCroppedPhoto, M, new Size(maxWidth, maxHeight));

        Core.rotate(sCroppedPhoto, sCroppedPhoto, Core.ROTATE_90_CLOCKWISE);

    }

    //finding largest conture in the frame
    public void findConture(){
        Imgproc.cvtColor(mRgba, mGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(mGray, mGray, new Size(5, 5), 0);
        Imgproc.Canny(mGray, mCanny, 75, 200);

        Imgproc.dilate(mCanny, mCanny, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2,2)));
        Imgproc.erode(mCanny, mCanny, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2,2)));

        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mCanny, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        //sorting of contours
        Collections.sort(contours, new Comparator<MatOfPoint>() {
            @Override
            public int compare(MatOfPoint lhs, MatOfPoint rhs) {
                return Double.valueOf(Imgproc.contourArea(rhs)).compareTo(Imgproc.contourArea(lhs));
            }
        });


        int i;
        //finding largest contoure with 4 points
        for(i = 0; i < contours.size(); i++){
            MatOfPoint c = contours.get(i);
            MatOfPoint2f c2f = new MatOfPoint2f(c.toArray());
            double peri = Imgproc.arcLength(c2f, true);
            MatOfPoint2f approx = new MatOfPoint2f();
            Imgproc.approxPolyDP(c2f, approx, 0.02 * peri, true);

            Point[] points = approx.toArray();
            if(points.length == 4){
                mContourPoints = sortPoints(points);
                break;
            }

        }

        if (contours.size() == 0 || i == contours.size())
            return;

        Imgproc.drawContours(mRgba, contours, i, mColor, 3, Core.LINE_8, hierarchy, 0, new Point());
        hierarchy.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mCapturedPhoto = mRgba.clone();

        findConture();

        return mRgba;
    }

    private Point[] sortPoints(Point[] src) {
        ArrayList<Point> srcPoints = new ArrayList<>(Arrays.asList(src));

        Point[] result = { null , null , null , null };

        Comparator<Point> sumComparator = new Comparator<Point>() {
            @Override
            public int compare(Point lhs, Point rhs) {
                return Double.valueOf(lhs.y + lhs.x).compareTo(rhs.y + rhs.x);
            }
        };

        Comparator<Point> diffComparator = new Comparator<Point>() {

            @Override
            public int compare(Point lhs, Point rhs) {
                return Double.valueOf(lhs.y - lhs.x).compareTo(rhs.y - rhs.x);
            }
        };

        // top-left corner = minimal sum
        result[0] = Collections.min(srcPoints, sumComparator);

        // bottom-right corner = maximal sum
        result[2] = Collections.max(srcPoints, sumComparator);

        // top-right corner = minimal diference
        result[1] = Collections.min(srcPoints, diffComparator);

        // bottom-left corner = maximal diference
        result[3] = Collections.max(srcPoints, diffComparator);

        return result;
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
            Toast.makeText(getApplicationContext(), "OpenCV successfully loaded", Toast.LENGTH_SHORT).show();
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        else{
            Toast.makeText(getApplicationContext(), "OpenCV not loaded", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(CaptureActivity.this, "Permission denied to use camera", Toast.LENGTH_SHORT).show();
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
