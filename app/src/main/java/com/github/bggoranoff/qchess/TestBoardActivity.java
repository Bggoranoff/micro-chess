package com.github.bggoranoff.qchess;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.bggoranoff.qchess.component.PieceView;
import com.github.bggoranoff.qchess.engine.board.Board;
import com.github.bggoranoff.qchess.engine.move.Move;
import com.github.bggoranoff.qchess.engine.piece.Piece;
import com.github.bggoranoff.qchess.engine.util.ChessColor;
import com.github.bggoranoff.qchess.engine.util.Coordinates;
import com.github.bggoranoff.qchess.util.ChessAnimator;
import com.github.bggoranoff.qchess.util.TextFormatter;

import java.util.Objects;

import static com.github.bggoranoff.qchess.util.ChessAnimator.getInDps;

public class TestBoardActivity extends BoardActivity {

    private ImageView resetView;
    private ImageView flipView;

    private void flipBoard(View view) {
        primaryColor = primaryColor == ChessColor.WHITE ? ChessColor.BLACK : ChessColor.WHITE;
        resetBoard(view);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_board);
        Objects.requireNonNull(getSupportActionBar()).hide();

        layout = findViewById(R.id.testBoardLayout);
        ChessAnimator.animateBackground(layout);

        resetView = findViewById(R.id.resetImageView);
        resetView.setOnClickListener(this::resetBoard);

        scoreView = findViewById(R.id.scoreTextView);
        scoreView.setText("0.0");
        scoreView.setTextColor(getResources().getColor(R.color.dark_green));

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float deviceHeight = (float) metrics.heightPixels / metrics.ydpi;
        pieceOffset = (4.780f - deviceHeight) * 100;

        primaryColor = ChessColor.WHITE;
        board = new Board();
        board.reset(ChessColor.WHITE);
        pieceViews = new PieceView[8][8];
        boardLayout = findViewById(R.id.boardLayout);
        fillBoard();

        flipView = findViewById(R.id.flipImageView);
        flipView.setOnClickListener(this::flipBoard);
    }

    @Override
    protected void clickSquare(View view) {
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

        resetBoardColors();
        ((ViewManager) lastPiece.getParent()).removeView(lastPiece);
        setScore(board.evaluate());
    }

    @Override
    protected void clickPiece(PieceView pieceView) {
        currentPiece = pieceView;
        View squareView = findViewById(pieceView.getSquareId());
        squareView.performClick();
    }
}