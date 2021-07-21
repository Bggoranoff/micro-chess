package com.github.bggoranoff.qchess.engine.move;

import com.github.bggoranoff.qchess.engine.util.Coordinates;

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
}
