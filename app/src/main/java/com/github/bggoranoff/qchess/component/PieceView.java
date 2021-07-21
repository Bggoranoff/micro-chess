package com.github.bggoranoff.qchess.component;

import android.content.Context;

import com.github.bggoranoff.qchess.engine.piece.Piece;
import com.github.bggoranoff.qchess.util.ResourceSelector;

public class PieceView extends androidx.appcompat.widget.AppCompatImageView {

    private Piece piece;
    private int squareId;

    public PieceView(Context context) {
        super(context);
    }

    public PieceView(Context context, Piece piece, int squareId) {
        super(context);
        this.piece = piece;
        this.squareId = squareId;
        setImageResource(ResourceSelector.getDrawable(context, piece.getIconName()));
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public int getSquareId() {
        return squareId;
    }

    public void setSquareId(int squareId) {
        this.squareId = squareId;
    }
}
