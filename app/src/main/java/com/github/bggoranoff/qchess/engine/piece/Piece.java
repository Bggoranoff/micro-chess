package com.github.bggoranoff.qchess.engine.piece;

import com.github.bggoranoff.qchess.engine.board.Board;
import com.github.bggoranoff.qchess.engine.board.Square;
import com.github.bggoranoff.qchess.engine.move.Move;
import com.github.bggoranoff.qchess.engine.util.ChessColor;
import com.github.bggoranoff.qchess.engine.util.Coordinates;

import java.util.ArrayList;
import java.util.List;

import static com.github.bggoranoff.qchess.engine.util.ChessTextFormatter.formatTag;

public abstract class Piece implements ChessPiece {

    protected String iconName;
    protected Square square = null;
    protected Board board;
    protected float probability;
    protected ChessColor color;
    protected boolean moved = false;

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
    public void move(Move move) {
        // TODO: handle probability measurements
        moved = true;
        board.executeMove(this, move);
    }

    @Override
    public void split(Square initialSquare, Square... squares) {
        // TODO: split the piece between two squares and reflect this on its probability
    }

    protected boolean isValid(int x, int y) {
        return x >= 0 && x < 8 && y >= 0 && y < 8 && (board.get(x, y).getPiece() == null || board.get(x, y).getPiece().getColor() != color);
    }

    protected void getRookAvailableSquares(List<String> availableSquares, int x, int y) {
        for(int i = 1; i < 8; i++) {
            if(isValid(x + i, y) && board.get(x + i, y).getPiece() == null) {
                availableSquares.add(formatTag(x + i, y));
            } else if(isValid(x + i, y)) {
                availableSquares.add(formatTag(x + i, y));
                break;
            } else {
                break;
            }
        }

        for(int i = 1; i < 8; i++) {
            if(isValid(x - i, y) && board.get(x - i, y).getPiece() == null) {
                availableSquares.add(formatTag(x - i, y));
            } else if(isValid(x - i, y)) {
                availableSquares.add(formatTag(x - i, y));
                break;
            } else {
                break;
            }
        }

        for(int i = 1; i < 8; i++) {
            if(isValid(x, y + i) && board.get(x, y + i).getPiece() == null) {
                availableSquares.add(formatTag(x, y + i));
            } else if(isValid(x, y + i)) {
                availableSquares.add(formatTag(x, y + i));
                break;
            } else {
                break;
            }
        }

        for(int i = 1; i < 8; i++) {
            if(isValid(x, y - i) && board.get(x, y - i).getPiece() == null) {
                availableSquares.add(formatTag(x, y - i));
            } else if(isValid(x, y - i)) {
                availableSquares.add(formatTag(x, y - i));
                break;
            } else {
                break;
            }
        }
    }

    protected void getBishopAvailableSquares(List<String> availableSquares, int x, int y) {
        for(int i = 1; i < 8; i++) {
            if(isValid(x + i, y + i) && board.get(x + i, y + i).getPiece() == null) {
                availableSquares.add(formatTag(x + i, y + i));
            } else if(isValid(x + i, y + i)) {
                availableSquares.add(formatTag(x + i, y + i));
                break;
            } else {
                break;
            }
        }

        for(int i = 1; i < 8; i++) {
            if(isValid(x + i, y - i) && board.get(x + i, y - i).getPiece() == null) {
                availableSquares.add(formatTag(x + i, y - i));
            } else if(isValid(x + i, y - i)) {
                availableSquares.add(formatTag(x + i, y - i));
                break;
            } else {
                break;
            }
        }

        for(int i = 1; i < 8; i++) {
            if(isValid(x - i, y + i) && board.get(x - i, y + i).getPiece() == null) {
                availableSquares.add(formatTag(x - i, y + i));
            } else if(isValid(x - i, y + i)) {
                availableSquares.add(formatTag(x - i, y + i));
                break;
            } else {
                break;
            }
        }

        for(int i = 1; i < 8; i++) {
            if(isValid(x - i, y - i) && board.get(x - i, y - i).getPiece() == null) {
                availableSquares.add(formatTag(x - i, y - i));
            } else if(isValid(x - i, y - i)) {
                availableSquares.add(formatTag(x - i, y - i));
                break;
            } else {
                break;
            }
        }
    }

    public void setSquare(Square square) {
        this.square = square;
    }

    public String getIconName() {
        return iconName;
    }

    public boolean isMoved() {
        return moved;
    }

    public float getProbability() {
        return probability;
    }

    public ChessColor getColor() {
        return color;
    }
}
