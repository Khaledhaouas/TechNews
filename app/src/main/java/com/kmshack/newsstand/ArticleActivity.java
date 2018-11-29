package com.kmshack.newsstand;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.utils.WebClient;

import khaled.newsapp.R;

public class ArticleActivity extends ActionBarActivity {

    private static final String TAG = "ArticleActivity";
    private WebView webView;
    private String url;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int category = getIntent().getIntExtra("category", 0);
        Log.e(TAG, "hello: "+category );
        switch (category) {
            case 0:
                getWindow().setStatusBarColor(getResources().getColor(R.color.statusBarColor1));
                break;
            case 1:
                getWindow().setStatusBarColor(getResources().getColor(R.color.statusBarColor2));
                break;
            case 2:
                getWindow().setStatusBarColor(getResources().getColor(R.color.statusBarColor3));
                break;
            case 3:
                getWindow().setStatusBarColor(getResources().getColor(R.color.statusBarColor4));
                break;
            case 4:
                getWindow().setStatusBarColor(getResources().getColor(R.color.statusBarColor5));
                break;
            case 5:
                getWindow().setStatusBarColor(getResources().getColor(R.color.statusBarColor6));
                break;
            case 6:
                getWindow().setStatusBarColor(getResources().getColor(R.color.statusBarColor7));
                break;
            case 7:
                getWindow().setStatusBarColor(getResources().getColor(R.color.statusBarColor8));
                break;
            case 8:
                getWindow().setStatusBarColor(getResources().getColor(R.color.statusBarColor9));
                break;
            default:
                break;
        }


        url = getIntent().getStringExtra("url");

        setContentView(R.layout.activity_article);
        webView = (WebView) findViewById(R.id.webView);
//                webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebClient());
        webView.loadUrl(url);


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

}







