package com.github.bggoranoff.qchess.util;

import android.content.Context;

public class ResourceSelector {

    public static int getDrawable(Context context, String fileName) {
        return context.getResources().getIdentifier(fileName, "drawable", context.getPackageName());
    }

    public static int getResourceId(Context context, String name) {
        try {
            return context.getResources().getIdentifier(name, "id", context.getPackageName());
        } catch(Exception ex) {
            ex.printStackTrace();
            return -1;
        }
    }
}
