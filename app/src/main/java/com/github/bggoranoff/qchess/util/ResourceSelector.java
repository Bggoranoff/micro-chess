package com.github.bggoranoff.qchess.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.core.content.res.ResourcesCompat;

public class ResourceSelector {

    public static Drawable getDrawable(Context context, String fileName) {
        int resourceId = context.getResources().getIdentifier(fileName, "drawable", context.getPackageName());
        Resources resources = context.getResources();
        return ResourcesCompat.getDrawable(resources, resourceId, context.getTheme());
    }
}
