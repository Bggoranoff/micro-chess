package com.github.bggoranoff.qchess;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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
import com.github.bggoranoff.qchess.util.ResourceSelector;
import com.github.bggoranoff.qchess.util.TextFormatter;
import com.github.bggoranoff.qchess.util.connection.MessageSendTask;
import com.github.bggoranoff.qchess.util.connection.MoveReceiveTask;

import static com.github.bggoranoff.qchess.util.ChessAnimator.getInDps;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;

public class GameActivity extends AppCompatActivity {

    private float pieceOffset = 10.0f;

    private ConstraintLayout layout;
    private TableLayout boardLayout;
    private Button resignButton;
    private Button drawButton;
    private TextView currentUsernameView;
    private TextView opponentUsernameView;

    private String opponentIp;
    private String username;
    private String opponentName;
    private SharedPreferences sharedPreferences;
    private int receivePort;
    private int sendPort;
    private float deviceHeight;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;

    private Board board;
    private PieceView currentPiece = null;
    private PieceView lastPiece = null;
    private View currentSquare = null;
    private PieceView[][] pieceViews;

    private Move firstSplitMove = null;
    private boolean pieceTaken = false;
    private boolean onTurn;
    private ChessColor primaryColor;
    private String color;

    private String pieceOnTakeIsThere = "y";
    private String pieceTakenIsThere = "y";

    public void exit() {
        manager.removeGroup(channel, null);
        finish();
    }
    
    private void resign(View view) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Resign")
                .setMessage("Do you want to resign?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    finishGame(color + " lost by resignation!", false);
                    sendMessageToOpponent(MoveReceiveTask.RESIGN);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void requestDraw(View view) {
        sendMessageToOpponent(MoveReceiveTask.ASK_DRAW);
        Toast.makeText(this, "Draw requested!", Toast.LENGTH_SHORT).show();
    }

    public void notifyDrawRequested() {
        runOnUiThread(() -> {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Draw")
                    .setMessage("Your opponent requested a draw")
                    .setPositiveButton("Accept", (dialog, which) -> {
                        finishGame("Game drawn!", true);
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
            finishGame("Game drawn!", true);
        });
    }
    
    public void finishGame(String message, boolean won) {
        runOnUiThread(() -> {
            new AlertDialog.Builder(this)
                    .setIcon(won ? android.R.drawable.ic_input_add : android.R.drawable.ic_delete)
                    .setTitle(message)
                    .setMessage("Do you want to save this game?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // TODO: save game
                        exit();
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        exit();
                    })
                    .show();
        });
    }

    private void clickSquare(View view) {
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
        }
    }

    private void clickPiece(PieceView pieceView) {
        currentPiece = pieceView;
        View squareView = findViewById(pieceView.getSquareId());
        squareView.performClick();
    }

    private void clickOnEmptySquare(View view) {
        if(currentPiece != null) {
            Piece containedPiece = currentPiece.getPiece();
            List<String> availableSquares = containedPiece.getAvailableSquares();
            for(String square : availableSquares) {
                Coordinates squareCoordinates = TextFormatter.getCoordinates(square);
                int squareId = ResourceSelector.getResourceId(
                        this,
                        "cell" + squareCoordinates.getX() + "" + squareCoordinates.getY()
                );
                View v = findViewById(squareId);
                v.setBackground(AppCompatResources.getDrawable(this, R.color.dark_red));
            }
            lastPiece = currentPiece;
            currentPiece = null;
        }
        view.setBackground(AppCompatResources.getDrawable(this, R.color.dark_green));
        currentSquare = view;
    }

    private void displaySplitMoves() {
        resetBoardColors();
        if(currentPiece.getPiece().getProbability() == 1.0f) {
            Piece containedPiece = currentPiece.getPiece();
            List<String> availableSquares = containedPiece.getAvailableSplitSquares();
            if (availableSquares.size() > 0) {
                for (String square : availableSquares) {
                    Coordinates squareCoordinates = TextFormatter.getCoordinates(square);
                    int squareId = ResourceSelector.getResourceId(
                            this,
                            "cell" + squareCoordinates.getX() + "" + squareCoordinates.getY()
                    );
                    View v = findViewById(squareId);
                    v.setBackground(AppCompatResources.getDrawable(this, R.color.teal_200));
                }
                currentSquare.setBackground(AppCompatResources.getDrawable(this, R.color.teal_700));
            }
            currentPiece = null;
        }
    }

    private void initiateSplit(View view, Coordinates startCoordinates, Coordinates endCoordinates) {
        firstSplitMove = new Move(startCoordinates, endCoordinates);
        PieceView pieceView = new PieceView(this, lastPiece.getPiece(), view.getId());
        pieceView.setAlpha(.5f);
        pieceView.setLayoutParams(new ConstraintLayout.LayoutParams(getInDps(this, 40), getInDps(this, 40)));
        pieceView.setOnClickListener(null);
        layout.addView(pieceView);
        setPieceLocation(pieceView, currentSquare);
        visualiseMove(pieceView, view);
        pieceViews[endCoordinates.getY()][endCoordinates.getX()] = pieceView;
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
        board.getHistory().add(moveMessage);
        if(onTurn) {
            sendMessageToOpponent("move:" + moveMessage);
            onTurn = false;
        } else {
            onTurn = true;
        }

        resetBoardColors();
        ((ViewManager) lastPiece.getParent()).removeView(lastPiece);
    }

    private void revealPieceOnTake() {
        if(lastPiece.getAlpha() == 1.0f || lastPiece.getPiece().isThere()) {
            pieceOnTakeIsThere = "y";
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
            pieceOnTakeIsThere = "n";
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
                pieceTakenIsThere = "y";
                Coordinates pairCoordinates = currentPiece.getPiece().getPair().getSquare().getCoordinates();
                PieceView pair = pieceViews[pairCoordinates.getY()][pairCoordinates.getX()];
                ((ViewManager) pair.getParent()).removeView(pair);
                pieceViews[pairCoordinates.getY()][pairCoordinates.getX()] = null;
                currentPiece.getPiece().setPair(null);
                currentPiece.setAlpha(1.0f);
            } else if (currentPiece.getPiece().getPair() != null) {
                pieceTakenIsThere = "n";
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

        String moveMessage = move.toString() + "-" + pieceOnTakeIsThere + "-" + pieceTakenIsThere;
        board.getHistory().add(moveMessage);
        if(onTurn) {
            sendMessageToOpponent("move:" + moveMessage);
            onTurn = false;
        } else {
            onTurn = true;
        }

        resetBoardColors();
        currentSquare = null;
        lastPiece = null;
        currentPiece = null;
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
        board.getHistory().add(firstMove.toString() + "$" + secondMove.toString());
        if(!onTurn) {
            onTurn = true;
        }
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

    private void setPieceLocation(PieceView pieceView, View squareView) {
        int[] location = new int[2];
        squareView.getLocationOnScreen(location);
        pieceView.setX(location[0]);
        pieceView.setY(location[1] - (float) 8 * squareView.getHeight() / 10 + pieceOffset);
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

                pieceOnTakeIsThere = "y";
                pieceTakenIsThere = "y";
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

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        deviceHeight = (float) metrics.heightPixels / metrics.ydpi;
        pieceOffset = (4.780f - deviceHeight) * 100;

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
                .setIcon(android.R.drawable.ic_dialog_alert)
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