package com.github.bggoranoff.qchess.model.piece;

import androidx.annotation.NonNull;

import com.github.bggoranoff.qchess.model.board.Board;
import com.github.bggoranoff.qchess.model.move.Move;
import com.github.bggoranoff.qchess.model.util.ChessColor;

import org.jetbrains.annotations.NotNull;

import static com.github.bggoranoff.qchess.model.util.ChessTextFormatter.formatTag;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {

    private boolean justMovedDouble = false;

    public Pawn(Board board, ChessColor color) {
        super(board, color);
        this.iconName = color.getLabel() + "_p";
        score = 100;
        scoreMatrix = new int[][]{
                new int[]{0,  0,  0,  0,  0,  0,  0,  0},
                new int[]{50, 50, 50, 50, 50, 50, 50, 50},
                new int[]{10, 10, 20, 30, 30, 20, 10, 10},
                new int[]{5,  5, 10, 25, 25, 10,  5,  5},
                new int[]{0,  0,  0, 20, 20,  0,  0,  0},
                new int[]{5, -5,-10,  0,  0,-10, -5,  5},
                new int[]{5, 10, 10,-20,-20, 10, 10,  5},
                new int[]{0,  0,  0,  0,  0,  0,  0,  0}
        };
    }

    public void getEnPassantMoves(List<String> availableMoves, int x, int y) {
        if(justMovedDouble) {
            if(color.equals(ChessColor.WHITE)) {
                if(
                        isValid(x + 1, y) && board.get(x + 1, y).getPiece() != null &&
                        !board.get(x + 1, y).getPiece().getColor().equals(color) &&
                        isValid(x + 1, y + 1) && board.get(x + 1, y + 1).getPiece() == null
                ) {
                    availableMoves.add(formatTag(x + 1, y + 1));
                }
                if(
                        isValid(x - 1, y) && board.get(x - 1, y).getPiece() != null &&
                                !board.get(x - 1, y).getPiece().getColor().equals(color) &&
                                isValid(x - 1, y + 1) && board.get(x - 1, y + 1).getPiece() == null
                ) {
                    availableMoves.add(formatTag(x - 1, y + 1));
                }
            } else {
                if(
                        isValid(x + 1, y) && board.get(x + 1, y).getPiece() != null &&
                                !board.get(x + 1, y).getPiece().getColor().equals(color) &&
                                isValid(x + 1, y - 1) && board.get(x + 1, y - 1).getPiece() == null
                ) {
                    availableMoves.add(formatTag(x + 1, y - 1));
                }
                if(
                        isValid(x - 1, y) && board.get(x - 1, y).getPiece() != null &&
                                !board.get(x - 1, y).getPiece().getColor().equals(color) &&
                                isValid(x - 1, y - 1) && board.get(x - 1, y - 1).getPiece() == null
                ) {
                    availableMoves.add(formatTag(x - 1, y - 1));
                }
            }
        }
    }

    @Override
    public List<String> getAvailableSquares() {
        ArrayList<String> availableSquares = new ArrayList<>();
        int x = square.getCoordinates().getX();
        int y = square.getCoordinates().getY();
        int i = color.equals(ChessColor.BLACK) ? -1 : +1;

        if(isValid(x, y + i) && board.get(x, y + i).getPiece() == null) {
            if(!moved && isValid(x, y + 2 * i) && board.get(x, y + 2 * i).getPiece() == null) {
                availableSquares.add(formatTag(x, y + 2 * i));
            }
            availableSquares.add(formatTag(x, y + i));
        }

        if(isValid(x + 1, y + i) && board.get(x + 1, y + i).getPiece() != null) {
            availableSquares.add(formatTag(x + 1, y + i));
        }
        if(isValid(x - 1, y + i) && board.get(x - 1, y + i).getPiece() != null) {
            availableSquares.add(formatTag(x - 1, y + i));
        }

        getEnPassantMoves(availableSquares, x, y);

        return availableSquares;
    }

    @Override
    public List<String> getAvailableSplitSquares() {
        return super.getAvailableSplitSquares();
    }

    @Override
    public void move(Move move) {
        justMovedDouble = !moved && Math.abs(move.getStart().getY() - move.getEnd().getY()) == 2;
        super.move(move);
    }

    @NonNull
    @NotNull
    @Override
    public String toString() {
        return "p";
    }
}
