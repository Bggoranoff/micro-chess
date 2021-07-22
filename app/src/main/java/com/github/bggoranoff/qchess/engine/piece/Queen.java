package com.github.bggoranoff.qchess.engine.piece;

import androidx.annotation.NonNull;

import com.github.bggoranoff.qchess.engine.board.Board;
import com.github.bggoranoff.qchess.engine.board.Square;
import com.github.bggoranoff.qchess.engine.move.Move;
import com.github.bggoranoff.qchess.engine.util.ChessColor;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {

    public Queen(Board board, ChessColor color) {
        super(board, color);
        this.iconName = color.getLabel() + "_q";
    }

    public Queen(Board board, ChessColor color, String id, float probability) {
        super(board, color, id, probability);
        this.iconName = color.getLabel() + "_q";
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

        firstSquare.setPiece(firstQueen);
        secondSquare.setPiece(secondQueen);

        board.getHistory().add(firstMove.toString() + "$" + secondMove.toString());

        return new Queen[]{firstQueen, secondQueen};
    }

    @NonNull
    @NotNull
    @Override
    public String toString() {
        return "q";
    }
}
