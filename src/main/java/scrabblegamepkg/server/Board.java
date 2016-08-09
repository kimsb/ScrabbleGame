package scrabblegamepkg.server;

import java.util.ArrayList;

public class Board {

    //TODO: burde bare være én grid, med en type Square-object som holder på char, multiplier
    char[][] charBoard = new char[15][15];
    char[][] charBoardBeforeLastMove = new char[15][15];
    boolean[][] isAnchor = new boolean[15][15];
    String[][] crossChecks = new String[15][15];

    Board() {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                //fyller charBoard med '-'
                charBoard[i][j] = '-';
                //fyller crossChecks med alle bokstaver
                crossChecks[i][j] = StringUtil.alphaString();
                //"tømmer" isAnchor
                isAnchor[i][j] = false;
            }
        }
        isAnchor[7][7] = true;
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
