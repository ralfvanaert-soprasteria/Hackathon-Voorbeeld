package sopra.steria.evaluation;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BBoardHelper;
import knight.clubbing.core.BPiece;
import knight.clubbing.core.PopLsbResult;

public class BadEvaluator implements Evaluator {
    private static final int[] PIECE_VALUE = {
            0,    // None
            100,  // Pawn
            300,  // Knight
            320,  // Bishop
            500,  // Rook
            900,  // Queen
            0,    // King
    };

    private static int[][] PST;

    static {
        PST = new int[7][];

        PST[0] = new int[64]; // None

        PST[BPiece.pawn] = new int[]{
                0,  0,  0,  0,  0,  0,  0,  0,
                50, 50, 50, 50, 50, 50, 50, 50,
                10, 10, 20, 30, 30, 20, 10, 10,
                5,  5, 10, 25, 25, 10,  5,  5,
                0,  0,  0, 20, 20,  0,  0,  0,
                5, -5,-10,  0,  0,-10, -5,  5,
                5, 10, 10,-20,-20, 10, 10,  5,
                0,  0,  0,  0,  0,  0,  0,  0,
        };

        PST[BPiece.knight] = new int[]{
                -50,-40,-30,-30,-30,-30,-40,-50,
                -40,-20,  0,  0,  0,  0,-20,-40,
                -30,  0, 10, 15, 15, 10,  0,-30,
                -30,  5, 15, 20, 20, 15,  5,-30,
                -30,  0, 15, 20, 20, 15,  0,-30,
                -30,  5, 10, 15, 15, 10,  5,-30,
                -40,-20,  0,  5,  5,  0,-20,-40,
                -50,-40,-30,-30,-30,-30,-40,-50,
        };

        PST[BPiece.bishop] = new int[]{
                -20,-10,-10,-10,-10,-10,-10,-20,
                -10,  0,  0,  0,  0,  0,  0,-10,
                -10,  0,  5, 10, 10,  5,  0,-10,
                -10,  5,  5, 10, 10,  5,  5,-10,
                -10,  0, 10, 10, 10, 10,  0,-10,
                -10, 10, 10, 10, 10, 10, 10,-10,
                -10,  5,  0,  0,  0,  0,  5,-10,
                -20,-10,-10,-10,-10,-10,-10,-20,
        };

        PST[BPiece.rook] = new int[]{
                0,  0,  0,  0,  0,  0,  0,  0,
                5, 10, 10, 10, 10, 10, 10,  5,
                -5,  0,  0,  0,  0,  0,  0, -5,
                -5,  0,  0,  0,  0,  0,  0, -5,
                -5,  0,  0,  0,  0,  0,  0, -5,
                -5,  0,  0,  0,  0,  0,  0, -5,
                -5,  0,  0,  0,  0,  0,  0, -5,
                0,  0,  0,  5,  5,  0,  0,  0,
        };

        PST[BPiece.queen] = new int[]{
                -20,-10,-10, -5, -5,-10,-10,-20,
                -10,  0,  0,  0,  0,  0,  0,-10,
                -10,  0,  5,  5,  5,  5,  0,-10,
                -5,  0,  5,  5,  5,  5,  0, -5,
                0,  0,  5,  5,  5,  5,  0, -5,
                -10,  5,  5,  5,  5,  5,  0,-10,
                -10,  0,  5,  0,  0,  0,  0,-10,
                -20,-10,-10, -5, -5,-10,-10,-20,
        };

        PST[BPiece.king] = new int[]{
                -30,-40,-40,-50,-50,-40,-40,-30,
                -30,-40,-40,-50,-50,-40,-40,-30,
                -30,-40,-40,-50,-50,-40,-40,-30,
                -30,-40,-40,-50,-50,-40,-40,-30,
                -20,-30,-30,-40,-40,-30,-30,-20,
                -10,-20,-20,-20,-20,-20,-20,-10,
                20, 20,  0,  0,  0,  0, 20, 20,
                20, 30, 10,  0,  0, 10, 30, 20,
        };
    }


    @Override
    public int evaluate(BBoard board) {
        int score = 0;

        score += pstAndMaterial(board);

        score += bishopPair(board);

        return board.isWhiteToMove() ? score : -score;
    }

    private int bishopPair(BBoard board) {
        long whiteBishops = Long.bitCount(board.getBitboard(BPiece.whiteBishop));
        long blackBishops = Long.bitCount(board.getBitboard(BPiece.blackBishop));
        return (whiteBishops >= 2 ? 50 : 0) - (blackBishops >= 2 ? 50 : 0);
    }

    public int pstAndMaterial(BBoard board) {
        int score = 0;

        long allPiecesBoard = board.getAllPiecesBoard();

        while (allPiecesBoard != 0) {
            PopLsbResult result = PopLsbResult.popLsb(allPiecesBoard);
            int square = result.index;

            int piece = board.getPieceBoards()[square];
            int mirroredSquare = BPiece.isWhite(piece) ? BBoardHelper.mirrorSquare(square) : square;

            score += BPiece.isWhite(piece) ? PIECE_VALUE[BPiece.getPieceType(piece)] : -PIECE_VALUE[BPiece.getPieceType(piece)];
            score += BPiece.isWhite(piece) ? PST[BPiece.getPieceType(piece)][mirroredSquare] : -PST[BPiece.getPieceType(piece)][square];

            allPiecesBoard = result.remaining;
        }


        return score;
    }
}
