package com.example.sss.rsssheep;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class PostViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);

        Bundle bundle = this.getIntent().getExtras();
        String postContent = bundle.getString("link");
        WebView webView = (WebView)findViewById(R.id.webView);
        webView.loadUrl(postContent);
    }
}