package amazons;


import java.util.HashMap;
import java.util.Stack;
import java.util.Iterator;
import java.util.Collections;
import static amazons.Piece.*;


/** The state of an Amazons Game.
 *  @author Ani Gottiparthy
 */
class Board {

    /** The number of squares on a side of the board. */
    static final int SIZE = 10;

    /** The pieces on each square. */
    private static HashMap<Square, Piece> boardMap;

    /** The stack containing each move. */
    private static Stack<Move> moveStack;

    /** Initializes a game board with SIZE squares on a side in the
     *  initial position. */
    Board() {
        init();
    }

    /** Initializes a copy of MODEL. */
    Board(Board model) {
        copy(model);
    }

    /** Copies MODEL into me. */
    void copy(Board model) {
        HashMap<Square, Piece> toCopy = model.boardmap();
        this.moveStack = (Stack<Move>) model.moveStack.clone();
        this._turn = model._turn;
        this.boardMap = new HashMap<>(100);
        for (Square x: toCopy.keySet()) {
            this.boardMap.put(x, toCopy.get(x));
        }
        this._winner = model._winner;
    }

    /** Clears the board to the initial position. */
    void init() {
        boardMap = new HashMap<>(100);
        _turn = WHITE;
        _winner = EMPTY;
        moveStack = new Stack<>();
        setboard();
    }

    /** Returns this boards hashMap. */
    HashMap<Square, Piece> boardmap() {
        return boardMap;
    }

    /** Creates the initial setting for the board. */
    void setboard() {
        for (int i = 0; i <= 9; i++) {
            for (int j = 0; j <= 9; j++) {
                boardMap.put(Square.sq(i, j), EMPTY);
            }
        }

        boardMap.put(Square.sq(3, 0), WHITE);
        boardMap.put(Square.sq(6, 0), WHITE);
        boardMap.put(Square.sq(0, 3), WHITE);
        boardMap.put(Square.sq(9, 3), WHITE);
        boardMap.put(Square.sq(0, 6), BLACK);
        boardMap.put(Square.sq(9, 6), BLACK);
        boardMap.put(Square.sq(3, 9), BLACK);
        boardMap.put(Square.sq(6, 9), BLACK);

    }

    /** Return the Piece whose move it is (WHITE or BLACK). */
    Piece turn() {
        return _turn;
    }

    /** Return the number of moves (that have not been undone) for this
     *  board. */
    int numMoves() {
        return moveStack.size();
    }

    /** Return the winner in the current position, or null if the game is
     *  not yet finished. */
    Piece winner() {
        if (!legalMoves(_turn).hasNext()) {
            return _turn.opponent();
        } else {
            return null;
        }
    }

    /** Return the contents the square at S. */
    final Piece get(Square s) {
        return get(s.col(), s.row());
    }

    /** Return the contents of the square at (COL, ROW), where
     *  0 <= COL, ROW <= 9. */
    final Piece get(int col, int row) {
        return boardMap.get(Square.sq(col, row));
    }

    /** Return the contents of the square at COL ROW. */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /** Set square S to P. */
    final void put(Piece p, Square s) {
        put(p, s.col(), s.row());
    }

    /** Set square (COL, ROW) to P. */
    final void put(Piece p, int col, int row) {
        boardMap.put(Square.sq(col, row), p);
    }

    /** Set square COL ROW to P. */
    final void put(Piece p, char col, char row) {
        put(p, col - 'a', row - '1');
    }

    /** Return true iff FROM - TO is an unblocked queen move on the current
     *  board, ignoring the contents of ASEMPTY, if it is encountered.
     *  For this to be true, FROM-TO must be a queen move and the
     *  squares along it, other than FROM and ASEMPTY, must be
     *  empty. ASEMPTY may be null, in which case it has no effect. */
    boolean isUnblockedMove(Square from, Square to, Square asEmpty) {
        try {
            from.isQueenMove(to);
        } catch (NullPointerException a) {
            return false;
        }

        if (!from.isQueenMove(to)) {
            return false;
        }
        int direction = from.direction(to);
        int rowdif = Math.abs(from.row() - to.row());
        int coldif = Math.abs(from.col() - to.col());
        int numSteps;
        if (rowdif == coldif) {
            numSteps = rowdif;
        } else if (rowdif == 0) {
            numSteps = coldif;
        } else {
            numSteps = rowdif;
        }
        for (int i = 1; i <= numSteps; i++) {
            if (from.queenMove(direction, i) == asEmpty) {
                continue;
            } else if (boardMap.get(from.queenMove(direction, i)) != EMPTY) {
                return false;
            }
        }
        return true;
    }

    /** Return true iff FROM is a valid starting square for a move. */
    boolean isLegal(Square from) {
        return boardMap.get(from) == _turn;
    }

    /** Return true iff FROM-TO is a valid first part of move, ignoring
     *  spear throwing. */
    boolean isLegal(Square from, Square to) {
        return from.isQueenMove(to) && isLegal(from)
                && isUnblockedMove(from, to, null);
    }

    /** Return true iff FROM-TO(SPEAR) is a legal move in the current
     *  position. */
    boolean isLegal(Square from, Square to, Square spear) {
        return isLegal(from, to) && isUnblockedMove(to, spear, from);
    }

    /** Return true iff MOVE is a legal move in the current
     *  position. */
    boolean isLegal(Move move) {
        return isLegal(move.from(), move.to(), move.spear());
    }

    /** Move FROM-TO(SPEAR), assuming this is a legal move. */
    void makeMove(Square from, Square to, Square spear) {
        boardMap.put(to, boardMap.get(from));
        boardMap.put(from, EMPTY);
        boardMap.put(spear, SPEAR);
        moveStack.push(Move.mv(from, to, spear));
        _turn = _turn.opponent();
    }

    /** Move according to MOVE, assuming it is a legal move. */
    void makeMove(Move move) {
        makeMove(move.from(), move.to(), move.spear());
    }

    /** Undo one move.  Has no effect on the initial board. */
    void undo() {
        if (moveStack.isEmpty()) {
            throw new IllegalCallerException("No moves to undo");
        }
        Move stackTop = moveStack.pop();
        boardMap.put(stackTop.spear(), EMPTY);
        boardMap.put(stackTop.from(), boardMap.get(stackTop.to()));
        boardMap.put(stackTop.to(), EMPTY);
        _turn = _turn.opponent();
    }

    /** Return an Iterator over the Squares that are reachable by an
     *  unblocked queen move from FROM. Does not pay attention to what
     *  piece (if any) is on FROM, nor to whether the game is finished.
     *  Treats square ASEMPTY (if non-null) as if it were EMPTY.  (This
     *  feature is useful when looking for Moves, because after moving a
     *  piece, one wants to treat the Square it came from as empty for
     *  purposes of spear throwing.) */
    Iterator<Square> reachableFrom(Square from, Square asEmpty) {
        return new ReachableFromIterator(from, asEmpty);
    }

    /** Return an Iterator over all legal moves on the current board. */
    Iterator<Move> legalMoves() {
        return new LegalMoveIterator(_turn);
    }

    /** Return an Iterator over all legal moves on the current board for
     *  SIDE (regardless of whose turn it is). */
    Iterator<Move> legalMoves(Piece side) {
        return new LegalMoveIterator(side);
    }

    /** An iterator used by reachableFrom. */
    private class ReachableFromIterator implements Iterator<Square> {

        /** Iterator of all squares reachable by queen move from FROM,
         *  treating ASEMPTY as empty. */
        ReachableFromIterator(Square from, Square asEmpty) {
            _from = from;
            _dir = 0;
            _steps = 0;
            _asEmpty = asEmpty;
            toNext();
        }

        @Override
        public boolean hasNext() {
            return _dir < 8;
        }

        @Override
        public Square next() {
            Square to = _from.queenMove(_dir, _steps);
            toNext();
            return to;
        }

        /** Advance _dir and _steps, so that the next valid Square is
         *  _steps steps in direction _dir from _from. */
        private void toNext() {
            _steps++;
            while (_dir < 8 && _from.queenMove(_dir, _steps) == null) {
                _dir++;
                _steps = 1;
            }
            while (_dir < 8
                    && !isUnblockedMove(_from,
                    _from.queenMove(_dir, _steps), _asEmpty)) {
                _dir++;
                _steps = 1;
            }
        }

        /** Starting square. */
        private Square _from;
        /** Current direction. */
        private int _dir;
        /** Current distance. */
        private int _steps;
        /** Square treated as empty. */
        private Square _asEmpty;
    }

    /** An iterator used by legalMoves. */
    private class LegalMoveIterator implements Iterator<Move> {

        /** All legal moves for SIDE (WHITE or BLACK). */
        LegalMoveIterator(Piece side) {
            _fromPiece = side;
            _startingSquares = Square.iterator();
            _spearThrows = NO_SQUARES;
            _pieceMoves = NO_SQUARES;
            toNext();
        }

        @Override
        public boolean hasNext() {
            return _spearThrows.hasNext();
        }

        @Override
        public Move next() {
            Move x = Move.mv(_start, _nextSquare, _spearThrows.next());
            toNext();
            return x;
        }

        /** Advance so that the next valid Move is
         *  _start-_nextSquare(sp), where sp is the next value of
         *  _spearThrows. */
        private void toNext() {
            while (true) {
                if (_spearThrows.hasNext()) {
                    return;
                } else if (_pieceMoves.hasNext()) {
                    _nextSquare = _pieceMoves.next();
                    _spearThrows = reachableFrom(_nextSquare, _start);
                } else if (_startingSquares.hasNext()) {
                    _start = _startingSquares.next();
                    if (get(_start) == _fromPiece) {
                        _pieceMoves = reachableFrom(_start, null);
                        _spearThrows = NO_SQUARES;
                    }
                } else {
                    _nextSquare = null;
                    _start = null;
                    break;
                }
            }
        }


        /** Color of side whose moves we are iterating. */
        private Piece _fromPiece;
        /** Current starting square. */
        private Square _start;
        /** Remaining starting squares to consider. */
        private Iterator<Square> _startingSquares;
        /** Current piece's new position. */
        private Square _nextSquare;
        /** Remaining moves from _start to consider. */
        private Iterator<Square> _pieceMoves;
        /** Remaining spear throws from _piece to consider. */
        private Iterator<Square> _spearThrows;
    }

    @Override
    public String toString() {
        String result = "";
        for (int i = 9; i >= 0; i--) {
            String row = "  ";
            for (int j = 0; j <= 9; j++) {
                row += (" " + boardMap.get(Square.sq(j, i)));
            }
            row += "\n";
            result += row;
        }
        return result;
    }

    /** An empty iterator for initialization. */
    private static final Iterator<Square> NO_SQUARES =
        Collections.emptyIterator();

    /** Piece whose turn it is (BLACK or WHITE). */
    private Piece _turn;
    /** Cached value of winner on this board, or EMPTY if it has not been
     *  computed. */
    private Piece _winner;
}
