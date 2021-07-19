package com.github.bggoranoff.qchess.engine.piece;

import com.github.bggoranoff.qchess.engine.board.Square;

public interface ChessPiece {

    String[] getAvailableSquares();

    void move(Square initialSquare, Square square);

    void split(Square initialSquare, Square... squares);
}
