package com.github.bggoranoff.qchess.util;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;

public class DatabaseManager {
    public static final String _ID = BaseColumns._ID;
    public static final String DB_NAME = "Games";
    public static final String TABLE_NAME = "games";
    public static final String USER = "user";
    public static final String OPPONENT = "opponent";
    public static final String TIME = "time";
    public static final String COLOR = "color";
    public static final String WINNER = "winner";
    public static final String HISTORY = "history";
    public static final String FORMATTED_HISTORY = "formattedHistory";

    public static void openOrCreateTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                _ID + " INTEGER PRIMARY KEY, " +
                USER + " VARCHAR, " +
                OPPONENT + " VARCHAR, " +
                COLOR + " VARCHAR, " +
                TIME + " INTEGER, " +
                WINNER + " VARCHAR, " +
                HISTORY + " VARCHAR," +
                FORMATTED_HISTORY + " VARCHAR" +
                ")"
        );
    }

    public static String parseHistory(ArrayList<String> history) {
        StringBuilder historyBuilder = new StringBuilder();
        for(int i = 0; i < history.size() - 1; i++) {
            historyBuilder.append(history.get(i)).append(",");
        }
        historyBuilder.append(history.get(history.size() - 1));
        return historyBuilder.toString();
    }

    public static void saveGame(SQLiteDatabase db, String user, String opponent, String color, long time, String icon, ArrayList<String> history, ArrayList<String> formattedHistory) {
        String parsedHistory = parseHistory(history);
        String parsedFormattedHistory = parseHistory(formattedHistory);
        ContentValues values = new ContentValues();
        values.put(USER, user);
        values.put(OPPONENT, opponent);
        values.put(COLOR, color);
        values.put(TIME, time);
        values.put(WINNER, icon);
        values.put(HISTORY, parsedHistory);
        values.put(FORMATTED_HISTORY, parsedFormattedHistory);
        db.insert(TABLE_NAME, null, values);
    }
}
