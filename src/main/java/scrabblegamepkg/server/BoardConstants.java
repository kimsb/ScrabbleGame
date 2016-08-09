package scrabblegamepkg.server;

import java.util.Arrays;

public class BoardConstants {

    private static int[][] letterMultipliers;
    static {
        letterMultipliers = new int[15][15];
        for (int[] row : letterMultipliers) {
            Arrays.fill(row, 1);
        }
        letterMultipliers[1][5] = 3;
        letterMultipliers[1][9] = 3;
        letterMultipliers[5][1] = 3;
        letterMultipliers[5][5] = 3;
        letterMultipliers[5][9] = 3;
        letterMultipliers[5][13] = 3;
        letterMultipliers[9][1] = 3;
        letterMultipliers[9][5] = 3;
        letterMultipliers[9][9] = 3;
        letterMultipliers[9][13] = 3;
        letterMultipliers[13][5] = 3;
        letterMultipliers[13][9] = 3;

        letterMultipliers[0][3] = 2;
        letterMultipliers[0][11] = 2;
        letterMultipliers[2][6] = 2;
        letterMultipliers[2][8] = 2;
        letterMultipliers[3][0] = 2;
        letterMultipliers[3][7] = 2;
        letterMultipliers[3][14] = 2;
        letterMultipliers[6][2] = 2;
        letterMultipliers[6][6] = 2;
        letterMultipliers[6][8] = 2;
        letterMultipliers[6][12] = 2;
        letterMultipliers[7][3] = 2;
        letterMultipliers[7][11] = 2;
        letterMultipliers[8][2] = 2;
        letterMultipliers[8][6] = 2;
        letterMultipliers[8][8] = 2;
        letterMultipliers[8][12] = 2;
        letterMultipliers[11][0] = 2;
        letterMultipliers[11][7] = 2;
        letterMultipliers[11][14] = 2;
        letterMultipliers[12][6] = 2;
        letterMultipliers[12][8] = 2;
        letterMultipliers[14][3] = 2;
        letterMultipliers[14][11] = 2;
    }

    private static int[][] wordMultipliers;
    static {
        wordMultipliers = new int[15][15];
        for (int[] row : wordMultipliers) {
            Arrays.fill(row, 1);
        }
        wordMultipliers[0][0] = 3;
        wordMultipliers[0][7] = 3;
        wordMultipliers[0][14] = 3;
        wordMultipliers[7][0] = 3;
        wordMultipliers[7][14] = 3;
        wordMultipliers[14][0] = 3;
        wordMultipliers[14][7] = 3;
        wordMultipliers[14][14] = 3;

        wordMultipliers[1][1] = 2;
        wordMultipliers[2][2] = 2;
        wordMultipliers[3][3] = 2;
        wordMultipliers[4][4] = 2;
        wordMultipliers[10][10] = 2;
        wordMultipliers[11][11] = 2;
        wordMultipliers[12][12] = 2;
        wordMultipliers[13][13] = 2;
        wordMultipliers[1][13] = 2;
        wordMultipliers[2][12] = 2;
        wordMultipliers[3][11] = 2;
        wordMultipliers[4][10] = 2;
        wordMultipliers[10][4] = 2;
        wordMultipliers[11][3] = 2;
        wordMultipliers[12][2] = 2;
        wordMultipliers[13][1] = 2;
        //stjerne
        wordMultipliers[7][7] = 2;
    }

    public static int getLetterMultiplier(int row, int column) {
        return letterMultipliers[row][column];
    }

    public static int getWordMultiplier(int row, int column) {
        return wordMultipliers[row][column];
    }
}
