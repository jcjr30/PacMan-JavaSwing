package com.jcjr30.boardCreator;

import java.io.File;
import java.io.FileWriter;

public class BoardWriter {

    public static void writeBoard(char[][] board) {

        File file = new File("data/customBoards/customBoard.txt");
        StringBuilder sb = new StringBuilder();

        for (char[] row : board) {
            for (char cell : row) {
                sb.append(cell);
            }
            sb.append(System.lineSeparator());
        }

        try (FileWriter write = new FileWriter(file)) {
            write.write(sb.toString());
            System.out.println("File saved");
        } catch (Exception e) {
            throw new RuntimeException("Error writing to file");
        }
    }
}
