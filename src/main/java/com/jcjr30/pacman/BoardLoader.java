package com.jcjr30.pacman;

import java.io.*;

public class BoardLoader {

    public static char[][] loadBoard(String resourcePath) throws IOException {
        //filePath = "board/board.txt";
        InputStream inputStream = BoardLoader.class.getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new IOException("File not found: " + resourcePath);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines()
                    .map(String::toCharArray)
                    .toArray(char[][]::new);
        }
    }

    public static char[][] loadBoard (File file)    {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return reader.lines()
                    .map(String::toCharArray)
                    .toArray(char[][]::new);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
