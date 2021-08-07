package com.github.bggoranoff.qchess.model.board;

import com.github.bggoranoff.qchess.model.move.Move;
import com.github.bggoranoff.qchess.model.piece.Piece;

public interface ChessBoard {

    void executeMove(Piece piece, Move move);

    float evaluate();

    void take(int x, int y);
}
