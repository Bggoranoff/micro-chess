package com.github.bggoranoff.qchess.engine.piece;

import com.github.bggoranoff.qchess.engine.board.Board;
import com.github.bggoranoff.qchess.engine.util.ChessColor;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {

    public Queen(Board board, ChessColor color) {
        super(board, color);
        this.iconName = color.getLabel() + "_q";
    }

    @Override
    public List<String> getAvailableSquares() {
        ArrayList<String> availableSquares = new ArrayList<>();
        int x = square.getCoordinates().getX();
        int y = square.getCoordinates().getY();

        getBishopAvailableSquares(availableSquares, x, y);
        getRookAvailableSquares(availableSquares, x, y);

        return availableSquares;
    }
}
