/* Author: Paul N. Hilfinger.  (C) 2008. */

package qirkat;

import org.junit.Test;
import static org.junit.Assert.*;

import static qirkat.Move.*;

/** Test Move creation.
 *  @author
 */
public class MoveTest {

    @Test
    public void testMove1() {
        Move m = move('a', '3', 'b', '2');
        assertNotNull(m);
        assertFalse("move should not be jump", m.isJump());
    }

    @Test
    public void testJump1() {
        Move m = move('a', '3', 'a', '5');
        assertNotNull(m);
        assertTrue("move should be jump", m.isJump());
    }

    @Test
    public void testString() {
        assertEquals("a3-b2", move('a', '3', 'b', '2').toString());
        assertEquals("a3-a5", move('a', '3', 'a', '5').toString());
        assertEquals("a3-a5-c3", move('a', '3', 'a', '5',
                                      move('a', '5', 'c', '3')).toString());
    }

    @Test
    public void testParseString() {
        assertEquals("a3-b2", parseMove("a3-b2").toString());
        assertEquals("a3-a5", parseMove("a3-a5").toString());
        assertEquals("a3-a5-c3", parseMove("a3-a5-c3").toString());
        assertEquals("a3-a5-c3-e1", parseMove("a3-a5-c3-e1").toString());
    }

    @Test
    public void testIsLeftMove() {
        Move m = move('b', '1', 'a', '1');
        assertEquals(m.isLeftMove(), true);
    }

    @Test
    public void testJumpedRow() {
        Move m = move('b', '1', 'b', '2');
        assertEquals(m.jumpedRow(), '2');

        Move m1 = move('b', '1', 'b', '3');
        assertEquals(m1.jumpedRow(), '2');
    }

    @Test
    public void testJumpedCol() {
        Move m = move('b', '1', 'c', '1');
        assertEquals(m.jumpedCol(), 'c');

        Move m1 = move('b', '1', 'd', '1');
        assertEquals(m1.jumpedCol(), 'c');

        Move m2 = move('d', '1', 'b', '1');
        assertEquals(m2.jumpedCol(), 'c');

    }

    @Test
    public void testIndex() {
        assertEquals(index('a', '2'), 5);
    }
}
