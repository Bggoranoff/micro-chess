package com.github.bggoranoff.qchess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.github.bggoranoff.qchess.util.ChessAnimator;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ConstraintLayout homeLayout;
    private TextView manualLink;

    private void openManual(View view) {
        manualLink.animate().alpha(0.5f).setDuration(100);
        Intent intent = new Intent(getApplicationContext(), ManualActivity.class);
        startActivity(intent);
        manualLink.animate().alpha(1.0f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        homeLayout = findViewById(R.id.homeLayout);
        ChessAnimator.animateBackground(homeLayout);

        manualLink = findViewById(R.id.manualLink);
        manualLink.setOnClickListener(this::openManual);
    }
}