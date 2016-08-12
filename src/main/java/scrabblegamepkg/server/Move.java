package scrabblegamepkg.server;

import java.util.ArrayList;

public class Move {

    public String word = "";
    public int row;
    public int startColumn;
    public boolean vertical;
    int moveScore;
    String usedFromRack;
    String leftOnRack;
    private ArrayList<String> words = new ArrayList<>();
    String AIString = "";

    Move(int r, int startColumn, boolean trans, String fromRack, char[][] charBoard, String leftOnRack) {
        vertical = trans;
        row = r;
        this.startColumn = startColumn;
        usedFromRack = fromRack;
        this.leftOnRack = leftOnRack;
        moveScore = getMoveScore(charBoard);
    }

    private int getMoveScore(char[][] charBoard) {
        //TODO: trenger ikke i/j - kan bruke row, wordstart direkte - for de endres ikke på
        int i = row;
        int j = startColumn;
        int horizontalMultiplier = 1;
        int horizontalScore = 0;
        int sum = 0;
        int letterFromRack = 0;

        int k = 0;
        while (letterFromRack < usedFromRack.length() || (j+k < 15 && charBoard[i][j+k] != '-')) {
            char letter = charBoard[i][j+k];
            if (letter == '-') {
                letter = usedFromRack.charAt(letterFromRack++);
                int letterScore = ScoreConstants.letterScore(letter) * BoardConstants.getLetterMultiplier(i, j+k);
                horizontalScore += letterScore;
                int wordMultiplier = BoardConstants.getWordMultiplier(i, j+k);
                horizontalMultiplier *= wordMultiplier;
                sum += getVerticalScore(charBoard, i, j+k, letter, letterScore, wordMultiplier);
            } else {
                horizontalScore += ScoreConstants.letterScore(letter);
            }
            word += letter;
            k++;
        }

        return sum + (horizontalScore * horizontalMultiplier) + (isBingo() ? 50 : 0);
    }

    private int getVerticalScore(char[][] charBoard, int i, int j, char c, int letterScore, int wordMultiplier) {
        int sum = letterScore;
        String word = "" + c;

        //upwards
        for (int k = i-1; k >= 0; k--) {
            char letter = charBoard[k][j];
            if (letter == '-') {
                break;
            }
            word = letter + word;
            sum += ScoreConstants.letterScore(letter);
        }
        //downwards
        for (int k = i+1; k < 15; k++) {
            char letter = charBoard[k][j];
            if (letter == '-') {
                break;
            }
            word += letter;
            sum += ScoreConstants.letterScore(letter);
        }
        if (word.length() > 1) {
            words.add(word);
            return sum * wordMultiplier;
        }
        return 0;
    }

    private boolean isBingo() {
        return usedFromRack.length() == 7;
    }

    public ArrayList<String> getWords() {
        return words;
    }
}
