package qirkat;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.Observable;
import java.util.Observer;

import static qirkat.PieceColor.*;
import static qirkat.Move.*;
import java.util.Stack;
import java.util.HashMap;
import java.util.Arrays;

/** A Qirkat board.   The squares are labeled by column (a char value between
 *  'a' and 'e') and row (a char value between '1' and '5').
 *
 *  For some purposes, it is useful to refer to squares using a single
 *  integer, which we call its "linearized index".  This is simply the
 *  number of the square in row-major order (with row 0 being the bottom row)
 *  counting from 0).
 *
 *  Moves on this board are denoted by Moves.
 *  @author Lila Chalabi
 */
class Board extends Observable {

    /** A new, cleared board at the start of the game. */
    Board() {
        for (int i = 0; i < MAX_INDEX + 1; i++) {
            _pieces.add(i, null);
            _visitedSquares.put(i, new Stack<Integer>());
        }
        _whoseMove = WHITE;
        _gameOver = false;

        setPieces(INIT_BOARD, _whoseMove);
    }

    /** A copy of B. */
    Board(Board b) {
        this();
        internalCopy(b);
    }

    /** Return a constant view of me (allows any access method, but no
     *  method that modifies it). */
    Board constantView() {
        return this.new ConstantBoard();
    }

    /** Clear me to my starting state, with pieces in their initial
     *  positions. */
    void clear() {
        _whoseMove = WHITE;
        _gameOver = false;

        setPieces(INIT_BOARD, _whoseMove);

        setChanged();
        notifyObservers();
    }

    /** Copy B into me. */
    void copy(Board b) {
        internalCopy(b);
    }

    /** Copy B into me. WAS PRIVATE */

    public void internalCopy(Board b) {
        for (int i = 0; i < _pieces.size(); i++) {
            _pieces.set(i, b._pieces.get(i));
        }
        this._madeMoves = b._madeMoves;
        this._whoseMove = b.whoseMove();
        this._copying = b._copying;
    }

    /** Set my contents as defined by STR.  STR consists of 25 characters,
     *  each of which is b, w, or -, optionally interspersed with whitespace.
     *  These give the contents of the Board in row-major order, starting
     *  with the bottom row (row 1) and left column (column a). All squares
     *  are initialized to allow horizontal movement in either direction.
     *  NEXTMOVE indicates whose move it is.
     */
    void setPieces(String str, PieceColor nextMove) {
        if (nextMove == EMPTY || nextMove == null) {
            throw new IllegalArgumentException("bad player color");
        }
        str = str.replaceAll("\\s", "");
        if (!str.matches("[bw-]{25}")) {
            throw new IllegalArgumentException("bad board description");
        }

        for (int i = 0; i < _visitedSquares.size(); i++) {
            _visitedSquares.put(i, new Stack<Integer>());
        }

        _whoseMove = nextMove;

        for (int k = 0; k < str.length(); k += 1) {
            switch (str.charAt(k)) {
            case '-':
                set(k, EMPTY);
                break;
            case 'b': case 'B':
                set(k, BLACK);
                break;
            case 'w': case 'W':
                set(k, WHITE);
                break;
            default:
                break;
            }
        }


        setChanged();
        notifyObservers();
    }

    /** Return true iff the game is over: i.e., if the current player has
     *  no moves. */
    boolean gameOver() {
        return _gameOver;
    }

    /** Return the current contents of square C R, where 'a' <= C <= 'e',
     *  and '1' <= R <= '5'.  */
    PieceColor get(char c, char r) {
        assert validSquare(c, r);
        return get(index(c, r));
    }

    /** Return the current contents of the square at linearized index K. */
    PieceColor get(int k) {
        assert validSquare(k);
        PieceColor p = _pieces.get(k);
        return _pieces.get(k);
    }

    /** Set get(C, R) to V, where 'a' <= C <= 'e', and
     *  '1' <= R <= '5'. */
    private void set(char c, char r, PieceColor v) {
        assert validSquare(c, r);
        set(index(c, r), v);
    }

    /** Set get(K) to V, where K is the linearized index of a square.
     * i would say rely on this method to set */
    private void set(int k, PieceColor v) {
        assert validSquare(k);
        _pieces.set(k, v);
    }

    /** Return true iff MOV is legal on the current board.
     * if mov is in getMoves() then it is a legal move*/
    boolean legalMove(Move mov) {
        if (gameOver()) {
            return false;
        }
        if (get(mov.fromIndex()) == EMPTY
                || get(mov.fromIndex()) == whoseMove().opposite()) {
            return false;
        } else if (!mov.isJump()) {
            if (jumpPossible()) {
                return false;

            } else if (whoseMove() == WHITE) {
                if (mov.row1() < mov.row0() || mov.fromIndex() > 19) {
                    return false;
                }

            } else if (whoseMove() == BLACK) {
                if (mov.row1() > mov.row0() || mov.fromIndex() < 5) {
                    return false;
                }
            }
            if (_visitedSquares.get(mov.fromIndex()).contains(mov.toIndex())) {
                return false;

            } else if (_pieces.get(mov.toIndex()) != EMPTY) {
                return false;
            }

        } else if (mov.isJump()) {
            if (_copying) {
                return checkJump(mov, true);
            } else {
                return checkJump(mov, false);
            }
        }
        return true;

    }

    /** Return a list of all legal moves from the current position. */
    ArrayList<Move> getMoves() {
        ArrayList<Move> result = new ArrayList<>();
        getMoves(result);
        return result;
    }

    /** Add all legal moves from the current position to MOVES. */
    void getMoves(ArrayList<Move> moves) {
        if (gameOver()) {
            return;
        }
        if (jumpPossible()) {
            for (int k = 0; k <= MAX_INDEX; k += 1) {
                getJumps(moves, k);
            }
        } else {
            for (int k = 0; k <= MAX_INDEX; k += 1) {
                getMoves(moves, k);
            }
        }
    }

    /** Add all legal non-capturing moves from the position
     *  with linearized index K to MOVES. */
    private void getMoves(ArrayList<Move> moves, int k) {
        if (get(k) != whoseMove()) {
            return;
        }
        if (k % 2 == 0) {
            if (whoseMove() == WHITE && validSquare(k + 6)
                    && col(k + 6) == (char) (col(k) + 1)
                    && row(k + 6) == (char) (row(k) + 1)) {
                PieceColor diagupright = get(k + 6);
                if (!diagupright.isPiece()) {
                    Move mdiagupright = Move.move(col(k), row(k),
                            col(k + 6), row(k + 6));
                    if (legalMove(mdiagupright)) {
                        moves.add(mdiagupright);
                    }
                }
            }
            if (whoseMove() == WHITE && validSquare(k + 4)
                    && col(k + 4) == (char) (col(k) - 1)
                    && row(k + 4) == (char) (row(k) + 1)) {
                PieceColor diagupleft = get(k + 4);
                if (!diagupleft.isPiece()) {
                    Move mdiagupleft = Move.move(col(k), row(k),
                            col(k + 4), row(k + 4));
                    if (legalMove(mdiagupleft)) {
                        moves.add(mdiagupleft);
                    }
                }
            }
            if (whoseMove() == BLACK && validSquare(k - 6)
                    && col(k - 6) == (char) (col(k) - 1)
                    && row(k - 6) == (char) (row(k) - 1)) {
                PieceColor diagdownleft = get(k - 6);
                if (!diagdownleft.isPiece()) {
                    Move mdiagdownleft = Move.move(col(k), row(k),
                            col(k - 6), row(k - 6));
                    if (legalMove(mdiagdownleft)) {
                        moves.add(mdiagdownleft);
                    }
                }
            }
            if (whoseMove() == BLACK && validSquare(k - 4)
                    && col(k - 4) == (char) (col(k) + 1)
                    && row(k - 4) == (char) (row(k) - 1)) {
                PieceColor diagdownright = get(k - 4);
                if (!diagdownright.isPiece()) {
                    Move mdiagdownright = Move.move(col(k), row(k),
                            col(k - 4), row(k - 4));
                    if (legalMove(mdiagdownright)) {
                        moves.add(mdiagdownright);
                    }
                }
            }

        }
        if (validSquare(k + 1) && col(k + 1) == (char) (col(k) + 1)) {
            PieceColor right = get(k + 1);
            if (!right.isPiece()) {
                Move mright = Move.move(col(k), row(k),
                        col(k + 1), row(k));
                if (legalMove(mright)) {
                    moves.add(mright);
                }
            }
        }

        if (validSquare(k - 1) && col(k - 1) == (char) (col(k) - 1)) {
            PieceColor left = get(k - 1);
            if (!left.isPiece()) {
                Move mleft = Move.move(col(k), row(k), col(k - 1), row(k));
                if (legalMove(mleft)) {
                    moves.add(mleft);
                }
            }
        }
        if (whoseMove() == WHITE && validSquare(k + 5)
                && row(k + 5) == (char) (row(k) + 1)) {
            PieceColor up = get(k + 5);
            if (!up.isPiece()) {
                Move mup = Move.move(col(k), row(k), col(k), row(k + 5));
                if (legalMove(mup)) {
                    moves.add(mup);
                }
            }
        }

        if (whoseMove() == BLACK && validSquare(k - 5)
                && row(k - 5) == (char) (row(k) - 1)) {
            PieceColor down = get(k - 5);
            if (!down.isPiece()) {
                Move mdown = Move.move(col(k), row(k), col(k), row(k - 5));
                if (legalMove(mdown)) {
                    moves.add(mdown);
                }
            }
        }
    }

    /** finds single jumps for a position on the board. */
    public ArrayList<Move> findSingleJumps(char c, char r) {
        ArrayList<Move> singlejumps = new ArrayList<Move>();
        if (get(c, r) == EMPTY
                || get(c, r) == _whoseMove.opposite()) {
            return null;
        }

        char cleft = (char) (c - 1);
        char cleft2 = (char) (cleft - 1);

        char cright = (char) (c + 1);
        char cright2 = (char) (cright + 1);

        char rup = (char) (r + 1);
        char rup2 = (char) (rup + 1);

        char rdown = (char) (r - 1);
        char rdown2 = (char) (rdown - 1);

        if (index(c, r) % 2 == 0) {
            if (validSquare(cleft, rup) && validSquare(cleft2, rup2)
                    && get(cleft2, rup2) == EMPTY
                    && get(cleft, rup) == _whoseMove.opposite()) {
                singlejumps.add(Move.move(c, r, cleft2, rup2));
            } else if (validSquare(cright, rup) && validSquare(cright2, rup2)
                    && get(cright2, rup2) == EMPTY
                    && get(cright, rup) == _whoseMove.opposite()) {
                singlejumps.add(Move.move(c, r, cright2, rup2));
            } else if (validSquare(cright, rdown)
                    && validSquare(cright2, rdown2)
                    && get(cright2, rdown2) == EMPTY
                    && get(cright, rdown) == _whoseMove.opposite()) {
                singlejumps.add(Move.move(c, r, cright2, rdown2));
            } else if (validSquare(cleft, rdown) && validSquare(cleft2, rdown2)
                    && get(cleft2, rdown2) == EMPTY
                    && get(cleft, rdown) == _whoseMove.opposite()) {
                singlejumps.add(Move.move(c, r, cleft2, rdown2));
            }
        }
        if (validSquare(cleft, r) && validSquare(cleft2, r)
                && get(cleft2, r) == EMPTY
                && get(cleft, r) == _whoseMove.opposite()) {
            singlejumps.add(Move.move(c, r, cleft2, r));
        } else if (validSquare(c, rup) && validSquare(c, rup2)
                && get(c, rup2) == EMPTY
                && get(c, rup) == _whoseMove.opposite()) {
            singlejumps.add(Move.move(c, r, c, rup2));
        } else if (validSquare(cright, r) && validSquare(cright2, r)
                && get(cright2, r) == EMPTY
                && get(cright, r) == _whoseMove.opposite()) {
            singlejumps.add(Move.move(c, r, cright2, r));
        } else if (validSquare(c, rdown) && validSquare(c, rdown2)
                && get(c, rdown2) == EMPTY
                && get(c, rdown) == _whoseMove.opposite()) {
            singlejumps.add(Move.move(c, r, c, rdown2));
        }

        return singlejumps;

    }


    /** Add all legal captures from the position with linearized index K
     *  to MOVES. */
    public void getJumps(ArrayList<Move> moves, int k) {
        _copying = true;
        Board copy = new Board(this);
        if (!jumpPossible(k)) {
            return;
        }
        ArrayList<Move> singlejumps = copy.findSingleJumps(col(k), row(k));
        Stack<Move> jumps = new Stack<Move>();
        for (Move m : singlejumps) {
            jumps.push(m);
        }
        singlejumps.clear();
        while (!jumps.empty()) {
            copy = new Board(this);
            Move j0 = jumps.pop();
            PieceColor t = whoseMove();
            PieceColor w = copy.whoseMove();
            copy.makeMove(j0);
            PieceColor w2 = copy.whoseMove();
            if (copy.whoseMove() != whoseMove()) {
                copy._whoseMove = whoseMove();
            }
            int jumpToIndex = j0.toIndex();

            Move pointer = j0;
            while (pointer != null) {
                jumpToIndex = pointer.toIndex();
                pointer = pointer.jumpTail();
            }


            if (!copy.jumpPossible(jumpToIndex)) {
                moves.add(j0);
            } else {
                singlejumps = copy.findSingleJumps(col(jumpToIndex),
                        row(jumpToIndex));
                for (Move j : singlejumps) {
                    jumps.add(Move.move(j0, j));
                }
            }

        }
        _copying = false;

    }

    /** Return true iff MOV is a valid jump sequence on the current board.
     *  MOV must be a jump or null.  If ALLOWPARTIAL, allow jumps that
     *  could be continued and are valid as far as they go.  */
    boolean checkJump(Move mov, boolean allowPartial) {
        if (mov == null) {
            return true;
        }
        if (allowPartial) {
            if (_pieces.get(mov.jumpedIndex()) != _whoseMove.opposite()) {
                return false;
            } else if (_pieces.get(mov.toIndex()) != EMPTY) {
                return false;
            } else if (mov.fromIndex() % 2 == 1) {
                int toIndex = mov.toIndex();
                int fromIndex = mov.fromIndex();
                ArrayList<Integer> diagtoIndexIndex = new ArrayList<Integer>();
                diagtoIndexIndex.addAll(Arrays.asList(fromIndex + 12,
                        fromIndex - 12, fromIndex + 8, fromIndex - 8));
                if (diagtoIndexIndex.contains(toIndex)) {
                    return false;
                }
            }
        } else if (!allowPartial) {
            if (!checkJump(mov, true)) {
                return false;
            }
            Move pointer = mov;
            PieceColor start;
            int startindex;
            int endindex;
            Board copy = new Board(this);
            while (pointer.jumpTail() != null) {
                if (!copy.checkJump(pointer, true)) {
                    return false;
                }
                start = copy.get(pointer.col0(), pointer.row0());
                startindex = index(pointer.col0(), pointer.row0());
                endindex = index(pointer.col1(), pointer.row1());
                int jumpedIndex = pointer.jumpedIndex();
                copy.set(jumpedIndex, EMPTY);
                copy.set(endindex, start);
                copy.set(startindex, EMPTY);
                pointer = pointer.jumpTail();
            }
            if (!copy.checkJump(pointer, true)) {
                return false;
            }
            start = copy.get(pointer.col0(), pointer.row0());
            startindex = index(pointer.col0(), pointer.row0());
            endindex = index(pointer.col1(), pointer.row1());
            int jumpedIndex = pointer.jumpedIndex();
            copy.set(jumpedIndex, EMPTY);
            copy.set(endindex, start);
            copy.set(startindex, EMPTY);
            if (copy.jumpPossible(pointer.toIndex())) {
                return false;
            }
        }
        return true;
    }

    /** Return true iff a jump is possible for a piece at position C R. */
    boolean jumpPossible(char c, char r) {

        if (get(c, r) == EMPTY || get(c, r) == _whoseMove.opposite()) {
            return false;
        }

        char cleft = (char) (c - 1);
        char cleft2 = (char) (cleft - 1);

        char cright = (char) (c + 1);
        char cright2 = (char) (cright + 1);

        char rup = (char) (r + 1);
        char rup2 = (char) (rup + 1);

        char rdown = (char) (r - 1);
        char rdown2 = (char) (rdown - 1);

        if (index(c, r) % 2 == 0) {
            if (validSquare(cleft, rup)
                    && validSquare(cleft2, rup2) && get(cleft2, rup2) == EMPTY
                    && get(cleft, rup) == _whoseMove.opposite()) {
                return true;
            } else if (validSquare(cright, rup) && validSquare(cright2, rup2)
                    && get(cright2, rup2) == EMPTY
                    && get(cright, rup) == _whoseMove.opposite()) {
                return true;
            } else if (validSquare(cright, rdown)
                    && validSquare(cright2, rdown2)
                    && get(cright2, rdown2) == EMPTY
                    && get(cright, rdown) == _whoseMove.opposite()) {
                return true;
            } else if (validSquare(cleft, rdown) && validSquare(cleft2, rdown2)
                    && get(cleft2, rdown2) == EMPTY
                    && get(cleft, rdown) == _whoseMove.opposite()) {
                return true;
            }
        }

        if (validSquare(cleft, r) && validSquare(cleft2, r)
                && get(cleft2, r) == EMPTY
                && get(cleft, r) == _whoseMove.opposite()) {
            return true;
        } else if (validSquare(c, rup) && validSquare(c, rup2)
                && get(c, rup2) == EMPTY
                && get(c, rup) == _whoseMove.opposite()) {
            return true;
        } else if (validSquare(cright, r) && validSquare(cright2, r)
                && get(cright2, r) == EMPTY
                && get(cright, r) == _whoseMove.opposite()) {
            return true;
        } else if (validSquare(c, rdown) && validSquare(c, rdown2)
                && get(c, rdown2) == EMPTY
                && get(c, rdown) == _whoseMove.opposite()) {
            return true;
        }

        return false;

    }

    /** Return true iff a jump is possible for a piece at position with
     *  linearized index K. */
    boolean jumpPossible(int k) {
        char c = col(k);
        char r = row(k);

        return jumpPossible(c, r);
    }

    /** Return true iff a jump is possible from the current board. */
    boolean jumpPossible() {
        for (int k = 0; k <= MAX_INDEX; k += 1) {
            if (jumpPossible(k)) {
                return true;
            }
        }
        return false;
    }

    /** Return the color of the player who has the next move.  The
     *  value is arbitrary if gameOver(). */
    PieceColor whoseMove() {
        return _whoseMove;
    }

    /** Perform the move C0R0-C1R1, or pass if C0 is '-'.  For moves
     *  other than pass, assumes that legalMove(C0, R0, C1, R1).
     *  use Move.jumpedIndex*/
    void makeMove(char c0, char r0, char c1, char r1) {
        makeMove(Move.move(c0, r0, c1, r1, null));
    }

    /** Make the multi-jump C0 R0-C1 R1..., where NEXT is C1R1....
     *  Assumes the result is legal. */
    void makeMove(char c0, char r0, char c1, char r1, Move next) {
        makeMove(Move.move(c0, r0, c1, r1, next));
    }

    /** Make the Move MOV on this Board, assuming it is legal. */
    void makeMove(Move mov) {

        assert legalMove(mov);
        PieceColor start = get(mov.col0(), mov.row0());
        int startindex = index(mov.col0(), mov.row0());
        int endindex = index(mov.col1(), mov.row1());

        if (!mov.isJump()) {
            set(endindex, start);
            set(startindex, EMPTY);
            setVisitedSquares(startindex, endindex, mov);

        } else if (mov.isJump()) {
            Move pointer = mov;
            while (pointer.jumpTail() != null) {

                start = get(pointer.col0(), pointer.row0());
                startindex = index(pointer.col0(), pointer.row0());
                endindex = index(pointer.col1(), pointer.row1());

                int jumpedIndex = pointer.jumpedIndex();
                set(jumpedIndex, EMPTY);
                set(endindex, start);
                set(startindex, EMPTY);
                PieceColor p = get(endindex);
                setVisitedSquares(startindex, endindex, pointer);
                pointer = pointer.jumpTail();
            }

            start = get(pointer.col0(), pointer.row0());
            startindex = index(pointer.col0(), pointer.row0());
            endindex = index(pointer.col1(), pointer.row1());

            int jumpedIndex = pointer.jumpedIndex();
            set(jumpedIndex, EMPTY);
            set(startindex, EMPTY);
            set(endindex, start);
            setVisitedSquares(startindex, endindex, pointer);
        }

        _madeMoves.push(mov);
        _whoseMove = whoseMove().opposite();

        setChanged();
        notifyObservers();
    }

    /** keeps squares from moving back where they came from. */
    private void setVisitedSquares(int startindex, int endindex, Move move) {
        Stack<Integer> pieceStack = _visitedSquares.get(startindex);
        if (!move.isJump()) {
            pieceStack.push(startindex);
            _visitedSquares.put(endindex, pieceStack);
            _visitedSquares.put(startindex, new Stack<Integer>());
        } else {
            _visitedSquares.put(move.jumpedIndex(), new Stack<Integer>());
            _visitedSquares.put(startindex, new Stack<Integer>());
        }

    }

    /** Undo the last move, if any. */
    void undo() {
        _whoseMove = _whoseMove.opposite();
        Move lastmove = _madeMoves.peek();
        Stack<Move> jumpmoves = new Stack<Move>();
        Move pointer = lastmove;
        if (lastmove.isJump()) {
            while (pointer.jumpTail() != null) {
                char col0 = pointer.col1();
                char row0 = pointer.row1();
                char col1 = pointer.col0();
                char row1 = pointer.row0();
                Move move = Move.move(col0, row0, col1, row1);
                jumpmoves.push(move);
                pointer = pointer.jumpTail();
            }
            char col0 = pointer.col1();
            char row0 = pointer.row1();
            char col1 = pointer.col0();
            char row1 = pointer.row0();
            jumpmoves.push(Move.move(col0, row0, col1, row1));
            while (!jumpmoves.empty()) {
                Move lastjump = jumpmoves.pop();
                int lastJumpedIndex = lastjump.jumpedIndex();
                set(lastJumpedIndex, _whoseMove.opposite());
                makeMove(lastjump);
                _whoseMove = whoseMove().opposite();
                _madeMoves.pop();
                set(lastJumpedIndex, _whoseMove.opposite());
            }
        } else if (!lastmove.isJump()) {
            char col0 = pointer.col1();
            char row0 = pointer.row1();
            char col1 = pointer.col0();
            char row1 = pointer.row0();

            Move mov = Move.move(col0, row0, col1, row1);

            PieceColor start = get(mov.col0(), mov.row0());
            int startindex = index(mov.col0(), mov.row0());
            int endindex = index(mov.col1(), mov.row1());

            set(endindex, start);
            set(startindex, EMPTY);
            PieceColor p = get(endindex);

        }

        _madeMoves.pop();
        setChanged();
        notifyObservers();
    }

    @Override
    public String toString() {
        return toString(false);
    }

    /** Return a text depiction of the board.  If LEGEND, supply row and
     *  column numbers around the edges. */
    String toString(boolean legend) {
        Formatter out = new Formatter();
        if (!legend) {
            for (int j = 20; j >= 0; j -= 5) {
                out.format(" ");
                for (int i = j; i < j + 5; i++) {
                    PieceColor piececolor = _pieces.get(i);
                    out.format(" %s", piececolor.shortName());
                }
                if (j != 0) {
                    out.format("\n");
                }

            }
        }
        return out.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Board) {
            Board b = (Board) o;
            return (b.toString().equals(toString())
                    && b._whoseMove == whoseMove()
                    && b._madeMoves.equals(_madeMoves));
        } else {
            return false;
        }
    }

    /** Return true iff there is a move for the current player.
     * signifies if game is over. WAS PRIVATE*/
    public boolean isMove() {
        if (getMoves().isEmpty()) {
            _gameOver = true;
            return false;
        }
        return true;
    }

    /** Added this to initialize the board in the constructor/clear method. */
    private static final String INIT_BOARD =
            "  w w w w w\n  w w w w w\n  b b - w w\n  b b b b b\n  b b b b b";

    /** Player that is on move. */
    private PieceColor _whoseMove;

    /** Set true when game ends. */
    private boolean _gameOver;

    /** Allows allowpartial to happen. */
    private boolean _copying = false;

    /** Contains visited pieces. */
    public HashMap<Integer, Stack<Integer>> _visitedSquares
            = new HashMap<Integer, Stack<Integer>>(MAX_INDEX + 1, 10);

    /** Contains made moves. */
    public Stack<Move> _madeMoves = new Stack<Move>();

    /** Convenience value giving values of pieces at each ordinal position. */
    static final PieceColor[] PIECE_VALUES = PieceColor.values();

    /** One cannot create arrays of ArrayList<Move>, so we introduce
     *  a specialized private list type for this purpose. */
    private static class MoveList extends ArrayList<Move> {
    }

    /** holds the pieces of the board. */
    public ArrayList<PieceColor> _pieces = new ArrayList<PieceColor>();


    /** A read-only view of a Board. */
    private class ConstantBoard extends Board implements Observer {
        /** A constant view of this Board. */
        ConstantBoard() {
            super(Board.this);
            Board.this.addObserver(this);
        }

        @Override
        void copy(Board b) {
            assert false;
        }

        @Override
        void clear() {
            assert false;
        }

        @Override
        void makeMove(Move move) {
            assert false;
        }

        /** Undo the last move. */
        @Override
        void undo() {
            assert false;
        }

        @Override
        public void update(Observable obs, Object arg) {
            super.copy((Board) obs);
            setChanged();
            notifyObservers(arg);
        }
    }
}
