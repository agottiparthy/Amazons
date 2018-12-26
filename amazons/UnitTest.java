package amazons;

import org.junit.Test;

import static amazons.Square.findCol;
import static org.junit.Assert.*;
import ucb.junit.textui;


import java.util.Iterator;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Set;
import java.util.Arrays;

import static amazons.Piece.*;



/** The suite of all JUnit tests for the enigma package.
 *  @author
 */
public class UnitTest {

    /** Run the JUnit tests in this package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }


    /** Tests basic correctness of put and get on the initialized board. */
    @Test
    public void testBasicPutGet() {
        Board b = new Board();
        b.put(BLACK, Square.sq(3, 5));
        assertEquals(b.get(3, 5), BLACK);
        b.put(WHITE, Square.sq(9, 9));
        assertEquals(b.get(9, 9), WHITE);
        b.put(EMPTY, Square.sq(3, 5));
        assertEquals(b.get(3, 5), EMPTY);
    }

    /** Tests proper identification of legal/illegal queen moves. */
    @Test
    public void testIsQueenMove() {
        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(1, 5)));
        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(2, 7)));
        assertFalse(Square.sq(0, 0).isQueenMove(Square.sq(5, 1)));
        assertTrue(Square.sq(1, 1).isQueenMove(Square.sq(9, 9)));
        assertTrue(Square.sq(2, 7).isQueenMove(Square.sq(8, 7)));
        assertTrue(Square.sq(3, 0).isQueenMove(Square.sq(3, 4)));
        assertTrue(Square.sq(7, 9).isQueenMove(Square.sq(0, 2)));
    }

    /** Tests toString for initial board state and a smiling board state. :) */
    @Test
    public void testToString() {
        Board b = new Board();
        assertEquals(INIT_BOARD_STATE, b.toString());
        makeSmile(b);
        assertEquals(SMILE, b.toString());
    }

    private void makeSmile(Board b) {
        b.put(EMPTY, Square.sq(0, 3));
        b.put(EMPTY, Square.sq(0, 6));
        b.put(EMPTY, Square.sq(9, 3));
        b.put(EMPTY, Square.sq(9, 6));
        b.put(EMPTY, Square.sq(3, 0));
        b.put(EMPTY, Square.sq(3, 9));
        b.put(EMPTY, Square.sq(6, 0));
        b.put(EMPTY, Square.sq(6, 9));
        for (int col = 1; col < 4; col += 1) {
            for (int row = 6; row < 9; row += 1) {
                b.put(SPEAR, Square.sq(col, row));
            }
        }
        b.put(EMPTY, Square.sq(2, 7));
        for (int col = 6; col < 9; col += 1) {
            for (int row = 6; row < 9; row += 1) {
                b.put(SPEAR, Square.sq(col, row));
            }
        }
        b.put(EMPTY, Square.sq(7, 7));
        for (int lip = 3; lip < 7; lip += 1) {
            b.put(WHITE, Square.sq(lip, 2));
        }
        b.put(WHITE, Square.sq(2, 3));
        b.put(WHITE, Square.sq(7, 3));
    }

    static final String INIT_BOARD_STATE =
            "   - - - B - - B - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   B - - - - - - - - B\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   W - - - - - - - - W\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - W - - W - - -\n";

    static final String SMILE =
            "   - - - - - - - - - -\n"
                    + "   - S S S - - S S S -\n"
                    + "   - S - S - - S - S -\n"
                    + "   - S S S - - S S S -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - W - - - - W - -\n"
                    + "   - - - W W W W - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n";

    @Test
    public void testIsUnblocked() {
        Board b = new Board();
        b.put(BLACK, Square.sq(3, 5));
        b.put(WHITE, Square.sq(9, 9));
        assertFalse(b.isUnblockedMove(Square.sq(3, 0), Square.sq(3, 9), null));
        assertTrue(b.isUnblockedMove(Square.sq(3, 0), Square.sq(3, 3), null));
        assertFalse(b.isUnblockedMove(Square.sq(0, 9), Square.sq(9, 9), null));
        assertTrue(b.isUnblockedMove(Square.sq(3, 0),
                Square.sq(3, 8), Square.sq(3, 5)));
    }

    @Test
    public void testMove() {
        Board d = new Board();
        Move move2 = Move.mv(Square.sq(3, 9), Square.sq(3, 5), Square.sq(3, 9));
        d.makeMove(move2);
        assertEquals(BLACK, d.get(3, 5));
        assertEquals(SPEAR, d.get(3, 9));
    }



    @Test
    public void testReachableIterator() {
        Board b = new Board();
        Iterator<Square> iter = b.reachableFrom(Square.sq(3, 9), null);
        assertTrue(iter.hasNext());
        assertEquals(Square.sq(4, 9), iter.next());
        assertEquals(Square.sq(5, 9), iter.next());
        assertEquals(Square.sq(4, 8), iter.next());
        assertEquals(Square.sq(5, 7), iter.next());
        assertEquals(Square.sq(6, 6), iter.next());
        assertEquals(Square.sq(7, 5), iter.next());
        assertEquals(Square.sq(8, 4), iter.next());
        assertEquals(Square.sq(3, 8), iter.next());
        assertEquals(Square.sq(3, 7), iter.next());
        assertEquals(Square.sq(3, 6), iter.next());
        assertEquals(Square.sq(3, 5), iter.next());
        assertEquals(Square.sq(3, 4), iter.next());
        assertEquals(Square.sq(3, 3), iter.next());
        assertEquals(Square.sq(3, 2), iter.next());
        assertEquals(Square.sq(3, 1), iter.next());
        assertEquals(Square.sq(2, 8), iter.next());
        assertEquals(Square.sq(1, 7), iter.next());
        assertEquals(Square.sq(2, 9), iter.next());
        assertEquals(Square.sq(1, 9), iter.next());
        assertEquals(Square.sq(0, 9), iter.next());
        assertFalse(iter.hasNext());

        Iterator<Square> iter2 = b.reachableFrom(Square.sq(9, 3), null);
        assertTrue(iter2.hasNext());
        assertEquals(Square.sq(9, 4), iter2.next());
        assertEquals(Square.sq(9, 5), iter2.next());
        assertEquals(Square.sq(9, 2), iter2.next());
        assertEquals(Square.sq(9, 1), iter2.next());
        assertEquals(Square.sq(9, 0), iter2.next());
        assertEquals(Square.sq(8, 2), iter2.next());
        assertEquals(Square.sq(7, 1), iter2.next());
        assertEquals(Square.sq(8, 3), iter2.next());
        assertEquals(Square.sq(7, 3), iter2.next());
        assertEquals(Square.sq(6, 3), iter2.next());
        assertEquals(Square.sq(5, 3), iter2.next());
        assertEquals(Square.sq(4, 3), iter2.next());
        assertEquals(Square.sq(3, 3), iter2.next());
        assertEquals(Square.sq(2, 3), iter2.next());
        assertEquals(Square.sq(1, 3), iter2.next());
        assertEquals(Square.sq(8, 4), iter2.next());
        assertEquals(Square.sq(7, 5), iter2.next());
        assertEquals(Square.sq(6, 6), iter2.next());
        assertEquals(Square.sq(5, 7), iter2.next());
        assertEquals(Square.sq(4, 8), iter2.next());
        assertFalse(iter2.hasNext());

        Board c = new Board();
        c.makeMove(Square.sq(3, 0), Square.sq(3, 1), Square.sq(3, 0));
        Iterator<Square> it = c.reachableFrom(Square.sq(3, 1), null);
        HashSet<Square> squares = new HashSet<>();
        while (it.hasNext()) {
            Square x = it.next();
            squares.add(x);
        }
        assertEquals(27, squares.size());
    }



    @Test
    public void testLegalIterator() {
        Board b = new Board();
        Iterator<Move> m = b.legalMoves(WHITE);
        HashSet<Move> moves = new HashSet<>();
        assertTrue(m.hasNext());
        Iterator<Square> x = Square.iterator();
        while (x.hasNext()) {
            Square s = x.next();
            if (b.get(s) == EMPTY) {
                b.put(SPEAR, s);
            }
        }
        Iterator<Move> m2 = b.legalMoves(WHITE);
        Iterator<Move> m3 = b.legalMoves(BLACK);
        assertFalse(m2.hasNext());
        assertFalse(m3.hasNext());

        while (m.hasNext()) {
            moves.add(m.next());
        }


    }

    @Test
    public void testQueenMove() {
        Square test1 = Square.sq(4, 5);
        Square test2 = Square.sq(4, 6);
        Square test3 = Square.sq(9, 9);
        Square test4 = Square.sq(4, 4);
        Square holder = test1.queenMove(0, 1);
        assertEquals(test2, test1.queenMove(0, 1));
        assertNull(test3.queenMove(0, 3));
        assertEquals(test4, test3.queenMove(5, 5));
    }

    @Test
    public void testunblocked() {
        Board b = new Board();
        b.put(BLACK, Square.sq(3, 5));
        b.put(WHITE, Square.sq(9, 9));
        Square from = Square.sq(3, 0);
        Square to = Square.sq(3, 7);
        assertEquals(b.isUnblockedMove(from, to, null), false);
        assertFalse(b.isUnblockedMove(Square.sq(7, 9), to, null));
        b.put(WHITE, Square.sq(7, 9));
        assertFalse(b.isUnblockedMove(Square.sq(7, 9), to, null));
    }

    @Test
    public void testReachableFrom() {
        Board b = new Board();
        buildBoard(b, REACHABLEFROMTESTBOARD);
        int numSquares = 0;
        Set<Square> squares = new HashSet<>();
        Iterator<Square> reachableFrom = b.reachableFrom(Square.sq(5, 4), null);
        while (reachableFrom.hasNext()) {
            Square s = reachableFrom.next();
            assertTrue(REACHABLEFROMTESTSQUARES.contains(s));
            numSquares += 1;
            squares.add(s);
        }
        assertEquals(REACHABLEFROMTESTSQUARES.size(), numSquares);
        assertEquals(REACHABLEFROMTESTSQUARES.size(), squares.size());
    }

    /** Tests legalMovesIterator to make sure it returns all legal Moves.
     *  This method needs to be finished and may need to be changed
     *  based on your implementation. */

    @Test
    public void testLegalMoves() {
        Board b = new Board();
        ArrayList<Move> moves = new ArrayList<>();
        Iterator<Move> legalMoves = b.legalMoves(Piece.WHITE);
        while (legalMoves.hasNext()) {
            Move m = legalMoves.next();
            moves.add(m);

        }
        assertEquals(2176, moves.size());


    }


    private void buildBoard(Board b, Piece[][] target) {
        for (int col = 0; col < Board.SIZE; col++) {
            for (int row = Board.SIZE - 1; row >= 0; row--) {
                Piece piece = target[Board.SIZE - row - 1][col];
                b.put(piece, Square.sq(col, row));
            }
        }
    }


    static final Piece E = Piece.EMPTY;

    static final Piece W = Piece.WHITE;

    static final Piece B = Piece.BLACK;

    static final Piece S = Piece.SPEAR;

    static final Piece[][] REACHABLEFROMTESTBOARD =
    {
        { E, E, E, E, E, E, E, E, E, E },
        { E, E, E, E, E, E, E, E, W, W },
        { E, E, E, E, E, E, E, S, E, S },
        { E, E, E, S, S, S, S, E, E, S },
        { E, E, E, S, E, E, E, E, B, E },
        { E, E, E, S, E, W, E, E, B, E },
        { E, E, E, S, S, S, B, W, B, E },
        { E, E, E, E, E, E, E, E, E, E },
        { E, E, E, E, E, E, E, E, E, E },
        { E, E, E, E, E, E, E, E, E, E },
    };

    static final Set<Square> REACHABLEFROMTESTSQUARES =
            new HashSet<>(Arrays.asList(
                    Square.sq(5, 5),
                    Square.sq(4, 5),
                    Square.sq(4, 4),
                    Square.sq(6, 4),
                    Square.sq(7, 4),
                    Square.sq(6, 5),
                    Square.sq(7, 6),
                    Square.sq(8, 7)));

    @Test
    public void testOneMoveWhite() {
        Board b = new Board();
        buildBoard(b, ONEMOVEWHITE);
        int numMoves = 0;
        Set<Move> moves = new HashSet<>();
        Iterator<Move> legalMoves = b.legalMoves(Piece.WHITE);
        while (legalMoves.hasNext()) {
            Move m = legalMoves.next();
            assertTrue(ONEMOVEWHITESQUARE.contains(m));
            numMoves += 1;
            moves.add(m);
        }
        assertEquals(1, numMoves);
        assertEquals(1, moves.size());
    }
    static final Piece[][] ONEMOVEWHITE =
    {
        { E, E, S, W, S, S, W, S, E, E },
        { S, S, S, S, S, S, S, S, S, S },
        { W, E, S, E, E, E, E, E, S, W },
        { S, S, S, E, E, E, E, E, S, S },
        { E, E, E, E, E, E, E, E, E, E },
        { E, E, E, E, E, E, E, E, E, E },
        { E, E, E, E, E, E, E, E, E, E },
        { B, E, E, E, E, E, E, E, E, B },
        { E, E, E, E, E, E, E, E, E, E },
        { E, E, E, B, E, E, B, E, E, E },
    };

    static final Set<Move> ONEMOVEWHITESQUARE =
            new HashSet<>(Arrays.asList(
                    Move.mv(Square.sq(0, 7), Square.sq(1, 7), Square.sq(0, 7))
            ));

    @Test
    public void testOnePiece() {
        Board b = new Board();
        buildBoard(b, ONEPIECE);
        ArrayList<Move> moves = new ArrayList<>();
        Iterator<Move> legalMoves = b.legalMoves(Piece.WHITE);
        while (legalMoves.hasNext()) {
            Move m = legalMoves.next();
            moves.add(m);

        }
        assertEquals(769, moves.size());
    }

    static final Piece[][] ONEPIECE =
    {
        { W, E, E, E, E, E, E, E, E, E },
        { E, E, E, E, E, E, E, E, E, E },
        { E, E, E, E, E, E, E, E, E, E },
        { E, E, E, E, E, E, E, E, E, E },
        { E, E, E, E, E, E, E, E, E, E },
        { E, E, E, E, E, E, E, E, E, E },
        { E, E, E, E, E, E, E, E, E, E },
        { E, E, E, E, E, E, E, E, E, E },
        { E, E, E, E, E, E, E, E, E, E },
        { E, E, E, E, E, E, E, E, E, E },
    };

    private void makeBoard(Board b) {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if (col > row) {
                    b.put(Piece.WHITE, Square.sq(col, row));
                }
                if (col == row || row > 5) {
                    b.put(Piece.SPEAR, Square.sq(col, row));
                }
                if (row == 5) {
                    b.put(Piece.BLACK, Square.sq(col, row));
                }

            }
        }
        b.put(Piece.SPEAR, Square.sq(1, 3));
        b.put(Piece.BLACK, Square.sq(3, 2));
        b.put(Piece.BLACK, Square.sq(4, 3));
    }

    @Test
    public void testQueenMove2() {
        assertEquals(Square.sq(0, 9),
                Square.sq(0, 0).queenMove(0, 9));
        assertEquals(Square.sq(9, 9).toString(),
                Square.sq(0, 0).queenMove(1, 9).toString());
        assertEquals(Square.sq(0, 9).toString(),
                Square.sq(9, 0).queenMove(7, 9).toString());
        assertEquals(Square.sq(0, 0).toString(),
                Square.sq(9, 9).queenMove(5, 9).toString());
        assertEquals(Square.sq(9, 0).toString(),
                Square.sq(0, 9).queenMove(3, 9).toString());
    }

    @Test
    public void testString() {
        assertEquals("a9", Square.sq(0, 8).toString());
        assertEquals("j10", Square.sq(9, 9).toString());
        assertEquals("d5", Square.sq(3, 4).toString());
        assertEquals("j1", Square.sq(9, 0).toString());
    }

    @Test
    public void testFindCol() {
        assertEquals(0, findCol('a'));
        assertEquals(3, findCol('d'));
        assertEquals(9, findCol('j'));
    }

    @Test
    public void testSQ() {
        assertEquals(Square.sq(9, 9), Square.sq("j10"));
        assertEquals(Square.sq(1, 9), Square.sq("b10"));
        assertEquals(Square.sq(1, 9), Square.sq("b", "10"));
    }

    @Test
    public void testDirection() {
        assertEquals(0, Square.sq(0, 0).direction(Square.sq(0, 9)));
        assertEquals(1, Square.sq(0, 0).direction(Square.sq(1, 1)));
        assertEquals(2, Square.sq(0, 0).direction(Square.sq(1, 0)));
        assertEquals(3, Square.sq(0, 1).direction(Square.sq(1, 0)));
        assertEquals(4, Square.sq(0, 3).direction(Square.sq(0, 0)));
        assertEquals(5, Square.sq(2, 2).direction(Square.sq(0, 0)));
        assertEquals(6, Square.sq(9, 9).direction(Square.sq(0, 9)));
        assertEquals(7, Square.sq(9, 0).direction(Square.sq(0, 9)));
    }

}



