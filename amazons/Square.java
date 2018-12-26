package amazons;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/** Represents a position on an Amazons board.  Positions are numbered
 *  from 0 (lower-left corner) to 99 (upper-right corner).  Squares
 *  are immutable and unique: there is precisely one square created for
 *  each distinct position.  Clients create squares using the factory method
 *  sq, not the constructor.  Because there is a unique Square object for each
 *  position, you can freely use the cheap == operator (rather than the
 *  .equals method) to compare Squares, and the program does not waste time
 *  creating the same square over and over again.
 *  @author Ani Gottiparthy
 */
final class Square {

    /** The regular expression for a square designation (e.g.,
     *  a3). For convenience, it is in parentheses to make it a
     *  group.  This subpattern is intended to be incorporated into
     *  other pattern that contain square designations (such as
     *  patterns for moves). */
    static final String SQ = "([a-j](?:[1-9]|10))";

    /** Return my row position, where 0 is the bottom row. */
    int row() {
        return _row;
    }

    /** Return my column position, where 0 is the leftmost column. */
    int col() {
        return _col;
    }

    /** Return my index position (0-99).  0 represents square a1, and 99
     *  is square j10. */
    int index() {
        return _index;
    }

    /** Return true iff THIS - TO is a valid queen move. */
    boolean isQueenMove(Square to) {
        int rowdif = Math.abs(to.row() - row());
        int coldif = Math.abs(to.col() - col());
        return this != to && (rowdif == coldif || rowdif == 0 || coldif == 0);
    }

    /** Definitions of direction for queenMove.  DIR[k] = (dcol, drow)
     *  means that to going one step from (col, row) in direction k,
     *  brings us to (col + dcol, row + drow). */
    private static final int[][] DIR = {
        { 0, 1 }, { 1, 1 }, { 1, 0 }, { 1, -1 },
        { 0, -1 }, { -1, -1 }, { -1, 0 }, { -1, 1 }
    };

    /** Return the Square that is STEPS>0 squares away from me in direction
     *  DIR, or null if there is no such square.
     *  DIR = 0 for north, 1 for northeast, 2 for east, etc., up to 7 for
     *  northwest. If DIR has another value, return null. Thus, unless the
     *  result is null the resulting square is a queen move away from me. */
    Square queenMove(int dir, int steps) {
        if (dir < 0 || dir > 7) {
            return null;
        }
        int rowSteps = DIR[dir][1] * steps;
        int colSteps = DIR[dir][0] * steps;
        if (_col + colSteps < 0 || _col + colSteps > 9
                || _row + rowSteps < 0 || _row + rowSteps > 9) {
            return null;
        }
        return sq(col() + colSteps, row() + rowSteps);
    }



    /** Return the direction (an int as defined in the documentation
     *  for quee0nMove) of the queen move THIS-TO. */
    int direction(Square to) {
        int row = row();
        int col = col();
        int torow = to.row();
        int tocol = to.col();
        if (torow > row && tocol == col) {
            return 0;
        } else if (torow < row && tocol == col) {
            return 4;
        } else if (tocol > col && torow == row) {
            return 2;
        } else if (tocol < col && torow == row) {
            return 6;
        } else if (torow > row && tocol > col) {
            return 1;
        } else if (torow < row && tocol > col) {
            return 3;
        } else if (torow > row && tocol < col) {
            return 7;
        } else if (torow < row && tocol < col) {
            return 5;
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        return _str;
    }

    /** Return true iff COL ROW is a legal square. */
    public static boolean exists(int col, int row) {
        return row >= 0 && col >= 0 && row < Board.SIZE && col < Board.SIZE;
    }

    /** Return the (unique) Square denoting COL ROW. */
    static Square sq(int col, int row) {
        if (!exists(row, col)) {
            return null;
        }
        return SQUARES[10 * row + col];
    }

    /** Return the (unique) Square denoting the position with index INDEX. */
    static Square sq(int index) {
        if (index < 100) {
            return SQUARES[index];
        } else {
            return null;
        }

    }

    /** Return the (unique) Square denoting the position COL ROW, where
     *  COL ROW is the standard text format for a square (e.g., a4). */
    static Square sq(String col, String row) {
        int row1 = Integer.parseInt(row);
        row1 -= 1;
        char col1 = col.charAt(0);
        int column = findCol(col1);
        return sq(column, row1);
    }

    /** Return the (unique) Square denoting the position in POSN, in the
     *  standard text format for a square (e.g. a4). POSN must be a
     *  valid square designation. */
    static Square sq(String posn)  {
        assert posn.matches(SQ);
        String col = posn.substring(0, 1);
        String row = posn.substring(1);
        return sq(col, row);
    }

    /** Return the int number for column with char A. */
    static int findCol(char a) {
        int result = 0;
        char[] x = letters();
        while (x[result] != a && result <= x.length) {
            result++;
        }
        return result;
    }

    /** Return an iterator over all Squares. */
    static Iterator<Square> iterator() {
        return SQUARE_LIST.iterator();
    }

    /** Returns an array of columns of board. */
    static char[] letters() {
        char[] letters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'};
        return letters;
    }

    /** Return the Square with index INDEX. */
    private Square(int index) {
        _index = index;
        _row = index / 10;
        _col = index % 10;
        char [] letters = letters();
        String letterCode = Character.toString(letters[_col]);
        String numberCode = Integer.toString(_row + 1);
        _str = letterCode + numberCode;
    }

    /** The cache of all created squares, by index. */
    private static final Square[] SQUARES =
        new Square[Board.SIZE * Board.SIZE];

    /** SQUARES viewed as a List. */
    private static final List<Square> SQUARE_LIST = Arrays.asList(SQUARES);

    static {
        for (int i = Board.SIZE * Board.SIZE - 1; i >= 0; i -= 1) {
            SQUARES[i] = new Square(i);
        }
    }

    /** My index position. */
    private final int _index;

    /** My row and column (redundant, since these are determined by _index). */
    private final int _row, _col;

    /** My String denotation. */
    private final String _str;
}
