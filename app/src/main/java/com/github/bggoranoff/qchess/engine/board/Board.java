package com.github.bggoranoff.qchess.engine.board;

import com.github.bggoranoff.qchess.engine.move.Move;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Board implements ChessBoard {

    private Square[][] matrix;
    private ArrayList<Square> takenBlackPieces;
    private ArrayList<Square> takenWhitePieces;
    private ArrayList<Move> history; // could be deque
    private boolean finished = false;
    private String result = "";

    public Board() {
        matrix = new Square[8][8];
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                matrix[i][j] = new Square(j, i);
            }
        }
        takenBlackPieces = new ArrayList<>();
        takenWhitePieces = new ArrayList<>();
        history = new ArrayList<>();
    }

    @Override
    public boolean executeMove(Move move) {
        // TODO: execute move by given coordinates and add it to the move history
        return false;
    }

    @Override
    public boolean takeBack() {
        // TODO: undo the last move and remove it from the game history
        return false;
    }

    @Override
    public float evaluate() {
        // TODO: use an algorithm to make an evaluation of the current board state
        return 0.0f;
    }

    public boolean isFinished() {
        return finished;
    }

    public String getResult() {
        return result;
    }

    public ArrayList<Move> getHistory() {
        return history;
    }

    @Override
    public @NotNull
    String toString() {
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                result.append(matrix[i][j].toString()).append(" ");
            }
            result.append("\n");
        }
        return result.toString();
    }
}
