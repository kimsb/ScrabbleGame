package scrabblegamepkg.server;

import javax.swing.*;

public class TipsCalculator extends SwingWorker<Void, Void> {

    private ScrabbleGame scrabbleGame;

    public TipsCalculator(ScrabbleGame scrabbleGame) {
        this.scrabbleGame = scrabbleGame;
    }

    @Override
    protected Void doInBackground() {
        scrabbleGame.calculateTips();
        return null;
    }

    @Override
    protected void done() {
        scrabbleGame.scrabbleGameFrame.tipsButton.setEnabled(true);
    }

}
