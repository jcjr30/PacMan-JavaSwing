package com.jcjr30.boardCreator;

import com.jcjr30.pacman.App;
import com.jcjr30.pacman.BoardLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

import java.util.Arrays;
import java.util.Objects;

public class BoardCreator extends JPanel implements KeyListener, ActionListener, MouseListener {

    private final JFrame frame;

    private char tool = ' ';

    int columnCount = 28;
    int rowCount = 36;
    int tileSize = 32;
    int boardWidth = columnCount * tileSize;
    int boardHeight = rowCount * tileSize;

    private final Image wallImage;
    private final Image topLeftCornerImage;
    private final Image topRightCornerImage;
    private final Image bottomLeftCornerImage;
    private final Image bottomRightCornerImage;

    private final Image blueGhostImage;
    private final Image orangeGhostImage;
    private final Image pinkGhostImage;
    private final Image redGhostImage;

    private final Image pacmanRightImage;

    private final Image powerPelletImage;

    private final Image leftPortalImage;
    private final Image rightPortalImage;

    private final Image eraserImage;

    private final Image returnArrowImage;

    char[][] board;

    // add String filepath to constructor to create board from file
    //********************************************************************************************************************
    public BoardCreator(String filepath) {

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

        pacmanRightImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("img/pacmanRight.png"))).getImage();

        powerPelletImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("img/powerPellet.png"))).getImage();

        leftPortalImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("img/leftPortal.png"))).getImage();
        rightPortalImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("img/rightPortal.png"))).getImage();

        returnArrowImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("img/returnArrow.png"))).getImage();

        eraserImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("img/eraser.png"))).getImage();

        if (filepath != null) {
            File file = new File(filepath);
            board = BoardLoader.loadBoard(file);

            for (int r = 0; r < rowCount; r++) {
                for (int c = 0; c < columnCount; c++) {
                    char tileMapChar = board[r][c];

                    if (tileMapChar == '\\') {
                        board[r][c] = '\\';
                    } else if (tileMapChar == '/') {
                        board[r][c] = '/';
                    } else if (tileMapChar == '<') {
                        board[r][c] = '<';
                    } else if (tileMapChar == '>') {
                        board[r][c] = '>';
                    } else if (tileMapChar == '#') {
                        board[r][c] = '#';
                    } else if (tileMapChar == '|') {
                    } else if (tileMapChar == '-') {
                    } else if (tileMapChar == '.') {
                        board[r][c] = '.';
                    } else if (tileMapChar == 'P') {
                        board[r][c] = 'P';
                    } else if (tileMapChar == 'r') {
                        board[r][c] = 'r';
                    } else if (tileMapChar == 'b') {
                        board[r][c] = 'b';
                    } else if (tileMapChar == 'p') {
                        board[r][c] = 'p';
                    } else if (tileMapChar == 'o') {
                        board[r][c] = 'o';
                    } else if (tileMapChar == '1') {
                        board[r][c] = '1';
                    } else if (tileMapChar == '2') {
                        board[r][c] = '2';
                    } else if (tileMapChar == ',') {
                        board[r][c] = ',';
                    }
                }
            }
        } else {
            createBoard();
        }

        this.frame = App.frame;
        frame.addMouseListener(this);

        setPreferredSize(new Dimension(boardWidth + 32, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {

        g.setColor(Color.WHITE);
        for (int x = 0; x < columnCount; x++) {
            for (int y = 0; y < rowCount; y++) {
                g.drawRect(x * tileSize, y * tileSize, tileSize, tileSize);
            }
        }
        int xPos = tileSize * 28 + 2;

        g.drawImage(wallImage, xPos, 2, tileSize - 4, tileSize - 4, null);
        g.drawImage(topLeftCornerImage, xPos, tileSize + 2, tileSize - 4, tileSize - 4, null);
        g.drawImage(topRightCornerImage, xPos, tileSize * 2 + 2, tileSize - 4, tileSize - 4, null);
        g.drawImage(bottomLeftCornerImage, xPos, tileSize * 3 + 2, tileSize - 4, tileSize - 4, null);
        g.drawImage(bottomRightCornerImage, xPos, tileSize * 4 + 2, tileSize - 4, tileSize - 4, null);

        g.drawImage(blueGhostImage, xPos, tileSize * 5 + 2, tileSize - 4, tileSize - 4, null);
        g.drawImage(orangeGhostImage, xPos, tileSize * 6 + 2, tileSize - 4, tileSize - 4, null);
        g.drawImage(pinkGhostImage, xPos, tileSize * 7 + 2, tileSize - 4, tileSize - 4, null);
        g.drawImage(redGhostImage, xPos, tileSize * 8 + 2, tileSize - 4, tileSize - 4, null);

        g.drawImage(pacmanRightImage, xPos, tileSize * 9 + 2, tileSize - 4, tileSize - 4, null);
        g.drawImage(powerPelletImage, xPos + 2, tileSize * 10 + 4, tileSize - 8, tileSize - 8, null);
        g.fillRect(xPos + 10, tileSize * 11 + 12, tileSize - 24, tileSize - 24);

        g.drawImage(leftPortalImage, xPos, tileSize * 12 + 2, tileSize - 4, tileSize - 4, null);
        g.drawImage(rightPortalImage, xPos, tileSize * 13 + 2, tileSize - 4, tileSize - 4, null);

        g.drawImage(eraserImage, xPos, tileSize * 19 + 2, tileSize - 4, tileSize - 4, null);

        g.drawImage(returnArrowImage, xPos, tileSize * 35 + 2, tileSize - 4, tileSize - 4, null);

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                char tileMapChar = board[r][c];

                int x = c * tileSize;
                int y = r * tileSize;

                if (tileMapChar == '\\') {
                    g.drawImage(bottomLeftCornerImage, x, y, tileSize, tileSize, null);
                } else if (tileMapChar == '/') {
                    g.drawImage(bottomRightCornerImage, x, y, tileSize, tileSize, null);
                } else if (tileMapChar == '<') {
                    g.drawImage(topLeftCornerImage, x, y, tileSize, tileSize, null);
                } else if (tileMapChar == '>') {
                    g.drawImage(topRightCornerImage, x, y, tileSize, tileSize, null);
                } else if (tileMapChar == '#') {
                    g.drawImage(wallImage, x, y, tileSize, tileSize, null);
                } else if (tileMapChar == '|') {
                } else if (tileMapChar == '-') {
                } else if (tileMapChar == '.') {
                    g.setColor(Color.WHITE);
                    g.fillRect(x + 14, y + 14, tileSize - 28, tileSize - 28);
                } else if (tileMapChar == 'P') {
                    g.drawImage(pacmanRightImage, x, y, tileSize, tileSize, null);
                } else if (tileMapChar == 'r') {
                    g.drawImage(redGhostImage, x, y, tileSize, tileSize, null);
                } else if (tileMapChar == 'b') {
                    g.drawImage(blueGhostImage, x, y, tileSize, tileSize, null);
                } else if (tileMapChar == 'p') {
                    g.drawImage(pinkGhostImage, x, y, tileSize, tileSize, null);
                } else if (tileMapChar == 'o') {
                    g.drawImage(orangeGhostImage, x, y, tileSize, tileSize, null);
                } else if (tileMapChar == '1') {
                    g.drawImage(leftPortalImage, x, y, tileSize, tileSize, null);
                } else if (tileMapChar == '2') {
                    g.drawImage(rightPortalImage, x, y, tileSize, tileSize, null);
                } else if (tileMapChar == ',') {
                    g.drawImage(powerPelletImage, x + 3, y + 3, tileSize - 6, tileSize - 6, null);
                }
            }
        }
    }

    private void toolSelect(char toolSelection) {
        this.tool = toolSelection;
    }

    private void createBoard() {
        char[][] board = new char[rowCount][columnCount];
        for (int x = 0; x < columnCount; x++) {
            for (int y = 0; y < rowCount; y++) {
                board[y][x] = ' ';
            }
        }
        this.board = board;
    }

    private void addToBoard(int x, int y, char tool) {
        board[y][x] = tool;
        System.out.println("tool: " + tool);
        BoardWriter.writeBoard(board);
    }

    private void drawTile(int x, int y) {

        if (this.tool == ' ') {
            return;
        }
        if (this.tool == '#') {
            Graphics g = getGraphics();
            clearSquare(x, y);
            g.drawImage(wallImage, x * tileSize, y * tileSize, tileSize, tileSize, null);
            addToBoard(x, y, '#');
        } else if (this.tool == '<') {
            Graphics g = getGraphics();
            clearSquare(x, y);
            g.drawImage(topLeftCornerImage, x * tileSize, y * tileSize, tileSize, tileSize, null);
            addToBoard(x, y, '<');

        } else if (this.tool == '>') {
            Graphics g = getGraphics();
            clearSquare(x, y);
            g.drawImage(topRightCornerImage, x * tileSize, y * tileSize, tileSize, tileSize, null);
            addToBoard(x, y, '>');

        } else if (this.tool == '\\') {
            Graphics g = getGraphics();
            clearSquare(x, y);
            g.drawImage(bottomLeftCornerImage, x * tileSize, y * tileSize, tileSize, tileSize, null);
            addToBoard(x, y, '\\');

        } else if (this.tool == '/') {
            Graphics g = getGraphics();
            clearSquare(x, y);
            g.drawImage(bottomRightCornerImage, x * tileSize, y * tileSize, tileSize, tileSize, null);
            addToBoard(x, y, '/');

        } else if (this.tool == 'b') {
            Graphics g = getGraphics();
            clearSquare(x, y);
            g.drawImage(blueGhostImage, x * tileSize, y * tileSize, tileSize, tileSize, null);
            addToBoard(x, y, 'b');

        } else if (this.tool == 'o') {
            Graphics g = getGraphics();
            clearSquare(x, y);
            g.drawImage(orangeGhostImage, x * tileSize, y * tileSize, tileSize, tileSize, null);
            addToBoard(x, y, 'o');

        } else if (this.tool == 'p') {
            Graphics g = getGraphics();
            clearSquare(x, y);
            g.drawImage(pinkGhostImage, x * tileSize, y * tileSize, tileSize, tileSize, null);
            addToBoard(x, y, 'p');

        } else if (this.tool == 'r') {
            Graphics g = getGraphics();
            clearSquare(x, y);
            g.drawImage(redGhostImage, x * tileSize, y * tileSize, tileSize, tileSize, null);
            addToBoard(x, y, 'r');

        } else if (this.tool == 'P') {
            Graphics g = getGraphics();
            if (!containsChar(board, 'P')) {
                clearSquare(x, y);
                g.drawImage(pacmanRightImage, x * tileSize, y * tileSize, tileSize, tileSize, null);
                addToBoard(x, y, 'P');
            }
        } else if (this.tool == ',') {
            Graphics g = getGraphics();
            clearSquare(x, y);
            g.drawImage(powerPelletImage, x * tileSize + 3, y * tileSize + 3, tileSize - 6, tileSize - 6, null);
            addToBoard(x, y, ',');
        } else if (this.tool == '.') {
            Graphics g = getGraphics();
            clearSquare(x, y);
            g.setColor(Color.WHITE);
            g.fillRect(x * tileSize + 14, y * tileSize + 14, tileSize - 28, tileSize - 28);
            addToBoard(x, y, '.');
        } else if (this.tool == 'L') {
            Graphics g = getGraphics();
            if (!containsChar(board, '1')) {
                clearSquare(x, y);
                g.drawImage(leftPortalImage, x * tileSize, y * tileSize, tileSize, tileSize, null);
                addToBoard(x, y, '1');
            }
        } else if (this.tool == 'R') {
            Graphics g = getGraphics();
            if (!containsChar(board, '2')) {
                clearSquare(x, y);
                g.drawImage(rightPortalImage, x * tileSize, y * tileSize, tileSize, tileSize, null);
                addToBoard(x, y, '2');
            }
        }


        else if (this.tool == 'E') {
            clearSquare(x, y);
            addToBoard(x, y, ' ');
        }
    }

    private void clearSquare(int x, int y) {
        Graphics g = getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
        g.setColor(Color.WHITE);
        g.drawRect(x * tileSize, y * tileSize, tileSize, tileSize);
    }

    private boolean containsChar(char[][] board, char compare) {
        return Arrays.stream(board)
                .flatMapToInt(row -> new String(row).chars())
                .anyMatch(ch -> ch == compare);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }


    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX() / tileSize;
        int y = (e.getY() / tileSize) - 1;
        int centerX = x * tileSize + tileSize / 2;
        int centerY = y * tileSize + tileSize / 2;
        int radius = tileSize / 2;

        if (Math.pow(e.getX() - centerX, 2) + Math.pow((e.getY() - 32) - centerY, 2) <= Math.pow(radius, 2)) {
            System.out.println("x: " + x + ", y: " + y);
            if (x == 28 && y == 0) {
                System.out.println("Solid wall clicked");
                toolSelect('#');
            } else if (x == 28 && y == 1) {
                System.out.println("Top left corner clicked");
                toolSelect('<');
            } else if (x == 28 && y == 2) {
                System.out.println("Top right corner clicked");
                toolSelect('>');
            } else if (x == 28 && y == 3) {
                System.out.println("Bottom left corner clicked");
                toolSelect('\\');
            } else if (x == 28 && y == 4) {
                System.out.println("Bottom right corner clicked");
                toolSelect('/');
            } else if (x == 28 && y == 5) {
                System.out.println("Blue ghost clicked");
                toolSelect('b');
            } else if (x == 28 && y == 6) {
                System.out.println("Orange ghost clicked");
                toolSelect('o');
            } else if (x == 28 && y == 7) {
                System.out.println("Pink ghost clicked");
                toolSelect('p');
            } else if (x == 28 && y == 8) {
                System.out.println("Red ghost clicked");
                toolSelect('r');
            } else if (x == 28 && y == 9) {
                System.out.println("Pacman clicked");
                toolSelect('P');
            } else if (x == 28 && y == 10) {
                System.out.println("Power pellet clicked");
                toolSelect(',');
            } else if (x == 28 && y == 11) {
                System.out.println("Food tile clicked");
                toolSelect('.');
            } else if (x == 28 && y == 12) {
                System.out.println("Left portal clicked");
                toolSelect('L');
            } else if (x == 28 && y == 13) {
                System.out.println("Right portal clicked");
                toolSelect('R');
            } else if (x == 28 && y == 19) {
                System.out.println("Eraser clicked");
                toolSelect('E');
            } else if (x == 28 && y == 35) {
                System.out.println("Return arrow clicked");
                frame.remove(this);
                frame.add(App.startScreen);
                frame.revalidate();
                frame.setSize(boardWidth + 16, boardHeight + 16);
                frame.repaint();
            }
            else if (x != 28) {
                drawTile(x, y);
            }
        }
    }
    @Override
    public void keyPressed(KeyEvent e) {
    }
    @Override
    public void mouseClicked(MouseEvent e) {
    }
    @Override
    public void mouseEntered(MouseEvent e) {
    }
    @Override
    public void mouseExited(MouseEvent e) {
    }
    @Override
    public void keyTyped(KeyEvent e) {
    }
    @Override
    public void keyReleased(KeyEvent e) {
    }
    @Override
    public void mouseReleased(MouseEvent e) {
    }
}

