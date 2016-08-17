/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package scrabblegamepkg.server;

import com.BoxOfC.MDAG.MDAG;
import com.BoxOfC.MDAG.MDAGNode;
import scrabblegamepkg.client.BoardPanel;
import scrabblegamepkg.client.RackPanel;
import scrabblegamepkg.client.ScrabbleGameFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Kim
 */
public class ScrabbleGame {

    public ScrabbleGame() throws IOException {

        scrabbleGameFrame = new ScrabbleGameFrame(this);

        (dictionaryCreator = new DictionaryCreator()).execute();
        (playerNameCreator = new PlayerNameCreator()).execute();
    }

    public void playAction() throws Exception {
        Move move = getMoveFromBoard();
        if (checkWords(move)) {
            updatePlayerScore(move.moveScore);
            if (move.usedFromRack.length() == 7) {
                updatePlayerNotes("*" + move.word, move.moveScore);
            } else {
                updatePlayerNotes(move.word, move.moveScore);

                //sjekker om player hadde bingo p� h�nda
                if (!possibleBingos.isEmpty() || !impossibleBingos.isEmpty()) {
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
                }
            }

            updateRemaining(move.usedFromRack);

            game.getPlayer().getRack().removeTiles(scrabbleGameFrame.boardPanel.getSquaresWithMovableTiles().stream().map(square -> square.tile).collect(Collectors.toCollection(ArrayList::new)));
            //trekke nye brikker
            game.getPlayer().getRack().addTiles(game.getBag().pickTiles(move.usedFromRack.length()));

            //TODO: generell programflyt -> rendering skal ikke styres fra server, men fra returverdier til klienten (gjelder flere steder)
            scrabbleGameFrame.renderRack(game.getPlayer().getRack());

            //brikker har blitt lagt, oppdaterer charBoard

            game.getBoard().addToCharBoard(move);
            scrabbleGameFrame.boardPanel.lockTiles();

            game.getPlayer().addTurn(new Turn(Action.MOVE, move));

            if (game.getPlayer().getRack().isEmpty()) {
                finishGame();
            } else {
                computerMove();
            }
        } else {
            //turen avsluttes
            if (retryIfWordIsNotValid) {
            } else {
                game.getPlayer().addTurn(new Turn(Action.DISALLOWED));
                scrabbleGameFrame.rackPanel.putBack(scrabbleGameFrame.boardPanel.getSquaresWithMovableTiles());
                updatePlayerNotes("(ikke godkjent)", 0);

                computerMove();
            }
        }

        for (Tile tile : game.getPlayer().getRack().getTiles()) {
            if (!tile.isMovable) {
                throw new Exception("Rack: not movable: " + tile.letter);
            }
        }

        scrabbleGameFrame.bagCountLabel.setText("Brikker igjen i posen: " + game.getBag().tileCount());
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

    private Move getMoveFromBoard() throws Exception {
        //TODO: dette b�r v�re representert annerledes senere, fra klient til server
        ArrayList<Square> squares = scrabbleGameFrame.boardPanel.getSquaresWithMovableTiles();
        boolean hasAnchor = squares.stream().filter(square -> game.getBoard().getAnchors(game.getBoard().charBoard)[square.row][square.column]).count() > 0;

        long rowCount = squares.stream().map(square -> square.row).distinct().count();
        long columnCount = squares.stream().map(square -> square.column).distinct().count();

        if (squares.isEmpty()) {
            throw new Exception("Ugyldig legg (ingen brikker lagt");
        }
        if (!hasAnchor) {
            throw new Exception("Ugyldig legg (ingen anchor)");
        }
        if (rowCount > 1 && columnCount > 1) {
            throw new Exception("Ugyldig legg (ikke p� �n rekke)");
        }
        //TODO: m� ogs� sjekke at legget ikke har hull
        Square firstSquare = squares.get(0);
        boolean transposed = rowCount > 1;
        int row = transposed ? firstSquare.column : firstSquare.row;
        int startColumn = transposed ? firstSquare.row : firstSquare.column;
        String lettersUsed = "";
        String remainingTiles = game.getPlayer().getRack().toString();
        for (Square square : squares) {
            lettersUsed += square.tile.letter;
            remainingTiles = StringUtil.removeChar(remainingTiles, square.tile.letter);
        }
        return new Move(row, startColumn, transposed, lettersUsed, (transposed ? game.getBoard().getTransposedCharBoard() : game.getBoard().charBoard), remainingTiles);
    }

    private class PlayerNameCreator extends SwingWorker<Void, Void> {

        public PlayerNameCreator() {
        }

        @Override
        protected Void doInBackground() throws IOException {
            String p = JOptionPane.showInputDialog(null, "Hei, hva heter du?");
            if (p != null) {
                playerName = p;
            }
            return null;
        }

        @Override
        protected void done() {
            //while(!dictionaryIsCreated) {
            //  System.out.println("STUCK!");
            //}
            nameGiven = true;
            if (dictionaryIsCreated) {
                (newGame = new NewGame()).execute();
            }
        }
    }

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
            dictionaryIsCreated = true;
            if (nameGiven) {
                (newGame = new NewGame()).execute();
            }
        }
    }

    private class NewGame extends SwingWorker<Void, Void> {

        public NewGame() {
        }

        @Override
        protected Void doInBackground() {
            scrabbleGameFrame.enableButtons(false);
            initGame();
            return null;
        }

        @Override
        protected void done() {
            scrabbleGameFrame.enableButtons(true);
        }
    }


    void computerMove() {
        game.computersTurn = true;
        JScrollBar verticalScrollBar = scrabbleGameFrame.firstPlayerScrollPane.getVerticalScrollBar();
        verticalScrollBar.setValue(verticalScrollBar.getMaximum());
        verticalScrollBar = scrabbleGameFrame.secondPlayerScrollPane.getVerticalScrollBar();
        verticalScrollBar.setValue(verticalScrollBar.getMaximum());
        new CPUThinker(this).execute();
    }

    public void challengeAction() {

        System.out.println("challenge funker ikke n� - bruk Turns n�r dette er implementert");

        /*
        boolean wordRemoved = false;
        for (String s : cpuLastWord.getWords()) {
            Object[] options = {"Ja", "Nei"};
            int n = JOptionPane.showOptionDialog(scrabbleGameFrame,
                    "Fjerne " + s + " fra ordlisten?",
                    "Message",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,     //do not use a custom Icon
                    options,  //the titles of buttons
                    options[1]); //default button title
            if (n == 0) { //skal fjernes
                //fjerner ordet fra dictionary
                //TEST
                System.out.println("kommer til fjerning");
                wordRemoved = true;
                try {
                    PrintWriter ikkeGodkjent = new PrintWriter(new BufferedWriter(new FileWriter("ikkeGodkjent.txt", true)));
                    ikkeGodkjent.println(s);
                    ikkeGodkjent.close();
                    dictionary.removeString(s);
                } catch (IOException ex) {
                    System.out.println("fjerne ord fra dictionary - exception!");
                    Logger.getLogger(ScrabbleGame.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        if (wordRemoved) {

            //TEST
            System.out.println("kommer til wordRemoved");

            //legger tilbake i posen
            for (int i = 0; i < cpuNewlyPicked.length(); i++) {
                game.getBag().add(new Tile(cpuNewlyPicked.charAt(i)));
            }
            //omgj�r rack til forrige rack
            rackString =    previousRackString;
            //fjerner fra brett og oppdaterer charBoard
            for (int i = 0; i < 15; i++) {
                for (int j = 0; j < 15; j++) {
                    if (game.getBoard().charBoardBeforeLastMove[i][j] != game.getBoard().charBoard[i][j]) {
                        game.getBoard().charBoard[i][j] = game.getBoard().charBoardBeforeLastMove[i][j];
                        scrabbleGameFrame.boardPanel.squareGrid[i][j].tile = null;
                        scrabbleGameFrame.boardPanel.squareGrid[i][j].setIcon(null);
                    }
                }
            }
            //fjerner fra computerScore
            game.getComputer().removeScore(previousCPUMoveScore);
            //oppdaterer gjenv�rende brikker
            tilesLeft = previousTilesLeft;
            scrabbleGameFrame.remainingLabel.setText(tilesLeft);
            //oppdaterer cpuNotes
            cpuNotes = previousCPUNotes;
            JLabel noteLabel = scrabbleGameFrame.firstPlayerLabel;
            JScrollPane scrollPane = scrabbleGameFrame.firstPlayerScrollPane;
            if (game.playerIsFirst) {
                noteLabel = scrabbleGameFrame.secondPlayerLabel;
                scrollPane = scrabbleGameFrame.secondPlayerScrollPane;
            }
            noteLabel.setText("<html><body>" + cpuNotes + "<b>" + game.getComputer().getScore() + "</b></body></html>");
            JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
            verticalScrollBar.setValue(verticalScrollBar.getMaximum());
            computerMove();
        }
        */
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
                updatePlayerNotes("(bytte)", 0);
                computerMove();
            }
        }
    }

    public void newGameAction() {
        Object[] options = {"Ja", "Nei"};
        int n = JOptionPane.showOptionDialog(scrabbleGameFrame,
                "Vil du avslutte dette spillet og starte et nytt?",
                "Message",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,     //do not use a custom Icon
                options,  //the titles of buttons
                options[1]); //default button title
        if (n == 0) { //skal fjernes
            (newGame = new NewGame()).execute();
        }
    }

    //TODO: denne m� ut i tipscalculator
    void calculateTips() {
        String playerRack = game.getPlayer().getRack().toString();
        bestTipScore = 0;
        tipsWords.clear();

        //bruker nye metoden for � finne ord
        MoveFinder moveFinder = new MoveFinder();
        ArrayList<Move> allMoves = moveFinder.findAllMoves(dictionary, game.getBoard(), playerRack);

        for (int i = 0; i < allMoves.size(); i++) {
            tipsWords.put(allMoves.get(i).moveScore + (i * 0.0000001), allMoves.get(i));
        }

        if (!tipsWords.isEmpty()) {
            bestTipScore = tipsWords.firstEntry().getValue().moveScore;
        }

        possibleBingos.clear();
        //TODO: impossible settes ikke lenger, lag metode som sender in rack.toString og returener liste over bingoer
        impossibleBingos.clear();
        for (Map.Entry<Double, Move> entry : tipsWords.entrySet()) {
            Move poss = entry.getValue();
            if (poss.usedFromRack.length() == 7) {
                if (!possibleBingos.contains(poss.word)) {
                    possibleBingos.add(poss.word);
                }
            }
        }

    }

    public void tipsAction() {
        int count = 0;
        String tipsString = "<html><body>";
        ArrayList<String> tipsGiven = new ArrayList<>();
        if (tipsWords.isEmpty()) {
            tipsString += "Det finnes ingen mulige legg";
        } else {
            tipsString += "<b><u>H�yest scorende legg:</u></b>";
        }
        for (Map.Entry<Double, Move> entry : tipsWords.entrySet()) {
            Move poss = entry.getValue();

            if (count < 5) {
                if (!tipsGiven.contains(poss.word)) {
                    tipsString += ("<br>" + poss.moveScore + ", " + poss.word);
                    count++;
                    tipsGiven.add(poss.word);
                }
            }
        }
        tipsString += "</body></html>";
        JOptionPane.showMessageDialog(null, tipsString);
    }

    //TODO: fjerne computer-boolean, men n� har jeg i hvert fall fjernet global variabel
    public void pass(boolean computersTurn) {
        if (computersTurn) {
            updateCPUNotes("(pass)", 0);
            addedToThisMove.clear();
        } else {
            //legger evt brikker tilbake p� racken
            scrabbleGameFrame.rackPanel.putBack(newlyAddedToBoard);
            game.getPlayer().getRack().alphabetize();

            game.getPlayer().addTurn(new Turn(Action.PASS));
            updatePlayerNotes("(pass)", 0);
            //fjerner fra listen over nylig lagt til brikker
            addedToThisMove.clear();
            computerMove();
            playerPassed = true;
        }
    }

    boolean checkWord(String word) {
        if (!dictionary.contains(word.toUpperCase())) {
            Object[] options = {"OK", "Legg til ord"};
            int n = JOptionPane.showOptionDialog(scrabbleGameFrame,
                    word + " st�r ikke i ordlisten",
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

    void updatePlayerNotes(String word, int score) {
        JLabel noteLabel = scrabbleGameFrame.firstPlayerLabel;
        if (!game.playerIsFirst) {
            noteLabel = scrabbleGameFrame.secondPlayerLabel;
        }
        playerNotes += word + " ";
        if (score != 0) {
            playerNotes += score;
            if (score == bestTipScore) {
                playerNotes += "!";
            }
            Move poss = tipsWords.firstEntry().getValue();
            if (score < bestTipScore) {
                String message = "<html><body><u><b>Du kunne lagt:</u></b><br>";
                message += (poss.moveScore + ", " + poss.word);
                JOptionPane.showMessageDialog(null, message);
            } else if (score > bestTipScore && !newWordAdded) {
                String message = ("BUG - h�yeste CPU fant var: " +
                        poss.moveScore + ", " + poss.word);
                JOptionPane.showMessageDialog(null, message);
                playerNotes += "!";
            }
        }
        playerNotes += "<br>";
        noteLabel.setText("<html><body>" + playerNotes + "<b>" + game.getPlayer().getScore() + "</b></body></html>");
    }

    void updateCPUNotes(String word, int score) {
        previousCPUNotes = cpuNotes;
        JLabel noteLabel = scrabbleGameFrame.firstPlayerLabel;
        if (game.playerIsFirst) {
            noteLabel = scrabbleGameFrame.secondPlayerLabel;
        }
        cpuNotes += word + " ";
        if (score != 0) {
            cpuNotes += score;
        }
        cpuNotes += "<br>";
        noteLabel.setText("<html><body>" + cpuNotes + "<b>" + game.getComputer().getScore() + "</b></body></html>");
    }

    void updatePlayerScore(int moveScore) {
        game.getPlayer().addScore(moveScore);
    }

    void updateRemaining(String toRemoveString) {
        previousTilesLeft = tilesLeft;
        for (int i = 0; i < toRemoveString.length(); i++) {
            char c = toRemoveString.charAt(i);
            if (Character.isLowerCase(c)) {
                tilesLeft = tilesLeft.substring(0, tilesLeft.indexOf('[')) + '-' + tilesLeft.substring(tilesLeft.indexOf('[') + 2);
            } else {
                tilesLeft = tilesLeft.substring(0, tilesLeft.indexOf(c)) + '-' + tilesLeft.substring(tilesLeft.indexOf(c) + 1);
            }
        }
        scrabbleGameFrame.remainingLabel.setText(tilesLeft);
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
        if (cpuMinus < 0) {
            updateCPUNotes("(" + cpuTiles + ")", cpuMinus);
        }

        //trekker fra score for players rack
        int playerRackScore = game.getPlayer().getRack().rackScore();
        String playerTiles = game.getPlayer().getRack().toString();

        if (playerRackScore > 0) {
            game.getPlayer().removeScore(playerRackScore);
            updatePlayerNotes("(" + playerTiles + ")", -playerRackScore);
        }

        //legger til score til den andre spilleren
        if (cpuTiles.compareTo("") == 0) {
            game.getComputer().addScore(Math.abs(playerRackScore));
            updateCPUNotes("(" + playerTiles + ")", Math.abs(playerRackScore));
        }
        if (playerTiles.compareTo("") == 0) {
            game.getPlayer().addScore(Math.abs(cpuMinus));
            updatePlayerNotes("(" + cpuTiles + ")", Math.abs(cpuMinus));
        }

        //if CPU wins
        if (game.getComputer().getScore() > game.getPlayer().getScore()) {
            JOptionPane.showMessageDialog(null, "CPU vant!");
        } else if (game.getComputer().getScore() == game.getPlayer().getScore()) { //draw
            JOptionPane.showMessageDialog(null, "Kampen endte uavgjort");
        } else { //player won
            JOptionPane.showMessageDialog(null, "DU VANT!");
        }

        scrabbleGameFrame.enableButtons(false);
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

    void initGame() {
        scrabbleGameFrame.boardPanel.cleanUp();

        //for � skrive ut gjenv�rende brikker        
        tilesLeft = "<html><body>AAAAAAA EEEEEEEEE<br>" +
                "IIIII OOOO UUU<br>" +
                "Y � �� ��<br>" +
                "BBB C DDDDD FFFF<br>" +
                "GGGG HHH JJ KKKK<br>" +
                "LLLLL MMM NNNNNN<br>" +
                "PP RRRRRR SSSSSS<br>" +
                "TTTTTT VVV W<br>" +
                "[][]</body></html>";
        scrabbleGameFrame.remainingLabel.setText(tilesLeft);

        newlyAddedToBoard.clear();
        addedToThisMove.clear();


        Bag bag = new Bag();

        game = new Game(new Board(), bag, playerName, this);

        scrabbleGameFrame.rackPanel.renderRack(game.getPlayer().getRack());

        scrabbleGameFrame.tilesLeftTitleLabel.setText("<html><body><b><u>Gjenv�rende brikker:</u></b></body></html>");
        scrabbleGameFrame.bagCountLabel.setText("Brikker igjen i posen: " + bag.tileCount());
        playerNotes = "<b><u>" + playerName + ":</u></b><br>";
        cpuNotes = "<u><b>CPU:</b></u><br>";
        updatePlayerNotes("", 0);
        updateCPUNotes("", 0);

        if (game.computersTurn) {
            computerMove();
        }
    }

    // My variables

    ScrabbleGameFrame scrabbleGameFrame;
    MDAG dictionary;
    ArrayList<Square> newlyAddedToBoard = new ArrayList<>();

    Game game;

    NewGame newGame;
    DictionaryCreator dictionaryCreator;
    PlayerNameCreator playerNameCreator;


    ArrayList<Square> addedToThisMove = new ArrayList<>();

    Square selectedSquare;
    int previousCPUMoveScore;   // int previousCPUMoveScore;

    boolean playerPassed;
    String playerNotes;
    String cpuNotes;
    String previousCPUNotes;
    String tilesLeft;
    String previousTilesLeft;
    double vowelRatioLeft;
    boolean dictionaryIsCreated = false;
    boolean nameGiven = false;
    int bestTipScore;
    //TODO: fjerne all bruk av disse, heller bruke game.getComputer().getRack().toString();
    String rackString = "";
    String previousRackString;
    String cpuNewlyPicked;
    String rackStringCpy = "";
    boolean newWordAdded = false;

    //Solver variables

    Move cpuLastWord;

    TreeMap<Double, Move> tipsWords = new TreeMap<>(Collections.reverseOrder());

    ArrayList<String> possibleBingos = new ArrayList<>();
    ArrayList<String> impossibleBingos = new ArrayList<>();
    //Innstillinger
    boolean retryIfWordIsNotValid = false;
    String playerName = "player";

}
