package com.github.bggoranoff.qchess.engine.piece;

import androidx.annotation.NonNull;

import com.github.bggoranoff.qchess.engine.board.Board;
import com.github.bggoranoff.qchess.engine.board.Square;
import com.github.bggoranoff.qchess.engine.move.Move;
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

    public King(Board board, ChessColor color, String id, float probability) {
        super(board, color, id, probability);
        this.iconName = color.getLabel() + "_k";
    }

    private boolean isRookForCastling(int x, int y) {
        return board.get(x, y).getPiece() != null && board.get(x, y).getPiece() instanceof Rook && !board.get(x, y).getPiece().isMoved();
    }

    private void getCastlingSquares(int file, List<String> availableSquares) {
        boolean isCastlingAvailable = true;
        int x = square.getCoordinates().getX();

        // king side castling
        for(int i = x - 1; i > 0; i--) {
            if(board.get(i, file).getPiece() != null) {
                isCastlingAvailable = false;
                break;
            }
        }
        if(isCastlingAvailable && isRookForCastling(x - 4, file)) {
            availableSquares.add(formatTag(x - 2, file));
        }

        // queen side castling
        isCastlingAvailable = true;
        for(int i = x + 1; i < 7; i++) {
            if(board.get(i, file).getPiece() != null) {
                isCastlingAvailable = false;
                break;
            }
        }
        if(isCastlingAvailable && isRookForCastling(x + 3, file)) {
            availableSquares.add(formatTag(x + 2, file));
        }
    }

    @Override
    public List<String> getAvailableSquares() {
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

        if(!moved) {
            getCastlingSquares(y, availableSquares);
        }

        return availableSquares;
    }

    @Override
    public List<String> getAvailableSplitSquares() {
        ArrayList<String> availableSquares = new ArrayList<>();
        int x = square.getCoordinates().getX();
        int y = square.getCoordinates().getY();

        if(probability == 1.0f) {
            if (isAvailableForSplit(x - 1, y)) {
                availableSquares.add(formatTag(x - 1, y));
            }
            if (isAvailableForSplit(x + 1, y)) {
                availableSquares.add(formatTag(x + 1, y));
            }

            if (isAvailableForSplit(x, y - 1)) {
                availableSquares.add(formatTag(x, y - 1));
            }
            if (isAvailableForSplit(x, y + 1)) {
                availableSquares.add(formatTag(x, y + 1));
            }

            if (isAvailableForSplit(x - 1, y - 1)) {
                availableSquares.add(formatTag(x - 1, y - 1));
            }
            if (isAvailableForSplit(x - 1, y + 1)) {
                availableSquares.add(formatTag(x - 1, y + 1));
            }

            if (isAvailableForSplit(x + 1, y - 1)) {
                availableSquares.add(formatTag(x + 1, y - 1));
            }
            if (isAvailableForSplit(x + 1, y + 1)) {
                availableSquares.add(formatTag(x + 1, y + 1));
            }
        }

        return availableSquares;
    }

    @Override
    public King[] split(Move firstMove, Move secondMove) {
        board.get(firstMove.getStart().getX(), firstMove.getStart().getY()).setPiece(null);
        Square firstSquare = board.get(firstMove.getEnd().getX(), firstMove.getEnd().getY());
        Square secondSquare = board.get(secondMove.getEnd().getX(), secondMove.getEnd().getY());

        King firstKing = new King(board, color, id, .5f);
        King secondKing = new King(board, color, id, .5f);

        firstSquare.setPiece(firstKing);
        secondSquare.setPiece(secondKing);

        board.getHistory().add(firstMove.toString() + "$" + secondMove.toString());

        return new King[]{firstKing, secondKing};
    }

    @NonNull
    @NotNull
    @Override
    public String toString() {
        return "k";
    }
}
