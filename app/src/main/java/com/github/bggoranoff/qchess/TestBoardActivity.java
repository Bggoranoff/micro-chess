package com.github.bggoranoff.qchess;

import android.os.Bundle;
import android.view.View;

import com.github.bggoranoff.qchess.component.PieceView;
import com.github.bggoranoff.qchess.util.ChessAnimator;

import java.util.Objects;

public class TestBoardActivity extends BoardActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_board);
        Objects.requireNonNull(getSupportActionBar()).hide();

        layout = findViewById(R.id.testBoardLayout);
        ChessAnimator.animateBackground(layout);
    }

    @Override
    protected void clickSquare(View view) {

    }

    @Override
    protected void clickPiece(PieceView pieceView) {

    }
}