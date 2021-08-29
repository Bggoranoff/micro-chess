package com.github.bggoranoff.qchess.model.move;

import com.github.bggoranoff.qchess.model.util.Coordinates;

import org.jetbrains.annotations.NotNull;

public class Move {

    private Coordinates start;
    private Coordinates end;

    public Move(Coordinates start, Coordinates end) {
        this.start = start;
        this.end = end;
    }

    public Coordinates getStart() {
        return start;
    }

    public Coordinates getEnd() {
        return end;
    }

    @Override
    public @NotNull String toString() {
        return start.toString() + "-" + end.toString();
    }
}
