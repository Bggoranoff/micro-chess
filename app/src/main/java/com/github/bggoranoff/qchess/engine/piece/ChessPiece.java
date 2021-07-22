package com.github.bggoranoff.qchess.engine.piece;

import com.github.bggoranoff.qchess.engine.board.Square;
import com.github.bggoranoff.qchess.engine.move.Move;
import com.github.bggoranoff.qchess.engine.util.Coordinates;

import java.util.List;

public interface ChessPiece {

    List<String> getAvailableSquares();

    List<String> getAvailableSplitSquares();

    void move(Move move);

    Piece[] split(Move firstMove, Move secondMove);
}
