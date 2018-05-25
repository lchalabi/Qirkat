package qirkat;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.InputStreamReader;

public class GameTest {

    @Test
    public void testLoad() {
        Board b0 = new Board();

        Game game = new Game(b0,
                new ReaderSource(new InputStreamReader(System.in),
                        true),
                new TextReporter());


    }

}
