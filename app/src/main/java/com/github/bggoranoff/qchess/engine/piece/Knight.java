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
        score = 320;
        scoreMatrix = new int[][]{
                new int[]{-50,-40,-30,-30,-30,-30,-40,-50},
                new int[]{-40,-20,  0,  0,  0,  0,-20,-40},
                new int[]{-30,  0, 10, 15, 15, 10,  0,-30},
                new int[]{-30,  5, 15, 20, 20, 15,  5,-30},
                new int[]{-30,  0, 15, 20, 20, 15,  0,-30},
                new int[]{-30,  5, 10, 15, 15, 10,  5,-30},
                new int[]{-40,-20,  0,  5,  5,  0,-20,-40},
                new int[]{-50,-40,-30,-30,-30,-30,-40,-50}
        };
    }

    public Knight(Board board, ChessColor color, String id, float probability) {
        super(board, color, id, probability);
        this.iconName = color.getLabel() + "_n";
        score = 320;
        scoreMatrix = new int[][]{
                new int[]{-50,-40,-30,-30,-30,-30,-40,-50},
                new int[]{-40,-20,  0,  0,  0,  0,-20,-40},
                new int[]{-30,  0, 10, 15, 15, 10,  0,-30},
                new int[]{-30,  5, 15, 20, 20, 15,  5,-30},
                new int[]{-30,  0, 15, 20, 20, 15,  0,-30},
                new int[]{-30,  5, 10, 15, 15, 10,  5,-30},
                new int[]{-40,-20,  0,  5,  5,  0,-20,-40},
                new int[]{-50,-40,-30,-30,-30,-30,-40,-50}
        };
    }

    @Override
    public List<String> getAvailableSquares() {
        ArrayList<String> availableSquares = new ArrayList<>();
        int x = square.getCoordinates().getX();
        int y = square.getCoordinates().getY();

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
    public Knight[] split(Move firstMove, Move secondMove) {
        board.get(firstMove.getStart().getX(), firstMove.getStart().getY()).setPiece(null);
        Square firstSquare = board.get(firstMove.getEnd().getX(), firstMove.getEnd().getY());
        Square secondSquare = board.get(secondMove.getEnd().getX(), secondMove.getEnd().getY());

        Knight firstKnight = new Knight(board, color, id, .5f);
        Knight secondKnight = new Knight(board, color, id, .5f);

        firstKnight.setPair(secondKnight);
        secondKnight.setPair(firstKnight);

        firstSquare.setPiece(firstKnight);
        secondSquare.setPiece(secondKnight);

        return new Knight[]{firstKnight, secondKnight};
    }

    @NonNull
    @NotNull
    @Override
    public String toString() {
        return "n";
    }
}
