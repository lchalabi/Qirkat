package qirkat;

import static qirkat.PieceColor.*;
import java.util.ArrayList;

/** A Player that computes its own moves.
 *  @author Lila Chalabi
 */
public class AI extends Player {

    /** Maximum minimax search depth before going to static evaluation. */
    private static final int MAX_DEPTH = 5;
    /** A position magnitude indicating a win (for white if positive, black
     *  if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 1;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new AI for GAME that will play MYCOLOR. */
    AI(Game game, PieceColor myColor) {
        super(game, myColor);
    }

    @Override
    Move myMove() {
        Main.startTiming();
        Move move;
        Main.endTiming();

        move = findMove();
        return move;
    }

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    public Move findMove() {
        Board b = new Board(board());
        if (myColor() == WHITE) {
            findMove(b, MAX_DEPTH, true, 1, -INFTY, INFTY);
        } else {
            findMove(b, MAX_DEPTH, true, -1, -INFTY, INFTY);
        }
        return _lastFoundMove;
    }

    /** The move found by the last call to one of the ...FindMove methods
     *  below. */
    private Move _lastFoundMove;

    /** Minimax alpha beta implementation. Find a move from position BOARD and return its value, recording
     *  the move found in _lastFoundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _lastMoveFound. */
    public int findMove(Board board, int depth, boolean saveMove, int sense,
                        int alpha, int beta) {
        Move best;
        best = null;
        int best_score = 0;

        if (sense == 1) {
            if (depth == 0 || !board.isMove()) {
                return staticScore(board);
            }

            ArrayList<Move> moves = board.getMoves();

            for (Move m : moves) {
                Board next = new Board(board);
                next.makeMove(m);
                int response = findMove(next, depth - 1, true, -1, alpha, beta);
                if (best == null || response >= best_score) {
                    best = m;
                    best_score = response;
                    alpha = Math.max(alpha, response);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }

        } else {
            if (depth == 0 || !board.isMove()) {
                return staticScore(board);
            }
            ArrayList<Move> moves = board.getMoves();

            for (Move m : moves) {
                Board next = new Board(board);
                next.makeMove(m);
                int response = findMove(next, depth - 1, true, 1, alpha, beta);
                if (best == null || response <= best_score) {
                    best = m;
                    best_score = response;
                    beta = Math.min(beta, response);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
        }

        if (saveMove) {
            Board view = new Board(board);
            _lastFoundMove = best;
        }

        return best_score;
    }



    /** Return a heuristic value for BOARD. set this equal to number of white
     * pieces minus number of black*/
    public int staticScore(Board board) {
        if (!board.isMove() && board.whoseMove() == PieceColor.WHITE) {
            return -INFTY;
        } else if (!board.isMove() && board.whoseMove() == PieceColor.BLACK) {
            return INFTY;
        }
        int whiteCount = 0;
        int blackCount = 0;
        for (PieceColor p : board._pieces) {
            if (p == WHITE) {
                whiteCount++;
            } else if (p == BLACK) {
                blackCount++;
            }
        }
        return whiteCount - blackCount;

    }
}
