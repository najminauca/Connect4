import processing.core.PApplet;

import java.util.Arrays;
import java.util.EmptyStackException;

public class Connect4GUI extends PApplet {
    Connect4Interface c4;

    int titleSize = 30;
    int smallButton = 15;
    int bigButton = 20;

    boolean pvp;
    boolean pvc;
    boolean play;
    int player;

    Button title;
    Button start;
    Button vsComp;
    Button exit;
    Button replay;
    Button undo;

    int black = color(22, 22, 24);
    int lightGray = color(80, 102, 128);
    int fontDark = 0;
    int fontLight = 255;
    int alt = color(96, 214, 214);
    int clay = color(173, 80, 73);
    int wood = color(186, 140, 99);
    int green = color(54, 89, 74);
    int p1Col = color(255, 109, 116);
    int p2Col = color(255, 197, 98);

    Grid[] grid;
    SelectArrow s;
    String playerString;
    String p1 = "Player 1";
    String p2 = "Player 2";
    String com = "Computer";

    public static void main(String[] args) {
        PApplet.runSketch(new String[]{""}, new Connect4GUI());
    }

    public void settings() {
        size(800,750);
    }

    public void setup() {
        pvp = false;
        pvc = false;
        play = false;
        player = 1;
        playerString = p1;
        c4 = Connect4.of(new int[42]);  //Initialize so newGame can be used

        title = new Button(0, 0, width, height - 50, "Connect 4", green, 100, fontLight);
        start = new Button(0, height - 50, width / 2, 50, "1 vs 1", lightGray, bigButton, fontLight);
        vsComp = new Button(width / 2, height - 50, width / 2, 50, "1 vs COM", lightGray, bigButton, fontLight);
        exit = new Button(width - 90, 0, 90, 30, "MAIN MENU", lightGray, smallButton, fontLight);
        replay = new Button(width - 180, 0, 90, 30, "RESTART", lightGray, smallButton, fontLight);
        undo = new Button(width - 270, 0, 90, 30, "UNDO", lightGray, smallButton, fontLight);
    }

    public void draw() {
        background(green);
        if(!pvp && !pvc) {
            title.draw();
            start.update();
            start.draw();
            vsComp.update();
            vsComp.draw();
            if(start.isClicked) {
                System.out.println("\nNEW GAME PLAYER VS PLAYER");
                pvp = true;
                createNewGame();
                updateGrid();
                System.out.println(c4.toString());
            }
            else if(vsComp.isClicked) {
                System.out.println("\nNEW GAME PLAYER VS COMPUTER");
                pvc = true;
                createNewGame();
                updateGrid();
                System.out.println(c4.toString());
            }
        }
        else {
            Button rect = new Button(40,130,720,650, "", lightGray, bigButton, fontLight);
            rect.draw();
            Button turn = new Button(40, 700, 720, 50, playerString, lightGray, bigButton, fontLight);
            turn.draw();
            if(pvp) {
                play = true;
                pvp();
            }
            if(pvc) {
                play = true;
                pvc();
            }
        }
    }

    void pvp() {
        Arrays.stream(grid).forEach(Grid::update);
        Arrays.stream(grid).forEach(Grid::draw);

        if(c4.isGameOver() == 0) { //Game is NOT over if == 0
            s.draw();
            s.update();

            if(s.isClicked && !c4.columnIsFull(s.column)) {
                if(player == 1) {
                    System.out.println("\nPLAYER 1 MOVE:");
                    System.out.println("\nBefore");
                    System.out.println(c4.toString());
                    System.out.println("\nAfter");
                    playerString = p2;
                }
                else {
                    System.out.println("\nPLAYER 2 MOVE:");
                    System.out.println("\nBefore");
                    System.out.println(c4.toString());
                    System.out.println("\nAfter");
                    playerString = p1;
                }
                c4 = c4.play(Move.of(player, s.column));
                player = -1 * player;
                updateGrid();
                System.out.println(c4.toString());
            }
        }

        if(c4.isGameOver() == -1) {
            Button winner = new Button(200, 40, 400, 80, p2 + " Wins", green, titleSize, fontLight);
            winner.draw();
        } else if(c4.isGameOver() == 1) {
            Button winner = new Button(200, 40, 400, 80, p1 + " Wins", green, titleSize, fontLight);
            winner.draw();
        } else if(c4.isGameOver() == 3) {
            Button winner = new Button(200, 40, 400, 80, "Tie Game", green, titleSize, fontLight);
            winner.draw();
        }

        replay.draw();
        replay.update();
        if(replay.isClicked) {
            System.out.println("\nREPLAY GAME");
            player = 1;
            playerString = p1;
            createNewGame();
            updateGrid();
            System.out.println(c4.toString());
        }

        exit.draw();
        exit.update();
        if(exit.isClicked) {
            pvp = false;
            setup();
        }

        undo.draw();
        undo.update();
        if(undo.isClicked) {
            try {
                c4 = c4.undo();
            } catch(EmptyStackException e) {
                return;
            }
            if(player == 1) {
                System.out.println("\nUNDO PLAYER 2 MOVE");
                playerString = p2;
            } else {
                System.out.println("\nUNDO PLAYER 1 MOVE");
                playerString = p1;
            }
            player = -player;
            s = new SelectArrow(); //BUG if not reinitialized
            updateGrid();
            System.out.println(c4.toString());
        }
    }

    void pvc() {
        Arrays.stream(grid).forEach(Grid::update);
        Arrays.stream(grid).forEach(Grid::draw);

        if(c4.isGameOver() == 0) { //Game is NOT over if == 0
            s.draw();
            s.update();

            if(s.isClicked && player == 1 && !c4.columnIsFull(s.column)) {
                System.out.println("\nPLAYER 1 MOVE:");
                System.out.println("\nBefore");
                System.out.println(c4.toString());
                System.out.println("\nAfter");

                c4 = c4.play(Move.of(player, s.column));
                player = -1 * player;
                playerString = com;
                updateGrid();
                System.out.println(c4.toString());
            } else if(player == -1 && c4.isGameOver() == 0) { //Game is NOT over if == 0
                Move m = c4.bestMove(-1);

                System.out.println("\nCOMPUTER MOVE:");
                System.out.print("\nBefore");
                System.out.println(c4.toString());

                c4 = c4.play(m);
                player = -player;
                playerString = p1;
                System.out.print("\nAfter");
                updateGrid();
                System.out.println(c4.toString());
            }
        }

        if(c4.isGameOver() == -1) {
            Button winner = new Button(200, 40, 400, 80, com + " Wins", green, titleSize, fontLight);
            winner.draw();
        } else if(c4.isGameOver() == 1) {
            Button winner = new Button(200, 40, 400, 80, p1 + " Wins", green, titleSize, fontLight);
            winner.draw();
        } else if(c4.isGameOver() == 3) {
            Button winner = new Button(200, 40, 400, 80, "Tie Game", green, titleSize, fontLight);
            winner.draw();
        }

        replay.draw();
        replay.update();
        if(replay.isClicked) {
            System.out.println("\nREPLAY GAME");
            player = 1;
            playerString = p1;
            createNewGame();
            updateGrid();
            System.out.println(c4.toString());
        }

        exit.draw();
        exit.update();
        if(exit.isClicked) {
            pvp = false;
            setup();
        }

        undo.draw();
        undo.update();
        if(undo.isClicked) {
            try {
                c4 = c4.undo();
                c4 = c4.undo();
            } catch(EmptyStackException e) {
                return;
            }
            System.out.println("\nUNDO LAST PLAYER MOVE");
            s = new SelectArrow(); //BUG if not reinitialized
            updateGrid();
            System.out.println(c4.toString());
        }
    }

    void createNewGame() {
        s = new SelectArrow(); //BUG if not reinitialized
        c4 = c4.newGame();
    }

    public void updateGrid() {
        int[] arr = c4.getGrid();

        grid = new Grid[42];

        int y = 100;
        int x = 100;
        for(int i = 0; i < 42; i++) {
            if(i % 7 == 0) {
                x = 100;
                y += 90;
            }
            grid[i] = new Grid(x, y, arr[i]);
            x += 100;
        }
    }

    class Button {
        boolean isPressed = false;
        boolean isClicked = false;
        float w;
        float h;
        int posX;
        int posY;
        String text;
        int c;
        int borderC = 0;
        int resetC;
        int size;
        int textC;

        Button(int posX, int posY, int w, int h, String text, int c, int size, int textC) {
            this.posX = posX;
            this.posY = posY;
            this.w = w;
            this.h = h;
            this.text = text;
            this.c = c;
            this.resetC = c;
            this.size = size;
            this.textC = textC;
        }

        void update() {
            if(mouseX >= posX && mouseX <= posX + w && mouseY >= posY && mouseY <= posY + h) {
                borderC = 255;
                if(mousePressed && mouseButton == LEFT && !isPressed) {
                    isPressed = true;
                    isClicked = true;
                    c = 150;
                }
                else {
                    isClicked = false;
                }
            }
            else {
                borderC = 0;
            }
            if(!mousePressed) {
                isPressed = false;
                c = resetC;
            }
        }

        void draw() {
            stroke(borderC);
            fill(c);
            rect(posX, posY, w, h);

            fill(textC);
            textSize(size);
            textAlign(CENTER, CENTER);
            text(text, posX + (w / 2), posY + (h / 2));
        }
    }

    class Grid {
        int player = 0;
        int posX;
        int posY;
        int color;
        int border = 0;
        int emptyCol = green;

        Grid(int posX, int posY, int player) {
            assert player == 0 || player == 1 || player == -1;
            this.posX = posX;
            this.posY = posY;
            this.player = player;
        }

        void draw() {
            stroke(border);
            fill(color);
            ellipse(posX, posY, 80, 80);
        }

        void update() {
            switch (player) {
                case 0 -> color = emptyCol;
                case 1 -> color = p1Col;
                case -1 -> color = p2Col;
            }
        }
    }

    class SelectArrow {
        boolean isPressed = false;
        boolean isClicked = false;
        int posX = 100;
        int posY = 115;
        int w = 30;
        int h = 60;
        int color = color(97, 168, 232);
        int column;

        void draw() {
            stroke(0);
            fill(color);
            triangle(posX, posY, posX + w, posY - h, posX - w, posY - h);
        }

        void update() {
            if(mouseY >= posY && mouseY <= height - 80) {
                if(mouseX >= 50 && mouseX < 150) {
                    posX = 100;
                    column = 1;
                    checkClick();
                } else if(mouseX >= 150 && mouseX < 250) {
                    posX = 200;
                    column = 2;
                    checkClick();
                } else if(mouseX >= 250 && mouseX < 350) {
                    posX = 300;
                    column = 3;
                    checkClick();
                } else if(mouseX >= 350 && mouseX < 450) {
                    posX = 400;
                    column = 4;
                    checkClick();
                } else if(mouseX >= 450 && mouseX < 550) {
                    posX = 500;
                    column = 5;
                    checkClick();
                } else if(mouseX >= 550 && mouseX < 650) {
                    posX = 600;
                    column = 6;
                    checkClick();
                } else if(mouseX >= 650 && mouseX < 750) {
                    posX = 700;
                    column = 7;
                    checkClick();
                } else {
                    isPressed = false;
                    isClicked = false;
                }
            }
            if(!mousePressed) {
                isPressed = false;
            }
        }

        void checkClick() {
            if(mousePressed && mouseButton == LEFT && !isPressed) {
                isPressed = true;
                isClicked = true;
            }
            else {
                isClicked = false;
            }
        }
    }
}
