package com.github.bggoranoff.qchess.engine.piece;

import androidx.annotation.NonNull;

import com.github.bggoranoff.qchess.engine.board.Board;
import com.github.bggoranoff.qchess.engine.util.ChessColor;

import org.jetbrains.annotations.NotNull;

import static com.github.bggoranoff.qchess.engine.util.ChessTextFormatter.formatTag;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {

    public King(Board board, ChessColor color) {
        super(board, color);
        this.iconName = color.getLabel() + "_k";
    }

    public void castle(int file) {
        // TODO: find rook on file, move rook and king
    }

    @Override
    public List<String> getAvailableSquares() {
        // TODO: sync x and y in formatting
        ArrayList<String> availableSquares = new ArrayList<>();
        int x = square.getCoordinates().getX();
        int y = square.getCoordinates().getY();

        if(isValid(x - 1, y)) {
            availableSquares.add(formatTag(x - 1, y));
        }
        if(isValid(x + 1, y)) {
            availableSquares.add(formatTag(x + 1, y));
        }

        if(isValid(x, y - 1)) {
            availableSquares.add(formatTag(x, y - 1));
        }
        if(isValid(x, y + 1)) {
            availableSquares.add(formatTag(x, y + 1));
        }

        if(isValid(x - 1, y - 1)) {
            availableSquares.add(formatTag(x - 1, y - 1));
        }
        if(isValid(x - 1, y + 1)) {
            availableSquares.add(formatTag(x - 1, y + 1));
        }

        if(isValid(x + 1, y - 1)) {
            availableSquares.add(formatTag(x + 1, y - 1));
        }
        if(isValid(x + 1, y + 1)) {
            availableSquares.add(formatTag(x + 1, y + 1));
        }

        return availableSquares;
    }

    @NonNull
    @NotNull
    @Override
    public String toString() {
        return "k";
    }
}
