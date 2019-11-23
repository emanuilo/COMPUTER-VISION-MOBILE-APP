package com.example.emanu.diplomskiclient;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.example.emanu.diplomskiclient.DB.RelatedPicture;

import java.util.List;

public class GalleryActivity extends AppCompatActivity {
    public static final String FULL_SCREEN_IMAGE = "full_screen_image";

    private String mTitle;

    private GridView mGridView;
    private List<RelatedPicture> mRelatedPictures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        //postavljanje custom toolbar-a
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Bundle extras = getIntent().getExtras();
        mTitle = extras.getString(ScanActivity.TITLE);
        mRelatedPictures = ScanActivity.sRelatedPictures;

        mGridView = findViewById(R.id.gridView);
        mGridView.setAdapter(new ImageAdapter(this, mRelatedPictures, getScreenHeight(), getScreenWidth()));
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(GalleryActivity.this, FullScreenPictureActivity.class);
                intent.putExtra(FULL_SCREEN_IMAGE, mRelatedPictures.get(position).getPictureBlob());
                startActivity(intent);
            }
        });

        changeActivityLabelFont();
    }

    public static int getScreenWidth(){
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(){
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }

        return false;
    }

    public void changeActivityLabelFont(){
        Typeface typeface = ResourcesCompat.getFont(this, R.font.montserrat_medium);

        TextView textView = new TextView(this);
        textView.setText(mTitle);
        textView.setTypeface(typeface);
        textView.setTextSize(19);
        textView.setTextColor(Color.WHITE);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(textView);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

}
