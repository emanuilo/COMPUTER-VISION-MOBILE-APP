package com.example.emanu.diplomskiclient;


import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;

/**
 * Created by emanu on 8/6/2018.
 */

public class CloudShape extends View {

    public static final int LEFT = 250;
    public static final int RIGHT = 20;
    public static final int BOTTOM = 20;
    public static final int TOP = 20;
    public static final int CORNER_RADIUS = 12;
    public static final int TRIANGLE_Y1 = 26;
    public static final int TRIANGLE_X2 = 285;
    public static final int TRIANGLE_Y2 = 140;
    public static final int TRIANGLE_Y3 = 110;


    private int mHeight;
    private int mWidth;
    private Paint mPaint;
    private Path mPath;

    public CloudShape(Context context, int height, int width) {
        super(context);
        mHeight = height;
        mWidth = width;

        init(null);
    }

    public CloudShape(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    public CloudShape(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    private void init(@Nullable AttributeSet set){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.parseColor("#EFEFEF"));
        mPath = new Path();
    }

    public void setHeight(int height){
        mHeight = height;
    }

    public void setWidth(int width){
        mWidth = width;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mWidth = 285;

        canvas.drawRoundRect(mWidth - LEFT, TOP, mWidth - RIGHT, mHeight - BOTTOM, CORNER_RADIUS, CORNER_RADIUS, mPaint);

        mPath.moveTo(mWidth - LEFT, mHeight / 2 - TRIANGLE_Y1);
        mPath.lineTo(mWidth - TRIANGLE_X2, mHeight / 2);
        mPath.lineTo(mWidth - LEFT, mHeight / 2 + TRIANGLE_Y1);
        canvas.drawPath(mPath, mPaint);

    }
//        mPaint.setShadowLayer(8, -10, 0, Color.GRAY);
//        setLayerType(LAYER_TYPE_SOFTWARE, null);

}
