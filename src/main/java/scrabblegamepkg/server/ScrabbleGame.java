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

/**
 *
 * @author Kim
 */
public class ScrabbleGame {

    public ScrabbleGame() throws IOException {

        scrabbleGameFrame = new ScrabbleGameFrame(this);

        (dictionaryCreator = new DictionaryCreator()).execute();
        (playerNameCreator = new PlayerNameCreator()).execute();
    }

    public void playAction() {
        if (computersTurn) {
            return;
        }
        System.out.println("Player kaller play med " + rack.toString() + " på racket.");
        //må sjekke om ordet er lovlig plassert
        if (moveIsAllowed()) {
            //må sjekke om ordet/ordene er gyldig
            //hvis ikke, bør turen avsluttes
            if (wordsAreAllowed()) {

                System.out.println("PLAYERRACK: " + rack.toString());

                //regne ut score (av alle ord)
                int moveScore = 0;
                String newWord = "<MISTAKE>";
                for (Square[] sA : newWordsAdded) {
                    moveScore += scoreSingleWord(sA);
                    String word = "";
                    int tileCount = 0;
                    for (Square s : sA) {
                        word += s.tile.letter;
                        if (s.tile.isMovable) {
                            tileCount++;
                        }
                    }
                    if (tileCount == addedToThisMove.size()) {
                        newWord = word;
                    }
                }
                updatePlayerScore(moveScore);
                if (addedToThisMove.size() == 7) {
                    updatePlayerNotes("*" + newWord, moveScore);
                } else {
                    updatePlayerNotes(newWord, moveScore);
                    //sjekker om player hadde bingo på hånda
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

                String toRemoveFromRemaining = "";
                //fjerne fra newlyAdded
                //låser brikkene til brettet
                for (Square s : addedToThisMove) {
                    s.tile.isMovable = false;
                    if (s.tile.isBlank()) {
                        toRemoveFromRemaining += '-';
                        rack.removeTile('-');
                    } else {
                        toRemoveFromRemaining += s.tile.letter;
                        rack.removeTile(s.tile.letter);
                    }
                }
                updateRemaining(toRemoveFromRemaining);

                //trekke nye brikker
                rack.addTiles(bag.pickTiles(addedToThisMove.size()));
                System.out.println("Playerrack etter å ha plukket brikker: " + rack.toString());

                //TODO: generell programflyt -> rendering skal ikke styres fra server, men fra returverdier til klienten (gjelder flere steder)
                scrabbleGameFrame.renderRack(rack);

                //brikker har blitt lagt, oppdaterer charBoard
                board.updateCharBoard(addedToThisMove);
                //fjerner fra listen over nylig lagt til brikker
                addedToThisMove.clear();
                newWordsAdded.clear();
                System.out.println("kommer hit 11");
                if (rack.isEmpty()) {
                    System.out.println("kommer hit 22");
                    finishGame();
                } else {
                    System.out.println("kommer hit 33");
                    computersTurn = true;
                    computerMove();
                }
            } else {
                //turen avsluttes
                if (retryIfWordIsNotValid) {
                    newWordsAdded.clear();
                } else {
                    scrabbleGameFrame.rackPanel.putBack(addedToThisMove);
                    updatePlayerNotes("(ikke godkjent)", 0);
                    //fjerner fra listen over nylig lagt til brikker
                    addedToThisMove.clear();
                    newWordsAdded.clear();
                    computersTurn = true;
                    computerMove();
                }
            }
        } else { //hvis ikke lovlig plassert
            JOptionPane.showMessageDialog(null, "Ikke godkjent plassering");
            addedToThisMove.clear();
            newWordsAdded.clear();
        }
        scrabbleGameFrame.bagCountLabel.setText("Brikker igjen i posen: " + bag.tileCount());
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
        JScrollBar verticalScrollBar = scrabbleGameFrame.firstPlayerScrollPane.getVerticalScrollBar();
        verticalScrollBar.setValue(verticalScrollBar.getMaximum());
        verticalScrollBar = scrabbleGameFrame.secondPlayerScrollPane.getVerticalScrollBar();
        verticalScrollBar.setValue(verticalScrollBar.getMaximum());
        new CPUThinker(this).execute();
    }

    public void challengeAction() {
        boolean wordRemoved = false;
        for (String s : cpuLastWord.words) {
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
                bag.add(new Tile(cpuNewlyPicked.charAt(i)));
            }
            //omgjør rack til forrige rack
            rackString = previousRackString;
            //fjerner fra brett og oppdaterer charBoard
            for (int i = 0; i < 15; i++) {
                for (int j = 0; j < 15; j++) {
                    if (board.charBoardBeforeLastMove[i][j] != board.charBoard[i][j]) {
                        board.charBoard[i][j] = board.charBoardBeforeLastMove[i][j];
                        scrabbleGameFrame.boardPanel.squareGrid[i][j].tile = null;
                        scrabbleGameFrame.boardPanel.squareGrid[i][j].setIcon(null);
                    }
                }
            }
            //fjerner fra computerScore
            computerScore -= previousCPUMoveScore;
            //oppdaterer gjenværende brikker
            tilesLeft = previousTilesLeft;
            scrabbleGameFrame.remainingLabel.setText(tilesLeft);
            //oppdaterer cpuNotes
            cpuNotes = previousCPUNotes;
            JLabel noteLabel = scrabbleGameFrame.firstPlayerLabel;
            JScrollPane scrollPane = scrabbleGameFrame.firstPlayerScrollPane;
            if (playerIsFirst) {
                noteLabel = scrabbleGameFrame.secondPlayerLabel;
                scrollPane = scrabbleGameFrame.secondPlayerScrollPane;
            }
            noteLabel.setText("<html><body>" + cpuNotes + "<b>" + computerScore + "</b></body></html>");
            JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
            verticalScrollBar.setValue(verticalScrollBar.getMaximum());
            computersTurn = true;
            firstMove = true;
            for (int i = 0; i < 15; i++) {
                for (int j = 0; j < 15; j++) {
                    if (board.charBoard[i][j] != '-') {
                        firstMove = false;
                        break;
                    }
                }
            }
            computerMove();
        }

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
            if (bag.tileCount() < 7) {
                JOptionPane.showMessageDialog(null, "Det er ikke nok brikker i posen");
            } else {
                int size = toSwap.size();
                System.out.println("Bytter " + size + " brikker");

                ArrayList<Tile> tilesToSwap = toSwap.stream().map(square -> square.tile).collect(Collectors.toCollection(ArrayList::new));
                ArrayList<Tile> newTiles = bag.pickTiles(toSwap.size());
                tilesToSwap.forEach(tile -> bag.add(tile));

                rack.removeTiles(tilesToSwap);
                rack.addTiles(newTiles);

                toSwap.forEach(Square::cleanUp);
                scrabbleGameFrame.rackPanel.renderRack(rack);

                updatePlayerNotes("(bytte)", 0);
                computersTurn = true;
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

    //TODO: denne må ut i tipscalculator
    void calculateTips() {
        String playerRack = rack.toString();
        bestTipScore = 0;
        tipsWords.clear();

        //bruker nye metoden for å finne ord
        MoveFinder moveFinder = new MoveFinder();
        ArrayList<Move> allMoves = moveFinder.findAllMoves(dictionary, board, playerRack);

        for (int i = 0; i < allMoves.size(); i++) {
            tipsWords.put(allMoves.get(i).moveScore + (i * 0.0000001), allMoves.get(i));
        }

        if (!tipsWords.isEmpty()) {
            bestTipScore = tipsWords.firstEntry().getValue().wordScore;
        }
        
        possibleBingos.clear();
        //TODO: impossible settes ikke lenger, lag metode som sender in rack.toString og returener liste over bingoer
        impossibleBingos.clear();
        for (Map.Entry<Double,Move> entry : tipsWords.entrySet()) {
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
            tipsString += "<b><u>Høyest scorende legg:</u></b>";
        }
        for (Map.Entry<Double,Move> entry : tipsWords.entrySet()) {
            Move poss = entry.getValue();

            if (count < 5) {
                if (!tipsGiven.contains(poss.word)) {
                    tipsString += ("<br>" + poss.wordScore + ", " + poss.word);
                    count++;
                    tipsGiven.add(poss.word);
                }
            }
        }
        tipsString += "</body></html>";
        JOptionPane.showMessageDialog(null, tipsString);
    }

    public void pass() {
        if (computersTurn) {
            computersTurn = false;
            updateCPUNotes("(pass)", 0);
            addedToThisMove.clear();
            newWordsAdded.clear();
        } else {
            System.out.println("kommer hit - pass 1");
            //legger evt brikker tilbake på racken

            scrabbleGameFrame.rackPanel.putBack(newlyAddedToBoard);
            rack.alphabetize();

            updatePlayerNotes("(pass)", 0);
            //fjerner fra listen over nylig lagt til brikker
            addedToThisMove.clear();
            newWordsAdded.clear();
            computersTurn = true;
            computerMove();
            playerPassed = true;
        }
    }
    


    //TODO: her må man jo heller bare slå opp ordene...
    //avslutter med en gang et ord som ikke er godkjent kommer
    boolean wordsAreAllowed() {
        newWordAdded = false;
        for (Square[] sA : newWordsAdded) {
            String word = "";
            for (Square s : sA) {
                word += s.tile.letter;
            }
            if (! dictionary.contains(word)) {
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
        }
        return true;
    }
    
    void updatePlayerNotes(String word, int score) {
        JLabel noteLabel = scrabbleGameFrame.firstPlayerLabel;
        if (!playerIsFirst) {
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
                message += (poss.wordScore + ", " + poss.word);
                JOptionPane.showMessageDialog(null, message);
            } else if (score > bestTipScore && !newWordAdded) {
                String message = ("BUG - høyeste CPU fant var: " +
                        poss.wordScore + ", " + poss.word);
                JOptionPane.showMessageDialog(null, message);
                playerNotes += "!";
            }
        }
        playerNotes += "<br>";
        noteLabel.setText("<html><body>" + playerNotes + "<b>" + playerScore + "</b></body></html>");
        if (score == 0) {
            if (++pointlessTurns == 6) {
                JOptionPane.showMessageDialog(null, "<html><body>Det har gått 6 runder uten poeng,<br>kampen avsluttes</body></html>");
                finishGame();
            }
        } else {
            pointlessTurns = 0;
            if (firstMove) {
                firstMove = false;
                scrabbleGameFrame.challengeButton.setEnabled(true);
            }
        }
    }

    void updateCPUNotes(String word, int score) {
        previousCPUNotes = cpuNotes;
        JLabel noteLabel = scrabbleGameFrame.firstPlayerLabel;
        if (playerIsFirst) {
            noteLabel = scrabbleGameFrame.secondPlayerLabel;
        }
        cpuNotes += word + " ";
        if (score != 0) {
            cpuNotes += score;
        }
        cpuNotes += "<br>";
        noteLabel.setText("<html><body>" + cpuNotes + "<b>" + computerScore + "</b></body></html>");

        if (score == 0) {
            if (++pointlessTurns == 6) {
                JOptionPane.showMessageDialog(null, "<html><body>Det har gått 6 runder uten poeng,<br>kampen avsluttes</body></html>");
                finishGame();
            }
        } else {
            pointlessTurns = 0;
            if (firstMove) {
                firstMove = false;
                scrabbleGameFrame.challengeButton.setEnabled(true);
            }
        }
    }
    
    void updatePlayerScore(int moveScore) {
        playerScore += moveScore;
    }

    void updateRemaining(String toRemoveString) {
        previousTilesLeft = tilesLeft;
        for (int i = 0; i < toRemoveString.length(); i++) {
            char c = toRemoveString.charAt(i);
            if (c == '-') {
                tilesLeft = tilesLeft.substring(0,tilesLeft.indexOf('[')) + '-' + tilesLeft.substring(tilesLeft.indexOf('[')+2);
            } else {
                tilesLeft = tilesLeft.substring(0,tilesLeft.indexOf(c)) + '-' + tilesLeft.substring(tilesLeft.indexOf(c)+1);
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
            computerScore -= letterScore;
            cpuMinus -= letterScore;
            cpuTiles += rackString.charAt(i);
            System.out.println("Trekker fra CPU " + letterScore + " poeng for '" + rackString.charAt(i) + "'");
        }
        if (cpuMinus < 0) {
            updateCPUNotes("(" + cpuTiles + ")", cpuMinus);
        }
        
        //trekker fra score for players rack
        int playerRackScore = rack.rackScore();
        String playerTiles = rack.toString();

        if (playerRackScore > 0) {
            playerScore -= playerRackScore;
            updatePlayerNotes("(" + playerTiles + ")", -playerRackScore);
        }
        
        //legger til score til den andre spilleren
        if (cpuTiles.compareTo("") == 0) {
            computerScore += Math.abs(playerRackScore);
            updateCPUNotes("(" + playerTiles + ")", Math.abs(playerRackScore));
        }
        if (playerTiles.compareTo("") == 0) {
            playerScore += Math.abs(cpuMinus);
            updatePlayerNotes("(" + cpuTiles + ")", Math.abs(cpuMinus));
        }        
        
        //if CPU wins
        if (computerScore > playerScore) {
            JOptionPane.showMessageDialog(null, "CPU vant!");
        } else if (computerScore == playerScore) { //draw
            JOptionPane.showMessageDialog(null, "Kampen endte uavgjort");
        } else { //player won
            JOptionPane.showMessageDialog(null, "DU VANT!");
        }

        scrabbleGameFrame.enableButtons(false);
        scrabbleGameFrame.newGameButton.setEnabled(true);
    }
    
    boolean moveIsAllowed() {
        int rowStart = 0;
        int columnStart = 0;
        int rowEnd = 0;
        int columnEnd = 0;
        boolean firstNewTile = true;
            
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                Tile t = scrabbleGameFrame.boardPanel.squareGrid[i][j].tile;
                //leter etter første nye brikke
                if (t != null && t.isMovable) {
                    addedToThisMove.add(scrabbleGameFrame.boardPanel.squareGrid[i][j]);
                    if (firstNewTile) {
                        rowStart = i;
                        rowEnd = i;
                        columnStart = j;
                        columnEnd = j;
                        firstNewTile = false;
                        boolean gapExists = false;
                        //sjekker videre på samme rad
                        for (int k = i+1; k < 15; k++) {
                            Tile nextTile = scrabbleGameFrame.boardPanel.squareGrid[k][j].tile;
                            if (nextTile != null) {
                                if (nextTile.isMovable) {
                                    if (gapExists) {
                                        System.out.println("now allowed 1");
                                        return false;
                                    } else {
                                        rowEnd = k;
                                    }
                                }
                            } else {
                                gapExists = true;
                            }
                        }
                        //sjekker videre på samme kolonne
                        gapExists = false;
                        for (int k = j+1; k < 15; k++) {
                           Tile nextTile = scrabbleGameFrame.boardPanel.squareGrid[i][k].tile;
                           if (nextTile != null) {
                               if (nextTile.isMovable) {
                                   if (gapExists) {
                                       System.out.println("not allowed 2");
                                       return false;
                                   } else {
                                       columnEnd = k;
                                   }
                                }
                            } else {
                                gapExists = true;
                            }
                        }
                        
                        if (rowEnd != rowStart && columnEnd != columnStart) {
                            System.out.println("not allowed 3");
                            return false;
                        }
                            
                        //sjekker resten av brettet
                        } else if (i != rowStart && j != columnStart) {
                            System.out.println("not allowed 4");
                            return false;
                        }
                    }
                }
            }

            int wordStart;
            int wordEnd;
            //finds all new horizontal words  
            for (int i = rowStart; i <= rowEnd; i++) {
                wordStart = columnStart;
                wordEnd = columnEnd;
                while(wordStart > 0 && scrabbleGameFrame.boardPanel.squareGrid[i][wordStart - 1].tile != null) {
                    wordStart--;
                }
                while(wordEnd < 14 && scrabbleGameFrame.boardPanel.squareGrid[i][wordEnd + 1].tile != null) {
                    wordEnd++;
                }
                    
                //WE HAVE A WORD! at boardPanel.squareGrid[i][wordStart --> wordEnd] if (start != end)
                if (wordStart != wordEnd) {
                    Square[] newWord = new Square[(wordEnd - wordStart) + 1];
                    int index = 0;
                    for (int j = wordStart; j <= wordEnd; j++) {
                        newWord[index++] = scrabbleGameFrame.boardPanel.squareGrid[i][j];
                    }
                    //burde kunne løses på annen måte, men sjekker om ord har ny bokstav
                    for (Square s : newWord) {
                        if (s.tile.isMovable) {
                            newWordsAdded.add(newWord);
                            
                            //TEST
                            System.out.println("adds new Horizontal word");
                         break;   
                        }
                    }

                }

            }
            //finds all new vertical words
            for (int j = columnStart; j <= columnEnd; j++) {
                wordStart = rowStart;
                wordEnd = rowEnd;
                while(wordStart > 0 && scrabbleGameFrame.boardPanel.squareGrid[wordStart - 1][j].tile != null) {
                    wordStart--;
                }
                while(wordEnd < 14 && scrabbleGameFrame.boardPanel.squareGrid[wordEnd + 1][j].tile != null) {
                    wordEnd++;
                }
                    
                //WE HAVE A WORD! at boardPanel.squareGrid[wordStart --> wordEnd][j] (if start != end)
                if (wordStart != wordEnd) {
                    Square[] newWord = new Square[(wordEnd - wordStart) + 1];
                    int index = 0;
                    for (int i = wordStart; i <= wordEnd; i++) {
                        newWord[index++] = scrabbleGameFrame.boardPanel.squareGrid[i][j];
                    }
                    //burde kunne løses på annen måte, men sjekker om ord har ny bokstav
                    for (Square s : newWord) {
                        if (s.tile.isMovable) {                              
                            newWordsAdded.add(newWord);
                            //TEST
                            System.out.println("adds new vertical word");
                            break;
                        }
                    }

                }
            }
            if (newWordsAdded.size() == 0) {
                return false;
            }
            if (newWordsAdded.size() < 2) {
                if (firstMove) {
                    if (scrabbleGameFrame.boardPanel.squareGrid[7][7].tile == null) {
                        System.out.println("not allowed 5");
                        return false;
                    } else if (addedToThisMove.size() == 1) {
                        System.out.println("not allowed 6");
                        return false;
                    }                     
                } else if (! (newWordsAdded.get(0).length > addedToThisMove.size())) {
                    System.out.println("not allowed 7");
                    return false;
                }
            } 

        return true;
    }
    
    int scoreSingleWord(Square[] word) {
        
        //TEST
        System.out.print("playerScore kalles med word: ");
        for (Square s : word) {
            System.out.print(s.tile.letter);
        }
        System.out.println("");
        //TEST SLUTT
                
        
        int multiplier = 1;
        int tempSum = 0;
        int newTiles = 0;
        for (Square s : word) {
            if (s.tile.isMovable) {
                newTiles++;
            }

            int letterMultiplier = s.tile.isMovable ? s.letterMultiplier() : 1;
            tempSum += s.tile.value * letterMultiplier;

            multiplier *= s.tile.isMovable ? s.wordMultiplier() : 1;

        }
        tempSum *= multiplier;
        if (newTiles == 7) { //bingo!
            tempSum += 50;
        }           
        return tempSum;
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
       board = new Board();
       scrabbleGameFrame.boardPanel.cleanUp();

        rackString = "";
        rackStringCpy = "";

        playerScore = 0;
        computerScore = 0;

        //for å skrive ut gjenværende brikker        
        tilesLeft = "<html><body>AAAAAAA EEEEEEEEE<br>" + 
                "IIIII OOOO UUU<br>" + 
                "Y Æ ØØ ÅÅ<br>" + 
                "BBB C DDDDD FFFF<br>" +
                "GGGG HHH JJ KKKK<br>" + 
                "LLLLL MMM NNNNNN<br>" +
                "PP RRRRRR SSSSSS<br>" +
                "TTTTTT VVV W<br>" +
                "[][]</body></html>";
        scrabbleGameFrame.remainingLabel.setText(tilesLeft);
        
        newlyAddedToBoard.clear();
        addedToThisMove.clear();
        newWordsAdded.clear();
        
        bag = new Bag();


         //hvis computer starter
         if(new Random().nextInt(2) == 0) {
             
             //TEST
             System.out.println("cpu Starter");
             computersTurn = true;
             for (int i = 0; i < 7; i++) {
                 Tile t = bag.pickTile();
                 rackString += t.letter;
             }
             rack = new Rack(bag.pickTiles(7));
         } else { //hvis pl1 starter
            computersTurn = false;
            rack = new Rack(bag.pickTiles(7));
            for (int i = 0; i < 7; i++) {
                 Tile t = bag.pickTile();
                 rackString += t.letter;
            }
        }

        scrabbleGameFrame.rackPanel.renderRack(rack);

         scrabbleGameFrame.tilesLeftTitleLabel.setText("<html><body><b><u>Gjenværende brikker:</u></b></body></html>");
         firstMove = true;
         scrabbleGameFrame.bagCountLabel.setText("Brikker igjen i posen: " + bag.tileCount());
         playerIsFirst = !computersTurn;
         playerNotes = "<b><u>" + playerName + ":</u></b><br>";
         cpuNotes = "<u><b>CPU:</b></u><br>";
         updatePlayerNotes("", 0);
         updateCPUNotes("", 0);
         if (computersTurn) {
            computerMove();
         } else {
            new TipsCalculator(this).execute();
         }
         pointlessTurns = 0;         
   }

    // My variables

    ScrabbleGameFrame scrabbleGameFrame;
    MDAG dictionary;
    Bag bag = new Bag();
    ArrayList<Square> newlyAddedToBoard = new ArrayList<>();

    NewGame newGame;
    DictionaryCreator dictionaryCreator;
    PlayerNameCreator playerNameCreator;

    
    ArrayList<Square> addedToThisMove = new ArrayList<>();
    ArrayList<Square[]> newWordsAdded = new ArrayList<>();

    Board board;
    Rack rack;
    Square selectedSquare;
    boolean firstMove = true;
    int playerScore, computerScore;
    int previousCPUMoveScore;   // int previousCPUMoveScore;
    boolean computersTurn;

    boolean playerIsFirst;
    boolean playerPassed;
    String playerNotes;
    String cpuNotes;
    String previousCPUNotes;
    String tilesLeft;
    String previousTilesLeft;
    int pointlessTurns;
    double vowelRatioLeft;
    boolean dictionaryIsCreated = false;
    boolean nameGiven = false;
    int bestTipScore;
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
