package com.example.puzzledroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            exitHelpPage();
        }

        return super.onOptionsItemSelected(item);
    }
    // Método para lanzar la pantalla de ayuda.
    private void exitHelpPage() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
