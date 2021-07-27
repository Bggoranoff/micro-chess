package com.github.bggoranoff.qchess;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.bggoranoff.qchess.component.PieceView;
import com.github.bggoranoff.qchess.engine.board.Board;
import com.github.bggoranoff.qchess.engine.board.Square;
import com.github.bggoranoff.qchess.engine.util.ChessColor;
import com.github.bggoranoff.qchess.engine.util.ChessTextFormatter;
import com.github.bggoranoff.qchess.util.ChessAnimator;
import com.github.bggoranoff.qchess.util.DatabaseManager;
import com.github.bggoranoff.qchess.util.ResourceSelector;

import java.util.Objects;

import static com.github.bggoranoff.qchess.util.ChessAnimator.getInDps;

public class PastGameActivity extends AppCompatActivity {

    private float pieceOffset = 10.0f;
    private float deviceHeight;

    private ConstraintLayout layout;
    private TableLayout boardLayout;
    private TextView currentUsernameView;
    private TextView opponentUsernameView;
    private TextView scoreView;
    private TextView historyView;
    private TextView historyBackgroundView;

    private Board board;
    private PieceView currentPiece = null;
    private PieceView lastPiece = null;
    private View currentSquare = null;
    private View lastSquare = null;
    private PieceView[][] pieceViews;
    private ChessColor primaryColor;

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
                .setIcon(android.R.drawable.ic_menu_save)
                .setTitle("Game History")
                .setMessage(fullHistory)
                .setPositiveButton("Ok", null)
                .show();
    }

    private void clickSquare(View view) {
        if(lastSquare != null) {
            lastSquare.setBackground(AppCompatResources.getDrawable(this, ChessAnimator.getSquareColor(lastSquare.getTag().toString())));
        }
        view.setBackground(AppCompatResources.getDrawable(this, R.color.dark_green));
        lastSquare = view;
    }

    private void clickPiece(PieceView pieceView) {
        View squareView = findViewById(pieceView.getSquareId());
        squareView.performClick();
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void fillBoard() {
        for(int i = 0; i < 8; i++) {
            int rowId = ResourceSelector.getResourceId(this, "row" + i);
            TableRow currentRow = boardLayout.findViewById(rowId);
            for(int j = 0; j < 8; j++) {
                int y = primaryColor.equals(ChessColor.WHITE) ? 7 - i : i;
                Square currentSquare = board.get(j, y);
                int squareId = ResourceSelector.getResourceId(this, currentSquare.getId());
                View squareView = new View(this);
                squareView.setId(squareId);
                squareView.setLayoutParams(new TableRow.LayoutParams(getInDps(this, 40), getInDps(this, 40)));
                squareView.setTag(ChessTextFormatter.formatTag(y, j));
                squareView.setBackground(AppCompatResources.getDrawable(
                        this,
                        board.get(j, y).getColor().equals(ChessColor.WHITE) ? R.color.white : R.color.black
                ));
                currentRow.addView(squareView);
                squareView.setOnClickListener(this::clickSquare);
                if(currentSquare.getPiece() != null) {
                    PieceView pieceView = new PieceView(this, currentSquare.getPiece(), squareId);
                    pieceViews[currentSquare.getCoordinates().getY()][currentSquare.getCoordinates().getX()] = pieceView;
                    pieceView.setLayoutParams(new ConstraintLayout.LayoutParams(getInDps(this, 40), getInDps(this, 40)));
                    layout.addView(pieceView);
                    squareView.post(() -> {
                        setPieceLocation(pieceView, squareView);
                    });
                    pieceView.setOnClickListener(v -> clickPiece(pieceView));
                }
            }
        }
    }

    private void setPieceLocation(PieceView pieceView, View squareView) {
        int[] location = new int[2];
        squareView.getLocationOnScreen(location);
        pieceView.setX(location[0]);
        pieceView.setY(location[1] - (float) 8 * squareView.getHeight() / 10 + pieceOffset);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_game);
        Objects.requireNonNull(getSupportActionBar()).hide();

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

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        deviceHeight = (float) metrics.heightPixels / metrics.ydpi;
        pieceOffset = (4.780f - deviceHeight) * 100;

        primaryColor = cursor.getString(colorIndex).equals("White") ? ChessColor.WHITE : ChessColor.BLACK;
        board = new Board();
        board.reset(ChessColor.WHITE);
        pieceViews = new PieceView[8][8];
        boardLayout = findViewById(R.id.boardLayout);
        fillBoard();

        scoreView = findViewById(R.id.scoreTextView);
        scoreView.setText("0.0");
        scoreView.setTextColor(getResources().getColor(R.color.dark_green, getTheme()));

        gameHistory = cursor.getString(historyIndex).split(",");
        gameFormattedHistory = cursor.getString(formattedHistoryIndex).split(",");

        StringBuilder formattedHistoryBuilder = new StringBuilder();
        for(int i = gameFormattedHistory.length - 1; i >= 0 && i >= gameFormattedHistory.length - 4; i--) {
            formattedHistoryBuilder.append(gameFormattedHistory[i]).append(" ");
        }
        formattedHistoryBuilder.deleteCharAt(formattedHistoryBuilder.length() - 1);
        formattedHistoryBuilder.append("...");

        historyView = findViewById(R.id.historyTextView);
        historyView.setText(formattedHistoryBuilder.toString());
        historyView.setOnClickListener(this::displayHistory);
        historyBackgroundView = findViewById(R.id.backgroundHistoryTextView);
        historyBackgroundView.setText(formattedHistoryBuilder.toString());
    }
}