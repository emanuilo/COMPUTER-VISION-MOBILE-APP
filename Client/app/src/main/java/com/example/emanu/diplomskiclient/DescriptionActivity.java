package com.example.emanu.diplomskiclient;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;

public class DescriptionActivity extends AppCompatActivity {

    public static final String HTML_DESC = "html_desc";
    private static final String TITLE = "About";
    private ProgressDialog progDailog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        Bundle extras = getIntent().getExtras();

        //postavljanje custom toolbar-a
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        progDailog = ProgressDialog.show(this, "Loading","Please wait...", true);
        progDailog.setCancelable(false);

        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);

        if(URLUtil.isValidUrl(extras.getString(HTML_DESC))){
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.loadUrl(extras.getString(HTML_DESC));
            webView.setWebViewClient(new WebViewClient(){

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    progDailog.show();
                    view.loadUrl(url);
                    return true;
                }
                @Override
                public void onPageFinished(WebView view, final String url) {
                    progDailog.dismiss();
                }
            });
        }
        else{
            String unencodedHtml = extras.getString(HTML_DESC);
            String encodedHtml = Base64.encodeToString(unencodedHtml.getBytes(), Base64.NO_PADDING);
            webView.loadData(encodedHtml, "text/html", "base64");
            progDailog.dismiss();
        }

        changeActivityLabelFont();
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
        textView.setText(TITLE);
        textView.setTypeface(typeface);
        textView.setTextSize(19);
        textView.setTextColor(Color.WHITE);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(textView);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
