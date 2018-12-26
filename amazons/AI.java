package amazons;

import java.util.ArrayList;
import java.util.Iterator;

import static java.lang.Math.*;

import static amazons.Piece.*;

/** A Player that automatically generates moves.
 *  @author Ani Gottiparthy
 */
class AI extends Player {

    /** A position magnitude indicating a win (for white if positive, black
     *  if negative). */
    private static final int WINNING_VALUE = 30;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new AI with no piece or controller (intended to produce
     *  a template). */
    AI() {
        this(null, null);
    }

    /** A new AI playing PIECE under control of CONTROLLER. */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    String myMove() {
        Move move = findMove();
        _controller.reportMove(move);
        return move.toString();
    }

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        Board b = new Board(board());
        if (_myPiece == WHITE) {
            findMove(b, maxDepth(b), true, 1, -INFTY, INFTY);
        } else {
            findMove(b, maxDepth(b), true, -1, -INFTY, INFTY);
        }
        return _lastFoundMove;
    }

    /** The move found by the last call to one of the ...FindMove methods
     *  below. */
    private Move _lastFoundMove;

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _lastFoundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _lastMoveFound. */
    private int findMove(Board board, int depth, boolean saveMove, int sense,
                         int alpha, int beta) {
        if (depth == 0 || board.winner() != null) {
            return staticScore(board);
        }

        if (sense == 1) {
            int bestValue = -INFTY;
            Iterator<Move> legalMoves = board.legalMoves(WHITE);
            while (legalMoves.hasNext()) {
                Move x = legalMoves.next();
                board.makeMove(x);
                int before = bestValue;
                int value = findMove(board, depth - 1, false, -1, alpha, beta);
                bestValue = max(bestValue, value);
                if (saveMove) {
                    if (bestValue != before) {
                        _lastFoundMove = x;
                    }
                }
                alpha = max(alpha, bestValue);
                board.undo();
                if (beta <= alpha) {
                    break;
                }
            }
            return bestValue;


        } else {
            int bestValue = INFTY;
            Iterator<Move> legalMoves = board.legalMoves(BLACK);
            while (legalMoves.hasNext()) {
                Move x = legalMoves.next();
                int before = bestValue;
                board.makeMove(x);
                int value = findMove(board, depth - 1, false, 1, alpha, beta);
                bestValue = min(bestValue, value);
                if (saveMove) {
                    if (bestValue != before) {
                        _lastFoundMove = x;
                    }
                }
                beta = min(beta, bestValue);
                board.undo();
                if (beta <= alpha) {
                    break;
                }
            }
            return bestValue;
        }
    }



    /** Return a heuristically determined maximum search depth
     *  based on characteristics of BOARD. */
    private int maxDepth(Board board) {
        int N = board.numMoves();
        int i;
        int period = 15 + 3;
        if (N < period) {
            i = 1;
            return i;
        } else if (N < 2 * period) {
            i = 2;
            return i;
        } else if (N < 3 * period) {
            i = 3;
            return i;
        } else if (N < 4 * period) {
            i = 4;
            return i;
        } else {
            i = 5;
            return i;
        }
    }


    /** Return a heuristic value for BOARD. */
    private int staticScore(Board board) {
        Piece winner = board.winner();
        if (winner == BLACK) {
            return -WINNING_VALUE;
        } else if (winner == WHITE) {
            return WINNING_VALUE;
        } else {

            Iterator<Square> iter = Square.iterator();
            int i = 1;
            ArrayList<Square> queenLocations = new ArrayList<>();
            while (iter.hasNext() && i < 5) {
                Square x = iter.next();
                if (board.get(x) == WHITE) {
                    queenLocations.add(x);
                    i++;
                }
            }

            ArrayList<Square> blackLocations = new ArrayList<>();
            int j = 1;
            while (iter.hasNext() && j < 5) {
                Square x = iter.next();
                if (board.get(x) == BLACK) {
                    blackLocations.add(x);
                    j++;
                }
            }

            int score = 0;
            for (Square x: queenLocations) {
                Iterator<Square> moves = board.reachableFrom(x, null);
                while (moves.hasNext()) {
                    score++;
                    moves.next();
                }
            }

            int score2 = 0;
            for (Square x: blackLocations) {
                Iterator<Square> moves = board.reachableFrom(x, null);
                while (moves.hasNext()) {
                    score2++;
                    moves.next();
                }
            }
            return score - score2;
        }

    }


}
