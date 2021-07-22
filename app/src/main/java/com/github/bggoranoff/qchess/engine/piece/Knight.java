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

public class Knight extends Piece {

    public Knight(Board board, ChessColor color) {
        super(board, color);
        this.iconName = color.getLabel() + "_n";
    }

    public Knight(Board board, ChessColor color, String id, float probability) {
        super(board, color, id, probability);
        this.iconName = color.getLabel() + "_n";
    }

    @Override
    public List<String> getAvailableSquares() {
        ArrayList<String> availableSquares = new ArrayList<>();
        int x = square.getCoordinates().getX();
        int y = square.getCoordinates().getY();

        if(probability == 1.0f) {
            if (isValid(x - 2, y + 1)) {
                availableSquares.add(formatTag(x - 2, y + 1));
            }
            if (isValid(x - 2, y - 1)) {
                availableSquares.add(formatTag(x - 2, y - 1));
            }

            if (isValid(x + 2, y + 1)) {
                availableSquares.add(formatTag(x + 2, y + 1));
            }
            if (isValid(x + 2, y - 1)) {
                availableSquares.add(formatTag(x + 2, y - 1));
            }

            if (isValid(x + 1, y - 2)) {
                availableSquares.add(formatTag(x + 1, y - 2));
            }
            if (isValid(x - 1, y - 2)) {
                availableSquares.add(formatTag(x - 1, y - 2));
            }

            if (isValid(x + 1, y + 2)) {
                availableSquares.add(formatTag(x + 1, y + 2));
            }
            if (isValid(x - 1, y + 2)) {
                availableSquares.add(formatTag(x - 1, y + 2));
            }
        }

        return availableSquares;
    }

    @Override
    public List<String> getAvailableSplitSquares() {
        ArrayList<String> availableSquares = new ArrayList<>();
        int x = square.getCoordinates().getX();
        int y = square.getCoordinates().getY();

        if(isAvailableForSplit(x - 2, y + 1)) {
            availableSquares.add(formatTag(x - 2, y + 1));
        }
        if(isAvailableForSplit(x - 2, y - 1)) {
            availableSquares.add(formatTag(x - 2, y - 1));
        }

        if(isAvailableForSplit(x + 2, y + 1)) {
            availableSquares.add(formatTag(x + 2, y + 1));
        }
        if(isAvailableForSplit(x + 2, y - 1)) {
            availableSquares.add(formatTag(x + 2, y - 1));
        }

        if(isAvailableForSplit(x + 1, y - 2)) {
            availableSquares.add(formatTag(x + 1, y - 2));
        }
        if(isAvailableForSplit(x - 1, y - 2)) {
            availableSquares.add(formatTag(x - 1, y - 2));
        }

        if(isAvailableForSplit(x + 1, y + 2)) {
            availableSquares.add(formatTag(x + 1, y + 2));
        }
        if(isAvailableForSplit(x - 1, y + 2)) {
            availableSquares.add(formatTag(x - 1, y + 2));
        }

        return availableSquares;
    }

    @Override
    public void split(Move firstMove, Move secondMove) {
        board.get(firstMove.getStart().getX(), firstMove.getStart().getY()).setPiece(null);
        Square firstSquare = board.get(firstMove.getEnd().getX(), firstMove.getEnd().getY());
        Square secondSquare = board.get(secondMove.getEnd().getX(), secondMove.getEnd().getY());

        Knight firstKnight = new Knight(board, color, id, .5f);
        Knight secondKnight = new Knight(board, color, id, .5f);

        firstSquare.setPiece(firstKnight);
        secondSquare.setPiece(secondKnight);

        board.getHistory().add(firstMove.toString() + "$" + secondMove.toString());
    }

    @NonNull
    @NotNull
    @Override
    public String toString() {
        return "n";
    }
}
