package com.jcjr30.pacman;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;

public class PacMan extends JPanel implements ActionListener, KeyListener {

    //A 16x16 block on the grid
    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startX;
        int startY;

        char direction = 'U';
        int velocityX = 0;
        int velocityY = 0;

        boolean waiting = false;
        long waitingTime = 0;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;

            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction) {
            if (waiting) return;

            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();

            this.x += this.velocityX;
            this.y += this.velocityY;
            for (HashSet<Block> collideableSet : collidables) {
                for (Block collideableBlock : collideableSet) {
                    if (collision(this, collideableBlock)) {
                        this.x -= this.velocityX;
                        this.y -= this.velocityY;
                        this.direction = prevDirection;
                        updateVelocity();
                        break;
                    }
                }
            }
        }

        void updateVelocity() {
            int maxVelocity = tileSize / 4;

            if (this.direction == 'U') {
                this.velocityX = 0;
                this.velocityY = -maxVelocity;
            } else if (this.direction == 'D') {
                this.velocityX = 0;
                this.velocityY = maxVelocity;
            } else if (this.direction == 'L') {
                this.velocityX = -maxVelocity;
                this.velocityY = 0;
            } else if (this.direction == 'R') {
                this.velocityX = maxVelocity;
                this.velocityY = 0;
            }
        }

        void reset() {
            this.x = startX;
            this.y = startY;
        }

        void startWaiting(long delay) {
            waiting = true;
            waitingTime = System.currentTimeMillis() + delay;
        }

        void checkWaiting() {
            if (waiting && System.currentTimeMillis() > waitingTime) {
                waiting = false;
                updateVelocity();
            }
        }
    }


    private final int columnCount = 28;
    private final int rowCount = 36;
    private final int tileSize = 32;
    private final int boardWidth = columnCount * tileSize;
    private final int boardHeight = rowCount * tileSize;

    int levelsBeaten;

    private final Image wallImage;
    private final Image topLeftCornerImage;
    private final Image topRightCornerImage;
    private final Image bottomLeftCornerImage;
    private final Image bottomRightCornerImage;

    private final Image blueGhostImage;
    private final Image orangeGhostImage;
    private final Image pinkGhostImage;
    private final Image redGhostImage;
    private final Image scaredGhostImage;

    private final Image pacmanUpImage;
    private final Image pacmanDownImage;
    private final Image pacmanLeftImage;
    private final Image pacmanRightImage;

    private final Image powerPelletImage;
    private final Image cherryImage;

    private final File highScoreFontFile = new File("src/main/resources/com/jcjr30/pacman/fonts/emulogic.ttf");
    private final File titleFontFile = new File("src/main/resources/com/jcjr30/pacman/fonts/CrackMan.TTF");
    private final Font highScoreFont;
    private final Font titleFont;

    private PowerUp powerUp;

    private int frameCount = 0;
    private int frameCountDelay = 0;


    // '<'
    HashSet<Block> topLeftCorners;
    // '>'
    HashSet<Block> topRightCorners;
    // '\'
    HashSet<Block> bottomLeftCorners;
    // '/'
    HashSet<Block> bottomRightCorners;
    HashSet<Block>[] collidables;

    // '#
    HashSet<Block> solidWalls;
    // '|'
    HashSet<Block> vertWalls;
    // '-'
    HashSet<Block> horizWalls;
    // '.'
    HashSet<Block> foods;
    // ','
    HashSet<Block> powerPellets;

    HashSet<Block> fruits;

    HashSet<Block> ghosts;
    int tryRandomMove = 0;

    Block pacman;

    boolean isFirstFruitAte = false;
    boolean isFirstFruitDrawing = false;
    boolean isSecondFruitAte = false;
    boolean isSecondFruitDrawing = false;

    Block leftPortal;
    Block rightPortal;

    Timer gameLoop;
    boolean debug = true;

    char[] directions = {'U', 'D', 'L', 'R'};
    private char directionBuffer;
    Random random = new Random();

    int score = 0;
    int lastRoundScore = 0;
    int lives = 3;
    boolean gameOver = false;

    PacMan() throws IOException, FontFormatException {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        //Load images
        wallImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("img/solidWall.png"))).getImage();
        topLeftCornerImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("img/wall-TopLeftCorner.png"))).getImage();
        topRightCornerImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("img/wall-TopRightCorner.png"))).getImage();
        bottomLeftCornerImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("img/wall-BottomLeftCorner.png"))).getImage();
        bottomRightCornerImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("img/wall-BottomRightCorner.png"))).getImage();

        blueGhostImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("img/blueGhost.png"))).getImage();
        orangeGhostImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("img/orangeGhost.png"))).getImage();
        pinkGhostImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("img/pinkGhost.png"))).getImage();
        redGhostImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("img/redGhost.png"))).getImage();
        scaredGhostImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("img/scaredGhost.png"))).getImage();

        pacmanUpImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("img/pacmanUp.png"))).getImage();
        pacmanDownImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("img/pacmanDown.png"))).getImage();
        pacmanLeftImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("img/pacmanLeft.png"))).getImage();
        pacmanRightImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("img/pacmanRight.png"))).getImage();

        powerPelletImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("img/powerPellet.png"))).getImage();
        cherryImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("img/cherry.png"))).getImage();


        highScoreFont = Font.createFont(Font.TRUETYPE_FONT, highScoreFontFile);
        titleFont = Font.createFont(Font.TRUETYPE_FONT, titleFontFile);

        loadMap();

        for (Block ghost : ghosts) {
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }

        gameLoop = new Timer(50, this); //20fps
        gameLoop.start();
    }

    private void loadMap() throws IOException {
        foods = new HashSet<>();
        powerPellets = new HashSet<>();
        fruits = new HashSet<>();

        ghosts = new HashSet<>();

        topLeftCorners = new HashSet<>();
        topRightCorners = new HashSet<>();
        bottomLeftCorners = new HashSet<>();
        bottomRightCorners = new HashSet<>();
        solidWalls = new HashSet<>();
        vertWalls = new HashSet<>();
        horizWalls = new HashSet<>();

        //Parse the Tile Map
        char[][] board = BoardLoader.loadBoard();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                char tileMapChar = board[r][c];

                int x = c * tileSize;
                int y = r * tileSize;

                if (tileMapChar == '\\') {
                    bottomLeftCorners.add(new Block(bottomLeftCornerImage, x, y, tileSize, tileSize));
                } else if (tileMapChar == '/') {
                    bottomRightCorners.add(new Block(bottomRightCornerImage, x, y, tileSize, tileSize));
                } else if (tileMapChar == '<') {
                    topLeftCorners.add(new Block(topLeftCornerImage, x, y, tileSize, tileSize));
                } else if (tileMapChar == '>') {
                    topRightCorners.add(new Block(topRightCornerImage, x, y, tileSize, tileSize));
                } else if (tileMapChar == '#') {
                    solidWalls.add(new Block(wallImage, x, y, tileSize, tileSize));
                } else if (tileMapChar == '|') {
                    vertWalls.add(new Block(wallImage, x, y, tileSize, tileSize));
                } else if (tileMapChar == '-') {
                    horizWalls.add(new Block(wallImage, x, y, tileSize, tileSize));
                } else if (tileMapChar == '.') {
                    foods.add(new Block(null, x + 14, y + 14, 4, 4));
                } else if (tileMapChar == 'P') {
                    pacman = new Block(pacmanRightImage, x, y, tileSize - 2, tileSize - 2);
                } else if (tileMapChar == 'r') {
                    ghosts.add(new Block(redGhostImage, x, y, tileSize, tileSize));
                } else if (tileMapChar == 'b') {
                    ghosts.add(new Block(blueGhostImage, x, y, tileSize, tileSize));
                } else if (tileMapChar == 'p') {
                    ghosts.add(new Block(pinkGhostImage, x, y, tileSize, tileSize));
                } else if (tileMapChar == 'o') {
                    ghosts.add(new Block(orangeGhostImage, x, y, tileSize, tileSize));
                } else if (tileMapChar == '1') {
                    leftPortal = new Block(null, x - 64, y, tileSize, tileSize);
                } else if (tileMapChar == '2') {
                    rightPortal = new Block(null, x + 64, y, tileSize, tileSize);
                } else if (tileMapChar == ',') {
                    powerPellets.add(new Block(powerPelletImage, x + 3, y + 3, tileSize - 6, tileSize - 6));
                }
            }
        }
        //Add all the walls to the collidable set
        collidables = new HashSet[]{topLeftCorners, topRightCorners, bottomLeftCorners, bottomRightCorners, solidWalls, vertWalls, horizWalls};

        //Generate fruits and add to fruits set
        fruits.add(new Block(cherryImage, tileSize * 14 - 16, tileSize * 20, tileSize, tileSize));
        fruits.add(new Block(cherryImage, tileSize * 14 - 16, tileSize * 20, tileSize, tileSize));
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
    private void draw(Graphics g) {
        frameCount++;
        //Draw Pac-Man
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        //Draw ghosts
        for (Block ghost : ghosts) {
            if (powerUp != null && powerUp.isActive()) {
                g.drawImage(scaredGhostImage, ghost.x, ghost.y, ghost.width, ghost.height, null);
            } else {
                g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
            }
        }
        //Draw walls
        for (HashSet<Block> anyWall : collidables) {
            for (Block wall : anyWall) {
                g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
            }
        }

        //Draw Power Pellets
        for (Block powerPellet : powerPellets) {
            g.drawImage(powerPellet.image, powerPellet.x, powerPellet.y, powerPellet.width, powerPellet.height, null);
        }

        if (score >= 700 && !isFirstFruitAte && levelsBeaten == 0) {
            g.drawImage(cherryImage, tileSize * 14 - 16, tileSize * 20, tileSize, tileSize, null);
            isFirstFruitDrawing = true;
        } else if (score >= lastRoundScore+700 && !isFirstFruitAte && levelsBeaten > 0) {
            g.drawImage(cherryImage, tileSize * 14 - 16, tileSize * 20, tileSize, tileSize, null);
            isFirstFruitDrawing = true;
        } else {isFirstFruitDrawing = false;}

        if (score >= 1400 && !isSecondFruitAte && isFirstFruitAte && levelsBeaten == 0) {
            g.drawImage(cherryImage, tileSize * 14 - 16, tileSize * 20, tileSize, tileSize, null);
            isSecondFruitDrawing = true;
        } else if (score >= lastRoundScore+1400 && !isSecondFruitAte && isFirstFruitAte && levelsBeaten > 0) {
            g.drawImage(cherryImage, tileSize * 14 - 16, tileSize * 20, tileSize, tileSize, null);
            isSecondFruitDrawing = true;
        } else {isSecondFruitDrawing = false;}

        if (debug)  {
            drawDebugGrid(g);
        }

        g.setColor(Color.WHITE);
        for (Block food : foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }

        //Score
        g.setFont(highScoreFont.deriveFont(Font.PLAIN, 20));
        if (gameOver) {
            g.drawString("Game Over: " + score, tileSize / 2, tileSize / 2 + 32);
        } else {
            g.drawString("x" + lives + "  Score: " + score + "  Levels Beaten: " + levelsBeaten, tileSize / 2, tileSize / 2 + 32);
        }
    }
    private void drawDebugGrid(Graphics g) {
        g.setColor(Color.RED);
        for (int c = 0; c < columnCount; c++) {
            for (int r = 0; r < rowCount; r++) {
                int y = r * tileSize;
                int x = c * tileSize;
                g.drawRect(x, y, tileSize, tileSize);
                g.drawString(c + "," + r, x + tileSize/6-2, y+tileSize/6+4);
            }
        }

    }

    private void move() throws IOException {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        //Set PacMan image based on velocity
        updatePacManImage();

        if (!isFrameCountTimerDone()) {
            System.out.println("Frame Count: " + frameCount);
            pacman.updateDirection(directionBuffer);
        }

        //Wall collision detection
        for (HashSet<Block> collideableSet : collidables) {
            for (Block collideableBlock : collideableSet) {
                if (collision(pacman, collideableBlock)) {
                    pacman.x -= pacman.velocityX;
                    pacman.y -= pacman.velocityY;
                    break;
                }
            }
        }

        //Ghost collision detection if power up is active
        if (powerUp != null && powerUp.isActive()) {
            for (Block ghost : ghosts) {
                ghost.checkWaiting();

                if (!ghost.waiting && collision(ghost, pacman)) {
                    ghost.reset();
                    ghost.startWaiting(5000);
                    score += 50;
                }
                ghostAI(ghost);
            }
        } else {
            //Ghost collision detection
            for (Block ghost : ghosts) {
                ghost.checkWaiting();

                if (!ghost.waiting && collision(ghost, pacman)) {
                    lives -= 1;
                    if (lives == 0) {
                        gameOver = true;
                        return;
                    }
                    resetPositions();
                }
                ghostAI(ghost);
            }
        }

        //Portal Collision
        if (collision(pacman, leftPortal)) {
            pacman.x = rightPortal.x - 32;
            pacman.y = rightPortal.y;
        } else if (collision(pacman, rightPortal)) {
            pacman.x = leftPortal.x + 32;
            pacman.y = leftPortal.y;
        }

        //Power Pellet Collision
        Block powerPelletEaten = null;
        for (Block powerPellet : powerPellets) {
            if (collision(pacman, powerPellet)) {

                powerPelletEaten = powerPellet;

                powerUp = new PowerUp(7500);
                powerUp.activate();
            }
        }
        powerPellets.remove(powerPelletEaten);


        //Fruit Collision DEBUG
        Block fruitEaten = null;
        if (isFirstFruitDrawing && !fruits.isEmpty()) {
            for (Block currentFruit : fruits) {
                if (collision(pacman, currentFruit)) {
                    isFirstFruitAte = true;
                    score += 100;
                    fruitEaten = currentFruit;
                }
            }
            fruits.remove(fruitEaten);
            fruitEaten = null;
        }
        if (isSecondFruitDrawing && !fruits.isEmpty()) {
            for (Block currentFruit : fruits) {
                if (collision(pacman, currentFruit)) {
                    isSecondFruitAte = true;
                    score += 100;
                    fruitEaten = currentFruit;
                }
            }
            fruits.remove(fruitEaten);
        }

        //Food Collision
        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(pacman, food)) {
                foodEaten = food;
                score += 10;
            }
        }
        foods.remove(foodEaten);

        //Win if foods is empty
        if (foods.isEmpty()) {
            lastRoundScore = score;
            isFirstFruitAte = false;
            isSecondFruitAte = false;

            isFirstFruitDrawing = false;
            isSecondFruitDrawing = false;

            loadMap();
            resetPositions();
            levelsBeaten += 1;
            lives += 1;
        }
    }

    private void ghostAI(Block ghost) {
        if (!ghost.waiting) {
            //Ghost escape start AI
            if (ghostLeaveStartAi(ghost)) {
                ghost.updateDirection('U');
            }
            //General Ghost AI
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;

            tryRandomMove = random.nextInt(100);
            if (aiHitboxCollision(ghost, pacman)) {
                if (!isGhostInStartBlock(ghost)) {

                    if (tryRandomMove <= 1) {
                        if (ghost.x < pacman.x && ghost.direction != 'R') {
                            ghost.updateDirection('R');
                        } else if (ghost.x > pacman.x && ghost.direction != 'L') {
                            ghost.updateDirection('L');
                        } else if (ghost.y < pacman.y && ghost.direction != 'D') {
                            ghost.updateDirection('D');
                        } else if (ghost.y > pacman.y && ghost.direction != 'U') {
                            ghost.updateDirection('U');
                        }
                    }
                }
//                else if (tryRandomMove <= 10) {
//                    char newDirection = directions[random.nextInt(4)];
//                    ghost.updateDirection(newDirection);
//                }
            }

            for (HashSet<Block> collideableSet : collidables) {
                for (Block collideableBlock : collideableSet) {
                    if (collision(ghost, collideableBlock)) {
                        ghost.x -= ghost.velocityX;
                        ghost.y -= ghost.velocityY;
                        char newDirection = directions[random.nextInt(4)];
                        ghost.updateDirection(newDirection);
                    }
                }
            }
        }
    }
    private boolean ghostLeaveStartAi(Block ghost) {
        return (ghost.y > tileSize*17 && ghost.y < tileSize*19) && (ghost.x > tileSize*13+12 && ghost.x < tileSize *14-12) && ghost.direction != 'U';
    }
    private boolean isGhostInStartBlock(Block ghost) {
        return (ghost.y > tileSize*15 && ghost.y < tileSize * 19) && (ghost.x > tileSize * 10 && ghost.x < tileSize * 17);
    }

    private boolean collision(Block a, Block b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }
    private boolean aiHitboxCollision(Block a, Block b) {
        int radius = 224;
        return a.x < b.x + b.width + radius &&
                a.x + a.width + radius > b.x &&
                a.y < b.y + b.height + radius &&
                a.y + a.height + radius > b.y;
    }

    private void resetPositions() {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;

        for (Block ghost : ghosts) {
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            move();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        repaint();
        if (gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver && e.getKeyCode() == KeyEvent.VK_SPACE) {
            try {
                loadMap();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            resetPositions();
            lives = 3;
            score = 0;
            gameOver = false;
            gameLoop.start();
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_P:
                if (gameLoop.isRunning()) {
                    gameLoop.stop();
                } else {
                    gameLoop.start();
                }
                break;

            case KeyEvent.VK_F1:
                debug = !debug;
                break;
        }

        char newDirection = switch (e.getKeyCode()) {
            case KeyEvent.VK_UP, KeyEvent.VK_W -> 'U';
            case KeyEvent.VK_DOWN, KeyEvent.VK_S -> 'D';
            case KeyEvent.VK_LEFT, KeyEvent.VK_A -> 'L';
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> 'R';
            default -> ' ';
        };

        if (newDirection != ' ' && pacman.direction != newDirection) {
            pacman.updateDirection(newDirection);
            frameCountTimerStart(newDirection);
        }
    }

    private void frameCountTimerStart(char direction) {
        if (pacman.direction == direction) {
            return;
        }
        if (isFrameCountTimerDone()) {
            frameCountDelay = 2;
            frameCountDelay += frameCount;
            directionBuffer = direction;
        } else if (pacman.direction != directionBuffer) {
            frameCountTimerEnd();
            frameCountDelay = 2;
            frameCountDelay += frameCount;
            directionBuffer = direction;
        }
    }
    private boolean isFrameCountTimerDone() {
        return frameCount >= frameCountDelay;
    }
    private void frameCountTimerEnd() {
        frameCountDelay = 0;
    }
    private void updatePacManImage() {
        if (pacman.velocityX > 0) {
            pacman.image = pacmanRightImage;
        } else if (pacman.velocityX < 0) {
            pacman.image = pacmanLeftImage;
        } else if (pacman.velocityY > 0) {
            pacman.image = pacmanDownImage;
        } else if (pacman.velocityY < 0) {
            pacman.image = pacmanUpImage;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }
}


