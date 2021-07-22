package com.github.bggoranoff.qchess.engine.board;

import com.github.bggoranoff.qchess.engine.move.Move;
import com.github.bggoranoff.qchess.engine.piece.Piece;

public interface ChessBoard {

    void executeMove(Piece piece, Move move);

    boolean takeBack();

    float evaluate();

    void take(int x, int y);
}
