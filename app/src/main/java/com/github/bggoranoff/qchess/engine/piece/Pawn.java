package com.github.bggoranoff.qchess.engine.piece;

import com.github.bggoranoff.qchess.engine.board.Board;
import com.github.bggoranoff.qchess.engine.util.ChessColor;
import static com.github.bggoranoff.qchess.engine.util.ChessTextFormatter.formatTag;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {

    public Pawn(Board board, ChessColor color) {
        super(board, color);
        this.iconName = color.getLabel() + "_p";
    }

    public void enPassant() {
        // TODO: check available en passant moves & take
    }

    @Override
    public List<String> getAvailableSquares() {
        ArrayList<String> availableSquares = new ArrayList<>();
        int x = square.getCoordinates().getX();
        int y = square.getCoordinates().getY();
        int i = color.equals(ChessColor.BLACK) ? -1 : +1;

        if(isAvailable(x, y + i) && board.get(x, y + i).getPiece() == null) {
            availableSquares.add(formatTag(x, y + i));
        }
        if(isAvailable(x, y + 2 * i) && board.get(x, y + i).getPiece() == null) {
            availableSquares.add(formatTag(x, y + 2 * i));
        }

        if(isAvailable(x + 1, y + i) && board.get(x + 1, y + i).getPiece() != null) {
            availableSquares.add(formatTag(x + 1, y + i));
        }
        if(isAvailable(x - 1, y + i) && board.get(x - 1, y + i).getPiece() != null) {
            availableSquares.add(formatTag(x - 1, y + i));
        }

        return availableSquares;
    }
}
