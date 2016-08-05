package scrabblegamepkg.server;

import com.BoxOfC.MDAG.MDAG;
import scrabblegamepkg.client.BoardPanel;

import java.util.ArrayList;

public class Board {

    //TODO: burde bare være én grid, med en type Square-object som holder på char, multiplier
    boolean transposed = false;
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

    //TODO: skjønner ikke at boardPanel trengs her?
    void transposeBoard(BoardPanel boardPanel) {

        transposed = !transposed;

        Square[][] tempSquareGrid = new Square[15][15];
        char[][] tempCharBoard = new char[15][15];
        String[][] tempCrossChecks = new String[15][15];
        boolean[][] tempIsAnchor = new boolean[15][15];
        //copy
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                tempCharBoard[i][j] = charBoard[i][j];
                tempCrossChecks[i][j] = crossChecks[i][j];
                tempIsAnchor[i][j] = isAnchor[i][j];
                tempSquareGrid[i][j] = boardPanel.squareGrid[i][j];
            }
        }
        //"transpose"
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                charBoard[i][j] = tempCharBoard[j][i];
                crossChecks[i][j] = tempCrossChecks[j][i];
                isAnchor[i][j] = tempIsAnchor[j][i];
                boardPanel.squareGrid[i][j] = tempSquareGrid[j][i];
            }
        }
    }

    //TODO: mye i denne klasse hører kanskje ikke hjemme her, men i egen klasse som spiller for CPU...
    //denne kan gjøres raskere, nå sjekker jeg alle felter
    void doCrossChecks(MDAG dictionary, String alphaString) {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (charBoard[i][j] == '-') {
                    int tilesOver = 0;
                    int tilesUnder = 0;
                    while(i - tilesOver != 0 && charBoard[i-(tilesOver+1)][j] != '-') {
                        tilesOver++;
                    }
                    while(i + tilesUnder != 14 && charBoard[i+(tilesUnder+1)][j] != '-') {
                        tilesUnder++;
                    }
                    if (tilesOver != 0 || tilesUnder != 0) {
                        crossChecks[i][j] = "";
                        String lettersOver = "";
                        String lettersUnder = "";
                        for (int k = tilesOver; k > 0; k--) {
                            lettersOver += charBoard[i-k][j];
                        }
                        for (int k = 1; k <= tilesUnder; k++) {
                            lettersUnder += charBoard[i+k][j];
                        }
                        //sjekker alle bokstaver i alfabetet
                        for (int k = 0; k < 29; k++) {
                            if (dictionary.contains(lettersOver + alphaString.charAt(k) + lettersUnder)) {
                                crossChecks[i][j] += alphaString.charAt(k);
                            }
                        }
                    }
                }
            }
        }
    }

    void updateAnchors() {
        if (transposed) {
            System.out.println("Trying to update Anchors, but 'transposed' is true. Swapped?");
        }

        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                isAnchor[i][j] = false;
                if (charBoard[i][j] == '-') {
                    if (i != 0 && charBoard[i-1][j] != '-') {
                        isAnchor[i][j] = true;
                    } else if (j != 0 && charBoard[i][j-1] != '-') {
                        isAnchor[i][j] = true;
                    } else if (i != 14 && charBoard[i+1][j] != '-') {
                        isAnchor[i][j] = true;
                    } else if (j != 14 && charBoard[i][j+1] != '-') {
                        isAnchor[i][j] = true;
                    }
                }
            }
        }
    }


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
}
