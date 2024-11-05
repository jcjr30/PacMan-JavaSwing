package com.jcjr30.pacman;

import com.jcjr30.boardCreator.BoardCreator;

import javax.swing.*;
import java.awt.*;

import java.io.IOException;

public class App {

    public static PacMan pacManGame;
    public static JFrame frame;
    private static BoardCreator boardCreator;
    public static StartScreen startScreen;

    static int columnCount = 28;
    static int rowCount = 36;
    static int tileSize = 32;

    static int boardWidth = columnCount * tileSize;
    static int boardHeight = rowCount * tileSize;

    public static void main(String[] args) throws IOException, FontFormatException {


        startScreen = new StartScreen();

        frame = new JFrame("Pac Man");
        frame.setSize(boardWidth+16, boardHeight+16);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(startScreen);

        startScreen.requestFocus();
        frame.setVisible(true);

    }

    public static void startPacManGame(String boardPath) throws IOException, FontFormatException {
        frame.remove(startScreen);

        pacManGame = new PacMan(boardPath);

        pacManGame.setPacManAi();

        frame.add(pacManGame);
        frame.pack();
        pacManGame.requestFocus();
        frame.setVisible(true);
    }

    public static void startBoardCreator(String boardPath) {
        boardCreator = new BoardCreator(boardPath);

        frame.remove(startScreen);
        frame.add(boardCreator);
        frame.pack();
        boardCreator.requestFocus();
        frame.setVisible(true);
    }
}
