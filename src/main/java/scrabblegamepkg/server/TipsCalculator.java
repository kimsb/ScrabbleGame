package scrabblegamepkg.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class TipsCalculator {

    private ScrabbleGame scrabbleGame;
    public ArrayList<String> possibleBingos = new ArrayList<>();
    public ArrayList<String> impossibleBingos = new ArrayList<>();
    public TreeMap<Double, Move> tipsWords = new TreeMap<>(Collections.reverseOrder());

    public TipsCalculator(ScrabbleGame scrabbleGame) {
        this.scrabbleGame = scrabbleGame;
    }

    public TipsCalculator calculateTips(Board board, String rackString) {

        tipsWords.clear();

        //bruker nye metoden for å finne ord
        MoveFinder moveFinder = new MoveFinder();
        ArrayList<Move> allMoves = moveFinder.findAllMoves(scrabbleGame.dictionary, board, rackString);

        for (int i = 0; i < allMoves.size(); i++) {
            tipsWords.put(allMoves.get(i).moveScore + (i * 0.0000001), allMoves.get(i));
        }

        possibleBingos.clear();
        for (Map.Entry<Double, Move> entry : tipsWords.entrySet()) {
            Move poss = entry.getValue();
            if (poss.usedFromRack.length() == 7) {
                if (!possibleBingos.contains(poss.word)) {
                    possibleBingos.add(poss.word);
                }
            }
        }

        impossibleBingos.clear();
        ArrayList<Move> wordsOnRack = moveFinder.findAllMoves(scrabbleGame.dictionary, new Board(), rackString);
        wordsOnRack.forEach(bingo -> {
            if (bingo.word.length() == 7 && !possibleBingos.contains(bingo.word)) {
                impossibleBingos.add(bingo.word);
            }
        });
        return this;
    }
}
