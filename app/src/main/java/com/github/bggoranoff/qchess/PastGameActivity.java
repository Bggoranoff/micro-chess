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
import android.view.ViewManager;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.bggoranoff.qchess.component.PieceView;
import com.github.bggoranoff.qchess.engine.board.Board;
import com.github.bggoranoff.qchess.engine.board.Square;
import com.github.bggoranoff.qchess.engine.move.Move;
import com.github.bggoranoff.qchess.engine.piece.King;
import com.github.bggoranoff.qchess.engine.piece.Pawn;
import com.github.bggoranoff.qchess.engine.piece.Piece;
import com.github.bggoranoff.qchess.engine.util.ChessColor;
import com.github.bggoranoff.qchess.engine.util.ChessTextFormatter;
import com.github.bggoranoff.qchess.engine.util.Coordinates;
import com.github.bggoranoff.qchess.util.ChessAnimator;
import com.github.bggoranoff.qchess.util.DatabaseManager;
import com.github.bggoranoff.qchess.util.ResourceSelector;

import java.util.Arrays;
import java.util.Locale;
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
    private ImageView forwardView;

    private Board board;
    private PieceView currentPiece = null;
    private PieceView lastPiece = null;
    private View currentSquare = null;
    private View lastSquare = null;
    private PieceView[][] pieceViews;
    private ChessColor primaryColor;
    private boolean pieceTaken = false;
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

    private void resetBoardColors() {
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                Square currentSquare = board.get(i, j);
                int squareId = ResourceSelector.getResourceId(this, currentSquare.getId());
                View squareView = findViewById(squareId);
                squareView.setBackground(
                        AppCompatResources.getDrawable(
                                this,
                                currentSquare.getColor().equals(ChessColor.WHITE) ? R.color.white : R.color.black
                        )
                );
            }
        }
    }

    private void setScore(float score) {
        scoreView.setText(String.format(Locale.ENGLISH, (score > 0 ? "+" : "") + "%.1f", score));
        scoreView.setTextColor(getResources().getColor(score < 0 ? R.color.dark_red : R.color.dark_green, getTheme()));
    }

    private void visualiseMove(PieceView pieceView, View squareView) {
        squareView.post(() -> {
            int[] location = new int[2];
            squareView.getLocationOnScreen(location);
            pieceView.animate()
                    .y(location[1] - (float) 8 * squareView.getHeight() / 10 + pieceOffset)
                    .x(location[0])
                    .setDuration(350)
                    .start();
            pieceView.setY(location[1] - (float) 8 * squareView.getHeight() / 10 + pieceOffset);
        });
    }

    public void parseMove(String moveMessage) {
        runOnUiThread(() -> {
            if(moveMessage.contains("$")) {
                parseSplit(moveMessage);
            } else {
                String[] decomposedMove = moveMessage.split("-");
                String[] decomposedStart = decomposedMove[0].split("\\s+");
                String[] decomposedEnd = decomposedMove[1].split("\\s+");

                boolean takingPieceIsThere = decomposedMove[2].equals("y");
                boolean takenPieceIsThere = decomposedMove[3].equals("y");

                Coordinates startCoordinates = new Coordinates(
                        Integer.parseInt(decomposedStart[0]),
                        Integer.parseInt(decomposedStart[1])
                );
                Coordinates endCoordinates = new Coordinates(
                        Integer.parseInt(decomposedEnd[0]),
                        Integer.parseInt(decomposedEnd[1])
                );

                Move move = new Move(startCoordinates, endCoordinates);

                View view = findViewById(ResourceSelector.getResourceId(
                        this,
                        "cell" + endCoordinates.getY() + "" + endCoordinates.getX()
                ));
                currentSquare = findViewById(ResourceSelector.getResourceId(
                        this,
                        "cell" + startCoordinates.getY() + "" + startCoordinates.getX()
                ));
                currentSquare.setBackground(AppCompatResources.getDrawable(this, R.color.teal_700));

                lastPiece = pieceViews[startCoordinates.getY()][startCoordinates.getX()];
                lastPiece.getPiece().setThere(takingPieceIsThere);

                currentPiece = pieceViews[endCoordinates.getY()][endCoordinates.getX()];

                if (currentPiece != null) {
                    currentPiece.getPiece().setThere(takenPieceIsThere);
                }

                performMove(move, view);
            }
        });
    }

    private void parseSplit(String moveMessage) {
        String[] decomposedSplit = moveMessage.split("\\$");
        String[] decomposedFirstMove = decomposedSplit[0].split("-");
        String[] decomposedSecondMove = decomposedSplit[1].split("-");

        String[] decomposedStart = decomposedFirstMove[0].split("\\s+");
        String[] decomposedFirstEnd = decomposedFirstMove[1].split("\\s+");
        String[] decomposedSecondEnd = decomposedSecondMove[1].split("\\s+");

        Coordinates startCoordinates = new Coordinates(
                Integer.parseInt(decomposedStart[0]),
                Integer.parseInt(decomposedStart[1])
        );
        Coordinates firstEndCoordinates = new Coordinates(
                Integer.parseInt(decomposedFirstEnd[0]),
                Integer.parseInt(decomposedFirstEnd[1])
        );
        Coordinates secondEndCoordinates = new Coordinates(
                Integer.parseInt(decomposedSecondEnd[0]),
                Integer.parseInt(decomposedSecondEnd[1])
        );

        Move firstMove = new Move(startCoordinates, firstEndCoordinates);
        Move secondMove = new Move(startCoordinates, secondEndCoordinates);

        currentSquare = findViewById(ResourceSelector.getResourceId(
                this,
                "cell" + startCoordinates.getY() + "" + startCoordinates.getX()
        ));
        lastPiece = pieceViews[startCoordinates.getY()][startCoordinates.getX()];

        performSplit(firstMove, secondMove);
    }

    private void performSplit(Move firstMove, Move secondMove) {
        Piece[] resultingPieces = lastPiece.getPiece().split(firstMove, secondMove);

        View firstView = findViewById(ResourceSelector.getResourceId(
                this,
                "cell" + firstMove.getEnd().getY() + "" + firstMove.getEnd().getX()
        ));
        View secondView = findViewById(ResourceSelector.getResourceId(
                this,
                "cell" + secondMove.getEnd().getY() + "" + secondMove.getEnd().getX()
        ));

        PieceView firstPieceView = new PieceView(this, resultingPieces[0], firstView.getId());
        firstPieceView.setAlpha(.5f);
        firstPieceView.setLayoutParams(new ConstraintLayout.LayoutParams(getInDps(this, 40), getInDps(this, 40)));
        layout.addView(firstPieceView);
        setPieceLocation(firstPieceView, currentSquare);
        visualiseMove(firstPieceView, firstView);
        pieceViews[firstMove.getEnd().getY()][firstMove.getEnd().getX()] = firstPieceView;
        firstPieceView.setOnClickListener(v -> clickPiece(firstPieceView));

        PieceView secondPieceView = new PieceView(this, resultingPieces[1], secondView.getId());
        secondPieceView.setAlpha(.5f);
        secondPieceView.setLayoutParams(new ConstraintLayout.LayoutParams(getInDps(this, 40), getInDps(this, 40)));
        secondPieceView.setOnClickListener(v -> clickPiece(secondPieceView));
        layout.addView(secondPieceView);
        setPieceLocation(secondPieceView, currentSquare);
        visualiseMove(secondPieceView, secondView);
        pieceViews[secondMove.getEnd().getY()][secondMove.getEnd().getX()] = secondPieceView;

        resetBoardColors();
        ((ViewManager) lastPiece.getParent()).removeView(lastPiece);
        pieceViews[firstMove.getStart().getY()][firstMove.getStart().getX()] = null;
        setScore(board.evaluate());
    }

    private void revealPieceOnTake() {
        if(lastPiece.getAlpha() == 1.0f || lastPiece.getPiece().isThere()) {
            lastPiece.getPiece().setThere(true);
            ((ViewManager) currentPiece.getParent()).removeView(currentPiece);

            if (currentPiece.getPiece().getId().equals(lastPiece.getPiece().getId())) {
                lastPiece.setAlpha(1.0f);
                lastPiece.getPiece().setProbability(1.0f);
            } else if(lastPiece.getPiece().getPair() != null) {
                pieceTaken = true;
                lastPiece.setAlpha(1.0f);
                Coordinates pairCoordinates = lastPiece.getPiece().getPair().getSquare().getCoordinates();
                PieceView pair = pieceViews[pairCoordinates.getY()][pairCoordinates.getX()];
                ((ViewManager) pair.getParent()).removeView(pair);
                pieceViews[pairCoordinates.getY()][pairCoordinates.getX()] = null;
                lastPiece.getPiece().setPair(null);
            } else {
                pieceTaken = true;
            }
        } else {
            Coordinates pairCoordinates = lastPiece.getPiece().getPair().getSquare().getCoordinates();
            PieceView pair = pieceViews[pairCoordinates.getY()][pairCoordinates.getX()];
            pair.setAlpha(1.0f);
            ((ViewManager) lastPiece.getParent()).removeView(lastPiece);
            pair.getPiece().setPair(null);
        }
    }

    private void revealTakenPiece() {
        if(pieceTaken) {
            if (currentPiece.getPiece().isThere() && currentPiece.getPiece().getPair() != null) {
                Coordinates pairCoordinates = currentPiece.getPiece().getPair().getSquare().getCoordinates();
                PieceView pair = pieceViews[pairCoordinates.getY()][pairCoordinates.getX()];
                ((ViewManager) pair.getParent()).removeView(pair);
                pieceViews[pairCoordinates.getY()][pairCoordinates.getX()] = null;
                currentPiece.getPiece().setPair(null);
                currentPiece.setAlpha(1.0f);
            } else if (currentPiece.getPiece().getPair() != null) {
                Coordinates pairCoordinates = currentPiece.getPiece().getPair().getSquare().getCoordinates();
                PieceView pair = pieceViews[pairCoordinates.getY()][pairCoordinates.getX()];
                pair.setAlpha(1.0f);
                pair.getPiece().setPair(null);
            }
        }
        pieceTaken = false;
    }

    private void performMove(Move move, View view) {
        Coordinates startCoordinates = move.getStart();
        Coordinates endCoordinates = move.getEnd();

        lastPiece.getPiece().move(move);

        pieceViews[startCoordinates.getY()][startCoordinates.getX()] = null;
        pieceViews[endCoordinates.getY()][endCoordinates.getX()] = lastPiece;

        if(Math.abs(move.getStart().getX() - move.getEnd().getX()) == 2 && lastPiece.getPiece() instanceof King) {
            performCastling(move);
        }

        if(lastPiece.getPiece() instanceof Pawn && move.getStart().getX() != move.getEnd().getX() && currentPiece == null) {
            performEnPassant(move);
        }

        lastPiece.setSquareId(view.getId());
        visualiseMove(lastPiece, view);

        if(currentPiece != null) {
            revealPieceOnTake();
            revealTakenPiece();

            Coordinates pieceCoordinates = new Coordinates(move.getStart().getX(), move.getStart().getY()); // TODO: see if fixed, getStart()
            pieceViews[pieceCoordinates.getY()][pieceCoordinates.getX()] = null;
        }

        resetBoardColors();
        currentSquare = null;
        lastPiece = null;
        currentPiece = null;

        setScore(board.evaluate());
    }

    private void performCastling(Move move) {
        PieceView rook;
        Coordinates rookCoordinates;
        Coordinates updatedRookCoordinates;
        if(move.getStart().getX() > move.getEnd().getX()) {
            rookCoordinates = new Coordinates(move.getStart().getX() - 4, move.getStart().getY());
            rook = pieceViews[rookCoordinates.getY()][rookCoordinates.getX()];
            updatedRookCoordinates = new Coordinates(rookCoordinates.getX() + 3, rookCoordinates.getY());
        } else {
            rookCoordinates = new Coordinates(move.getStart().getX() + 3, move.getStart().getY());
            rook = pieceViews[rookCoordinates.getY()][rookCoordinates.getX()];
            updatedRookCoordinates = new Coordinates(rookCoordinates.getX() - 2, rookCoordinates.getY());
        }
        performCastling(rook, rookCoordinates, updatedRookCoordinates);
    }

    private void performCastling(PieceView rook, Coordinates rookCoordinates, Coordinates updatedRookCoordinates) {
        if(rook != null) {
            Move rookMove = new Move(
                    rookCoordinates,
                    updatedRookCoordinates
            );
            rook.getPiece().move(rookMove);

            pieceViews[rookCoordinates.getY()][rookCoordinates.getX()] = null;
            pieceViews[updatedRookCoordinates.getY()][updatedRookCoordinates.getX()] = null;

            int rookSquareId = ResourceSelector.getResourceId(
                    this,
                    "cell" + updatedRookCoordinates.getY() + "" + updatedRookCoordinates.getX()
            );
            View rookSquare = findViewById(rookSquareId);
            rook.setSquareId(rookSquareId);
            visualiseMove(rook, rookSquare);
        }
    }

    private void performEnPassant(Move move) {
        Coordinates pieceCoordinates = new Coordinates(move.getEnd().getX(), move.getStart().getY());
        PieceView pieceToTake = pieceViews[pieceCoordinates.getY()][pieceCoordinates.getX()];
        if(pieceToTake != null) {
            board.take(move.getEnd().getX(), move.getStart().getY());
            ((ViewManager) pieceToTake.getParent()).removeView(pieceToTake);
            pieceViews[pieceCoordinates.getY()][pieceCoordinates.getX()] = null;
        }
    }

    private void performNextMove(View view) {
        System.out.println(Arrays.toString(gameHistory));
        parseMove(gameHistory[currentMove]);
        currentMove++;
        if(currentMove >= gameHistory.length) {
            view.setEnabled(false);
        }
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

        forwardView = findViewById(R.id.forwardImageView);
        forwardView.setOnClickListener(this::performNextMove);

        cursor.close();
    }
}