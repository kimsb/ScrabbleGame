package scrabblegamepkg.server;

import java.util.ArrayList;

public class Move {

    public String word;
    public int row;
    public int wordStart;
    public boolean vertical;
    int wordScore;
    int moveScore;
    String usedFromRack;
    String leftOnRack;
    ArrayList<String> words = new ArrayList<>();
    String AIString = "";

    Move(String w, int r, int wordEnd, boolean trans, int wScore, String fromRack, Board board, String leftOnRack) {
        vertical = trans;
        word = w;
        row = r;
        int wordLen = word.length();
        wordStart = wordEnd - wordLen + 1;
        usedFromRack = fromRack;
        this.leftOnRack = leftOnRack;
        wordScore = board == null ? wScore : getMoveScore(board);
        moveScore = board == null ? 0 : getMoveScore(board);
    }

    private int getMoveScore(Board board) {
        //trenger ikke transpose multiplier fordi brettet er symmetrisk
        char[][] charBoard = vertical ? board.getTransposedCharBoard() : board.charBoard;
        int i = row;
        int j = wordStart;
        int horizontalMultiplier = 1;
        int horizontalScore = 0;
        int sum = 0;

        for (int k = 0; k < word.length(); k++) {
            char letter = word.charAt(k);
            boolean addedNow = charBoard[i][j+k] == '-';
            if (addedNow) {
                int letterScore = ScoreConstants.letterScore(letter) * BoardConstants.getLetterMultiplier(i, j+k);
                horizontalScore += letterScore;
                int wordMultiplier = BoardConstants.getWordMultiplier(i, j+k);
                horizontalMultiplier *= wordMultiplier;
                sum += getVerticalScore(charBoard, i, j+k, letterScore, wordMultiplier);
            } else {
                horizontalScore += ScoreConstants.letterScore(letter);
            }
        }

        return sum + (horizontalScore * horizontalMultiplier) + (isBingo() ? 50 : 0);
    }

    private int getVerticalScore(char[][] charBoard, int i, int j, int letterScore, int wordMultiplier) {
        int sum = letterScore;
        boolean createsVerticalWord = false;
        //upwards
        for (int k = i-1; k >= 0; k--) {
            char letter = charBoard[k][j];
            if (letter == '-') {
                break;
            }
            createsVerticalWord = true;
            sum += ScoreConstants.letterScore(letter);
        }
        //downwards
        for (int k = i+1; k < 15; k++) {
            char letter = charBoard[k][j];
            if (letter == '-') {
                break;
            }
            createsVerticalWord = true;
            sum += ScoreConstants.letterScore(letter);
        }
        return createsVerticalWord ? sum * wordMultiplier : 0;
    }

    private boolean isBingo() {
        return usedFromRack.length() == 7;
    }

}
