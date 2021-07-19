package com.github.bggoranoff.qchess.engine.piece;

import com.github.bggoranoff.qchess.engine.board.Board;
import com.github.bggoranoff.qchess.engine.board.Square;
import com.github.bggoranoff.qchess.engine.util.ChessColor;

import java.util.ArrayList;
import java.util.List;

public abstract class Piece implements ChessPiece {

    protected String iconName;
    protected Square square = null;
    protected Board board;
    protected float probability;
    protected ChessColor color;

    public Piece(Board board, ChessColor color) {
        this.board = board;
        this.color = color;
        this.probability = 1.0f;
    }

    @Override
    public List<String> getAvailableSquares() {
        // TODO: get all valid squares to move by tag
        return new ArrayList<>();
    }

    @Override
    public void move(Square initialSquare, Square square) {
        // TODO: change the square the piece is on
    }

    @Override
    public void split(Square initialSquare, Square... squares) {
        // TODO: split the piece between two squares and reflect this on its probability
    }

    protected boolean isAvailable(int x, int y) {
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

    public String getIconName() {
        return iconName;
    }

    public float getProbability() {
        return probability;
    }

    public ChessColor getColor() {
        return color;
    }
}
