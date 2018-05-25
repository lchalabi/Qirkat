package qirkat;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.InputStreamReader;

public class AITest {

    @Test
    public void testStaticScore() {
        Board b0 = new Board();

        Game game = new Game(b0,
                new ReaderSource(new InputStreamReader(System.in),
                        true),
                new TextReporter());

        AI A = new AI(game, PieceColor.BLACK);

        b0.setPieces("----- ----- ----w --bb- bb-bb", PieceColor.WHITE);

        assertEquals(A.staticScore(b0), -5);
    }


    @Test
    public void testFindMove1() {
        Board b0 = new Board();
        b0.setPieces("----- -w--- -bbb- ----- -----", PieceColor.WHITE);
        System.out.println("possible moves: " + b0.getMoves() + "\n");

        Game game = new Game(b0,
                new ReaderSource(new InputStreamReader(System.in),
                        true),
                new TextReporter());


        AI A = new AI(game, PieceColor.WHITE);

        System.out.println(b0.toString());
        System.out.println("\n" + "chosen move: " + A.findMove());

    }

    @Test
    public void testFindMove2() {
        Board b0 = new Board();
        b0.setPieces("----- -w--- ----- -bb-- -----", PieceColor.WHITE);
        System.out.println("possible moves: " + b0.getMoves() + "\n");


        Game game = new Game(b0,
                new ReaderSource(new InputStreamReader(System.in),
                        true),
                new TextReporter());


        AI A = new AI(game, PieceColor.WHITE);

        System.out.println(b0.toString());

        System.out.println("\n" + "chosen move: " + A.findMove());

    }

    @Test
    public void testFindMove3() {
        Board b0 = new Board();

        b0.setPieces("----- w---- ----- -b--- -----", PieceColor.WHITE);
        System.out.println("possible moves: " + b0.getMoves() + "\n");

        Game game = new Game(b0,
                new ReaderSource(new InputStreamReader(System.in),
                        true),
                new TextReporter());


        AI A = new AI(game, PieceColor.WHITE);

        System.out.println(b0.toString());

        System.out.println("\n" + "chosen move: " + A.findMove());

    }

    @Test
    public void testFindMove4() {
        Board b0 = new Board();

        System.out.println("possible moves: " + b0.getMoves() + "\n");

        Game game = new Game(b0,
                new ReaderSource(new InputStreamReader(System.in),
                        true),
                new TextReporter());


        AI A = new AI(game, PieceColor.WHITE);

        System.out.println(b0.toString());

        System.out.println("\n" + "chosen move: " + A.findMove());
    }


    @Test
    public void testFindMove5() {
        Board b0 = new Board();
        b0.setPieces("wwwww wbwww bb-ww bbb-b bbbbb", PieceColor.WHITE);
        System.out.println("possible moves: " + b0.getMoves() + "\n");

        Game game = new Game(b0,
                new ReaderSource(new InputStreamReader(System.in),
                        true),
                new TextReporter());


        AI A = new AI(game, PieceColor.WHITE);

        System.out.println(b0.toString());

        System.out.println("\n" + "chosen move: " + A.findMove());

    }

    @Test
    public void testFindMove6() {
        Board b0 = new Board();
        b0.setPieces("----- --www ----- -bbb- --bb-", PieceColor.WHITE);
        System.out.println("possible moves: " + b0.getMoves() + "\n");

        Game game = new Game(b0,
                new ReaderSource(new InputStreamReader(System.in),
                        true),
                new TextReporter());


        AI A = new AI(game, PieceColor.WHITE);

        System.out.println(b0.toString());

        System.out.println("\n" + "chosen move: " + A.findMove());
    }

    @Test
    public void testFindMove7() {
        Board b0 = new Board();
        b0.setPieces("----- -ww-- -b--- --bb- -----", PieceColor.WHITE);
        System.out.println("possible moves: " + b0.getMoves() + "\n");

        Game game = new Game(b0,
                new ReaderSource(new InputStreamReader(System.in),
                        true),
                new TextReporter());


        AI A = new AI(game, PieceColor.WHITE);

        System.out.println(b0.toString());

        System.out.println("\n" + "chosen move: " + A.findMove());
    }

    @Test
    public void testFindMove8() {
        Board b0 = new Board();
        b0.setPieces("----- --w-- ----- -bbb- --b--", PieceColor.WHITE);
        System.out.println("possible moves: " + b0.getMoves() + "\n");

        Game game = new Game(b0,
                new ReaderSource(new InputStreamReader(System.in),
                        true),
                new TextReporter());


        AI A = new AI(game, PieceColor.WHITE);

        System.out.println(b0.toString());

        System.out.println("\n" + "chosen move: " + A.findMove());
    }

    @Test
    public void testFindMove9() {
        Board b0 = new Board();
        b0.setPieces("wwwww wwwww b--bw bbbbb bbbbb", PieceColor.WHITE);

        System.out.println("possible moves: " + b0.getMoves() + "\n");

        Game game = new Game(b0,
                new ReaderSource(new InputStreamReader(System.in),
                        true),
                new TextReporter());


        AI A = new AI(game, PieceColor.WHITE);

        System.out.println(b0.toString());

        System.out.println("\n" + "chosen move: " + A.findMove());

    }

    @Test
    public void testFindMove10() {
        Board b0 = new Board();
        b0.setPieces("wwwww wwwww b-w-- bbbbb bbbbb", PieceColor.BLACK);

        System.out.println("possible moves: " + b0.getMoves() + "\n");

        Game game = new Game(b0,
                new ReaderSource(new InputStreamReader(System.in),
                        true),
                new TextReporter());


        AI A = new AI(game, PieceColor.BLACK);

        System.out.println(b0.toString());

        System.out.println("\n" + "chosen move: " + A.findMove());

    }


}
