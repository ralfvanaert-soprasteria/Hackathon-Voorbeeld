package sopra.steria.ordering;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BBoardHelper;
import knight.clubbing.core.BMove;
import knight.clubbing.core.BPiece;

public class BadMoveOrderer implements MoveOrderer {

    private static final int[][] MVV_LVA = {
            //  attacker: None, Pawn,  Knight, Bishop, Rook,  Queen, King,  7,     8
            {      0,      0,      0,      0,      0,      0,      0}, // victim: None
            {    900,    800,    600,    580,    400,      0,    900}, // victim: Pawn   (100)
            {   3000,   2900,   2700,   2680,   2500,   2100,   3000}, // victim: Knight (300)
            {   3200,   3100,   2900,   2880,   2700,   2300,   3200}, // victim: Bishop (320)
            {   5000,   4900,   4700,   4680,   4500,   4100,   5000}, // victim: Rook   (500)
            {   9000,   8900,   8700,   8680,   8500,   8100,   9000}, // victim: Queen  (900)
            {      0,      0,      0,      0,      0,      0,      0}, // victim: King
            {      0,      0,      0,      0,      0,      0,      0}, // victim: 7
            {      0,      0,      0,      0,      0,      0,      0}, // victim: 8
    };


    @Override
    public void orderMoves(BMove[] moves, BBoard board) {
        int[] scores = new int[moves.length];

        for (int i = 0; i < moves.length; i++) {
            scores[i] = score(moves[i], board);
        }

        sortMovesByScore(moves, scores);
    }

    private int score(BMove move, BBoard board) {
        int score = 0;

        int movingPiece = board.getPieceBoards()[move.startSquare()];
        int capturedPiece = board.getPieceBoards()[move.targetSquare()];

        int rank = BBoardHelper.rankIndex(move.startSquare());
        int file = BBoardHelper.fileIndex(move.startSquare());

        if (rank == 3 || file == 3 || rank == 4 || file == 4)
            score += 300;

        score += MVV_LVA[BPiece.getPieceType(capturedPiece)][BPiece.getPieceType(movingPiece)];

        return score;
    }

    private void sortMovesByScore(BMove[] moves, int[] scores) {
        for (int i = 0; i < moves.length - 1; i++) {
            for (int j = i + 1; j < moves.length; j++) {
                if (scores[j] > scores[i]) {
                    // Swap moves
                    BMove tempMove = moves[i];
                    moves[i] = moves[j];
                    moves[j] = tempMove;

                    // Swap scores
                    int tempScore = scores[i];
                    scores[i] = scores[j];
                    scores[j] = tempScore;
                }
            }
        }
    }
}
