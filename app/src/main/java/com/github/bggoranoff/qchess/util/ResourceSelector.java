package com.github.bggoranoff.qchess.util;

import android.content.Context;

public class ResourceSelector {

    public static int getDrawable(Context context, String fileName) {
        return context.getResources().getIdentifier(fileName, "drawable", context.getPackageName());
    }
}
