package com.github.bggoranoff.qchess.engine.piece;

import com.github.bggoranoff.qchess.engine.board.Board;
import com.github.bggoranoff.qchess.engine.util.ChessColor;
import static com.github.bggoranoff.qchess.engine.util.ChessTextFormatter.formatTag;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {

    public Knight(Board board, ChessColor color) {
        super(board, color);
        this.iconName = color.getLabel() + "_n";
    }

    @Override
    public List<String> getAvailableSquares() {
        ArrayList<String> availableSquares = new ArrayList<>();
        int x = square.getCoordinates().getX();
        int y = square.getCoordinates().getY();

        if(isValid(x - 2, y + 1)) {
            availableSquares.add(formatTag(x - 2, y + 1));
        }
        if(isValid(x - 2, y - 1)) {
            availableSquares.add(formatTag(x - 2, y - 1));
        }

        if(isValid(x + 2, y + 1)) {
            availableSquares.add(formatTag(x + 2, y + 1));
        }
        if(isValid(x + 2, y - 1)) {
            availableSquares.add(formatTag(x + 2, y - 1));
        }

        if(isValid(x + 1, y - 2)) {
            availableSquares.add(formatTag(x + 1, y - 2));
        }
        if(isValid(x - 1, y - 2)) {
            availableSquares.add(formatTag(x - 1, y - 2));
        }

        if(isValid(x + 1, y + 2)) {
            availableSquares.add(formatTag(x + 1, y + 2));
        }
        if(isValid(x - 1, y + 2)) {
            availableSquares.add(formatTag(x - 1, y + 2));
        }

        return super.getAvailableSquares();
    }
}
