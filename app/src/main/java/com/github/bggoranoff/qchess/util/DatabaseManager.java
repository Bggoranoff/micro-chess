package com.github.bggoranoff.qchess.util;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static void openOrCreateTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                _ID + " INTEGER PRIMARY KEY, " +
                USER + " VARCHAR, " +
                OPPONENT + " VARCHAR, " +
                COLOR + " VARCHAR, " +
                TIME + " INTEGER, " +
                WINNER + " INTEGER, " +
                HISTORY + " VARCHAR" +
                ")"
        );
    }

    public static String parseHistory(ArrayList<String> history) {
        StringBuilder historyBuilder = new StringBuilder();
        for(int i = 0; i < history.size() - 1; i++) {
            historyBuilder.append(history.get(i) + ",");
        }
        historyBuilder.append(history.get(history.size() - 1));
        return historyBuilder.toString();
    }

    public static List<String> parseHistory(String history) {
        String[] splitHistory = history.split(",");
        return Arrays.asList(splitHistory);
    }

    public static void saveGame(SQLiteDatabase db, String user, String opponent, String color, long time, int winner, ArrayList<String> history) {
        String parsedHistory = parseHistory(history);
        ContentValues values = new ContentValues();
        values.put(USER, user);
        values.put(OPPONENT, opponent);
        values.put(COLOR, color);
        values.put(TIME, time);
        values.put(WINNER, winner);
        values.put(HISTORY, parsedHistory);
        db.insert(TABLE_NAME, null, values);
    }
}
