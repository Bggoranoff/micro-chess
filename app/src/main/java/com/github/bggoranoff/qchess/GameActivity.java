package com.github.bggoranoff.qchess;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.Toast;

import com.github.bggoranoff.qchess.component.PieceView;
import com.github.bggoranoff.qchess.engine.board.Board;
import com.github.bggoranoff.qchess.engine.move.Move;
import com.github.bggoranoff.qchess.engine.piece.Piece;
import com.github.bggoranoff.qchess.engine.util.ChessColor;
import com.github.bggoranoff.qchess.engine.util.Coordinates;
import com.github.bggoranoff.qchess.util.ChessAnimator;
import com.github.bggoranoff.qchess.util.DatabaseManager;
import com.github.bggoranoff.qchess.util.ResourceSelector;
import com.github.bggoranoff.qchess.util.TextFormatter;
import com.github.bggoranoff.qchess.util.connection.MessageSendTask;
import com.github.bggoranoff.qchess.util.connection.MoveReceiveTask;

import static com.github.bggoranoff.qchess.util.ChessAnimator.getInDps;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Objects;

public class GameActivity extends BoardActivity {

    private Button resignButton;
    private Button drawButton;

    private String opponentIp;
    private String username;
    private String opponentName;
    private SharedPreferences sharedPreferences;
    private int receivePort;
    private int sendPort;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;

    private boolean onTurn;
    private String color;

    public void exit() {
        manager.removeGroup(channel, null);
        finish();
    }
    
    private void resign(View view) {
        cp.start();
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.alert)
                .setTitle("Resign")
                .setMessage("Do you want to resign?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    finishGame(color + " lost by resignation!", R.drawable.loss);
                    sendMessageToOpponent(MoveReceiveTask.RESIGN);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void requestDraw(View view) {
        cp.start();
        sendMessageToOpponent(MoveReceiveTask.ASK_DRAW);
        Toast.makeText(this, "Draw requested!", Toast.LENGTH_SHORT).show();
    }

    public void notifyDrawRequested() {
        runOnUiThread(() -> {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.alert)
                    .setTitle("Draw")
                    .setMessage("Your opponent requested a draw")
                    .setPositiveButton("Accept", (dialog, which) -> {
                        finishGame("Game drawn!", R.drawable.draw);
                        sendMessageToOpponent(MoveReceiveTask.DRAW);
                    })
                    .setNegativeButton("Decline", (dialog, which) -> {
                        sendMessageToOpponent(MoveReceiveTask.NO_DRAW);
                    })
                    .show();
        });
    }

    public void notifyDrawRejected() {
        runOnUiThread(() -> {
            Toast.makeText(this, "Draw rejected!", Toast.LENGTH_SHORT).show();
        });
    }

    public void notifyDrawAccepted() {
        runOnUiThread(() -> {
            finishGame("Game drawn!", R.drawable.draw);
        });
    }
    
    public void finishGame(String message, int iconId) {
        runOnUiThread(() -> {
            new AlertDialog.Builder(this)
                    .setIcon(iconId)
                    .setTitle(message)
                    .setMessage("Do you want to save this game?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        saveGame(iconId);
                        exit();
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        exit();
                    })
                    .show();
        });
    }

    private void displayHistory(View view) {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.save)
                .setTitle("Game History")
                .setMessage(board.formatFullHistory())
                .setPositiveButton("Ok", null)
                .show();
    }

    private void saveGame(int iconId) {
        SQLiteDatabase db = this.openOrCreateDatabase(DatabaseManager.DB_NAME, Context.MODE_PRIVATE, null);
        DatabaseManager.openOrCreateTable(db);
        Date date = new Date();
        long timeInMillis = date.getTime();
        DatabaseManager.saveGame(db, username, opponentName, color, timeInMillis, iconId, board.getHistory(), board.getFormattedHistory());
        db.close();
    }

    @Override
    protected void clickSquare(View view) {
        if(onTurn) {
            if (view.getBackground().getConstantState().equals(Objects.requireNonNull(AppCompatResources.getDrawable(this, R.color.dark_red)).getConstantState())) {
                if (lastPiece != null && currentSquare != null) {
                    Coordinates startCoordinates = TextFormatter.getCoordinates(currentSquare.getTag().toString());
                    Coordinates endCoordinates = TextFormatter.getCoordinates(view.getTag().toString());

                    Move move = new Move(
                            startCoordinates,
                            endCoordinates
                    );
                    performMove(move, view);

                    pieceOnTakeIsThere = "y";
                    pieceTakenIsThere = "y";
                }
            } else if (view.getBackground().getConstantState().equals(Objects.requireNonNull(AppCompatResources.getDrawable(this, R.color.dark_green)).getConstantState())) {
                currentSquare.setBackground(AppCompatResources.getDrawable(this, ChessAnimator.getSquareColor(currentSquare.getTag().toString())));
                if (currentPiece != null) {
                    displaySplitMoves();
                }
            } else if (view.getBackground().getConstantState().equals(Objects.requireNonNull(AppCompatResources.getDrawable(this, R.color.teal_700)).getConstantState())) {
                Coordinates startCoordinates = TextFormatter.getCoordinates(currentSquare.getTag().toString());
                if (firstSplitMove == null) {
                    resetBoardColors();
                } else {
                    completeSplit(view, startCoordinates, startCoordinates);
                    firstSplitMove = null;
                }
            } else if (view.getBackground().getConstantState().equals(Objects.requireNonNull(AppCompatResources.getDrawable(this, R.color.teal_200)).getConstantState())) {
                Coordinates startCoordinates = TextFormatter.getCoordinates(currentSquare.getTag().toString());
                Coordinates endCoordinates = TextFormatter.getCoordinates(view.getTag().toString());
                if (firstSplitMove == null) {
                    initiateSplit(view, startCoordinates, endCoordinates);
                } else {
                    completeSplit(view, startCoordinates, endCoordinates);
                    firstSplitMove = null;
                }
            } else {
                resetBoardColors();
                clickOnEmptySquare(view);
            }
        } else {
            resetBoardColors();
            view.setBackground(AppCompatResources.getDrawable(this, R.color.dark_green));
        }
    }

    @Override
    protected void clickPiece(PieceView pieceView) {
        currentPiece = pieceView;
        View squareView = findViewById(pieceView.getSquareId());
        if(currentPiece.getPiece().getColor().equals(primaryColor) || squareView.getBackground().getConstantState().equals(Objects.requireNonNull(AppCompatResources.getDrawable(this, R.color.dark_red)).getConstantState())) {
            squareView.performClick();
        } else {
            resetBoardColors();
            squareView.setBackground(AppCompatResources.getDrawable(this, R.color.dark_green));
        }
    }

    private void completeSplit(View view, Coordinates startCoordinates, Coordinates endCoordinates) {
        Move secondSplitMove = new Move(
                startCoordinates,
                endCoordinates
        );
        Piece[] resultingPieces = lastPiece.getPiece().split(firstSplitMove, secondSplitMove);

        pieceViews[startCoordinates.getY()][startCoordinates.getX()] = null;
        PieceView firstPieceView = pieceViews[firstSplitMove.getEnd().getY()][firstSplitMove.getEnd().getX()];
        firstPieceView.setPiece(resultingPieces[0]);
        firstPieceView.setOnClickListener(v -> clickPiece(firstPieceView));

        PieceView secondPieceView = new PieceView(this, resultingPieces[1], view.getId());
        secondPieceView.setAlpha(.5f);
        secondPieceView.setLayoutParams(new ConstraintLayout.LayoutParams(getInDps(this, 40), getInDps(this, 40)));
        secondPieceView.setOnClickListener(v -> clickPiece(secondPieceView));
        layout.addView(secondPieceView);
        setPieceLocation(secondPieceView, currentSquare);
        visualiseMove(secondPieceView, view);
        pieceViews[endCoordinates.getY()][endCoordinates.getX()] = secondPieceView;

        String moveMessage = firstSplitMove.toString() + "$" + secondSplitMove.toString();
        board.addToHistory(moveMessage, firstPieceView.getPiece().toString(), firstSplitMove.getEnd(), secondSplitMove.getEnd());
        historyView.setText(board.formatHistory());
        historyBackgroundView.setText(historyView.getText().toString());
        if(onTurn) {
            sendMessageToOpponent("move:" + moveMessage);
            onTurn = false;
        } else {
            onTurn = true;
        }

        resetBoardColors();
        ((ViewManager) lastPiece.getParent()).removeView(lastPiece);
        setScore(board.evaluate());
    }

    @Override
    protected void revealPieceOnTake() {
        if(lastPiece.getAlpha() == 1.0f || lastPiece.getPiece().isThere()) {
            pieceOnTakeIsThere = "y";
        } else {
            pieceOnTakeIsThere = "n";
        }
        super.revealPieceOnTake();
    }

    @Override
    protected void performMove(Move move, View view) {

        super.performMove(move, view);

        String moveMessage = move.toString() + "-" + pieceOnTakeIsThere + "-" + pieceTakenIsThere;
        board.addToHistory(moveMessage, lastPiece.getPiece().toString(), move.getEnd());
        historyView.setText(board.formatHistory());
        historyBackgroundView.setText(historyView.getText().toString());

        if(onTurn) {
            sendMessageToOpponent("move:" + moveMessage);
            onTurn = false;
        } else {
            onTurn = true;
        }

        if(board.isFinished()) {
            finishGame(board.getResult() + " wins by checkmate!", board.getResult().equals(color) ? R.drawable.win : R.drawable.loss);
        }

        lastPiece = null;
    }

    @Override
    protected String performSplit(Move firstMove, Move secondMove) {
        String firstPiece = super.performSplit(firstMove, secondMove);

        board.addToHistory(firstMove.toString() + "$" + secondMove.toString(), firstPiece, firstMove.getEnd(), secondMove.getEnd());
        historyView.setText(board.formatHistory());
        historyBackgroundView.setText(historyView.getText().toString());
        if(!onTurn) {
            onTurn = true;
        }
        return firstPiece;
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

                pieceOnTakeIsThere = "y";
                pieceTakenIsThere = "y";
            }
        });
    }

    private void sendMessageToOpponent(String message) {
        AsyncTask.execute(() -> {
            try {
                InetAddress serverAddress = InetAddress.getByName(opponentIp);
                MessageSendTask sendTask = new MessageSendTask(serverAddress, message, sendPort);
                sendTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } catch(UnknownHostException ex) {
                ex.printStackTrace();
            }
        });
    }
    
    public String getColor() {
        return color;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mp = MediaPlayer.create(getApplicationContext(), R.raw.move);
        cp = MediaPlayer.create(getApplicationContext(), R.raw.click);

        layout = findViewById(R.id.gameLayout);
        ChessAnimator.animateBackground(layout);

        board = new Board();
        board.reset(ChessColor.WHITE);
        pieceViews = new PieceView[8][8];

        color = getIntent().getStringExtra("color");
        primaryColor = color.equals("White") ? ChessColor.WHITE : ChessColor.BLACK;
        onTurn = primaryColor.equals(ChessColor.WHITE);

        boardLayout = findViewById(R.id.boardLayout);
        fillBoard();

        opponentIp = getIntent().getStringExtra("opponentIp");
        opponentName = getIntent().getStringExtra("opponentName");

        sharedPreferences = getSharedPreferences("com.github.bggoranoff.qchess", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "guest");

        currentUsernameView = findViewById(R.id.currentUsernameView);
        currentUsernameView.setText(username);

        opponentUsernameView = findViewById(R.id.opponentUsernameView);
        opponentUsernameView.setText(opponentName);

        scoreView = findViewById(R.id.scoreTextView);
        setScore(0.0f);

        historyView = findViewById(R.id.historyTextView);
        historyView.setText("...");
        historyView.setOnClickListener(this::displayHistory);
        historyBackgroundView = findViewById(R.id.backgroundHistoryTextView);
        historyBackgroundView.setText("...");

        resignButton = findViewById(R.id.resignButton);
        resignButton.setOnClickListener(this::resign);

        drawButton = findViewById(R.id.drawButton);
        drawButton.setOnClickListener(this::requestDraw);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        receivePort = primaryColor.equals(ChessColor.WHITE) ? 8891 : 8890;
        sendPort = primaryColor.equals(ChessColor.WHITE) ? 8890 : 8891;

        MoveReceiveTask receiveTask = new MoveReceiveTask(this, receivePort);
        receiveTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.alert)
                .setTitle("Exit")
                .setMessage("Do you want to exit this game?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    sendMessageToOpponent("exit");
                    exit();
                })
                .setNegativeButton("No", null)
                .show();
    }
}