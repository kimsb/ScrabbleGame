package scrabblegamepkg.server;

import java.util.ArrayList;

public class PotentialMove {

    String word;
    int row;
    int wordStart;
    boolean vertical;
    int wordScore;
    String usedFromRack;
    String leftOnRack;
    ArrayList<String> words;
    String AIString = "";

    //int score;
    //Square[] wordSquares;
    PotentialMove(String w, int r, int wordEnd, boolean trans, int wScore, String fromRack) {
        vertical = trans;
        word = w;
        row = r;
        int wordLen = word.length();
        wordStart = wordEnd - wordLen + 1;
        wordScore = wScore;
        usedFromRack = fromRack;
    }

}
