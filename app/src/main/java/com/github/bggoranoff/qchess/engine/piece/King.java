package com.github.bggoranoff.qchess.engine.piece;

import androidx.annotation.NonNull;

import com.github.bggoranoff.qchess.engine.board.Board;
import com.github.bggoranoff.qchess.engine.board.Square;
import com.github.bggoranoff.qchess.engine.move.Move;
import com.github.bggoranoff.qchess.engine.util.ChessColor;

import org.jetbrains.annotations.NotNull;

import static com.github.bggoranoff.qchess.engine.util.ChessTextFormatter.formatTag;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {

    private final int[][] SCORE_MATRIX_MIDDLE = new int[][]{
            new int[]{-30,-40,-40,-50,-50,-40,-40,-30},
            new int[]{-30,-40,-40,-50,-50,-40,-40,-30},
            new int[]{-30,-40,-40,-50,-50,-40,-40,-30},
            new int[]{-30,-40,-40,-50,-50,-40,-40,-30},
            new int[]{-20,-30,-30,-40,-40,-30,-30,-20},
            new int[]{-10,-20,-20,-20,-20,-20,-20,-10},
            new int[]{20, 20,  0,  0,  0,  0, 20, 20},
            new int[]{20, 30, 10, 0, 0, 10, 30, 20}
    };
    private final int[][] SCORE_MATRIX_END = new int[][]{
            new int[]{-50,-40,-30,-20,-20,-30,-40,-50},
            new int[]{-30,-20,-10,  0,  0,-10,-20,-30},
            new int[]{-30,-10, 20, 30, 30, 20,-10,-30},
            new int[]{-30,-10, 30, 40, 40, 30,-10,-30},
            new int[]{-30,-10, 30, 40, 40, 30,-10,-30},
            new int[]{-30,-10, 20, 30, 30, 20,-10,-30},
            new int[]{-30,-30,  0,  0,  0,  0,-30,-30},
            new int[]{-50,-30,-30,-30,-30,-30,-30,-50}
    };

    public King(Board board, ChessColor color) {
        super(board, color);
        this.iconName = color.getLabel() + "_k";
        score = 20000;
    }

    public King(Board board, ChessColor color, String id, float probability) {
        super(board, color, id, probability);
        this.iconName = color.getLabel() + "_k";
        score = 20000;
    }

    private boolean isRookForCastling(int x, int y) {
        return board.get(x, y).getPiece() != null && board.get(x, y).getPiece() instanceof Rook && !board.get(x, y).getPiece().isMoved();
    }

    private void getCastlingSquares(int file, List<String> availableSquares) {
        boolean isCastlingAvailable = true;
        int x = square.getCoordinates().getX();

        // king side castling
        for(int i = x - 1; i > 0; i--) {
            if(board.get(i, file).getPiece() != null) {
                isCastlingAvailable = false;
                break;
            }
        }
        if(isCastlingAvailable && isRookForCastling(x - 4, file)) {
            availableSquares.add(formatTag(x - 2, file));
        }

        // queen side castling
        isCastlingAvailable = true;
        for(int i = x + 1; i < 7; i++) {
            if(board.get(i, file).getPiece() != null) {
                isCastlingAvailable = false;
                break;
            }
        }
        if(isCastlingAvailable && isRookForCastling(x + 3, file)) {
            availableSquares.add(formatTag(x + 2, file));
        }
    }

    @Override
    public List<String> getAvailableSquares() {
        ArrayList<String> availableSquares = new ArrayList<>();
        int x = square.getCoordinates().getX();
        int y = square.getCoordinates().getY();

        if(isValid(x - 1, y)) {
            availableSquares.add(formatTag(x - 1, y));
        }
        if(isValid(x + 1, y)) {
            availableSquares.add(formatTag(x + 1, y));
        }

        if(isValid(x, y - 1)) {
            availableSquares.add(formatTag(x, y - 1));
        }
        if(isValid(x, y + 1)) {
            availableSquares.add(formatTag(x, y + 1));
        }

        if(isValid(x - 1, y - 1)) {
            availableSquares.add(formatTag(x - 1, y - 1));
        }
        if(isValid(x - 1, y + 1)) {
            availableSquares.add(formatTag(x - 1, y + 1));
        }

        if(isValid(x + 1, y - 1)) {
            availableSquares.add(formatTag(x + 1, y - 1));
        }
        if(isValid(x + 1, y + 1)) {
            availableSquares.add(formatTag(x + 1, y + 1));
        }

        if(!moved) {
            getCastlingSquares(y, availableSquares);
        }

        return availableSquares;
    }

    @Override
    public List<String> getAvailableSplitSquares() {
        ArrayList<String> availableSquares = new ArrayList<>();
        int x = square.getCoordinates().getX();
        int y = square.getCoordinates().getY();

        if(probability == 1.0f) {
            if (isAvailableForSplit(x - 1, y)) {
                availableSquares.add(formatTag(x - 1, y));
            }
            if (isAvailableForSplit(x + 1, y)) {
                availableSquares.add(formatTag(x + 1, y));
            }

            if (isAvailableForSplit(x, y - 1)) {
                availableSquares.add(formatTag(x, y - 1));
            }
            if (isAvailableForSplit(x, y + 1)) {
                availableSquares.add(formatTag(x, y + 1));
            }

            if (isAvailableForSplit(x - 1, y - 1)) {
                availableSquares.add(formatTag(x - 1, y - 1));
            }
            if (isAvailableForSplit(x - 1, y + 1)) {
                availableSquares.add(formatTag(x - 1, y + 1));
            }

            if (isAvailableForSplit(x + 1, y - 1)) {
                availableSquares.add(formatTag(x + 1, y - 1));
            }
            if (isAvailableForSplit(x + 1, y + 1)) {
                availableSquares.add(formatTag(x + 1, y + 1));
            }
        }

        return availableSquares;
    }

    @Override
    public King[] split(Move firstMove, Move secondMove) {
        board.get(firstMove.getStart().getX(), firstMove.getStart().getY()).setPiece(null);
        Square firstSquare = board.get(firstMove.getEnd().getX(), firstMove.getEnd().getY());
        Square secondSquare = board.get(secondMove.getEnd().getX(), secondMove.getEnd().getY());

        King firstKing = new King(board, color, id, .5f);
        King secondKing = new King(board, color, id, .5f);

        firstKing.setPair(secondKing);
        secondKing.setPair(firstKing);

        firstKing.setMoved(true);
        secondKing.setMoved(true);

        firstSquare.setPiece(firstKing);
        secondSquare.setPiece(secondKing);

        moved = true;

        return new King[]{firstKing, secondKing};
    }

    @Override
    public float evaluate() {
        int x = square.getCoordinates().getX();
        int y = color.equals(ChessColor.WHITE) ? square.getCoordinates().getY() : 7 - square.getCoordinates().getY();
        if(board.getTotalScore() <= 42000) {
            float result = probability * score + SCORE_MATRIX_END[y][x];
            return result / 100;
        } else {
            float result = probability * score + SCORE_MATRIX_MIDDLE[y][x];
            return result / 100;
        }
    }

    @NonNull
    @NotNull
    @Override
    public String toString() {
        return "k";
    }
}
