package com.github.bggoranoff.qchess.engine.board;

import com.github.bggoranoff.qchess.engine.move.Move;

public class Board implements ChessBoard {

    @Override
    public boolean executeMove(Move move) {
        // TODO: execute move by given coordinates and add it to the move history
        return false;
    }

    @Override
    public boolean takeBack() {
        // TODO: undo the last move and remove it from the game history
        return false;
    }

    @Override
    public float evaluate() {
        // TODO: use an algorithm to make an evaluation of the current board state
        return 0.0f;
    }
}
