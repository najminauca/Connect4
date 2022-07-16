import java.util.*;

class Move {
    final int player, column;
    public static Move of(int player, int column) {
        return new Move(player, column);
    }
    private Move(int player, int column) {
        this.player = player;
        this.column = column;
    }
}

interface Connect4Interface {
    Connect4 play(Move... m);
    Connect4 newGame();
    int isGameOver();
    Move bestMove(int player);
    int[] getGrid();
    boolean columnIsFull(int column);
    Connect4 undo();
}

public class Connect4 implements Connect4Interface{
    /*
            0   1   2   3   4   5   6
            7   8   9   10  11  12  13
            14  15  16  17  18  19  20
            21  22  23  24  25  26  27
            28  29  30  31  32  33  34
            35  36  37  38  39  40  41
     */
    int[] grid;
    static boolean debug = false;   //Static so it applies to all class instances in GUI
    Random r = new Random();
    private Move bestMove;
    private int desiredDepth;
    static Stack<Connect4> undo = new Stack<>();
    public static Connect4 of(int[] grids) {
        return new Connect4(grids);
    }
    private Connect4(int[] grids) {
        assert grids.length == 42 : "Illegal number of grids";
        assert Arrays.stream(grids).allMatch(n -> n >= -1) && Arrays.stream(grids).allMatch(n -> n <= 1) : "Illegal element in board";
        this.grid = Arrays.copyOf(grids, grids.length);
    }
    private Connect4 play(Move m) {
        assert isGameOver() == 0 : "Game is over"; //Game is NOT over if == 0
        assert m.player == 1 || m.player == -1 : "Wrong player input";
        assert !columnIsFull(m.column) : "Column selected is full";
        Connect4 c4 = Connect4.of(grid);
        for(int i = m.column - 1; i < c4.grid.length; i = i + 7) {
            if(c4.grid[i] == 0) {
                c4.grid[i] = m.player;
            }
            if(i + 7 < c4.grid.length) {
                if(c4.grid[i + 7] == 0) {
                    c4.grid[i] = 0;
                }
            }
        }
        return c4;
    }
    public Connect4 play(Move... moves) {
        Connect4 c4 = this;
        for(Move m : moves) {
            undo.push(Connect4.of(grid));   //Saves to stack
            c4 = c4.play(m);
        }
        return c4;
    }
    public Connect4 newGame() {
        undo = new Stack<>();   //Empty stack on a new game
        return Connect4.of(new int[42]);
    }
    public Move randomMove(int player) {
        assert isGameOver() == 0 : "Game is over"; //Game is NOT over if == 0
        int column;
        do {
            column = r.nextInt(7) + 1;
        } while(columnIsFull(column));
        return Move.of(player, column);
    }
    public Move bestMove(int player) {
        assert isGameOver() == 0 : "Game is over"; //Game is NOT over if == 0
        this.desiredDepth = 2;
        //saveUndo = false;   //Will not save to undo stack
        max(this, desiredDepth, player, 1000);
        //saveUndo = true;    //Turn on save to undo stack
        return bestMove;
    }
    int max(Connect4 c4, int depth, int player, int count) {
        println("(Minimax) - MAX METHOD CALL | depth = " + depth);
        if(depth == 0 || c4.isGameOver() != 0) { //Game is over if != 0
            println("\n> RETURN MAX EVALUATION SCORE :");
            return evaluate(c4, player, count); //max win count
        }
        int maxWert = Integer.MIN_VALUE;
        for(Move moveSim : generateMove(c4, player)) {
            println("\n> TRYING NEW MAX MOVE | depth = " + depth);
            println("Before");
            println(c4.toString());
            Connect4 c4Sim = Connect4.of(c4.getGrid());
            c4Sim = c4Sim.play(moveSim); //nothing is saved to undo
            println("After");
            println(c4Sim.toString());
            int wert = min(c4Sim, depth - 1, -player, count);
            if(wert > maxWert) {    //Saves positive scores (1)
                println("\nSAVE NEW MAX (HIGHEST) SCORE = " + wert + " | depth = " + depth);
                maxWert = wert;
                if(depth == desiredDepth) {    //Reached the desired depth
                    println("SAVE THIS MOVE : Move.of(" + moveSim.player + "," + moveSim.column + ")");
                    bestMove = moveSim;
                }
            }
        }
        println("\nMAX METHOD SCORE = " + maxWert + " | depth = " + depth);
        return maxWert;
    }
    int min(Connect4 c4, int depth, int player, int count) {
        println("(Minimax) - MIN METHOD CALL | depth = " + depth);
        if(depth == 0 || c4.isGameOver() != 0) { //Game is over if != 0
            println("\n> RETURN MIN EVALUATION SCORE :");
            return -1 * evaluate(c4, player, count); //min loss count
        }
        int minWert = Integer.MAX_VALUE;
        for(Move moveSim : generateMove(c4, player)) {
                println("\n> TRYING NEW MIN MOVE | depth = " + depth);
                println("Before");
                println(c4.toString());
            //Placeholder for nim Situation to test the move
            Connect4 c4Sim = Connect4.of(c4.getGrid());
            c4Sim = c4Sim.play(moveSim); //nothing is saved to undo
            println("After");
            println(c4Sim.toString());
            int wert = max(c4Sim, depth - 1, -player, count);
            if(wert < minWert) {    //Saves negative scores (-1)
                println("\nSAVE NEW MIN (LOWEST) SCORE = " + wert + " | depth = " + depth);
                minWert = wert;
            }
        }
        println("\nMIN METHOD SCORE = " + minWert + " | depth = " + depth);
        return minWert;
    }
    List<Move> generateMove(Connect4 c4, int player) {
        if(c4.isGameOver() != 0) { //Game is over if != 0
            return Collections.emptyList();
        }
        List<Move> moveList = new ArrayList<>();
        for(int i = 1; i <= 7; i++) {   //Simulates all possible moves
            if(!c4.columnIsFull(i)) {
                moveList.add(Move.of(player, i));
            }
        }
        return moveList;
    }
    int playRandomly(Connect4 c4, int player) {     //Random gameplay until game is finished
        int value = c4.isGameOver();
        while(value == 0) { //Game not over == 0
            c4 = c4.play(c4.randomMove(player)); //Not saved to undo stack
            player = -player;
            value = c4.isGameOver();
        }
        return value;
    }
    int[] monteSimulate(Connect4 c4, int player, int count) {    //Win Count Monte Carlo Simulation
        assert count > 0 : "Wrong input for simulation count";
        println("\n(Monte Carlo) - SIMULATING " + count + " TIMES");
        int[] tally = new int[3];
        while(count > 0) {
            Connect4 temp = Connect4.of(c4.getGrid());
            int value = playRandomly(temp, player);
            if(value == player) {
                tally[0] += 1;
            }
            else if(value == -player) {
                tally[1] += 1;
            }
            else if(value == 3) {
                tally[2] += 1;
            }
            count--;
        }
        println("SIMULATION RESULT -> Win = " + tally[0] + ", Loss = " + tally[1] + ", Draw = " + tally[2]);
        return tally;
    }
    int evaluate(Connect4 c4, int player, int count) {
        if(c4.isGameOver() == player) return 10000; //Game ended player won
        if(c4.isGameOver() == -player) return -10000;   //Game ended -player won
        if(c4.isGameOver() == 3) return 0;  //Game ended in draw
        int[] score = monteSimulate(c4, player, count);  //Game hasn't ended, simulate game result with Monte Carlo
        return score[0] - score[1] + score[2];  //Win - Loss + Draw
    }
    public boolean columnIsFull(int column) {
        assert column <= 7 && column >= 1 : "Wrong column input";
        int[] line = {0,7,14,21,28,35};
        return Arrays.stream(line).map(n -> n + column - 1).allMatch(n -> grid[n] != 0);
    }
    public boolean isPlayable() { //Checks if there is a possible Move
        for(int i = 1; i <= 7; i++) {
            if(!columnIsFull(i)) return true;
        }
        return false;
    }
    public int isGameOver() {   //Return != 0 if the Game is over
        if(hasWon(1)) return 1;
        if(hasWon(-1)) return -1;
        if(!isPlayable()) return 3;
        return 0;
    }
    public boolean hasWon(int player) {
        assert player == 1 || player == -1 : "Wrong player input";
        return checkHorizontal(player) || checkVertical(player) || checkDiagonal(player);
    }
    boolean checkHorizontal(int player) {
        for(int i = 0; i < 42; i += 7) {    //0 - 35, Increment by 7 for each row
            int last = -100;    //last value, not 1, -1, or 0
            int count = 0;
            for(int j = 0; j < 7; j++) {    //0 - 6, Increment by 1 for each column
                if(grid[j + i] != last || grid[j + i] != player) {    //j = column increment, i = row increment
                    last = grid[j + i];
                    count = 0;
                }
                count += 1;
                if(count >= 4) {
                    return true;
                }
            }
        }
        return false;
    }
    boolean checkVertical(int player) {
        for(int i = 0; i < 7; i++) {    //0 - 6, Increment by 1 for each column
            int last = 0;   //last value, not 1, -1, or 0
            int count = 0;
            for(int j = 0; j < 42; j += 7) {    //0 - 35, Increment by 7 for each row
                if(grid[j + i] != last || grid[j + i] != player) {    //j = row increment, i = column increment
                    last = grid[j + i];
                    count = 0;
                }
                count += 1;
                if(count >= 4) {
                    return true;
                }
            }
        }
        return false;
    }
    boolean checkDiagonal(int player) {
        int[] startingPosTopLeft = {0, 1, 2, 3, 7, 14}; //starting index of diagonal lines with at least 4 grids
        for(int start : startingPosTopLeft) {   //Check from top LEFT to Bottom RIGHT
            if(diagonally4(start, player, 8, 6)) return true; //Increment by 8 to go to BOTTOM RIGHT grid, (index % 7) == 6 is the LAST column (right border)
        }
        int[] startingPosTopRight = {3, 4, 5, 6, 13, 20}; //starting index of diagonal lines with at least 4 grids
        for(int start : startingPosTopRight) {  //Check from top RIGHT to Bottom LEFT
            if(diagonally4(start, player, 6, 0)) return true; //Increment by 6 to go to BOTTOM LEFT, (index % 7) == 0 is the FIRST column (left border)
        }
        return false;
    }
    boolean diagonally4(int index, int player, int increment, int border) {
        int last = -100;    //last value, not 1, -1, or 0
        int count = 0;
        int k = index;  //Starting index
        while(k < 42) { //Takes care of the bottom border (last row)
            if(grid[k] != last || grid[k] != player) {
                last = grid[k];
                count = 0;
            }
            count += 1;
            if(count >= 4) {
                return true;
            }
            if(k % 7 == border) break;  //Break if border is reached (most left/right column)
            k += increment;
        }
        return false;
    }
    public Connect4 undo() throws EmptyStackException { //Throw exception for GUI
        return undo.pop();
    }
    public int[] getGrid() {
        return grid;
    }
    void println(String s) {
        if(debug) System.out.println(s);
    }
    void debug() {
        debug = !debug;
    }
    public String toString() {
        String s = "\n";
        for(int i = 0; i < grid.length; i++) {
            s += grid[i] + " ";
            if(i % 7 == 6) {
                s += "\n";
            }
        }
        return s;
    }
}
