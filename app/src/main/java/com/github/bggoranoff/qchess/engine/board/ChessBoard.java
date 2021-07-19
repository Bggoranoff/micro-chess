package com.github.bggoranoff.qchess.engine.board;

import com.github.bggoranoff.qchess.engine.move.Move;

public interface ChessBoard {

    boolean executeMove(Move move);

    boolean takeBack();

    float evaluate();
}
