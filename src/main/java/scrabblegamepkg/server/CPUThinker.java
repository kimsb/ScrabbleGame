package scrabblegamepkg.server;

import javax.swing.*;
import java.util.Map;

import static scrabblegamepkg.server.ScrabbleGame.*;

//TODO: her er det mye å ta tak i!
// - skal ikke gjøre frontend-ting
// - selve kalkuleringa av trekk må ut i egen klasse, som også kan brukes ved laging av tips til player1
public class CPUThinker extends SwingWorker<Void, Void> {

    ScrabbleGame scrabbleGame;

    public CPUThinker(ScrabbleGame scrabbleGame) {
        this.scrabbleGame = scrabbleGame;
    }

    @Override
    protected Void doInBackground() {
        scrabbleGame.challengeButton.setEnabled(false);
        scrabbleGame.playButton.setEnabled(false);
        scrabbleGame.swapButton.setEnabled(false);
        scrabbleGame.passButton.setEnabled(false);
        scrabbleGame.newGameButton.setEnabled(false);
        scrabbleGame.tipsButton.setEnabled(false);
        scrabbleGame.computerAI();
        return null;
    }

    @Override
    protected void done() {
        scrabbleGame.challengeButton.setEnabled(true);
        scrabbleGame.playButton.setEnabled(true);
        scrabbleGame.swapButton.setEnabled(true);
        scrabbleGame.passButton.setEnabled(true);
        scrabbleGame.newGameButton.setEnabled(true);
        (scrabbleGame.tipsCalculator = new TipsCalculator(scrabbleGame)).execute();
        JScrollBar verticalScrollBar = scrabbleGame.firstPlayerScrollPane.getVerticalScrollBar();
        verticalScrollBar.setValue(verticalScrollBar.getMaximum());
        verticalScrollBar = scrabbleGame.secondPlayerScrollPane.getVerticalScrollBar();
        verticalScrollBar.setValue(verticalScrollBar.getMaximum());

    }
}
