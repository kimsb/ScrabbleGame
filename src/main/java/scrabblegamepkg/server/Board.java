package scrabblegamepkg.server;

import com.BoxOfC.MDAG.MDAG;
import scrabblegamepkg.client.BoardPanel;

import java.util.ArrayList;

public class Board {

    boolean transposed = false;
    char[][] charBoard = new char[15][15];
    char[][] charBoardBeforeLastMove = new char[15][15];
    boolean[][] isAnchor = new boolean[15][15];
    String[][] crossChecks = new String[15][15];

    void updateCharBoard(ArrayList<Square> addedToThisMove) {
        for (int i = 0; i < 15; i++) {
            System.arraycopy(charBoard[i], 0, charBoardBeforeLastMove[i], 0, 15);
        }

        for (Square s : addedToThisMove) {
            charBoard[s.row][s.column] = s.tile.letter;
        }
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
}
