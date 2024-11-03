package com.jcjr30.pacman;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class StartScreen extends JPanel {

    Image bgGif;

    StartScreen() {

        ImageIcon bgIcon = new ImageIcon(getClass().getResource("img/bg.gif"));
        bgGif = bgIcon.getImage();

        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> {
            try {
                App.startPacManGame();
            } catch (IOException | FontFormatException ex) {
                throw new RuntimeException(ex);
            }
        });
        this.add(startButton);

        JButton createButton = new JButton("Create Board");
        createButton.addActionListener(e -> App.startBoardCreator());
        this.add(createButton);

        this.setFocusable(true);
        this.requestFocusInWindow();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, App.boardWidth, App.boardHeight);

        g.drawImage(bgGif, 110, 50, null);
    }

}
