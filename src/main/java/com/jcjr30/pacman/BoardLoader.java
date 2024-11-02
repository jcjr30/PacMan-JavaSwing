package com.jcjr30.pacman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BoardLoader {

    public static char[][] loadBoard() throws IOException {
        String filePath = "board/board.txt";
        InputStream inputStream = BoardLoader.class.getResourceAsStream(filePath);
        if (inputStream == null) {
            throw new IOException("File not found: " + filePath);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines()
                    .map(String::toCharArray)
                    .toArray(char[][]::new);
        }
    }
}
