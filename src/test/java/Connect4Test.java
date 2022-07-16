import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class Connect4Test {

    @Test
    void bestMoveTest() {
        Connect4Interface c1 = Connect4.of(new int[42]);
        c1 = c1.play(Move.of(1,1), Move.of(1,2), Move.of(1,3));
        /*
            0 0 0 0 0 0 0
            0 0 0 0 0 0 0
            0 0 0 0 0 0 0
            0 0 0 0 0 0 0
            0 0 0 0 0 0 0
            1 1 1 0 0 0 0
         */
        c1 = c1.play(c1.bestMove(1));
        assertEquals(1, c1.isGameOver(), "Minimax should win horizontal");

        Connect4Interface c2 = Connect4.of(new int[42]);
        c2 = c2.play(Move.of(-1,1), Move.of(-1,1), Move.of(-1,1));
        /*
            0 0 0 0 0 0 0
            0 0 0 0 0 0 0
            0 0 0 0 0 0 0
            -1 0 0 0 0 0 0
            -1 0 0 0 0 0 0
            -1 0 0 0 0 0 0
         */
        c2 = c2.play(c2.bestMove(-1));
        assertEquals(-1, c2.isGameOver(), "Minimax should win vertical");

        Connect4Interface c3 = Connect4.of(new int[42]);
        c3 = c3.play(Move.of(-1,1), Move.of(-1,2), Move.of(-1,3), Move.of(1,4), Move.of(-1,1), Move.of(-1,2), Move.of(-1,1), Move.of(1,2), Move.of(1,1));
         /*
            0 0 0 0 0 0 0
            0 0 0 0 0 0 0
            1 0 0 0 0 0 0
            -1 1 0 0 0 0 0
            -1 -1 0 0 0 0 0
            -1 -1 -1 1 0 0 0
         */
        c3 = c3.play(c3.bestMove(1));
        assertEquals(1, c3.isGameOver(), "Minimax should win diagonal");

        //BUG if minimax depth = 4, Game Situation taken from gameplay testing
        Connect4Interface c4 = Connect4.of(new int[42]);
        c4 = c4.play(Move.of(1,2), Move.of(-1,3), Move.of(1,4), Move.of(1,5), Move.of(1,6), Move.of(-1,7), Move.of(1,3), Move.of(-1,4), Move.of(-1,5), Move.of(1,4), Move.of(-1,5), Move.of(-1,5), Move.of(1,7));
        /*
            0 0 0 0 0 0 0
            0 0 0 0 0 0 0
            0 0 0 0 -1 0 0
            0 0 0 1 -1 0 0
            0 0 1 -1 -1 0 1
            0 1 -1 1 1 1 -1
         */
        c4 = c4.play(c4.bestMove(-1));
        assertEquals(-1, c4.isGameOver(), "Best move chose the wrong move");
    }

    @Test
    void multipleMovesTest() {
        Connect4Interface movesTest = Connect4.of(new int[42]);
        movesTest = movesTest.play(Move.of(1,1), Move.of(1,2), Move.of(1,3), Move.of(1,4));
        Connect4Interface oneByOne = Connect4.of(new int[42]);
        oneByOne = oneByOne.play(Move.of(1,1));
        oneByOne = oneByOne.play(Move.of(1,2));
        oneByOne = oneByOne.play(Move.of(1,3));
        oneByOne = oneByOne.play(Move.of(1,4));

        assertArrayEquals(oneByOne.getGrid(), movesTest.getGrid(), "Play method doesn't take multiple moves");
    }

    @Test
    void gameOverTest() {
        Connect4Interface c3 = Connect4.of(new int[42]);
        assertEquals(0, c3.isGameOver(), "Game should not be over");

        c3 = c3.play(Move.of(-1,1), Move.of(-1,2), Move.of(-1,3), Move.of(1,4), Move.of(-1,1), Move.of(-1,2), Move.of(-1,1), Move.of(1,2), Move.of(1,3), Move.of(1,1));
        /*
            0 0 0 0 0 0 0
            0 0 0 0 0 0 0
            1 0 0 0 0 0 0
            -1 1 0 0 0 0 0
            -1 -1 1 0 0 0 0
            -1 -1 -1 1 0 0 0
         */

        assertTrue(c3.isGameOver() != 0, "Game should be over");
    }

    @Test
    void winnerTest() {
        Connect4Interface winner = Connect4.of(new int[42]);
        winner = winner.play(Move.of(-1,1), Move.of(-1,2), Move.of(-1,3), Move.of(1,4), Move.of(-1,1), Move.of(-1,2), Move.of(-1,1), Move.of(1,2), Move.of(1,3), Move.of(1,1));
        /*
            0 0 0 0 0 0 0
            0 0 0 0 0 0 0
            1 0 0 0 0 0 0
            -1 1 0 0 0 0 0
            -1 -1 1 0 0 0 0
            -1 -1 -1 1 0 0 0
         */

        assertEquals(1, winner.isGameOver(), "Player 1 should win");
        assertNotEquals(-1, winner.isGameOver(), "Player 2 should lose");
    }

    @Test
    void undoTest() {
        Connect4Interface undo = Connect4.of(new int[42]);
        undo = undo.newGame(); //Empties the undo stack (stack already filled from other tests)
        assertThrows(EmptyStackException.class, undo::undo, "EmptyStackException expected");

        undo = undo.play(Move.of(1,5));
        Connect4Interface test = Connect4.of(new int[42]);

        assertArrayEquals(test.getGrid(), undo.undo().getGrid(), "Undo returns wrong game situation");
        assertThrows(EmptyStackException.class, undo::undo, "EmptyStackException expected");

        undo = undo.play(Move.of(1,5));  //saves the game situation
        undo = undo.newGame(); //Creates new game and empties the undo stack
        assertThrows(EmptyStackException.class, undo::undo, "EmptyStackException expected");
    }

    @Test
    void getGridTest() {
        Random r = new Random();
        int[] grid = Arrays.stream(new int[42]).map(n -> ((r.nextInt(2) == 0 ? -1 : 1) * r.nextInt(2))).toArray();
        Connect4Interface gridTest = Connect4.of(grid);

        assertArrayEquals(grid, gridTest.getGrid(), "Array from getGrid is not the same");
    }

    @Test
    void fourInARowTest() {
        Connect4 c = Connect4.of(new int[42]);
        c = c.play(Move.of(1,1),Move.of(1,1),Move.of(1,1),Move.of(1,1));
        assertFalse(c.checkVertical(-1), "Player 2 should not have 4 in a row vertical");
        assertTrue(c.checkVertical(1), "Player 1 should have 4 in a row vertical");

        c = Connect4.of(new int[42]);
        c = c.play(Move.of(-1,1),Move.of(-1,2),Move.of(-1,3),Move.of(-1,4));
        assertFalse(c.checkHorizontal(1), "Player 1 should not have 4 in a row horizontal");
        assertTrue(c.checkHorizontal(-1), "Player 2 should have 4 in a row horizontal");

        c = Connect4.of(new int[42]);
        c = c.play(Move.of(-1,1), Move.of(-1,2), Move.of(-1,3), Move.of(1,4), Move.of(-1,1), Move.of(-1,2), Move.of(-1,1), Move.of(1,2), Move.of(1,3), Move.of(1,1));
        assertFalse(c.checkDiagonal(-1), "Player 2 should not have 4 in a row diagonal");
        assertTrue(c.checkDiagonal(1), "Player 1 should have 4 in a row diagonal");
    }

    @Test
    void fullColumnTest() {
        Connect4 c = Connect4.of(new int[42]);
        assertFalse(c.columnIsFull(6), "Column should not be full");
        c = c.play(Move.of(-1,6),Move.of(1,6),Move.of(-1,6),Move.of(1,6),Move.of(-1,6),Move.of(1,6));
        assertTrue(c.columnIsFull(6), "Column should be full");
        Connect4 drawTest = Connect4.of(new int[42]);
        drawTest = drawTest.play(Move.of(1,1),Move.of(-1,1),Move.of(1,1),Move.of(-1,1),Move.of(1,1),Move.of(1,1));
        drawTest = drawTest.play(Move.of(1,2),Move.of(1,2),Move.of(-1,2),Move.of(1,2),Move.of(-1,2),Move.of(-1,2));
        drawTest = drawTest.play(Move.of(-1,3),Move.of(-1,3),Move.of(1,3),Move.of(-1,3),Move.of(1,3),Move.of(-1,3));
        drawTest = drawTest.play(Move.of(1,4),Move.of(-1,4),Move.of(1,4),Move.of(-1,4),Move.of(1,4),Move.of(-1,4));
        drawTest = drawTest.play(Move.of(1,5),Move.of(1,5),Move.of(1,5),Move.of(-1,5),Move.of(-1,5),Move.of(1,5));
        drawTest = drawTest.play(Move.of(-1,6),Move.of(-1,6),Move.of(-1,6),Move.of(1,6),Move.of(1,6),Move.of(-1,6));
        drawTest = drawTest.play(Move.of(1,7),Move.of(-1,7),Move.of(1,7),Move.of(-1,7),Move.of(1,7),Move.of(-1,7));
        assertFalse(drawTest.isPlayable(), "There should not be any other possible moves");
        assertEquals(3, drawTest.isGameOver(), "3 should be returned by isGameOver");
    }

    @Test
    void minimaxMethodsTest() {
        Connect4 c = Connect4.of(new int[42]);
        assertTrue(c.max(c,2,1,1000) > 0, "Max should return value > 0");
        assertTrue(c.min(c,2,1,1000) < 0, "Min should return value < 0");
        c = c.play(Move.of(-1,6),Move.of(1,6),Move.of(-1,6),Move.of(1,6),Move.of(-1,6),Move.of(1,6));
        assertEquals(6, c.generateMove(c, 1).size(), "There should be exactly 6 moves generated");
    }

    @Test
    void objectCompTest() {
        Connect4 c = Connect4.of(new int[42]);
        Connect4 c1 = Connect4.of(c.getGrid());
        assertNotEquals(c, c1, "Should not be the same object");
        assertArrayEquals(c.getGrid(), c1.getGrid(), "Grids should be the same");
    }

    @Test
    void evaluateMethodsTest() {
        Connect4 randomMove = Connect4.of(new int[42]);
        int result = randomMove.playRandomly(randomMove,1);
        assertTrue(result == 1 || result == -1 || result == 3, "Result should be 1, -1, or 3");
        Connect4 c = Connect4.of(new int[42]);
        c = c.play(Move.of(1,1),Move.of(1,1),Move.of(1,1),Move.of(1,1));
        assertEquals(10000, c.evaluate(c,1,1000), "Eval for p1 = win, should return 10000");
        assertEquals(-10000, c.evaluate(c,-1,1000), "Eval for p2 = lose, should return -10000");
        Connect4 monte = Connect4.of(new int[42]);
        result = monte.evaluate(monte,1, 1000);
        assertTrue(result != 10000 && result != -10000, "Should return monte simulation score");
        Connect4 drawTest = Connect4.of(new int[42]);
        drawTest = drawTest.play(Move.of(1,1),Move.of(-1,1),Move.of(1,1),Move.of(-1,1),Move.of(1,1),Move.of(1,1));
        drawTest = drawTest.play(Move.of(1,2),Move.of(1,2),Move.of(-1,2),Move.of(1,2),Move.of(-1,2),Move.of(-1,2));
        drawTest = drawTest.play(Move.of(-1,3),Move.of(-1,3),Move.of(1,3),Move.of(-1,3),Move.of(1,3),Move.of(-1,3));
        drawTest = drawTest.play(Move.of(1,4),Move.of(-1,4),Move.of(1,4),Move.of(-1,4),Move.of(1,4),Move.of(-1,4));
        drawTest = drawTest.play(Move.of(1,5),Move.of(1,5),Move.of(1,5),Move.of(-1,5),Move.of(-1,5),Move.of(1,5));
        drawTest = drawTest.play(Move.of(-1,6),Move.of(-1,6),Move.of(-1,6),Move.of(1,6),Move.of(1,6),Move.of(-1,6));
        drawTest = drawTest.play(Move.of(1,7),Move.of(-1,7),Move.of(1,7),Move.of(-1,7),Move.of(1,7),Move.of(-1,7));
        assertEquals(0, drawTest.evaluate(drawTest,-1,1000), "Game tied, should return 0");
        assertEquals(0, drawTest.evaluate(drawTest,1,1000), "Game tied, should return 0");
    }
}