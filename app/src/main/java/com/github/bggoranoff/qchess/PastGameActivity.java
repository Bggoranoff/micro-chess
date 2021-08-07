package com.github.bggoranoff.qchess;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewManager;
import android.widget.ImageView;

import com.github.bggoranoff.qchess.component.PieceView;
import com.github.bggoranoff.qchess.engine.board.Board;
import com.github.bggoranoff.qchess.engine.move.Move;
import com.github.bggoranoff.qchess.engine.util.ChessColor;
import com.github.bggoranoff.qchess.util.ChessAnimator;
import com.github.bggoranoff.qchess.util.DatabaseManager;

import java.util.Objects;

public class PastGameActivity extends BoardActivity {

    private ImageView forwardView;
    private ImageView resetView;

    private View lastSquare = null;
    private int currentMove = 0;

    private SQLiteDatabase db;
    private int gameId;
    private String[] gameHistory;
    private String[] gameFormattedHistory;

    private void displayHistory(View view) {
        StringBuilder fullHistory = new StringBuilder();
        for(String move : gameFormattedHistory) {
            fullHistory.append(move).append(" ");
        }

        new AlertDialog.Builder(this)
                .setIcon(R.drawable.save)
                .setTitle("Game History")
                .setMessage(fullHistory)
                .setPositiveButton("Ok", null)
                .show();
    }

    @Override
    protected void clickSquare(View view) {
        if(lastSquare != null) {
            lastSquare.setBackground(AppCompatResources.getDrawable(this, ChessAnimator.getSquareColor(lastSquare.getTag().toString())));
        }
        view.setBackground(AppCompatResources.getDrawable(this, R.color.dark_green));
        lastSquare = view;
    }

    @Override
    protected void clickPiece(PieceView pieceView) {
        View squareView = findViewById(pieceView.getSquareId());
        squareView.performClick();
    }

    @Override
    protected void performMove(Move move, View view) {
        super.performMove(move, view);
        lastPiece = null;
    }

    private void performNextMove(View view) {
        cp.start();
        parseMove(gameHistory[currentMove]);
        currentMove++;
        if(currentMove >= gameHistory.length) {
            view.setEnabled(false);
        }
    }

    protected void resetBoard(View view) {
        super.resetBoard(view);
        currentMove = 0;
        forwardView.setEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_game);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mp = MediaPlayer.create(getApplicationContext(), R.raw.move);
        cp = MediaPlayer.create(getApplicationContext(), R.raw.click);

        layout = findViewById(R.id.pastGameLayout);
        ChessAnimator.animateBackground(layout);

        db = this.openOrCreateDatabase(DatabaseManager.DB_NAME, Context.MODE_PRIVATE, null);
        DatabaseManager.openOrCreateTable(db);
        gameId = getIntent().getIntExtra("id", 0);

        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseManager.TABLE_NAME + " WHERE " + DatabaseManager._ID + " = " + gameId, null);
        int userIndex = cursor.getColumnIndex(DatabaseManager.USER);
        int opponentIndex = cursor.getColumnIndex(DatabaseManager.OPPONENT);
        int colorIndex = cursor.getColumnIndex(DatabaseManager.COLOR);
        int historyIndex = cursor.getColumnIndex(DatabaseManager.HISTORY);
        int formattedHistoryIndex = cursor.getColumnIndex(DatabaseManager.FORMATTED_HISTORY);
        cursor.moveToFirst();

        currentUsernameView = findViewById(R.id.currentUsernameView);
        currentUsernameView.setText(cursor.getString(userIndex));

        opponentUsernameView = findViewById(R.id.opponentUsernameView);
        opponentUsernameView.setText(cursor.getString(opponentIndex));

        scoreView = findViewById(R.id.scoreTextView);
        setScore(0.0f);

        gameHistory = cursor.getString(historyIndex).split(",");
        gameFormattedHistory = cursor.getString(formattedHistoryIndex).split(",");

        primaryColor = cursor.getString(colorIndex).equals("White") ? ChessColor.WHITE : ChessColor.BLACK;
        cursor.close();
        db.close();

        board = new Board();
        board.reset(ChessColor.WHITE);
        pieceViews = new PieceView[8][8];
        boardLayout = findViewById(R.id.boardLayout);
        fillBoard();

        StringBuilder formattedHistoryBuilder = new StringBuilder();
        for(int i = gameFormattedHistory.length - 1; i >= 0 && i >= gameFormattedHistory.length - 3; i--) {
            formattedHistoryBuilder.append(gameFormattedHistory[i]).append(" ");
        }
        formattedHistoryBuilder.deleteCharAt(formattedHistoryBuilder.length() - 1);
        formattedHistoryBuilder.append("...");

        historyView = findViewById(R.id.historyTextView);
        historyView.setText(formattedHistoryBuilder.toString());
        historyView.setOnClickListener(this::displayHistory);
        historyBackgroundView = findViewById(R.id.backgroundHistoryTextView);
        historyBackgroundView.setText(formattedHistoryBuilder.toString());

        forwardView = findViewById(R.id.forwardImageView);
        forwardView.setOnClickListener(this::performNextMove);

        resetView = findViewById(R.id.resetImageView);
        resetView.setOnClickListener(this::resetBoard);
    }
}