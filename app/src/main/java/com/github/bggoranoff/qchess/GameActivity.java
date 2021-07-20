package com.github.bggoranoff.qchess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;

import com.github.bggoranoff.qchess.component.PieceView;
import com.github.bggoranoff.qchess.engine.board.Board;
import com.github.bggoranoff.qchess.engine.board.Square;
import com.github.bggoranoff.qchess.engine.util.ChessColor;
import com.github.bggoranoff.qchess.util.ChessAnimator;
import com.github.bggoranoff.qchess.util.ResourceSelector;
import static com.github.bggoranoff.qchess.util.ChessAnimator.getInDps;

import java.util.Objects;

public class GameActivity extends AppCompatActivity {

    private static final int PIECE_OFFSET = 10;

    private ConstraintLayout layout;
    private Board board;

    private void setPieceLocation(PieceView pieceView, View squareView) {
        squareView.post(() -> {
            int[] location = new int[2];
            squareView.getLocationOnScreen(location);
            pieceView.setX(location[0]);
            pieceView.setY(location[1] - squareView.getHeight() / 2 - PIECE_OFFSET);
        });
    }

    private void fillBoard() {
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(board.get(i, j).getPiece() != null) {
                    Square currentSquare = board.get(i, j);
                    PieceView pieceView = new PieceView(this, currentSquare.getPiece());
                    int squareId = ResourceSelector.getResourceId(this, currentSquare.getId());
                    View squareView = findViewById(squareId);
                    pieceView.setLayoutParams(new ConstraintLayout.LayoutParams(getInDps(this, 40), getInDps(this, 40)));
                    layout.addView(pieceView);
                    setPieceLocation(pieceView, squareView);
                }
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
        fillBoard();
    }
}