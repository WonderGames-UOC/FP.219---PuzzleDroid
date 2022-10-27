package com.example.puzzledroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebSettings;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        WebView helpWebView = findViewById(R.id.helpWebView);
        WebSettings webSettings = helpWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        helpWebView.loadUrl("file:///android_asset/webView.html");

    }
}
