package com.github.bggoranoff.qchess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.view.ViewManager;

import com.github.bggoranoff.qchess.component.PieceView;
import com.github.bggoranoff.qchess.engine.board.Board;
import com.github.bggoranoff.qchess.engine.board.Square;
import com.github.bggoranoff.qchess.engine.move.Move;
import com.github.bggoranoff.qchess.engine.piece.King;
import com.github.bggoranoff.qchess.engine.piece.Pawn;
import com.github.bggoranoff.qchess.engine.piece.Piece;
import com.github.bggoranoff.qchess.engine.util.ChessColor;
import com.github.bggoranoff.qchess.engine.util.Coordinates;
import com.github.bggoranoff.qchess.util.ChessAnimator;
import com.github.bggoranoff.qchess.util.ResourceSelector;
import com.github.bggoranoff.qchess.util.TextFormatter;

import static com.github.bggoranoff.qchess.util.ChessAnimator.getInDps;

import java.util.List;
import java.util.Objects;

public class GameActivity extends AppCompatActivity {

    private static final int PIECE_OFFSET = 10;

    private ConstraintLayout layout;

    private Board board;
    private PieceView currentPiece = null;
    private PieceView lastPiece = null;
    private View currentSquare = null;
    private PieceView[][] pieceViews;

    private void clickSquare(View view) {
        if(view.getBackground().getConstantState().equals(Objects.requireNonNull(AppCompatResources.getDrawable(this, R.color.dark_red)).getConstantState())) {
            // TODO: check if there is a split to perform
            if(lastPiece != null && currentSquare != null) {
                Coordinates startCoordinates = TextFormatter.getCoordinates(currentSquare.getTag().toString());
                Coordinates endCoordinates = TextFormatter.getCoordinates(view.getTag().toString());

                Move move = new Move(
                        startCoordinates,
                        endCoordinates
                );
                lastPiece.getPiece().move(move);

                pieceViews[startCoordinates.getY()][startCoordinates.getX()] = null;
                pieceViews[endCoordinates.getY()][endCoordinates.getX()] = lastPiece;

                // Castling check
                if(Math.abs(move.getStart().getX() - move.getEnd().getX()) == 2 && lastPiece.getPiece() instanceof King) {
                    PieceView rook;
                    Coordinates rookCoordinates;
                    Coordinates updatedRookCoordinates;
                    if(move.getStart().getX() > move.getEnd().getX()) {
                        rookCoordinates = new Coordinates(move.getStart().getX() - 3, move.getStart().getY());
                        rook = pieceViews[rookCoordinates.getY()][rookCoordinates.getX()];
                        updatedRookCoordinates = new Coordinates(rookCoordinates.getX() + 2, rookCoordinates.getY());
                    } else {
                        rookCoordinates = new Coordinates(move.getStart().getX() + 4, move.getStart().getY());
                        rook = pieceViews[rookCoordinates.getY()][rookCoordinates.getX()];
                        updatedRookCoordinates = new Coordinates(rookCoordinates.getX() - 3, rookCoordinates.getY());
                    }

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

                if(lastPiece.getPiece() instanceof Pawn && move.getStart().getX() != move.getEnd().getX() && currentPiece == null) {
                    Coordinates pieceCoordinates = new Coordinates(move.getEnd().getX(), move.getStart().getY());
                    PieceView pieceToTake = pieceViews[pieceCoordinates.getY()][pieceCoordinates.getX()];
                    if(pieceToTake != null) {
                        board.take(move.getEnd().getX(), move.getStart().getY());
                        ((ViewManager) pieceToTake.getParent()).removeView(pieceToTake);
                        pieceViews[pieceCoordinates.getY()][pieceCoordinates.getX()] = null;
                    }
                }

                lastPiece.setSquareId(view.getId());
                visualiseMove(lastPiece, view);

                if(currentPiece != null) {
                    ((ViewManager) currentPiece.getParent()).removeView(currentPiece);
                }

                resetBoardColors();
                currentSquare = null;
                lastPiece = null;
            }
        } else if(view.getBackground().getConstantState().equals(Objects.requireNonNull(AppCompatResources.getDrawable(this, R.color.dark_green)).getConstantState())) {
            // TODO: check if a split should be initiated
            currentSquare.setBackground(AppCompatResources.getDrawable(this, ChessAnimator.getSquareColor(currentSquare.getTag().toString())));
            if(currentPiece != null) {
                resetBoardColors();
                currentPiece = null;
            }
        } else {
            resetBoardColors();
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
    }

    private void clickPiece(PieceView pieceView) {
        currentPiece = pieceView;
        View squareView = findViewById(pieceView.getSquareId());
        squareView.performClick();
    }

    private void setPieceLocation(PieceView pieceView, View squareView) {
        squareView.post(() -> {
            int[] location = new int[2];
            squareView.getLocationOnScreen(location);
            pieceView.setX(location[0]);
            pieceView.setY(location[1] - (float) squareView.getHeight() / 2 - PIECE_OFFSET);
        });
    }

    private void visualiseMove(PieceView pieceView, View squareView) {
        squareView.post(() -> {
            int[] location = new int[2];
            squareView.getLocationOnScreen(location);
            pieceView.animate()
                    .y(location[1] - (float) squareView.getHeight() / 2 - PIECE_OFFSET)
                    .x(location[0])
                    .setDuration(350)
                    .start();
            pieceView.setY(location[1] - (float) squareView.getHeight() / 2 - PIECE_OFFSET);
        });
    }

    private void fillBoard() {
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                Square currentSquare = board.get(i, j);
                int squareId = ResourceSelector.getResourceId(this, currentSquare.getId());
                View squareView = findViewById(squareId);
                squareView.setOnClickListener(this::clickSquare);
                if(currentSquare.getPiece() != null) {
                    PieceView pieceView = new PieceView(this, currentSquare.getPiece(), squareId);
                    pieceViews[currentSquare.getCoordinates().getY()][currentSquare.getCoordinates().getX()] = pieceView;
                    pieceView.setLayoutParams(new ConstraintLayout.LayoutParams(getInDps(this, 40), getInDps(this, 40)));
                    layout.addView(pieceView);
                    setPieceLocation(pieceView, squareView);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Objects.requireNonNull(getSupportActionBar()).hide();

        layout = findViewById(R.id.gameLayout);
        ChessAnimator.animateBackground(layout);

        board = new Board();
        board.reset(ChessColor.WHITE);
        pieceViews = new PieceView[8][8];
        fillBoard();
    }
}