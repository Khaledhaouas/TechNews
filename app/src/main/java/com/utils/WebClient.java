package com.utils;

import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Khaled on 05/10/2015.
 */
public class WebClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url)
    {
        // Obvious next step is: document.forms[0].submit()
       // view.loadUrl("javascript:document.getElementsByClassName('header header-fixed-mobile cf header-context-news header-fixed')[0].style.display = 'none';");
        Log.i("aaaaaaa","aaaaaaa");
        view.loadUrl("javascript:alert('Hello World!')");


    }
}
