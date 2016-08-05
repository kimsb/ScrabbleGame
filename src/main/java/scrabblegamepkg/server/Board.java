package scrabblegamepkg.server;

import com.BoxOfC.MDAG.MDAG;
import scrabblegamepkg.client.BoardPanel;

import javax.swing.*;
import java.util.ArrayList;

public class Board {

    //TODO: burde bare være én grid, med en type Square-object som holder på char, multiplier
    char[][] charBoard = new char[15][15];
    char[][] charBoardBeforeLastMove = new char[15][15];
    boolean[][] isAnchor = new boolean[15][15];
    String[][] crossChecks = new String[15][15];
    String [][] multipliers;

    Board() {
        multipliers = setMultipliers();
    }

    void updateCharBoard(ArrayList<Square> addedToThisMove) {
        for (int i = 0; i < 15; i++) {
            System.arraycopy(charBoard[i], 0, charBoardBeforeLastMove[i], 0, 15);
        }

        for (Square s : addedToThisMove) {
            charBoard[s.row][s.column] = s.tile.letter;
        }
    }

    protected char[][] getTransposedCharBoard() {

        char[][] transposedCharBoard = new char[15][15];

        //"transpose"
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                transposedCharBoard[i][j] = charBoard[j][i];

            }
        }
        return transposedCharBoard;
    }


    //TODO: legge dette ut i egen klasse (BoardConstants eller noe sånt)
    private String[][] setMultipliers() {

        String[][] multipliers = new String[15][15];

        multipliers[0][0] = "TW";
        multipliers[0][7] = "TW";
        multipliers[0][14] = "TW";
        multipliers[7][0] = "TW";
        multipliers[7][14] = "TW";
        multipliers[14][0] = "TW";
        multipliers[14][7] = "TW";
        multipliers[14][14] = "TW";

        multipliers[1][1] = "DW";
        multipliers[2][2] = "DW";
        multipliers[3][3] = "DW";
        multipliers[4][4] = "DW";
        multipliers[10][10] = "DW";
        multipliers[11][11] = "DW";
        multipliers[12][12] = "DW";
        multipliers[13][13] = "DW";
        multipliers[1][13] = "DW";
        multipliers[2][12] = "DW";
        multipliers[3][11] = "DW";
        multipliers[4][10] = "DW";
        multipliers[10][4] = "DW";
        multipliers[11][3] = "DW";
        multipliers[12][2] = "DW";
        multipliers[13][1] = "DW";
        //stjerne
        multipliers[7][7] = "DW";

        multipliers[1][5] = "TL";
        multipliers[1][9] = "TL";
        multipliers[5][1] = "TL";
        multipliers[5][5] = "TL";
        multipliers[5][9] = "TL";
        multipliers[5][13] = "TL";
        multipliers[9][1] = "TL";
        multipliers[9][5] = "TL";
        multipliers[9][9] = "TL";
        multipliers[9][13] = "TL";
        multipliers[13][5] = "TL";
        multipliers[13][9] = "TL";

        multipliers[0][3] = "DL";
        multipliers[0][11] = "DL";
        multipliers[2][6] = "DL";
        multipliers[2][8] = "DL";
        multipliers[3][0] = "DL";
        multipliers[3][7] = "DL";
        multipliers[3][14] = "DL";
        multipliers[6][2] = "DL";
        multipliers[6][6] = "DL";
        multipliers[6][8] = "DL";
        multipliers[6][12] = "DL";
        multipliers[7][3] = "DL";
        multipliers[7][11] = "DL";
        multipliers[8][2] = "DL";
        multipliers[8][6] = "DL";
        multipliers[8][8] = "DL";
        multipliers[8][12] = "DL";
        multipliers[11][0] = "DL";
        multipliers[11][7] = "DL";
        multipliers[11][14] = "DL";
        multipliers[12][6] = "DL";
        multipliers[12][8] = "DL";
        multipliers[14][3] = "DL";
        multipliers[14][11] = "DL";

        return multipliers;
    }

    public int getLetterMultiplier(int row, int column) {
        String multiplier = multipliers[row][column];
        if (multiplier == null) {
            return 1;
        }
        switch (multiplier) {
            case "DL": return 2;
            case "TL": return 3;
            default: return 1;
        }
    }

    public int getWordMultiplier(int row, int column) {
        String multiplier = multipliers[row][column];
        if (multiplier == null) {
            return 1;
        }
        switch (multiplier) {
            case "DW": return 2;
            case "TW": return 3;
            default: return 1;
        }
    }

    public void addToCharBoard(Move move) {
        for (int i = 0; i < move.word.length(); i++) {
            if (move.vertical) {
                if (charBoard[move.wordStart + i][move.row] == '-') {
                    charBoard[move.wordStart + i][move.row] = move.word.charAt(i);
                }
            } else {
                if (charBoard[move.row][move.wordStart + i] == '-') {
                    charBoard[move.row][move.wordStart + i] = move.word.charAt(i);
                }
            }
        }
    }
}
