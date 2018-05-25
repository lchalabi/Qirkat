package qirkat;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Stack;
import java.util.ArrayList;

/** Tests of the Board class.
 *  @author Lila Chalabi
 */
public class BoardTest {

    private static final String INIT_BOARD =
        "  b b b b b\n  b b b b b\n  b b - w w\n  w w w w w\n  w w w w w";

    private static final String[] GAME1 =
            {"c2-c3", "c4-c2",
            "c1-c3", "a3-c1",
            "c3-a3", "c5-c4",
            "a3-c5-c3",
            };

    private static final String[] GAME2 = { "c2-c3", "c4-c2",
            "c1-c3", "a3-c1",
            "c3-a3", "b4-c4",
            "a2-b2"
            };

    private static final String GAME1_BOARD =
        "  b b - b b\n  b - - b b\n  - - w w w\n  w - - w w\n  w w b w w";

    private static void makeMoves(Board b, String[] moves) {
        for (String s : moves) {
            b.makeMove(Move.parseMove(s));
        }
    }


    @Test
    public void moves012Test() {
        Board b0 = new Board();
        b0.setPieces("w---- ----- ----- ----- b----", PieceColor.BLACK);
        b0.makeMove(Move.parseMove("a5-b4"));
        System.out.println(b0.toString());
    }


    @Test
    public void moves021Test() {
        Board b0 = new Board();
        b0.setPieces("----- --b-- ----- -w--- -----", PieceColor.WHITE);
        Move w1 = Move.parseMove("b4-c4");
        Move b1 = Move.parseMove("c2-b2");
        b0.makeMove(w1);
        b0.makeMove(b1);
        System.out.println(b0.toString());


        Move w2 = Move.parseMove("c4-b4");
        Move b2 = Move.parseMove("b2-c2");

        assertEquals(b0.legalMove(w2), false);
        assertEquals(b0.legalMove(b2), false);
    }




    @Test
    public void testingMove() {
        Board b0 = new Board();
        b0.setPieces("----- -w--- ----- -bb-- -----", PieceColor.WHITE);
        Move m = Move.parseMove("b3-b4");
        assertEquals(b0.legalMove(m), false);

        Move m1 = Move.parseMove("b2-b1");
        assertEquals(b0.legalMove(m1), false);

        Move m2 = Move.parseMove("b4-b3");
        assertEquals(b0.legalMove(m2), false);

        b0.setPieces("----- ----- -w--- -bb-- -----", PieceColor.BLACK);
        Move m3 = Move.parseMove("c4-a2");
        System.out.println(b0.toString());
        assertEquals(b0.legalMove(m3), false);

        b0.setPieces("----- ----- -w--- -b-wb -----", PieceColor.BLACK);
        Move m4 = Move.parseMove("e4-c4");
        Move m5 = Move.parseMove("e4-c4-c2");
        assertEquals(b0.legalMove(m4), true);
        assertEquals(b0.legalMove(m5), false);
    }



    @Test
    public void testAllowPartial() {
        Board b0 = new Board();
        b0.setPieces("----- ----- ----w --bb- bb-bb", PieceColor.WHITE);

        Move m = Move.move('e', '3', 'e', '5');

        assertEquals(b0.legalMove(m), false);

    }



    @Test
    public void testIsMove() {
        Board b0 = new Board();
        assertEquals(b0.isMove(), true);

        b0.setPieces("----- -w--- -bbb- --w-- -----", PieceColor.BLACK);
        assertEquals(b0.isMove(), true);

        b0.setPieces("----- -w--- --bb- ----- -----", PieceColor.WHITE);
        assertEquals(b0.isMove(), true);
        assertEquals(b0.getMoves().size(), 1);
        b0.makeMove(Move.parseMove("b2-d4-d2"));

        assertEquals(b0.whoseMove(), PieceColor.BLACK);
        assertEquals(b0.isMove(), false);
        assertEquals(b0.gameOver(), true);

    }
    @Test
    public void testGetMoves() {
        Board b0 = new Board();
        ArrayList<Move> moves = b0.getMoves();
        assertEquals(moves.size(), 4);

        b0.setPieces("----- -w--- -bbb- --w-- -----", PieceColor.BLACK);
        ArrayList<Move> moves1 = b0.getMoves();
        assertEquals(moves1.size(), 3);

        ArrayList<Move> movs = new ArrayList<>();
        assertEquals(movs.isEmpty(), true);
    }

    @Test
    public void testGetJumps() {
        Board b0 = new Board();
        b0.setPieces("----- -w--- -bbb- ----- -----", PieceColor.WHITE);
        ArrayList<Move> returnmoves = new ArrayList<Move>();
        b0.getJumps(returnmoves, 6);
        assertEquals(b0.getMoves().size(), 2);
        assertEquals(b0.getMoves().contains(Move.parseMove("b2-b4-d2-d4")),
                true);

    }


    @Test
    public void testFindSingleJump() {
        Board b0 = new Board();
        b0.setPieces("----- -w--- -bbb- ----- -----", PieceColor.BLACK);
        ArrayList<Move> singlejumps = b0.findSingleJumps('c', '3');
        assertEquals(singlejumps.size(), 1);
    }


    @Test
    public void testTest04() {
        String[] test04move = {"b2-b4-d2-d4"};
        Board b0 = new Board();
        b0.setPieces("----- -w--- -bbb- ----- -----", PieceColor.WHITE);

        makeMoves(b0, test04move);

    }

    @Test
    public void testInit1() {
        Board b0 = new Board();
        assertEquals(INIT_BOARD, b0.toString());
    }

    @Test
    public void testMoves1() {
        Board b0 = new Board();
        makeMoves(b0, GAME1);
        assertEquals(GAME1_BOARD, b0.toString());
    }

    @Test
    public void testMoves4() {
        Board b0 = new Board();
        makeMoves(b0, GAME2);
        b0.toString();
    }

    @Test
    public void testMoves2() {
        Board b0 = new Board();
        b0.makeMove('c', '2', 'c', '3');
        Stack<Integer> stack1 = new Stack<Integer>();
        stack1.push(7);
        assertEquals(b0._visitedSquares.get(12), stack1);

        b0.makeMove('c', '4', 'c', '2');

        b0.toString();

    }

    @Test
    public void testMoves3() {
        Board b0 = new Board();
        b0.setPieces("--b-- b--bw ----- w---- -----", PieceColor.BLACK);
        b0.toString();
        b0.makeMove('a', '2', 'a', '1');
        Stack<Integer> stack1 = new Stack<Integer>();
        stack1.push(5);
        assertEquals(b0._visitedSquares.get(0), stack1);

        Stack<Integer> stack2 = new Stack<Integer>();
        b0.makeMove('e', '2', 'c', '2');
        assertEquals(b0._visitedSquares.get(7), stack2);
        assertEquals(b0._visitedSquares.get(9), stack2);

        b0.toString();
    }


    @Test
    public void testLegalMove() {
        Board b0 = new Board();
        b0.setPieces("--b-- b--bw w---- ----- -----", PieceColor.BLACK);

        Move mov = Move.move('a', '2', 'a', '4');
        assertEquals(b0.legalMove(mov), true);

        b0.makeMove(mov);

        Move mov1 = Move.move('e', '2', 'e', '3');
        assertEquals(b0.legalMove(mov1), false);
        Move mov2 = Move.move('e', '2', 'c', '2');
        assertEquals(b0.legalMove(mov2), true);

        b0.makeMove(mov2);

        b0.toString();

    }


    @Test
    public void testUndo() {
        Board b0 = new Board();
        Board b1 = new Board(b0);
        makeMoves(b0, GAME1);
        Board b2 = new Board(b0);

        for (int i = 0; i < GAME1.length; i += 1) {
            b0.undo();
        }
        assertEquals(b0.get(7), PieceColor.WHITE);
        assertEquals("failed to return to start", b1, b0);
        makeMoves(b0, GAME1);

        assertEquals("second pass failed to reach same position", b2, b0);
    }

    @Test
    public void testInternalCopy() {
        Board b0 = new Board();
        Board b1 = new Board();
        String roworder =
                new StringBuilder("b---w -b-w- --b-- --wb- w---b")
                        .reverse().toString();
        b1.setPieces(roworder, PieceColor.BLACK);

        b0.internalCopy(b1);

        assertEquals(b0.toString(), b1.toString());

    }

    @Test
    public void testJumpPossible() {
        Board b0 = new Board();
        assertEquals(b0.jumpPossible(), false);
    }

    @Test
    public void testConstantView() {
        Board b0 = new Board();
        Board constant = b0.constantView();

    }


}
