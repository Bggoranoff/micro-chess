package com.github.bggoranoff.qchess.model.piece;

import com.github.bggoranoff.qchess.model.move.Move;

import java.util.List;

public interface ChessPiece {

    List<String> getAvailableSquares();

    List<String> getAvailableSplitSquares();

    void move(Move move);

    Piece[] split(Move firstMove, Move secondMove);

    void reveal();

    float evaluate();

    int getScore();
}
