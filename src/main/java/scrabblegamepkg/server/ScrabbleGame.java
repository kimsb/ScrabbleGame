/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package scrabblegamepkg.server;

import com.BoxOfC.MDAG.MDAG;
import scrabblegamepkg.client.ScrabbleGameFrame;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ScrabbleGame {

    public ScrabbleGame() throws IOException {

        new DictionaryCreator().execute();
        scrabbleGameFrame = new ScrabbleGameFrame(this);
    }

    private void checkForBingos() {
        //sjekker om player hadde bingo på hånda
        //TODO: legge dette inn i turn, og gi beskjeden fra klienten
       /* if (!possibleBingos.isEmpty() || !impossibleBingos.isEmpty()) {
            String bingoMessage = "<html><body>";
            if (!possibleBingos.isEmpty()) {
                bingoMessage += "<b><u>Du kunne lagt bingo:</u></b>";
                for (String s : possibleBingos) {
                    bingoMessage += ("<br>" + s);
                }
            }
            if (!impossibleBingos.isEmpty()) {
                if (!possibleBingos.isEmpty()) {
                    bingoMessage += "<br><br>";
                }
                bingoMessage += "<b><u>Du hadde bingo som ikke kunne legges:</u></b>";
                for (String s : impossibleBingos) {
                    bingoMessage += ("<br>" + s);
                }
            }
            bingoMessage += "</body></html>";
            JOptionPane.showMessageDialog(null, bingoMessage);
        }*/
    }

    public Game playAction(ArrayList<Tile> addedTiles) throws Exception {
        Move move = getMoveFromAddedTiles(addedTiles);
        if (checkWords(move)) {
            if (move.usedFromRack.length() < 7) {
                checkForBingos();
            }

            game.getPlayer().getRack().removeTiles(addedTiles);
            //trekke nye brikker
            game.getPlayer().getRack().addTiles(game.getBag().pickTiles(move.usedFromRack.length()));

            //brikker har blitt lagt, oppdaterer charBoard
            game.getBoard().addToCharBoard(move);

            game.getPlayer().addTurn(new Turn(Action.MOVE, move));

            if (game.getPlayer().getRack().isEmpty()) {
                finishGame();
            } else {
                computerMove();
            }
        } else {
            //turen avsluttes
                game.getPlayer().addTurn(new Turn(Action.DISALLOWED));

                computerMove();
        }

        return game;
    }

    private boolean checkWords(Move move) {
        if (!checkWord(move.word)) {
            return false;
        }
        for (String word : move.getWords()) {
            if (!checkWord(word)) {
                return false;
            }
        }
        return true;
    }

    private Move getMoveFromAddedTiles(ArrayList<Tile> addedTiles) throws Exception {

        boolean hasAnchor = addedTiles.stream().filter(tile -> game.getBoard().getAnchors(game.getBoard().getCharBoard())[tile.row][tile.column]).count() > 0;
        long rowCount = addedTiles.stream().map(tile -> tile.row).distinct().count();
        long columnCount = addedTiles.stream().map(tile -> tile.column).distinct().count();

        if (addedTiles.isEmpty()) {
            throw new Exception("Ugyldig legg (ingen brikker lagt");
        }
        if (!hasAnchor) {
            throw new Exception("Ugyldig legg (ingen anchor)");
        }
        if (rowCount > 1 && columnCount > 1) {
            throw new Exception("Ugyldig legg (ikke på én rekke)");
        }
        //TODO: må også sjekke at legget ikke har hull
        /*if (isHoleBetweenTiles(addedTiles)) {
            throw new Exception("Ugyldig legg (legget har hull)");
        }*/

        //TODO: dette feiler når firste Tile er påbygg av allerede lagte bokstaver, siden row bare er firste tiles row...
        Tile firstTile = addedTiles.get(0);
        boolean transposed = rowCount > 1;
        int row = transposed ? firstTile.column : firstTile.row;
        int startColumn = transposed ? firstTile.row : firstTile.column;
        String lettersUsed = "";
        String remainingTiles = game.getPlayer().getRack().toString();
        for (Tile tile : addedTiles) {
            lettersUsed += tile.letter;
            remainingTiles = StringUtil.removeChar(remainingTiles, tile.letter);
        }
        return new Move(row, startColumn, transposed, lettersUsed, (transposed ? game.getBoard().getTransposedCharBoard() : game.getBoard().getCharBoard()), remainingTiles);
    }

    //TODO: flytte ut i egen klasse
    void createDictionary() throws IOException {
        try {
            File file = new File(this.getClass().getResource("/yeslist.txt").toURI());
            dictionary = new MDAG(file);
            //fjerner ikke godkjente ord fra dictionary
            try {
                Scanner ikkeGodkjent = new Scanner(new File("ikkeGodkjent.txt"));
                while (ikkeGodkjent.hasNext()) {
                    dictionary.removeString(ikkeGodkjent.next());
                }
                ikkeGodkjent.close();
            } catch (FileNotFoundException ex) {
                System.out.println("Finner ikke ikkeGodkjent.txt");
            }
            //legger til godkjente ord til dictionary
            try {
                Scanner godkjent = new Scanner(new File("godkjent.txt"));
                while (godkjent.hasNext()) {
                    dictionary.addString(godkjent.next());
                }
                godkjent.close();
            } catch (FileNotFoundException ex) {
                System.out.println("Finner ikke godkjent.txt");
            }
        } catch (Exception e) {
            System.out.println("Laging av dictionary feiler");
            e.printStackTrace();
        }
    }

    private class DictionaryCreator extends SwingWorker<Void, Void> {

        public DictionaryCreator() {
        }

        @Override
        protected Void doInBackground() throws IOException {
            createDictionary();
            return null;
        }

        @Override
        protected void done() {
            System.out.println("dictionaryDone!");
        }
    }

    void computerMove() {
        game.computersTurn = true;
        new CPUThinker(this).execute();
    }

    public void swapAction() {
        System.out.println("Bytter");

        ArrayList<Square> toSwap = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                Tile t = scrabbleGameFrame.boardPanel.squareGrid[i][j].tile;
                if (t != null && t.isMovable) {
                    toSwap.add(scrabbleGameFrame.boardPanel.squareGrid[i][j]);
                }
            }
        }
        if (!toSwap.isEmpty()) {
            if (game.getBag().tileCount() < 7) {
                JOptionPane.showMessageDialog(null, "Det er ikke nok brikker i posen");
            } else {
                int size = toSwap.size();
                System.out.println("Bytter " + size + " brikker");

                ArrayList<Tile> tilesToSwap = toSwap.stream().map(square -> square.tile).collect(Collectors.toCollection(ArrayList::new));
                ArrayList<Tile> newTiles = game.getBag().pickTiles(toSwap.size());
                tilesToSwap.forEach(tile -> game.getBag().add(tile));

                game.getPlayer().getRack().removeTiles(tilesToSwap);
                game.getPlayer().getRack().addTiles(newTiles);

                toSwap.forEach(Square::cleanUp);
                scrabbleGameFrame.rackPanel.renderRack(game.getPlayer().getRack());

                game.getPlayer().addTurn(new Turn(Action.SWAP));
                computerMove();
            }
        }
    }

    public Game newGameAction() {
        newlyAddedToBoard.clear();
        addedToThisMove.clear();

        Bag bag = new Bag();

        game = new Game(new Board(), bag, this);

        if (game.computersTurn) {
            computerMove();
        }
        return game;
    }

    public TipsCalculator analyzeTipsAction(char[][] charBoard, String rackString) {
        Board board = new Board(charBoard);
        return new TipsCalculator(this).calculateTips(board, rackString);
    }

    public TipsCalculator tipsAction() {
        return new TipsCalculator(this).calculateTips(game.getBoard(), game.getPlayer().getRack().toString());
    }

    //TODO: fjerne computer-boolean, men nå har jeg i hvert fall fjernet global variabel
    public void pass(boolean computersTurn) {
        if (computersTurn) {
            game.getComputer().addTurn(new Turn(Action.PASS));
            addedToThisMove.clear();
        } else {
            //legger evt brikker tilbake på racken
            scrabbleGameFrame.rackPanel.putBack(newlyAddedToBoard);
            game.getPlayer().getRack().alphabetize();

            game.getPlayer().addTurn(new Turn(Action.PASS));
            //fjerner fra listen over nylig lagt til brikker
            addedToThisMove.clear();
            computerMove();
        }
    }

    boolean checkWord(String word) {
        if (!dictionary.contains(word.toUpperCase())) {
            Object[] options = {"OK", "Legg til ord"};
            int n = JOptionPane.showOptionDialog(scrabbleGameFrame,
                    word + " står ikke i ordlisten",
                    "Message",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,     //do not use a custom Icon
                    options,  //the titles of buttons
                    options[0]); //default button title
            if (n == 0) { //ok
                return false;
            } else {
                //legger til ordet i dictionary
                try {
                    PrintWriter godkjent = new PrintWriter(new BufferedWriter(new FileWriter("godkjent.txt", true)));
                    godkjent.println(word);
                    godkjent.close();
                    dictionary.addString(word);
                    newWordAdded = true;
                } catch (IOException ex) {
                    System.out.println("legge til ord i dictionary - exception!");
                    Logger.getLogger(ScrabbleGame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return true;
    }

    void finishGame() {
        //trekker fra score for CPUs rack
        int cpuMinus = 0;
        String cpuTiles = "";
        for (int i = 0; i < rackString.length(); i++) {
            int letterScore = 0;
            if (rackString.charAt(i) != '-') {
                letterScore = ScoreConstants.letterScore(rackString.charAt(i));
            }
            game.getComputer().removeScore(letterScore);
            cpuMinus -= letterScore;
            cpuTiles += rackString.charAt(i);
            System.out.println("Trekker fra CPU " + letterScore + " poeng for '" + rackString.charAt(i) + "'");
        }

        //trekker fra score for players rack
        int playerRackScore = game.getPlayer().getRack().rackScore();
        String playerTiles = game.getPlayer().getRack().toString();

        if (playerRackScore > 0) {
            game.getPlayer().removeScore(playerRackScore);
        }

        //legger til score til den andre spilleren
        if (cpuTiles.compareTo("") == 0) {
            game.getComputer().addScore(Math.abs(playerRackScore));
        }
        if (playerTiles.compareTo("") == 0) {
            game.getPlayer().addScore(Math.abs(cpuMinus));
        }

        //if CPU wins
        if (game.getComputer().getScore() > game.getPlayer().getScore()) {
            JOptionPane.showMessageDialog(null, "CPU vant!");
        } else if (game.getComputer().getScore() == game.getPlayer().getScore()) { //draw
            JOptionPane.showMessageDialog(null, "Kampen endte uavgjort");
        } else { //player won
            JOptionPane.showMessageDialog(null, "DU VANT!");
        }

        scrabbleGameFrame.enableGameButtons(false);
        scrabbleGameFrame.newGameButton.setEnabled(true);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new ScrabbleGame().scrabbleGameFrame.setVisible(true);
            } catch (IOException ex) {
                Logger.getLogger(ScrabbleGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    // My variables

    ScrabbleGameFrame scrabbleGameFrame;
    MDAG dictionary;
    ArrayList<Square> newlyAddedToBoard = new ArrayList<>();

    Game game;

    ArrayList<Square> addedToThisMove = new ArrayList<>();

    Square selectedSquare;

    double vowelRatioLeft;
    //TODO: fjerne all bruk av disse, heller bruke game.getComputer().getRack().toString();
    String rackString = "";
    String previousRackString;
    String cpuNewlyPicked;
    String rackStringCpy = "";
    boolean newWordAdded = false;

}
