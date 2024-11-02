package com.jcjr30.pacman;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException, FontFormatException {
        int columnCount = 28;
        int rowCount = 36;
        int tileSize = 32;

        int boardWidth = columnCount * tileSize;
        int boardHeight = rowCount * tileSize;

        JFrame frame = new JFrame("Pac Man");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        PacMan pacManGame = new PacMan();
        frame.add(pacManGame);
        frame.pack();
        pacManGame.requestFocus();
        frame.setVisible(true);
    }
}
