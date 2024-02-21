package org.alex73.fanetyka.config;

import java.util.ArrayList;
import java.util.List;

public class Cell {

    private static final List<String> COL_NAMES;
    static {
        COL_NAMES = new ArrayList<>();
        for (char c1 = 'A'; c1 <= 'Z'; c1++) {
            COL_NAMES.add(Character.toString(c1));
        }
        for (char c2 = 'A'; c2 <= 'Z'; c2++) {
            for (char c1 = 'A'; c1 <= 'Z'; c1++) {
                COL_NAMES.add(Character.toString(c2) + Character.toString(c1));
            }
        }
        for (char c3 = 'A'; c3 <= 'Z'; c3++) {
            for (char c2 = 'A'; c2 <= 'Z'; c2++) {
                for (char c1 = 'A'; c1 <= 'Z'; c1++) {
                    COL_NAMES.add(Character.toString(c3) + Character.toString(c2) + Character.toString(c1));
                }
            }
        }
    }

    private final int row, col;

    /**
     * @param row - 0-based
     * @param col - 0-based
     */
    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public String toString() {
        return COL_NAMES.get(col) + row;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 18278; i++) {
            System.out.println(new Cell(0, i) + " " + i);
        }
    }
}
