package com.jcjr30.pacman;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
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



        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;

            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction)    {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();

            this.x += this.velocityX;
            this.y += this.velocityY;
            for (HashSet<Block> collideableSet : collideable) {
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
        void updateVelocity()   {

            if (this.direction == 'U')  {
                this.velocityX = 0;
                this.velocityY = -tileSize/4;
            } else if (this.direction == 'D')   {
                this.velocityX = 0;
                this.velocityY = tileSize/4;
            } else if (this.direction == 'L')   {
                this.velocityX = (-tileSize/4);
                this.velocityY = 0;
            } else if (this.direction == 'R')   {
                this.velocityX = (tileSize/4);
                this.velocityY = 0;
            }
        }
        void reset()    {
            this.x = startX;
            this.y = startY;
        }
    }


    private final int columnCount = 28;
    private final int rowCount = 36;
    private final int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;

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

    private final Image pacmanUpImage;
    private final Image pacmanDownImage;
    private final Image pacmanLeftImage;
    private final Image pacmanRightImage;

    private final Image powerPelletImage;

    private final File highScoreFontFile = new File("src/main/resources/com/jcjr30/pacman/fonts/emulogic.ttf");
    private final File titleFontFile = new File("src/main/resources/com/jcjr30/pacman/fonts/CrackMan.TTF");
    private final Font highScoreFont;
    private final Font titleFont;

    private PowerUp powerUp;


    // '<'
    HashSet<Block> topLeftCorners;
    // '>'
    HashSet<Block> topRightCorners;
    // '\'
    HashSet<Block> bottomLeftCorners;
    // '/'
    HashSet<Block> bottomRightCorners;
    HashSet<Block>[] collideable;

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

    HashSet<Block> ghosts;
    Block pacman;

    Block leftPortal;
    Block rightPortal;

    Timer gameLoop;

    char[] directions = {'U', 'D', 'L', 'R'};
    Random random = new Random();

    int score = 0;
    int lives = 3;
    boolean gameOver = false;

    PacMan() throws IOException, FontFormatException {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        //Load images
        wallImage = new ImageIcon(getClass().getResource("img/wall.png")).getImage();
        topLeftCornerImage = new ImageIcon(getClass().getResource("img/wall-TopLeftCorner.png")).getImage();
        topRightCornerImage = new ImageIcon(getClass().getResource("img/wall-TopRightCorner.png")).getImage();
        bottomLeftCornerImage = new ImageIcon(getClass().getResource("img/wall-BottomLeftCorner.png")).getImage();
        bottomRightCornerImage = new ImageIcon(getClass().getResource("img/wall-BottomRightCorner.png")).getImage();

        blueGhostImage = new ImageIcon(getClass().getResource("img/blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("img/orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("img/pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("img/redGhost.png")).getImage();

        pacmanUpImage = new ImageIcon(getClass().getResource("img/pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("img/pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("img/pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("img/pacmanRight.png")).getImage();

        powerPelletImage = new ImageIcon(getClass().getResource("img/powerPellet.png")).getImage();

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

    public void loadMap() throws IOException {
        foods = new HashSet<Block>();
        powerPellets = new HashSet<Block>();
        ghosts = new HashSet<Block>();

        topLeftCorners = new HashSet<Block>();
        topRightCorners = new HashSet<Block>();
        bottomLeftCorners = new HashSet<Block>();
        bottomRightCorners = new HashSet<Block>();
        solidWalls = new HashSet<Block>();
        vertWalls = new HashSet<Block>();
        horizWalls = new HashSet<Block>();

        //Parse the Tile Map
        char[][] board = BoardLoader.loadBoard();

        for (int r = 0; r < rowCount; r++)  {
            for (int c = 0; c < columnCount; c++)   {
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
                    foods.add(new Block(null, x+14, y+14, 4, 4));
                } else if (tileMapChar == 'P') {
                    pacman = new Block(pacmanRightImage, x, y, tileSize-2, tileSize-2);
                } else if (tileMapChar == 'r') {
                    ghosts.add(new Block(redGhostImage, x, y, tileSize, tileSize));
                } else if (tileMapChar == 'b') {
                    ghosts.add(new Block(blueGhostImage, x, y, tileSize, tileSize));
                } else if (tileMapChar == 'p') {
                    ghosts.add(new Block(pinkGhostImage, x, y, tileSize, tileSize));
                } else if (tileMapChar == 'o') {
                    ghosts.add(new Block(orangeGhostImage, x, y, tileSize, tileSize));
                } else if (tileMapChar == '1') {
                    leftPortal = new Block(null, x-64, y, tileSize, tileSize);
                } else if (tileMapChar == '2') {
                    rightPortal = new Block(null, x+64, y, tileSize, tileSize);
                } else if (tileMapChar == ',')  {
                    powerPellets.add(new Block(powerPelletImage, x+3, y+3, tileSize-6, tileSize-6));
                }
            }
        }
        collideable = new HashSet[] {topLeftCorners, topRightCorners, bottomLeftCorners, bottomRightCorners, solidWalls, vertWalls, horizWalls};
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }
        for (Block solidWall : solidWalls) {
            g.drawImage(solidWall.image, solidWall.x, solidWall.y, solidWall.width, solidWall.height, null);
        }
        for (Block vertWall : vertWalls) {
            g.drawImage(vertWall.image, vertWall.x, vertWall.y, vertWall.width, vertWall.height, null);
        }
        for (Block horizWall : horizWalls) {
            g.drawImage(horizWall.image, horizWall.x, horizWall.y, horizWall.width, horizWall.height, null);
        }
        for (Block leftCorner : topLeftCorners) {
            g.drawImage(leftCorner.image, leftCorner.x, leftCorner.y, leftCorner.width, leftCorner.height, null);
        }
        for (Block rightCorner : topRightCorners) {
            g.drawImage(rightCorner.image, rightCorner.x, rightCorner.y, rightCorner.width, rightCorner.height, null);
        }
        for (Block leftCorner : bottomLeftCorners) {
            g.drawImage(leftCorner.image, leftCorner.x, leftCorner.y, leftCorner.width, leftCorner.height, null);
        }
        for (Block rightCorner : bottomRightCorners) {
            g.drawImage(rightCorner.image, rightCorner.x, rightCorner.y, rightCorner.width, rightCorner.height, null);
        }
        for (Block powerPellet : powerPellets) {
            g.drawImage(powerPellet.image, powerPellet.x, powerPellet.y, powerPellet.width, powerPellet.height, null );
        }

        g.setColor(Color.WHITE);
        for (Block food : foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }

        //Score
        g.setFont(highScoreFont.deriveFont(Font.PLAIN, 20));
        if (gameOver)   {
            g.drawString("Game Over: " + score, tileSize/2, tileSize/2+32);
        }
        else {
            g.drawString("x" + lives + "  Score: " + score + "  Levels Beaten: " + levelsBeaten, tileSize/2, tileSize/2+32);
        }
    }

    public void move() throws IOException {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        //Wall collision detection
        for (HashSet<Block> collideableSet : collideable) {
            for (Block collideableBlock : collideableSet) {
                if (collision(pacman, collideableBlock)) {
                    pacman.x -= pacman.velocityX;
                    pacman.y -= pacman.velocityY;
                    break;
                }
            }
        }

        if (powerUp != null && powerUp.isActive()) {
            for (Block ghost : ghosts) {
                if (collision(ghost, pacman)){
                    ghost.reset();
                    score += 50;
                }
                //Ghost escape start AI
                if ((ghost.y > tileSize * 17 && ghost.y < tileSize * 19) && (ghost.x > tileSize * 13 + 12 && ghost.x < tileSize * 14 - 12) && ghost.direction != 'U') {
                    ghost.updateDirection('U');
                }
                //General Ghost AI
                ghost.x += ghost.velocityX;
                ghost.y += ghost.velocityY;
                for (HashSet<Block> collideableSet : collideable) {
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
        } else {
            //Ghost collision detection
            for (Block ghost : ghosts) {
                if (collision(ghost, pacman)) {
                    lives -= 1;
                    if (lives == 0) {
                        gameOver = true;
                        return;
                    }
                    resetPositions();
                }
                //Ghost escape start AI
                if ((ghost.y > tileSize * 17 && ghost.y < tileSize * 19) && (ghost.x > tileSize * 13 + 12 && ghost.x < tileSize * 14 - 12) && ghost.direction != 'U') {
                    ghost.updateDirection('U');
                }
                //General Ghost AI
                ghost.x += ghost.velocityX;
                ghost.y += ghost.velocityY;
                for (HashSet<Block> collideableSet : collideable) {
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

        //Portal Collision
        if (collision(pacman, leftPortal)) {
            pacman.x = rightPortal.x-32;
            pacman.y = rightPortal.y;
        } else if (collision(pacman, rightPortal)) {
            pacman.x = leftPortal.x+32;
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
            loadMap();
            resetPositions();
            levelsBeaten += 1;
            lives += 1;
        }
    }

    public boolean collision(Block a, Block b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }
    public void resetPositions() {
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
        if (gameOver && e.getKeyCode() == KeyEvent.VK_SPACE)   {
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

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            pacman.updateDirection('U');
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            pacman.updateDirection('D');
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            pacman.updateDirection('L');
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacman.updateDirection('R');
        }

        if (pacman.direction == 'U')    {
            pacman.image = pacmanUpImage;
        } else if (pacman.direction == 'D')    {
            pacman.image = pacmanDownImage;
        } else if (pacman.direction == 'L')    {
            pacman.image = pacmanLeftImage;
        } else if (pacman.direction == 'R')    {
            pacman.image = pacmanRightImage;
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyPressed(KeyEvent e) {}
}


