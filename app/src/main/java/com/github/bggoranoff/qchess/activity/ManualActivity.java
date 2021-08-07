package com.github.bggoranoff.qchess.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.webkit.WebView;

import com.github.bggoranoff.qchess.R;
import com.github.bggoranoff.qchess.util.ChessWebViewClient;

import java.util.Objects;

public class ManualActivity extends AppCompatActivity {

    private WebView manualView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);
        Objects.requireNonNull(getSupportActionBar()).hide();

        manualView = findViewById(R.id.manualView);
        manualView.getSettings().setJavaScriptEnabled(true);
        manualView.setWebViewClient(new ChessWebViewClient(new ProgressDialog(ManualActivity.this)));
        manualView.loadUrl("https://quantumchess.net/how-to-play");
    }
}