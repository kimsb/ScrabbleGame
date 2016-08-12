package scrabblegamepkg.server;

import java.util.ArrayList;

public class Board {

    //TODO: burde bare være én grid, med en type Square-object som holder på char, multiplier
    char[][] charBoard = new char[15][15];
    char[][] charBoardBeforeLastMove = new char[15][15];

    Board() {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                //fyller charBoard med '-'
                charBoard[i][j] = '-';
            }
        }
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

    public void addToCharBoard(Move move) {
        for (int i = 0; i < move.word.length(); i++) {
            if (move.vertical) {
                if (charBoard[move.startColumn + i][move.row] == '-') {
                    charBoard[move.startColumn + i][move.row] = move.word.charAt(i);
                }
            } else {
                if (charBoard[move.row][move.startColumn + i] == '-') {
                    charBoard[move.row][move.startColumn + i] = move.word.charAt(i);
                }
            }
        }
    }

    public boolean[][] getAnchors(char[][] charBoard) {
        boolean[][] isAnchor = new boolean[15][15];
        int anchorCount = 0;

        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (charBoard[i][j] == '-') {
                    if (i != 0 && charBoard[i-1][j] != '-') {
                        isAnchor[i][j] = true;
                        anchorCount++;
                    } else if (j != 0 && charBoard[i][j-1] != '-') {
                        isAnchor[i][j] = true;
                        anchorCount++;
                    } else if (i != 14 && charBoard[i+1][j] != '-') {
                        isAnchor[i][j] = true;
                        anchorCount++;
                    } else if (j != 14 && charBoard[i][j+1] != '-') {
                        isAnchor[i][j] = true;
                        anchorCount++;
                    }
                }
            }
        }

        if (anchorCount == 0) {
            isAnchor[7][7] = true;
        }

        return isAnchor;
    }
}
