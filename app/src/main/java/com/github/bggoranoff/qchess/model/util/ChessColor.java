package com.github.bggoranoff.qchess.model.util;

public enum ChessColor {

    BLACK("b"), WHITE("w");

    private final String label;

    ChessColor(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
