package com.jcjr30.pacman;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class StartScreen extends JPanel implements ActionListener, ImageObserver {

    int tileSize = 32;

    Image bgGif;
    private static Font highScoreFont;
    private static Font titleFont;

    private static String boardPath = null;

    public StartScreen() throws IOException, FontFormatException {

        ImageIcon bgIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("img/bg.gif")));
        bgGif = bgIcon.getImage();

        File highScoreFontFile = new File(Objects.requireNonNull(getClass().getResource("fonts/emulogic.ttf")).getFile());
        File titleFontFile = new File(Objects.requireNonNull(getClass().getResource("fonts/CrackMan.TTF")).getFile());

        highScoreFont = Font.createFont(Font.TRUETYPE_FONT, highScoreFontFile);
        titleFont = Font.createFont(Font.TRUETYPE_FONT, titleFontFile);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 896, 1152);
        layeredPane.setLayout(null);
        this.setLayout(new BorderLayout());
        this.add(layeredPane);


        JButton loadButton = new JButton("Load Map");

        loadButton.setForeground(Color.YELLOW);
        loadButton.setFont(titleFont.deriveFont(Font.PLAIN, 30));
        loadButton.setBorderPainted(false);
        loadButton.setContentAreaFilled(false);
        loadButton.setBounds(tileSize * 20, tileSize, loadButton.getPreferredSize().width, 50);
        loadButton.addActionListener(e -> {chooseFile();});

        layeredPane.add(loadButton, JLayeredPane.PALETTE_LAYER);


        PacMan pacMan = new PacMan(null);
        pacMan.setBounds(0, 0, 896, 1152);
        pacMan.setLayout(null);
        layeredPane.add(pacMan, JLayeredPane.DEFAULT_LAYER);


        JButton startButton = new JButton("Start");

        startButton.setForeground(Color.YELLOW);
        startButton.setFont(titleFont.deriveFont(Font.PLAIN, 30));
        startButton.setBorderPainted(false);
        startButton.setContentAreaFilled(false);
        startButton.setBounds(tileSize * 4, tileSize, startButton.getPreferredSize().width, 50);
        startButton.addActionListener(e -> {
            try {
                pacMan.gameLoop.stop();
                App.startPacManGame(boardPath);
            } catch (IOException | FontFormatException ex) {
                throw new RuntimeException(ex);
            }
        });
        layeredPane.add(startButton, JLayeredPane.PALETTE_LAYER);


        JButton createButton = new JButton("Create Map");

        createButton.setForeground(Color.YELLOW);
        createButton.setFont(titleFont.deriveFont(Font.PLAIN, 30));
        createButton.setBorderPainted(false);
        createButton.setContentAreaFilled(false);
        createButton.setBounds(tileSize * 12, tileSize, createButton.getPreferredSize().width, 50);
        createButton.addActionListener(e -> App.startBoardCreator(boardPath));

        layeredPane.add(createButton, JLayeredPane.PALETTE_LAYER);


        this.setFocusable(true);
        this.requestFocusInWindow();

        Timer loop = new Timer(100, this);
        loop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, App.boardWidth, App.boardHeight);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    public static Font getHighScoreFont() {
        return highScoreFont;
    }

    private static void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            boardPath = file.getAbsolutePath();
        }
    }
}

