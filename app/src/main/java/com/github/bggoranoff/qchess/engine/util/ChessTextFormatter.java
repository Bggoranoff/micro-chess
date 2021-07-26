package com.github.bggoranoff.qchess.engine.util;

import java.util.Locale;

public class ChessTextFormatter {

    public static String formatTag(int x, int y) {
        return String.format(Locale.ENGLISH, "%c%d", (char) y + 97, x + 1);
    }
}
