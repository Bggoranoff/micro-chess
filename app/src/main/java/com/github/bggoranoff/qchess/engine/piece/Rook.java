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

public class Rook extends Piece {

    public Rook(Board board, ChessColor color) {
        super(board, color);
        this.iconName = color.getLabel() + "_r";
    }

    public Rook(Board board, ChessColor color, String id, float probability) {
        super(board, color, id, probability);
        this.iconName = color.getLabel() + "_r";
    }

    @Override
    public List<String> getAvailableSquares() {
        ArrayList<String> availableSquares = new ArrayList<>();
        int x = square.getCoordinates().getX();
        int y = square.getCoordinates().getY();

        getRookAvailableSquares(availableSquares, x, y);

        return availableSquares;
    }

    @Override
    public List<String> getAvailableSplitSquares() {
        ArrayList<String> availableSquares = new ArrayList<>();
        int x = square.getCoordinates().getX();
        int y = square.getCoordinates().getY();

        if(probability == 1.0f) {
            getRookAvailableSplitSquares(availableSquares, x, y);
        }

        return availableSquares;
    }

    @Override
    public Rook[] split(Move firstMove, Move secondMove) {
        board.get(firstMove.getStart().getX(), firstMove.getStart().getY()).setPiece(null);
        Square firstSquare = board.get(firstMove.getEnd().getX(), firstMove.getEnd().getY());
        Square secondSquare = board.get(secondMove.getEnd().getX(), secondMove.getEnd().getY());

        Rook firstRook = new Rook(board, color, id, .5f);
        Rook secondRook = new Rook(board, color, id, .5f);

        firstRook.setPair(secondRook);
        secondRook.setPair(firstRook);

        firstSquare.setPiece(firstRook);
        secondSquare.setPiece(secondRook);

        board.getHistory().add(firstMove.toString() + "$" + secondMove.toString());

        return new Rook[]{firstRook, secondRook};
    }

    @NonNull
    @NotNull
    @Override
    public String toString() {
        return "r";
    }
}
