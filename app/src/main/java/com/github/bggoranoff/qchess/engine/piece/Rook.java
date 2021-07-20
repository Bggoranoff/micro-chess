package com.github.bggoranoff.qchess.engine.piece;

import com.github.bggoranoff.qchess.engine.board.Board;
import com.github.bggoranoff.qchess.engine.util.ChessColor;
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

        for(int i = 1; i < 8; i++) {
            if(isValid(x + i, y) && board.get(x + i, y).getPiece() == null) {
                availableSquares.add(formatTag(x + i, y));
            } else if(isValid(x + i, y)) {
                availableSquares.add(formatTag(x + i, y));
                break;
            }
        }

        for(int i = 1; i < 8; i++) {
            if(isValid(x - i, y) && board.get(x - i, y).getPiece() == null) {
                availableSquares.add(formatTag(x - i, y));
            } else if(isValid(x - i, y)) {
                availableSquares.add(formatTag(x - i, y));
                break;
            }
        }

        for(int i = 1; i < 8; i++) {
            if(isValid(x, y + i) && board.get(x, y + i).getPiece() == null) {
                availableSquares.add(formatTag(x, y + i));
            } else if(isValid(x, y + i)) {
                availableSquares.add(formatTag(x, y + i));
                break;
            }
        }

        for(int i = 1; i < 8; i++) {
            if(isValid(x, y - i) && board.get(x, y - i).getPiece() == null) {
                availableSquares.add(formatTag(x, y - i));
            } else if(isValid(x, y - i)) {
                availableSquares.add(formatTag(x, y - i));
                break;
            }
        }

        return availableSquares;
    }
}
