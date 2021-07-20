package com.github.bggoranoff.qchess.engine.piece;

import androidx.annotation.NonNull;

import com.github.bggoranoff.qchess.engine.board.Board;
import com.github.bggoranoff.qchess.engine.util.ChessColor;

import org.jetbrains.annotations.NotNull;

import static com.github.bggoranoff.qchess.engine.util.ChessTextFormatter.formatTag;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {

    public Rook(Board board, ChessColor color) {
        super(board, color);
        this.iconName = color.getLabel() + "_r";
    }

    @Override
    public List<String> getAvailableSquares() {
        ArrayList<String> availableSquares = new ArrayList<>();
        int x = square.getCoordinates().getX();
        int y = square.getCoordinates().getY();

        getRookAvailableSquares(availableSquares, x, y);

        return availableSquares;
    }

    @NonNull
    @NotNull
    @Override
    public String toString() {
        return "r";
    }
}
