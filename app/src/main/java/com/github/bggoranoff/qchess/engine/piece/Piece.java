package com.github.bggoranoff.qchess.engine.piece;

import com.github.bggoranoff.qchess.engine.board.Square;

public abstract class Piece implements ChessPiece {

    @Override
    public String[] getAvailableSquares() {
        // TODO: get all valid squares to move by tag
        return new String[0];
    }

    @Override
    public void move(Square initialSquare, Square square) {
        // TODO: change the square the piece is on
    }

    @Override
    public void split(Square initialSquare, Square... squares) {
        // TODO: split the piece between two squares and reflect this on its probability
    }
}
