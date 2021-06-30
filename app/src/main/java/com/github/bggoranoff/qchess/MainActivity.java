package com.github.bggoranoff.qchess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.widget.ViewAnimator;

import com.github.bggoranoff.qchess.util.ChessAnimator;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ConstraintLayout homeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        homeLayout = findViewById(R.id.homeLayout);
        ChessAnimator.animateBackground(homeLayout);
    }
}