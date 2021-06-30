package com.github.bggoranoff.qchess.util;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.github.bggoranoff.qchess.R;

public class ChessAnimator {

    private static final int ANIMATION_DURATION = 3000;

    public static void animateBackground(View view) {
        int colorFrom = ContextCompat.getColor(view.getContext(), R.color.light_bg);
        int colorTo = ContextCompat.getColor(view.getContext(), R.color.dark_bg);
        ValueAnimator colourAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo, colorFrom);
        colourAnimation.setRepeatCount(ValueAnimator.INFINITE);
        colourAnimation.setDuration(ANIMATION_DURATION);
        colourAnimation.addUpdateListener(animator ->
                view.setBackgroundColor((int) animator.getAnimatedValue())
        );
        colourAnimation.start();
    }
}
