package com.jcjr30.pacman;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.geometry.Insets;

public class StartScreen extends JPanel implements ActionListener, ImageObserver {

    Image bgGif;
    private static Font highScoreFont;
    private static Font titleFont;

    private static final String videoPath = "vid/PacManStartScreenBG.mp4";

    int frame = 0;

    StartScreen() throws IOException, FontFormatException {

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

        JFXPanel fxPanel = new JFXPanel();

        SwingUtilities.invokeLater(() -> {
                    fxPanel.setBounds(0, 0, 896, 1152);
                    layeredPane.add(fxPanel, JLayeredPane.DEFAULT_LAYER);
                });

        Platform.runLater(() -> {

            Media media = new Media(new File(Objects.requireNonNull(getClass().getResource(videoPath)).getFile()).toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            MediaView mediaView = new MediaView(mediaPlayer);

            StackPane root = new StackPane();
            root.getChildren().add(mediaView);

            Scene scene = new Scene(root, 896, 1152);

            fxPanel.setScene(scene);
            mediaPlayer.play();
        });

        JButton startButton = new JButton("Start");

        startButton.setForeground(Color.YELLOW);
        startButton.setFont(titleFont.deriveFont(Font.PLAIN, 30));

        startButton.setBorderPainted(false);
        startButton.setContentAreaFilled(false);

        startButton.setBounds(0, 0, 200, 50);

        startButton.addActionListener(e -> {
            try {
                App.startPacManGame();
            } catch (IOException | FontFormatException ex) {
                throw new RuntimeException(ex);
            }
        });
        layeredPane.add(startButton, JLayeredPane.PALETTE_LAYER);

        JButton createButton = new JButton("Create Board");

        createButton.setForeground(Color.YELLOW);
        createButton.setFont(titleFont.deriveFont(Font.PLAIN, 30));

        createButton.setBorderPainted(false);
        createButton.setContentAreaFilled(false);

        createButton.setBounds(0, 50, 200, 50);

        createButton.addActionListener(e -> App.startBoardCreator());

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
}
