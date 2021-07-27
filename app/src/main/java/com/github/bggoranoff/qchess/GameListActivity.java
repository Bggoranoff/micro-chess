package com.github.bggoranoff.qchess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.github.bggoranoff.qchess.util.ChessAnimator;
import com.github.bggoranoff.qchess.util.DatabaseManager;
import com.github.bggoranoff.qchess.util.PastGamesAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class GameListActivity extends AppCompatActivity {

    private ConstraintLayout layout;
    private ListView listView;
    private ArrayList<Integer> ids;
    private ArrayList<String> titles;
    private ArrayList<Long> dates;
    private ArrayList<Integer> icons;
    private PastGamesAdapter adapter;
    private SQLiteDatabase db;

    private void clickGame(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getApplicationContext(), PastGameActivity.class);
        intent.putExtra("id", ids.get(position));
        startActivity(intent);
    }

    public void resetGames() {
        ids = new ArrayList<>();
        titles = new ArrayList<>();
        dates = new ArrayList<>();
        icons = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseManager.TABLE_NAME, null);
        int idIndex = cursor.getColumnIndex(DatabaseManager._ID);
        int titleIndex = cursor.getColumnIndex(DatabaseManager.OPPONENT);
        int timeIndex = cursor.getColumnIndex(DatabaseManager.TIME);
        int iconIndex = cursor.getColumnIndex(DatabaseManager.WINNER);
        cursor.moveToFirst();
        do {
            ids.add(cursor.getInt(idIndex));
            titles.add(cursor.getString(titleIndex));
            icons.add(cursor.getInt(iconIndex));
            dates.add(cursor.getLong(timeIndex));
        } while(cursor.moveToNext());
        cursor.close();

        Collections.sort(ids, (a, b) -> (int) (dates.get(ids.indexOf(b)) - dates.get(ids.indexOf(a))));
        Collections.sort(titles, (a, b) -> (int) (dates.get(titles.indexOf(b)) - dates.get(titles.indexOf(a))));
        Collections.sort(icons, (a, b) -> (int) (dates.get(icons.indexOf(b)) - dates.get(icons.indexOf(a))));
        Collections.sort(dates, (a, b) -> (int) (b - a));
    }

    public void deleteGame(int position) {
        Toast.makeText(this, "Deleting " + titles.get(position), Toast.LENGTH_SHORT).show();
        db.execSQL("DELETE FROM " + DatabaseManager.TABLE_NAME + " WHERE " + DatabaseManager._ID + " = " + ids.get(position));
        ids.remove(position);
        dates.remove(position);
        titles.remove(position);
        icons.remove(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);
        Objects.requireNonNull(getSupportActionBar()).hide();

        layout = findViewById(R.id.gameListLayout);
        ChessAnimator.animateBackground(layout);

        db = this.openOrCreateDatabase(DatabaseManager.DB_NAME, Context.MODE_PRIVATE, null);
        DatabaseManager.openOrCreateTable(db);
        resetGames();

        listView = findViewById(R.id.gamesListView);
        adapter = new PastGamesAdapter(this, titles, dates, icons);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this::clickGame);
    }
}