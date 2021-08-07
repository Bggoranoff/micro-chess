package com.github.bggoranoff.qchess.model.piece;

import androidx.annotation.NonNull;

import com.github.bggoranoff.qchess.model.board.Board;
import com.github.bggoranoff.qchess.model.board.Square;
import com.github.bggoranoff.qchess.model.move.Move;
import com.github.bggoranoff.qchess.model.util.ChessColor;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {

    public Queen(Board board, ChessColor color) {
        super(board, color);
        this.iconName = color.getLabel() + "_q";
        score = 900;
        scoreMatrix = new int[][] {
                new int[]{-20,-10,-10, -5, -5,-10,-10,-20},
                new int[]{-10,  0,  0,  0,  0,  0,  0,-10},
                new int[]{-10,  0,  5,  5,  5,  5,  0,-10},
                new int[]{-5,  0,  5,  5,  5,  5,  0, -5},
                new int[]{0,  0,  5,  5,  5,  5,  0, -5},
                new int[]{-10,  5,  5,  5,  5,  5,  0,-10},
                new int[]{-10,  0,  5,  0,  0,  0,  0,-10},
                new int[]{-20,-10,-10, -5, -5,-10,-10,-20}
        };
    }

    public Queen(Board board, ChessColor color, String id, float probability) {
        super(board, color, id, probability);
        this.iconName = color.getLabel() + "_q";
        score = 900;
        scoreMatrix = new int[][] {
                new int[]{-20,-10,-10, -5, -5,-10,-10,-20},
                new int[]{-10,  0,  0,  0,  0,  0,  0,-10},
                new int[]{-10,  0,  5,  5,  5,  5,  0,-10},
                new int[]{-5,  0,  5,  5,  5,  5,  0, -5},
                new int[]{0,  0,  5,  5,  5,  5,  0, -5},
                new int[]{-10,  5,  5,  5,  5,  5,  0,-10},
                new int[]{-10,  0,  5,  0,  0,  0,  0,-10},
                new int[]{-20,-10,-10, -5, -5,-10,-10,-20}
        };
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

    @Override
    public List<String> getAvailableSplitSquares() {
        ArrayList<String> availableSquares = new ArrayList<>();
        int x = square.getCoordinates().getX();
        int y = square.getCoordinates().getY();

        if(probability == 1.0f) {
            getBishopAvailableSplitSquares(availableSquares, x, y);
            getRookAvailableSplitSquares(availableSquares, x, y);
        }

        return availableSquares;
    }

    @Override
    public Queen[] split(Move firstMove, Move secondMove) {
        board.get(firstMove.getStart().getX(), firstMove.getStart().getY()).setPiece(null);
        Square firstSquare = board.get(firstMove.getEnd().getX(), firstMove.getEnd().getY());
        Square secondSquare = board.get(secondMove.getEnd().getX(), secondMove.getEnd().getY());

        Queen firstQueen = new Queen(board, color, id, .5f);
        Queen secondQueen = new Queen(board, color, id, .5f);

        firstQueen.setPair(secondQueen);
        secondQueen.setPair(firstQueen);

        firstQueen.setRevealed(false);
        secondQueen.setRevealed(false);

        firstSquare.setPiece(firstQueen);
        secondSquare.setPiece(secondQueen);

        return new Queen[]{firstQueen, secondQueen};
    }

    @NonNull
    @NotNull
    @Override
    public String toString() {
        return "q";
    }
}
