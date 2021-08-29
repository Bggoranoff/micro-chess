package com.github.bggoranoff.qchess.model.board;

import com.github.bggoranoff.qchess.model.util.ChessColor;
import com.github.bggoranoff.qchess.model.piece.Piece;
import com.github.bggoranoff.qchess.model.util.Coordinates;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class Square {

    private ChessColor color;
    private Piece piece = null;
    private String id;
    private Coordinates coordinates;

    public Square(int x, int y) {
        this.coordinates = new Coordinates(x, y);
        color = (x + y) % 2 == 0 ? ChessColor.BLACK : ChessColor.WHITE;
        id = String.format(Locale.ENGLISH, "cell%d%d", y, x);
    }

    public ChessColor getColor() {
        return color;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
        if(piece != null) {
            piece.setSquare(this);
        }
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public String getId() {
        return id;
    }

    @Override
    public @NotNull String toString() {
        return piece == null ? "." : piece.toString();
    }
}
