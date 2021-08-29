package com.github.bggoranoff.qchess.util;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.TypedValue;
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

    public static int getInDps(Context context, int d) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, d, context.getResources().getDisplayMetrics());
    }

    public static int getSquareColor(String tag) {
        int file = tag.charAt(0) - 97;
        int rank = tag.charAt(1) - 48 - 1;
        return (file + rank) % 2 == 0 ? R.color.black : R.color.white;
    }
}
