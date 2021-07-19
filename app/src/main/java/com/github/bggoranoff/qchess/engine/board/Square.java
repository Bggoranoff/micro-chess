package com.github.bggoranoff.qchess.engine.board;

import com.github.bggoranoff.qchess.engine.util.ChessColor;
import com.github.bggoranoff.qchess.engine.piece.Piece;
import com.github.bggoranoff.qchess.engine.util.ChessTextFormatter;
import com.github.bggoranoff.qchess.engine.util.Coordinates;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class Square {

    private ChessColor color;
    private Piece piece = null;
    private String id;
    private String tag;
    private Coordinates coordinates;

    public Square(int x, int y) {
        this.coordinates = new Coordinates(x, y);
        color = (x + y) % 2 == 0 ? ChessColor.BLACK : ChessColor.WHITE;
        tag = ChessTextFormatter.formatTag(x, y);
        id = String.format(Locale.ENGLISH, "cell%d%d", y, x);
    }

    public Square(int x, int y, Piece piece) {
        this(x, y);
        this.piece = piece;
    }

    public ChessColor getColor() {
        return color;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public String getId() {
        return id;
    }

    @Override
    public @NotNull String toString() {
        return tag;
    }
}
