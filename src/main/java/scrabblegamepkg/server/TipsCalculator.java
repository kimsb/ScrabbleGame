package scrabblegamepkg.server;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Map;

public class TipsCalculator extends SwingWorker<Void, Void> {

    private ScrabbleGame scrabbleGame;

    public TipsCalculator(ScrabbleGame scrabbleGame) {
        this.scrabbleGame = scrabbleGame;
    }

    @Override
    protected Void doInBackground() {
        calculateTips();
        return null;
    }

    @Override
    protected void done() {
        scrabbleGame.scrabbleGameFrame.tipsButton.setEnabled(true);
    }

    void calculateTips() {

        String playerRack = scrabbleGame.game.getPlayer().getRack().toString();
        scrabbleGame.tipsWords.clear();

        //bruker nye metoden for å finne ord
        MoveFinder moveFinder = new MoveFinder();
        ArrayList<Move> allMoves = moveFinder.findAllMoves(scrabbleGame.dictionary, scrabbleGame.game.getBoard(), playerRack);

        for (int i = 0; i < allMoves.size(); i++) {
            scrabbleGame.tipsWords.put(allMoves.get(i).moveScore + (i * 0.0000001), allMoves.get(i));
        }

        scrabbleGame.possibleBingos.clear();
        for (Map.Entry<Double, Move> entry : scrabbleGame.tipsWords.entrySet()) {
            Move poss = entry.getValue();
            if (poss.usedFromRack.length() == 7) {
                if (!scrabbleGame.possibleBingos.contains(poss.word)) {
                    scrabbleGame.possibleBingos.add(poss.word);
                }
            }
        }

        scrabbleGame.impossibleBingos.clear();
        ArrayList<Move> wordsOnRack = moveFinder.findAllMoves(scrabbleGame.dictionary, new Board(), playerRack);
        wordsOnRack.forEach(bingo -> {
            if (bingo.word.length() == 7 && !scrabbleGame.possibleBingos.contains(bingo.word)) {
                scrabbleGame.impossibleBingos.add(bingo.word);
            }
        });
    }
}
