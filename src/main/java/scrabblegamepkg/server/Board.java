package scrabblegamepkg.server;

public class Board {

    public char[][] charBoard;

    Board() {
        charBoard = getEmptyCharBoard();
    }

    Board(char[][] charBoard) {
        this.charBoard = charBoard;
    }

    public char[][] getCharBoard() {
        return charBoard;
    }

    public char[][] getEmptyCharBoard() {
        char[][] charBoard = new char[15][15];
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                charBoard[i][j] = '-';
            }
        }
        return charBoard;
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
                    if (i != 0 && charBoard[i - 1][j] != '-') {
                        isAnchor[i][j] = true;
                        anchorCount++;
                    } else if (j != 0 && charBoard[i][j - 1] != '-') {
                        isAnchor[i][j] = true;
                        anchorCount++;
                    } else if (i != 14 && charBoard[i + 1][j] != '-') {
                        isAnchor[i][j] = true;
                        anchorCount++;
                    } else if (j != 14 && charBoard[i][j + 1] != '-') {
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

    public boolean hasAdjacent(Tile tile) {
        return (tile.column > 0 && getCharBoard()[tile.row][tile.column-1] != '-')
                || (tile.column < 14 && getCharBoard()[tile.row][tile.column+1] != '-') ;
    }
}
