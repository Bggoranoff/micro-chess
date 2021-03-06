package com.github.bggoranoff.qchess.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.github.bggoranoff.qchess.R;
import com.github.bggoranoff.qchess.util.ChessAnimator;
import com.github.bggoranoff.qchess.util.DatabaseManager;
import com.github.bggoranoff.qchess.util.Extras;
import com.github.bggoranoff.qchess.util.PastGamesAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class GameListActivity extends AppCompatActivity {

    private ConstraintLayout layout;
    private ListView listView;
    private ArrayList<Integer> ids;
    private ArrayList<String> titles;
    private ArrayList<Long> dates;
    private ArrayList<String> icons;
    private PastGamesAdapter adapter;
    private SQLiteDatabase db;
    private MediaPlayer mp;

    private void clickGame(AdapterView<?> parent, View view, int position, long id) {
        mp.start();
        Intent intent = new Intent(getApplicationContext(), PastGameActivity.class);
        intent.putExtra(Extras.ID, ids.get(position));
        startActivity(intent);
    }

    public void resetGames() {
        ids.clear();
        titles.clear();
        dates.clear();
        icons.clear();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseManager.TABLE_NAME, null);
        int idIndex = cursor.getColumnIndex(DatabaseManager._ID);
        int titleIndex = cursor.getColumnIndex(DatabaseManager.OPPONENT);
        int timeIndex = cursor.getColumnIndex(DatabaseManager.TIME);
        int iconIndex = cursor.getColumnIndex(DatabaseManager.WINNER);
        try {
            cursor.moveToFirst();
            do {
                ids.add(cursor.getInt(idIndex));
                titles.add(cursor.getString(titleIndex));
                icons.add(cursor.getString(iconIndex));
                dates.add(cursor.getLong(timeIndex));
            } while (cursor.moveToNext());
        } catch(CursorIndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
        cursor.close();

        // TODO: create a Game object and sort the list by date
//        Collections.sort(ids, (a, b) -> (int) (dates.get(ids.indexOf(b)) - dates.get(ids.indexOf(a))));
//        Collections.sort(titles, (a, b) -> (int) (dates.get(titles.indexOf(b)) - dates.get(titles.indexOf(a))));
//        Collections.sort(icons, (a, b) -> (int) (dates.get(icons.indexOf(b)) - dates.get(icons.indexOf(a))));
//        Collections.sort(dates, (a, b) -> (int) (b - a));

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        db.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        db = this.openOrCreateDatabase(DatabaseManager.DB_NAME, Context.MODE_PRIVATE, null);
        DatabaseManager.openOrCreateTable(db);
        resetGames();
    }

    public void deleteGame(int position) {
        Toast.makeText(this, "Deleted " + titles.get(position), Toast.LENGTH_SHORT).show();
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

        mp = MediaPlayer.create(getApplicationContext(), R.raw.click);

        layout = findViewById(R.id.gameListLayout);
        ChessAnimator.animateBackground(layout);

        ids = new ArrayList<>();
        titles = new ArrayList<>();
        dates = new ArrayList<>();
        icons = new ArrayList<>();

        listView = findViewById(R.id.gamesListView);
        adapter = new PastGamesAdapter(this, titles, dates, icons);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this::clickGame);
    }
}