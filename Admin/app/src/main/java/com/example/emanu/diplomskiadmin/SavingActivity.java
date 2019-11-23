package com.example.emanu.diplomskiadmin;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.opengl.Visibility;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.emanu.diplomskiadmin.DB.Content;


public class SavingActivity extends AppCompatActivity implements SavingFragment.FragmentUploadListener {

    public static final String ACTION_BAR_TITLE = "Save to the database";
    public static final String UPLOADING_SUCCESS = "Uploading successfully finished!";
    public static final String UPLOADING_FAIL = "Uploading failed!";

    private LoadingFragment mLoadingFragment;
    private SavingFragment mSavingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saving);

        //postavljanje custom toolbar-a
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Button button = findViewById(R.id.backButton);
        button.setVisibility(View.GONE);

        mSavingFragment = new SavingFragment();
        mLoadingFragment = new LoadingFragment();
        mLoadingFragment.setLoadingText(LoadingFragment.UPLOADING);

        //postavljanje fragmenta za cuvanje slike
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentFrame, mSavingFragment);
        fragmentTransaction.commit();

        changeActivityLabelFont();

    }

    @Override
    public void onFinishedUploading(boolean isSucceeded) {
        if (isSucceeded){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //vracanje na home activity
                    Intent intent = new Intent(SavingActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            });
            builder.setMessage(UPLOADING_SUCCESS);
            builder.create().show();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //pokreni upload ponovo
                    mSavingFragment.onClickConfirm();
                }
            });
            builder.setNegativeButton("Abort", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //vracanje na home activity
                    Intent intent = new Intent(SavingActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            });
            builder.setMessage(UPLOADING_FAIL);
            builder.create().show();
        }
    }

    public void onClickConfirm(){
        mSavingFragment.onClickConfirm();

        //zamena fragmenata
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(mSavingFragment);
        fragmentTransaction.commit();

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentFrame, mLoadingFragment);
        fragmentTransaction.commit();
    }

    public void onClickAddPhotos(View view) {
        mSavingFragment.onClickAddPhotos();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accept_menu_item:
                onClickConfirm();

                return true;
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.accept_menu, menu);
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
        Intent intent = new Intent(this, LibraryActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClickAddLang(View view) {
        final EditText editText = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(50, 5, 50, 10);
        editText.setLayoutParams(lp);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(editText);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mSavingFragment.mLanguages.add(editText.getText().toString());
                mSavingFragment.mAdapter.notifyDataSetChanged();
                mSavingFragment.mSpinner.setSelection(mSavingFragment.mLanguages.size() - 1);

                Content content = new Content();
                content.setLanguage(editText.getText().toString());
                mSavingFragment.mListContent.add(content);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setMessage("Add new language");
        builder.create().show();

    }


}
