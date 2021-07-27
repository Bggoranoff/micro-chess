package com.github.bggoranoff.qchess.engine.piece;

import androidx.annotation.NonNull;

import com.github.bggoranoff.qchess.engine.board.Board;
import com.github.bggoranoff.qchess.engine.board.Square;
import com.github.bggoranoff.qchess.engine.move.Move;
import com.github.bggoranoff.qchess.engine.util.ChessColor;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {

    public Bishop(Board board, ChessColor color) {
        super(board, color);
        this.iconName = color.getLabel() + "_b";
        score = 330;
        scoreMatrix = new int[][]{
                new int[]{-20,-10,-10,-10,-10,-10,-10,-20},
                new int[]{-10,  0,  0,  0,  0,  0,  0,-10},
                new int[]{-10,  0,  5, 10, 10,  5,  0,-10},
                new int[]{-10,  5,  5, 10, 10,  5,  5,-10},
                new int[]{-10,  0, 10, 10, 10, 10,  0,-10},
                new int[]{-10, 10, 10, 10, 10, 10, 10,-10},
                new int[]{-10,  5,  0,  0,  0,  0,  5,-10},
                new int[]{-20,-10,-10,-10,-10,-10,-10,-20}
        };
    }

    public Bishop(Board board, ChessColor color, String id, float probability) {
        super(board, color, id, probability);
        this.iconName = color.getLabel() + "_b";
        score = 330;
        scoreMatrix = new int[][]{
                new int[]{-20,-10,-10,-10,-10,-10,-10,-20},
                new int[]{-10,  0,  0,  0,  0,  0,  0,-10},
                new int[]{-10,  0,  5, 10, 10,  5,  0,-10},
                new int[]{-10,  5,  5, 10, 10,  5,  5,-10},
                new int[]{-10,  0, 10, 10, 10, 10,  0,-10},
                new int[]{-10, 10, 10, 10, 10, 10, 10,-10},
                new int[]{-10,  5,  0,  0,  0,  0,  5,-10},
                new int[]{-20,-10,-10,-10,-10,-10,-10,-20}
        };
    }

    @Override
    public List<String> getAvailableSquares() {
        ArrayList<String> availableSquares = new ArrayList<>();
        int x = square.getCoordinates().getX();
        int y = square.getCoordinates().getY();

        getBishopAvailableSquares(availableSquares, x, y);

        return availableSquares;
    }

    @Override
    public List<String> getAvailableSplitSquares() {
        ArrayList<String> availableSquares = new ArrayList<>();
        int x = square.getCoordinates().getX();
        int y = square.getCoordinates().getY();

        if(probability == 1.0f) {
            getBishopAvailableSplitSquares(availableSquares, x, y);
        }

        return availableSquares;
    }

    @Override
    public Bishop[] split(Move firstMove, Move secondMove) {
        board.get(firstMove.getStart().getX(), firstMove.getStart().getY()).setPiece(null);
        Square firstSquare = board.get(firstMove.getEnd().getX(), firstMove.getEnd().getY());
        Square secondSquare = board.get(secondMove.getEnd().getX(), secondMove.getEnd().getY());

        Bishop firstBishop = new Bishop(board, color, id, .5f);
        Bishop secondBishop = new Bishop(board, color, id, .5f);

        firstBishop.setPair(secondBishop);
        secondBishop.setPair(firstBishop);

        firstSquare.setPiece(firstBishop);
        secondSquare.setPiece(secondBishop);

        return new Bishop[]{firstBishop, secondBishop};
    }

    @NonNull
    @NotNull
    @Override
    public String toString() {
        return "b";
    }
}
