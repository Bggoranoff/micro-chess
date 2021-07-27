package com.github.bggoranoff.qchess.engine.board;

import com.github.bggoranoff.qchess.engine.move.Move;
import com.github.bggoranoff.qchess.engine.piece.Bishop;
import com.github.bggoranoff.qchess.engine.piece.King;
import com.github.bggoranoff.qchess.engine.piece.Knight;
import com.github.bggoranoff.qchess.engine.piece.Pawn;
import com.github.bggoranoff.qchess.engine.piece.Piece;
import com.github.bggoranoff.qchess.engine.piece.Queen;
import com.github.bggoranoff.qchess.engine.piece.Rook;
import com.github.bggoranoff.qchess.engine.util.ChessColor;
import com.github.bggoranoff.qchess.engine.util.ChessTextFormatter;
import com.github.bggoranoff.qchess.engine.util.Coordinates;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Board implements ChessBoard {

    private Square[][] matrix;
    private ArrayList<Piece> takenBlackPieces;
    private ArrayList<Piece> takenWhitePieces;
    private ArrayList<String> history;
    private ArrayList<String> formattedHistory;
    private boolean finished = false;
    private String result = "";

    public Board() {
        clear();
    }

    private void clear() {
        matrix = new Square[8][8];
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                matrix[i][j] = new Square(j, i);
            }
        }
        takenBlackPieces = new ArrayList<>();
        takenWhitePieces = new ArrayList<>();
        history = new ArrayList<>();
        formattedHistory = new ArrayList<>();
    }

    public void reset(ChessColor primaryColor) {
        ChessColor secondaryColor = primaryColor.equals(ChessColor.WHITE) ? ChessColor.BLACK : ChessColor.WHITE;
        clear();

        // user pawns
        for(int i = 0; i < 8; i++) {
            matrix[1][i].setPiece(new Pawn(this, primaryColor));
        }

        // user rooks
        matrix[0][0].setPiece(new Rook(this, primaryColor));
        matrix[0][7].setPiece(new Rook(this, primaryColor));

        // user knights
        matrix[0][1].setPiece(new Knight(this, primaryColor));
        matrix[0][6].setPiece(new Knight(this, primaryColor));

        // user bishops
        matrix[0][2].setPiece(new Bishop(this, primaryColor));
        matrix[0][5].setPiece(new Bishop(this, primaryColor));

        // user king and queen
        matrix[0][4].setPiece(new King(this, primaryColor));
        matrix[0][3].setPiece(new Queen(this, primaryColor));

        // opponent pawns
        for(int i = 0; i < 8; i++) {
            matrix[6][i].setPiece(new Pawn(this, secondaryColor));
        }

        // opponent rooks
        matrix[7][0].setPiece(new Rook(this, secondaryColor));
        matrix[7][7].setPiece(new Rook(this, secondaryColor));

        // opponent knights
        matrix[7][1].setPiece(new Knight(this, secondaryColor));
        matrix[7][6].setPiece(new Knight(this, secondaryColor));

        // opponent bishops
        matrix[7][2].setPiece(new Bishop(this, secondaryColor));
        matrix[7][5].setPiece(new Bishop(this, secondaryColor));

        // opponent king and queen
        matrix[7][4].setPiece(new King(this, secondaryColor));
        matrix[7][3].setPiece(new Queen(this, secondaryColor));
    }

    @Override
    public void executeMove(Piece piece, Move move) {
        get(move.getStart().getX(), move.getStart().getY()).setPiece(null);
        Square square = get(move.getEnd().getX(), move.getEnd().getY());
        if(square.getPiece() != null && !square.getPiece().getColor().equals(piece.getColor())) {
            piece.reveal();
            if(piece.isThere()) {
                if(piece.getProbability() < 1.0f) {
                    piece.setProbability(1.0f);
                    piece.getPair().getSquare().setPiece(null);
                    piece.getPair().setProbability(0.0f);
                }
                take(move.getEnd().getX(), move.getEnd().getY());
            } else {
                piece.getPair().setProbability(1.0f);
                piece.getSquare().setPiece(null);
                piece.setProbability(0.0f);
            }
        }
        square.setPiece(piece);
        System.out.println(toString());
    }

    @Override
    public float evaluate() {
        float scoreForWhite = evaluate(ChessColor.WHITE);
        float scoreForBlack = evaluate(ChessColor.BLACK);
        return scoreForWhite - scoreForBlack;
    }

    private float evaluate(ChessColor color) {
        float result = 0.0f;
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                Piece piece = get(j, i).getPiece();
                if(piece != null && piece.getColor().equals(color)) {
                    result += piece.evaluate();
                }
            }
        }
        return result;
    }

    @Override
    public void take(int x, int y) {
        Piece pieceToTake = get(x, y).getPiece();
        pieceToTake.reveal();
        if(pieceToTake.isThere() || pieceToTake.getPair() == null) {
            if (pieceToTake.getColor().equals(ChessColor.WHITE)) {
                if(pieceToTake instanceof King) {
                    result = "Black";
                    finished = true;
                }
                takenWhitePieces.add(pieceToTake);
            } else {
                if(pieceToTake instanceof King) {
                    result = "White";
                    finished = true;
                }
                takenBlackPieces.add(pieceToTake);
            }
            if(pieceToTake.getPair() != null) {
                pieceToTake.setProbability(1.0f);
                pieceToTake.getPair().getSquare().setPiece(null);
                pieceToTake.getPair().setProbability(0.0f);
                pieceToTake.getSquare().setPiece(null);
            }
        } else {
            pieceToTake.getPair().setProbability(1.0f);
            pieceToTake.getSquare().setPiece(null);
            pieceToTake.setProbability(0.0f);
        }
    }

    public int getTotalScore() {
        int result = 0;
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(get(j, i).getPiece() != null) {
                    result += get(j, i).getPiece().getScore();
                }
            }
        }
        return result;
    }

    public boolean isFinished() {
        return finished;
    }

    public String getResult() {
        return result;
    }

    public void addToHistory(String move, String piece, Coordinates end) {
        history.add(move);
        String formattedMove = piece + ChessTextFormatter.formatTag(end.getX(), end.getY());
        formattedHistory.add(formattedMove);
    }

    public void addToHistory(String move, String piece, Coordinates firstEnd, Coordinates secondEnd) {
        history.add(move);
        String formattedMove = piece + ChessTextFormatter.formatTag(firstEnd.getX(), firstEnd.getY()) +
                "$" + ChessTextFormatter.formatTag(secondEnd.getX(), secondEnd.getY());
        formattedHistory.add(formattedMove);
    }

    public String formatHistory() {
        StringBuilder result = new StringBuilder();
        for(int i = formattedHistory.size() - 1; i >= 0 && i >= formattedHistory.size() - 4; i--) {
            result.append(formattedHistory.get(i)).append(" ");
        }
        result.deleteCharAt(result.length() - 1);
        result.append("...");
        return result.toString();
    }

    public String formatFullHistory() {
        StringBuilder result = new StringBuilder();
        for(String move : formattedHistory) {
            result.append(move).append(" ");
        }
        return result.toString();
    }

    public ArrayList<String> getHistory() {
        return history;
    }

    public ArrayList<String> getFormattedHistory() {
        return formattedHistory;
    }

    public Square get(int x, int y) {
        return matrix[y][x];
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
