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
public class ScrabbleGame extends javax.swing.JFrame {

    /**
     * Creates new form ScrabbleGame
     */
    public ScrabbleGame() throws IOException {
        

                
        initComponents();
        challengeButton.setEnabled(false);
        playButton.setEnabled(false);
        swapButton.setEnabled(false);
        passButton.setEnabled(false);
        newGameButton.setEnabled(false); 
        tipsButton.setEnabled(false);
        
        initBoard();
        (dictionaryCreator = new DictionaryCreator()).execute();
        (playerNameCreator = new PlayerNameCreator()).execute();
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
        challengeButton.setEnabled(false);
        playButton.setEnabled(false);
        swapButton.setEnabled(false);
        passButton.setEnabled(false);
        newGameButton.setEnabled(false);
        tipsButton.setEnabled(false);
        initGame();
        return null;
    }
    
    @Override
    protected void done() {
        challengeButton.setEnabled(true);
        playButton.setEnabled(true);
        swapButton.setEnabled(true);
        passButton.setEnabled(true);
        newGameButton.setEnabled(true);
        tipsButton.setEnabled(true);
    } 
    }
    
    
    private void initComponents() {

        boardPanel = new BoardPanel(this);
        rackPanel = new RackPanel(this);
        playButton = new javax.swing.JButton();
        challengeButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        tilesLeftTitleLabel = new javax.swing.JLabel();
        remainingLabel = new javax.swing.JLabel();
        bagCountLabel = new javax.swing.JLabel();
        firstPlayerScrollPane = new javax.swing.JScrollPane();
        firstPlayerLabel = new javax.swing.JLabel();
        secondPlayerScrollPane = new javax.swing.JScrollPane();
        secondPlayerLabel = new javax.swing.JLabel();
        swapButton = new javax.swing.JButton();
        passButton = new javax.swing.JButton();
        newGameButton = new javax.swing.JButton();
        tipsButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(800, 650));
        setResizable(false);

        playButton.setText("Legg");
        playButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playButtonActionPerformed(evt);
            }
        });

        challengeButton.setText("Utfordre ord");
        challengeButton.setEnabled(false);
        challengeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                challengeButtonActionPerformed(evt);
            }
        });

        remainingLabel.setFont(new java.awt.Font("Courier New", 0, 18)); // NOI18N
        remainingLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        bagCountLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        firstPlayerScrollPane.setPreferredSize(new java.awt.Dimension(105, 280));

        firstPlayerLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        firstPlayerScrollPane.setViewportView(firstPlayerLabel);

        secondPlayerScrollPane.setPreferredSize(new java.awt.Dimension(105, 280));

        secondPlayerLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        secondPlayerScrollPane.setViewportView(secondPlayerLabel);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(remainingLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(firstPlayerScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(secondPlayerScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(bagCountLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tilesLeftTitleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(bagCountLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tilesLeftTitleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(remainingLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(firstPlayerScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(secondPlayerScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        swapButton.setText("Bytt");
        swapButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                swapButtonActionPerformed(evt);
            }
        });

        passButton.setText("Pass");
        passButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passButtonActionPerformed(evt);
            }
        });

        newGameButton.setText("Nytt spill");
        newGameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newGameButtonActionPerformed(evt);
            }
        });

        tipsButton.setText("Tips");
        tipsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tipsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 22, Short.MAX_VALUE)
                        .addComponent(challengeButton)
                        .addGap(18, 18, 18)
                        .addComponent(rackPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addComponent(playButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(swapButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(passButton)
                        .addGap(26, 26, 26)
                        .addComponent(tipsButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(newGameButton)
                        .addGap(152, 152, 152))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(boardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(141, 141, 141))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(boardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rackPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(swapButton)
                                .addComponent(passButton)
                                .addComponent(playButton)
                                .addComponent(challengeButton)
                                .addComponent(newGameButton)
                                .addComponent(tipsButton)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
        
    private void playButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_playButtonActionPerformed
        if (computersTurn) {
            return;
        }
        System.out.println("Player kaller play med " + rack.toString() + " p� racket.");
        //m� sjekke om ordet er lovlig plassert
        if (moveIsAllowed()) {  
            //m� sjekke om ordet/ordene er gyldig
            //hvis ikke, b�r turen avsluttes            
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
        
                String toRemoveFromRemaining = "";
                //fjerne fra newlyAdded
                //l�ser brikkene til brettet         
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
                System.out.println("Playerrack etter � ha plukket brikker: " + rack.toString());
                rackPanel.renderRack(rack);
                
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

                    rackPanel.putBack(addedToThisMove);
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
        bagCountLabel.setText("Brikker igjen i posen: " + bag.tileCount());
        
    }//GEN-LAST:event_playButtonActionPerformed

    void computerMove() {
        JScrollBar verticalScrollBar = firstPlayerScrollPane.getVerticalScrollBar();
        verticalScrollBar.setValue(verticalScrollBar.getMaximum());
        verticalScrollBar = secondPlayerScrollPane.getVerticalScrollBar();
        verticalScrollBar.setValue(verticalScrollBar.getMaximum());
        (cpuThinker = new CPUThinker(this)).execute();
    }
    
    private void challengeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_challengeButtonActionPerformed
        boolean wordRemoved = false;   
        for (String s : cpuLastWord.words) {
            Object[] options = {"Ja", "Nei"};
            int n = JOptionPane.showOptionDialog(this,
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
            //omgj�r rack til forrige rack
            rackString = previousRackString;
            //fjerner fra brett og oppdaterer charBoard
            for (int i = 0; i < 15; i++) {
                for (int j = 0; j < 15; j++) {
                    if (board.charBoardBeforeLastMove[i][j] != board.charBoard[i][j]) {
                        board.charBoard[i][j] = board.charBoardBeforeLastMove[i][j];
                        boardPanel.squareGrid[i][j].tile = null;
                        boardPanel.squareGrid[i][j].setIcon(null);
                    }
                }
            }
            //fjerner fra computerScore
            computerScore -= previousCPUMoveScore;
            //oppdaterer gjenv�rende brikker
            tilesLeft = previousTilesLeft;
            remainingLabel.setText(tilesLeft);
            //oppdaterer cpuNotes
            cpuNotes = previousCPUNotes;
            JLabel noteLabel = firstPlayerLabel;
            JScrollPane scrollPane = firstPlayerScrollPane;
            if (playerIsFirst) {
                noteLabel = secondPlayerLabel;
                scrollPane = secondPlayerScrollPane;
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
        
    }//GEN-LAST:event_challengeButtonActionPerformed

    private void swapButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_swapButtonActionPerformed
        System.out.println("Bytter");
        
        ArrayList<Square> toSwap = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                Tile t = boardPanel.squareGrid[i][j].tile;
                if (t != null && t.isMovable) {
                    toSwap.add(boardPanel.squareGrid[i][j]);
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
                rackPanel.renderRack(rack);

                updatePlayerNotes("(bytte)", 0);
                computersTurn = true;
                computerMove();
            }
        }
    }//GEN-LAST:event_swapButtonActionPerformed

    private void passButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passButtonActionPerformed
        pass();
        playerPassed = true;        
    }//GEN-LAST:event_passButtonActionPerformed

    private void newGameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newGameButtonActionPerformed
            Object[] options = {"Ja", "Nei"};
            int n = JOptionPane.showOptionDialog(this,
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
    }//GEN-LAST:event_newGameButtonActionPerformed

    void calculateTips() {
        String playerRack = rack.toString();
        playerTips = true;
        bestTipScore = 0;
        if (!firstMove) {
            board.updateAnchors();
            board.doCrossChecks(dictionary, alphaString);
        }
        tipsWords.clear();
        
        findAcrossMoves();
        //TRANSPOSING
        board.transposeBoard(boardPanel);
        board.doCrossChecks(dictionary, alphaString);
        //down moves
        findAcrossMoves();

        if (!tipsWords.isEmpty()) {
            bestTipScore = tipsWords.firstEntry().getValue().wordScore;
        }
        
        possibleBingos.clear();
        impossibleBingos.clear();
        for (Map.Entry<Double,PotentialMove> entry : tipsWords.entrySet()) {
            PotentialMove poss = entry.getValue();
            if (poss.usedFromRack.length() == 7) {
                if (!possibleBingos.contains(poss.word)) {
                    possibleBingos.add(poss.word);
                }
            }
        }
        
        if (rack.tileCount() == 7) {
            playerExtendRight("", (MDAGNode) dictionary.getSourceNode(), playerRack);
        }
        
        if (board.transposed) {
            board.transposeBoard(boardPanel);
        }
        playerTips = false;
    }
    
    private void tipsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tipsButtonActionPerformed

        int count = 0;
        String tipsString = "<html><body>";
        ArrayList<String> tipsGiven = new ArrayList<>();
        if (tipsWords.isEmpty()) {
            tipsString += "Det finnes ingen mulige legg";
        } else {
            tipsString += "<b><u>H�yest scorende legg:</u></b>";
        }
        for (Map.Entry<Double,PotentialMove> entry : tipsWords.entrySet()) {
            PotentialMove poss = entry.getValue();
            
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
    }//GEN-LAST:event_tipsButtonActionPerformed

    void pass() {
        if (computersTurn) {
            computersTurn = false;
            updateCPUNotes("(pass)", 0);
            addedToThisMove.clear();
            newWordsAdded.clear();
        } else {
            System.out.println("kommer hit - pass 1");
            //legger evt brikker tilbake p� racken

            rackPanel.putBack(newlyAddedToBoard);
            rack.alphabetize();

            updatePlayerNotes("(pass)", 0);
            //fjerner fra listen over nylig lagt til brikker
            addedToThisMove.clear();
            newWordsAdded.clear();
            computersTurn = true;
            computerMove();
        }
    }
    
    void computerSwap(String toSwap) {
        JOptionPane.showMessageDialog(null, "CPU bytter " + toSwap.length() + " brikker");
        System.out.println("CPU kaller swap med " + toSwap + ", rackString er " + rackString + "<-slutt");
        if (bag.tileCount() < 7) {
            System.out.println("CPU pr�ver � bytte med for lite i posen");
        } else {
            //trekker brikker 
            for (int i = 0; i < toSwap.length(); i++) {
                Tile t = bag.pickTile();
                rackString += t.letter;
            }
            System.out.println("etter � ha trukket opp: " + rackString);
            //legger gamle brikker tilbake i posen
            for (int i = 0; i < toSwap.length(); i++) {
                char c = toSwap.charAt(i);
                rackString = rackString.substring(0,rackString.indexOf(c)) + rackString.substring(rackString.indexOf(c)+1);
                bag.add(new Tile(c));
            }
            System.out.println("etter � ha lagt tilbake: " + rackString);
        }
        updateCPUNotes("(bytte)", 0);
    }
    
    void findAcrossMoves() {
        String playerRack = rack.toString();
        //for all anchorSquares
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                
                if (board.isAnchor[i][j]) {
                    currentAnchorI = i;
                    currentAnchorJ = j;
                    String partialWord = "";
                    int k = 0;
                    while (j - k != 0 && !board.isAnchor[i][j-(k+1)]) {
                        k++;
                    }
                    //hvis left part er fra brettet
                    if (k != 0 && board.charBoard[i][j-1] != '-') {
                        MDAGNode n = (MDAGNode) dictionary.getSourceNode();
                        for (int l = 0; l < k; l++) {
                            partialWord += board.charBoard[i][j - (k-l)];
                            n = n.transition(board.charBoard[i][j - (k-l)]);
                        }
                        if (playerTips) {
                            tipsExtendRight(partialWord, n, j, "", playerRack);
                        } else {
                        extendRight(partialWord, n, j, "");
                        }
                    } else {
                        if (playerTips) {
                            tipsLeftPart("", (MDAGNode) dictionary.getSourceNode(), k, "", playerRack);
                        } else {
                            leftPart("", (MDAGNode) dictionary.getSourceNode(), k, "");
                        }
                    }
                }
            }
        }
   }
    
    void tipsLeftPart(String partialWord, MDAGNode n, int limit, String usedFromRack, String playerRack) {
        tipsExtendRight(partialWord, n, currentAnchorJ, usedFromRack, playerRack);
        if (limit > 0) {
            //for each edge E out of N
            TreeMap<Character,MDAGNode> outGoingEdges = n.getOutgoingTransitions();
            for (Map.Entry<Character,MDAGNode> entry : outGoingEdges.entrySet()) {
                //if the letter l labeling edge e is in our rack
                char l = entry.getKey();
                int index = playerRack.indexOf(l);
                if (index != -1) {
                    //then remove a tile labeled l from the rack
                    playerRack = playerRack.substring(0,index) + playerRack.substring(index+1);
                    //let N' be the node reached by following edge E
                    MDAGNode nNext = entry.getValue();
                    //leftPart(...)
                    tipsLeftPart((partialWord + l), nNext, limit-1, usedFromRack + l, playerRack);
                    //put the tile back in the rack
                    playerRack += l;
                } else { //if not on rack, check for blanks
                    index = playerRack.indexOf('-');
                    if (index != -1) {
                    //then remove blank tile from the rack
                    playerRack = playerRack.substring(0,index) + playerRack.substring(index+1);
                    //let N' be the node reached by following edge E
                    MDAGNode nNext = entry.getValue();
                    //leftPart(...)
                    tipsLeftPart((partialWord + l), nNext, limit-1, usedFromRack + '-', playerRack);
                    //put the blank tile back in the rack
                    playerRack += '-';                    
                    }
                }
                
            }
        }
    }
    
    void tipsExtendRight(String partialWord, MDAGNode n, int squareJ, String usedFromRack, String playerRack) {
        //if square is vacant
        if (squareJ == 15 || board.charBoard[currentAnchorI][squareJ] == '-') {
            //if N si a terminal node
            if (squareJ != currentAnchorJ && n.isAcceptNode()) {
                PotentialMove newTip;
                //if (squareJ == currentAnchorJ) {
                  //  newPos = new PossibleWord(partialWord, currentAnchorI, squareJ);
                //    //possibleWords.add(new PossibleWord(partialWord, currentAnchorI, squareJ));
                //} else {
                    newCPUWords = new ArrayList<>();
                    newTip = new PotentialMove(partialWord, currentAnchorI, squareJ-1, board.transposed,
                            scoreTipsWords(partialWord, currentAnchorI, (squareJ - partialWord.length())),
                            usedFromRack);
                    newTip.words = newCPUWords;
                    newCPUWords = null;
                    //possibleWords.add(new PossibleWord(partialWord, currentAnchorI, squareJ-1));
               // }
                    //tipsWords.put(cpuAIScore(newTip), newTip);
                    tipsWords.put((double)newTip.wordScore + (0.0000001*(double) tipsWords.size()), newTip);
            }
            if (squareJ < 15) {
            //if rack isn't empty
            //if (rackString.length() != 0) {
            //    System.out.println("rackString is EMPTY");
            //}
            
                //for each edge E out of N
                TreeMap<Character,MDAGNode> outGoingEdges = n.getOutgoingTransitions();
                for (Map.Entry<Character,MDAGNode> entry : outGoingEdges.entrySet()) {
                    //if the letter l labeling edge e is in our rack
                    char l = entry.getKey();
                    int index = playerRack.indexOf(l);
                    if (index != -1) {
                        //and l is in the crossCheck set of square
                        if (board.crossChecks[currentAnchorI][squareJ].indexOf(l) != -1) {
                            //then remove a tile labeled l from the rack
                            playerRack = playerRack.substring(0,index) + playerRack.substring(index+1);
                            //let N' be the node reached by following edge E
                            MDAGNode nNext = entry.getValue();
                            //let next-square be the square to the right of square
                            //if (squareJ != 14) {
                                tipsExtendRight((partialWord + l), nNext, squareJ+1, usedFromRack + l, playerRack);
                            //}
                            //put the tile back in the rack
                            playerRack += l;
                        }
                    } else { // check for blank
                        index = playerRack.indexOf('-');
                        if (index != -1) {
                            //and l is in the crossCheck set of square
                            if (board.crossChecks[currentAnchorI][squareJ].indexOf(l) != -1) {
                                //then remove blank tile from the rack
                                playerRack = playerRack.substring(0,index) + playerRack.substring(index+1);
                                //let N' be the node reached by following edge E
                                MDAGNode nNext = entry.getValue();
                                //let next-square be the square to the right of square
                                if (squareJ != 14) {
                                    tipsExtendRight((partialWord + l), nNext, squareJ+1, usedFromRack + '-', playerRack);
                                }
                                //put the blank tile back in the rack
                                playerRack += '-';                    
                            }
                        }
                    }  
                }
            }
            //}
        } else { //if square not vacant
            //let l be the letter occupying square
            char l = board.charBoard[currentAnchorI][squareJ];
            //if N has an edge labeled by l that leads to some node N'
            if (n.hasOutgoingTransition(l)) {
                //let next-square be the square to the right of square
                //if (squareJ != 14) {
                    tipsExtendRight((partialWord + l), n.transition(l), squareJ+1, usedFromRack, playerRack);
                //}
            } 
        }
    }
    
    void leftPart(String partialWord, MDAGNode n, int limit, String usedFromRack) {
        extendRight(partialWord, n, currentAnchorJ, usedFromRack);
        if (limit > 0) {
            //for each edge E out of N
            TreeMap<Character,MDAGNode> outGoingEdges = n.getOutgoingTransitions();
            for (Map.Entry<Character,MDAGNode> entry : outGoingEdges.entrySet()) {
                //if the letter l labeling edge e is in our rack
                char l = entry.getKey();
                int index = rackString.indexOf(l);
                if (index != -1) {
                    //then remove a tile labeled l from the rack
                    rackString = rackString.substring(0,index) + rackString.substring(index+1);
                    //let N' be the node reached by following edge E
                    MDAGNode nNext = entry.getValue();
                    //leftPart(...)
                    leftPart((partialWord + l), nNext, limit-1, usedFromRack + l);
                    //put the tile back in the rack
                    rackString += l;
                } else { //if not on rack, check for blanks
                    index = rackString.indexOf('-');
                    if (index != -1) {
                    //then remove blank tile from the rack
                    rackString = rackString.substring(0,index) + rackString.substring(index+1);
                    //let N' be the node reached by following edge E
                    MDAGNode nNext = entry.getValue();
                    //leftPart(...)
                    leftPart((partialWord + l), nNext, limit-1, usedFromRack + '-');
                    //put the blank tile back in the rack
                    rackString += '-';
                    }
                }
                
            }
        }
    }
 
    void extendRight(String partialWord, MDAGNode n, int squareJ, String usedFromRack) {
        //if square is vacant
        if (squareJ == 15 || board.charBoard[currentAnchorI][squareJ] == '-') {
            //if N si a terminal node
            if (squareJ != currentAnchorJ && n.isAcceptNode()) {
                PotentialMove newPos;
                //if (squareJ == currentAnchorJ) {
                  //  newPos = new PossibleWord(partialWord, currentAnchorI, squareJ);
                //    //possibleWords.add(new PossibleWord(partialWord, currentAnchorI, squareJ));
                //} else {
                    newCPUWords = new ArrayList<>();
                    newPos = new PotentialMove(partialWord, currentAnchorI, squareJ-1, board.transposed,
                            scoreCPUWords(partialWord, currentAnchorI, (squareJ - partialWord.length())),
                            usedFromRack);
                    newPos.words = newCPUWords;
                    newCPUWords = null;
                    //possibleWords.add(new PossibleWord(partialWord, currentAnchorI, squareJ-1));
               // }
                ComputerAI computerAI = new ComputerAI(rackStringCpy, bag, vowelRatioLeft, alphaString,
                        playerScore, computerScore, pointlessTurns, board.isAnchor, firstMove,
                        boardPanel.squareGrid, board.charBoard, dictionary,
                        rackString, rack.tileCount());

                possibleWords.put(computerAI.cpuAIScore(newPos), newPos);
            }
            if (squareJ < 15) {
            //if rack isn't empty
            //if (rackString.length() != 0) {
            //    System.out.println("rackString is EMPTY");
            //}
            
                //for each edge E out of N
                TreeMap<Character,MDAGNode> outGoingEdges = n.getOutgoingTransitions();
                for (Map.Entry<Character,MDAGNode> entry : outGoingEdges.entrySet()) {
                    //if the letter l labeling edge e is in our rack
                    char l = entry.getKey();
                    int index = rackString.indexOf(l);
                    if (index != -1) {
                        //and l is in the crossCheck set of square
                        if (board.crossChecks[currentAnchorI][squareJ].indexOf(l) != -1) {
                            //then remove a tile labeled l from the rack
                            rackString = rackString.substring(0,index) + rackString.substring(index+1);
                            //let N' be the node reached by following edge E
                            MDAGNode nNext = entry.getValue();
                            //let next-square be the square to the right of square
                            //if (squareJ != 14) {
                                extendRight((partialWord + l), nNext, squareJ+1, usedFromRack + l);
                            //}
                            //put the tile back in the rack
                            rackString += l;
                        }
                    } else { // check for blank
                        index = rackString.indexOf('-');
                        if (index != -1) {
                            //and l is in the crossCheck set of square
                            if (board.crossChecks[currentAnchorI][squareJ].indexOf(l) != -1) {
                                //then remove blank tile from the rack
                                rackString = rackString.substring(0,index) + rackString.substring(index+1);
                                //let N' be the node reached by following edge E
                                MDAGNode nNext = entry.getValue();
                                //let next-square be the square to the right of square
                                if (squareJ != 14) {
                                    extendRight((partialWord + l), nNext, squareJ+1, usedFromRack + '-');
                                }
                                //put the blank tile back in the rack
                                rackString += '-';
                            }
                        }
                    }  
                }
            }
            //}
        } else { //if square not vacant
            //let l be the letter occupying square
            char l = board.charBoard[currentAnchorI][squareJ];
            //if N has an edge labeled by l that leads to some node N'
            if (n.hasOutgoingTransition(l)) {
                //let next-square be the square to the right of square
                //if (squareJ != 14) {
                    extendRight((partialWord + l), n.transition(l), squareJ+1, usedFromRack);
                //}
            } 
        }
    }
    
    void playerExtendRight(String partialWord, MDAGNode n, String playerRack) {
        //if terminal node
        if (partialWord.length() == 7 && n.isAcceptNode()) {
            if (!possibleBingos.contains(partialWord)) {
                impossibleBingos.add(partialWord);
            }
        } else {
            //for each edge E out of N
            TreeMap<Character,MDAGNode> outGoingEdges = n.getOutgoingTransitions();
            for (Map.Entry<Character,MDAGNode> entry : outGoingEdges.entrySet()) {
                //if the letter l labeling edge e is in players rack
                char l = entry.getKey();
                int index = playerRack.indexOf(l);
                if (index != -1) {
                    //then remove a tile labeled l from the rack
                    playerRack = playerRack.substring(0,index) + playerRack.substring(index+1);
                    //let N' be the node reached by following edge E
                    MDAGNode nNext = entry.getValue();
                    //let next-square be the square to the right of square
                    playerExtendRight((partialWord + l), nNext, playerRack);
                    //put the tile back in the rack
                    playerRack += l;
                } else { // check for blank
                    index = playerRack.indexOf('-');
                    if (index != -1) {
                        //then remove blank tile from the rack
                        playerRack = playerRack.substring(0,index) + playerRack.substring(index+1);
                        //let N' be the node reached by following edge E
                        MDAGNode nNext = entry.getValue();
                        //let next-square be the square to the right of square
                        playerExtendRight((partialWord + l), nNext, playerRack);
                        //put the blank tile back in the rack
                        playerRack += '-';                    
                    }
                }  
            }
        }
    }
    


    //TODO: her m� man jo heller bare sl� opp ordene...
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
                int n = JOptionPane.showOptionDialog(this,
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
        }
        return true;
    }
    
    void updatePlayerNotes(String word, int score) {
        JLabel noteLabel = firstPlayerLabel;
        if (!playerIsFirst) {
            noteLabel = secondPlayerLabel;
        }
        playerNotes += word + " ";
        if (score != 0) {
            playerNotes += score;
            if (score == bestTipScore) {
                playerNotes += "!";
            }
            PotentialMove poss = tipsWords.firstEntry().getValue();
            if (score < bestTipScore) { 
                String message = "<html><body><u><b>Du kunne lagt:</u></b><br>";
                message += (poss.wordScore + ", " + poss.word);
                JOptionPane.showMessageDialog(null, message);
            } else if (score > bestTipScore && !newWordAdded) {
                String message = ("BUG - h�yeste CPU fant var: " + 
                        poss.wordScore + ", " + poss.word);
                JOptionPane.showMessageDialog(null, message);
                playerNotes += "!";
            }
        }
        playerNotes += "<br>";
        noteLabel.setText("<html><body>" + playerNotes + "<b>" + playerScore + "</b></body></html>");
        if (score == 0) {
            if (++pointlessTurns == 6) {
                JOptionPane.showMessageDialog(null, "<html><body>Det har g�tt 6 runder uten poeng,<br>kampen avsluttes</body></html>");
                finishGame();
            }
        } else {
            pointlessTurns = 0;
            if (firstMove) {
                firstMove = false;
                challengeButton.setEnabled(true);
            }
        }
    }
    
    void updateCPUNotes(String word, int score) {
        previousCPUNotes = cpuNotes;
        JLabel noteLabel = firstPlayerLabel;
        if (playerIsFirst) {
            noteLabel = secondPlayerLabel;
        }
        cpuNotes += word + " ";
        if (score != 0) {
            cpuNotes += score;
        }
        cpuNotes += "<br>";
        noteLabel.setText("<html><body>" + cpuNotes + "<b>" + computerScore + "</b></body></html>");        
        
        if (score == 0) {
            if (++pointlessTurns == 6) {
                JOptionPane.showMessageDialog(null, "<html><body>Det har g�tt 6 runder uten poeng,<br>kampen avsluttes</body></html>");
                finishGame();
            }
        } else {
            pointlessTurns = 0;
            if (firstMove) {
                firstMove = false;
                challengeButton.setEnabled(true);
            }
        }
    }
    
    void updatePlayerScore(int moveScore) {
        playerScore += moveScore;
    }
    
    void updateComputerScore(int moveScore) {
        computerScore += moveScore;
        previousCPUMoveScore = moveScore;
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
        remainingLabel.setText(tilesLeft);
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
        
        challengeButton.setEnabled(false);
        playButton.setEnabled(false);
        swapButton.setEnabled(false);
        passButton.setEnabled(false);
        tipsButton.setEnabled(false);
    }
    
    boolean moveIsAllowed() {
        int rowStart = 0;
        int columnStart = 0;
        int rowEnd = 0;
        int columnEnd = 0;
        boolean firstNewTile = true;
            
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                Tile t = boardPanel.squareGrid[i][j].tile;
                //leter etter f�rste nye brikke
                if (t != null && t.isMovable) {
                    addedToThisMove.add(boardPanel.squareGrid[i][j]);
                    if (firstNewTile) {
                        rowStart = i;
                        rowEnd = i;
                        columnStart = j;
                        columnEnd = j;
                        firstNewTile = false;
                        boolean gapExists = false;
                        //sjekker videre p� samme rad
                        for (int k = i+1; k < 15; k++) {
                            Tile nextTile = boardPanel.squareGrid[k][j].tile;
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
                        //sjekker videre p� samme kolonne
                        gapExists = false;
                        for (int k = j+1; k < 15; k++) {
                           Tile nextTile = boardPanel.squareGrid[i][k].tile;
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
                while(wordStart > 0 && boardPanel.squareGrid[i][wordStart - 1].tile != null) {
                    wordStart--;
                }
                while(wordEnd < 14 && boardPanel.squareGrid[i][wordEnd + 1].tile != null) {
                    wordEnd++;
                }
                    
                //WE HAVE A WORD! at boardPanel.squareGrid[i][wordStart --> wordEnd] if (start != end)
                if (wordStart != wordEnd) {
                    Square[] newWord = new Square[(wordEnd - wordStart) + 1];
                    int index = 0;
                    for (int j = wordStart; j <= wordEnd; j++) {
                        newWord[index++] = boardPanel.squareGrid[i][j];
                    }
                    //burde kunne l�ses p� annen m�te, men sjekker om ord har ny bokstav
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
                while(wordStart > 0 && boardPanel.squareGrid[wordStart - 1][j].tile != null) {
                    wordStart--;
                }
                while(wordEnd < 14 && boardPanel.squareGrid[wordEnd + 1][j].tile != null) {
                    wordEnd++;
                }
                    
                //WE HAVE A WORD! at boardPanel.squareGrid[wordStart --> wordEnd][j] (if start != end)
                if (wordStart != wordEnd) {
                    Square[] newWord = new Square[(wordEnd - wordStart) + 1];
                    int index = 0;
                    for (int i = wordStart; i <= wordEnd; i++) {
                        newWord[index++] = boardPanel.squareGrid[i][j];
                    }
                    //burde kunne l�ses p� annen m�te, men sjekker om ord har ny bokstav
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
                    if (boardPanel.squareGrid[7][7].tile == null) {
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
            tempSum += s.tile.value;
            if (s.tile.isMovable) { //sjekker bare multiplier for nye brikker
               if (s.multiplier.equals("DL")) {
                    tempSum += s.tile.value;
                } else if (s.multiplier.equals("TL")) {
                    tempSum += (s.tile.value * 2);
                } else if (s.multiplier.equals("DW")) {
                    multiplier *= 2;
                } else if (s.multiplier.equals("TW")) {
                    multiplier *= 3;
                }
            }
        }
        tempSum *= multiplier;
        if (newTiles == 7) { //bingo!
            tempSum += 50;
            //System.out.println("kaller p� bingoMessage");
            //JOptionPane.showMessageDialog(null, "Bingo!");
        }           
        return tempSum;
    }

    int scoreCPUWords(String word, int row, int wordStart) {
        int totalSum = scoreCPUWord(word, row, wordStart);
        newCPUWords.add(word);
        for (int j = wordStart; j < wordStart+word.length(); j++) {
        if (board.charBoard[row][j] == '-') {
            int wordScore = 0;
            int multiplier = 1;
            int tilesOver = 0;
            int tilesUnder = 0;
            while(row - tilesOver != 0 && board.charBoard[row-(tilesOver+1)][j] != '-') {
                tilesOver++;
            }
            while(row + tilesUnder != 14 && board.charBoard[row+(tilesUnder+1)][j] != '-') {
                tilesUnder++;
            }
            if (tilesOver != 0 || tilesUnder != 0) {
                String newWord = "";
                for (int k = tilesOver; k > 0; k--) {
                    if (!boardPanel.squareGrid[row-k][j].tile.isBlank()) {
                        wordScore += ScoreConstants.letterScore(board.charBoard[row-k][j]);
                        newWord += board.charBoard[row-k][j];
                    }
                }
                newWord += word.charAt(j - wordStart);
                for (int k = 1; k <= tilesUnder; k++) {
                    if (!boardPanel.squareGrid[row+k][j].tile.isBlank()) {
                        wordScore += ScoreConstants.letterScore(board.charBoard[row+k][j]);
                        newWord += board.charBoard[row+k][j];
                    }                    
                }
                newCPUWords.add(newWord);
                int tileScore = 0;
                if (rackStringCpy.indexOf(word.charAt(j-wordStart)) != -1) {
                    tileScore = ScoreConstants.letterScore(word.charAt(j-wordStart));
                    wordScore += tileScore;
                }
                if (boardPanel.squareGrid[row][j].multiplier.equals("DL")) {
                    wordScore += tileScore;
                } else if (boardPanel.squareGrid[row][j].multiplier.equals("TL")) {
                    wordScore += (tileScore * 2);
                } else if (boardPanel.squareGrid[row][j].multiplier.equals("DW")) {
                    multiplier *= 2;
                } else if (boardPanel.squareGrid[row][j].multiplier.equals("TW")) {
                    multiplier *= 3;
                }
                wordScore *= multiplier;
                totalSum += wordScore;
                }
            }
        }
        return totalSum;
    }
    
    int scoreTipsWords(String word, int row, int wordStart) {
        int totalSum = scoreTipsWord(word, row, wordStart);
        newCPUWords.add(word);
        for (int j = wordStart; j < wordStart+word.length(); j++) {
        if (board.charBoard[row][j] == '-') {
            int wordScore = 0;
            int multiplier = 1;
            int tilesOver = 0;
            int tilesUnder = 0;
            while(row - tilesOver != 0 && board.charBoard[row-(tilesOver+1)][j] != '-') {
                tilesOver++;
            }
            while(row + tilesUnder != 14 && board.charBoard[row+(tilesUnder+1)][j] != '-') {
                tilesUnder++;
            }
            if (tilesOver != 0 || tilesUnder != 0) {
                String newWord = "";
                for (int k = tilesOver; k > 0; k--) {
                    if (!boardPanel.squareGrid[row-k][j].tile.isBlank()) {
                        wordScore += ScoreConstants.letterScore(board.charBoard[row-k][j]);
                        newWord += board.charBoard[row-k][j];
                    }
                }
                newWord += word.charAt(j - wordStart);
                for (int k = 1; k <= tilesUnder; k++) {
                    if (!boardPanel.squareGrid[row+k][j].tile.isBlank()) {
                        wordScore += ScoreConstants.letterScore(board.charBoard[row+k][j]);
                        newWord += board.charBoard[row+k][j];
                    }                    
                }
                newCPUWords.add(newWord);
                int tileScore = 0;
                if (rack.contains(word.charAt(j-wordStart))) {
                    tileScore = ScoreConstants.letterScore(word.charAt(j-wordStart));
                    wordScore += tileScore;
                }
                if (boardPanel.squareGrid[row][j].multiplier.equals("DL")) {
                    wordScore += tileScore;
                } else if (boardPanel.squareGrid[row][j].multiplier.equals("TL")) {
                    wordScore += (tileScore * 2);
                } else if (boardPanel.squareGrid[row][j].multiplier.equals("DW")) {
                    multiplier *= 2;
                } else if (boardPanel.squareGrid[row][j].multiplier.equals("TW")) {
                    multiplier *= 3;
                }
                wordScore *= multiplier;
                totalSum += wordScore;
                }
            }
        }
        return totalSum;
    }
    
    int scoreTipsWord(String word, int row, int wordStart) {
        int multiplier = 1;
        int tempSum = 0;
        int newTiles = 0;
        int tileScore = 0;
        char fromRack;
        String rackCpy = rack.toString();
        for (int i = 0; i < word.length(); i++) {
            int j = wordStart+i;
            //if tile is from rack
            if (board.charBoard[row][j] == '-') {
                newTiles++;
                tileScore = 0;
                fromRack = word.charAt(i);            
                int index = rackCpy.indexOf(fromRack);
                if (index == -1) {
                    fromRack = '-';
                    index = rackCpy.indexOf(fromRack);
                } else {
                    tileScore = ScoreConstants.letterScore(fromRack);
                }
                if (boardPanel.squareGrid[row][j].multiplier.equals("DL")) {
                    tempSum += tileScore;
                } else if (boardPanel.squareGrid[row][j].multiplier.equals("TL")) {
                    tempSum += (tileScore * 2);
                } else if (boardPanel.squareGrid[row][j].multiplier.equals("DW")) {
                    multiplier *= 2;
                } else if (boardPanel.squareGrid[row][j].multiplier.equals("TW")) {
                    multiplier *= 3;
                }
                rackCpy = rackCpy.substring(0, index) + rackCpy.substring(index+1);
            } else if (!boardPanel.squareGrid[row][j].tile.isBlank()){
                tileScore = ScoreConstants.letterScore(board.charBoard[row][j]);
            }
            tempSum += tileScore;
        }
        tempSum *= multiplier;
        if (newTiles == 7) { //bingo!
            tempSum += 50;
            //System.out.println("kaller p� bingoMessage");
            //JOptionPane.showMessageDialog(null, "Bingo!");
        }              
        return tempSum;
    }       

    
    int scoreCPUWord(String word, int row, int wordStart) {
        int multiplier = 1;
        int tempSum = 0;
        int newTiles = 0;
        int tileScore = 0;
        char fromRack;
        String rackCpy = rackStringCpy;
        for (int i = 0; i < word.length(); i++) {
            int j = wordStart+i;
            //if tile is from rack
            if (board.charBoard[row][j] == '-') {
                newTiles++;
                tileScore = 0;
                fromRack = word.charAt(i);            
                int index = rackCpy.indexOf(fromRack);
                if (index == -1) {
                    fromRack = '-';
                    index = rackCpy.indexOf(fromRack);
                } else {
                    tileScore = ScoreConstants.letterScore(fromRack);
                }
                if (boardPanel.squareGrid[row][j].multiplier.equals("DL")) {
                    tempSum += tileScore;
                } else if (boardPanel.squareGrid[row][j].multiplier.equals("TL")) {
                    tempSum += (tileScore * 2);
                } else if (boardPanel.squareGrid[row][j].multiplier.equals("DW")) {
                    multiplier *= 2;
                } else if (boardPanel.squareGrid[row][j].multiplier.equals("TW")) {
                    multiplier *= 3;
                }
                rackCpy = rackCpy.substring(0, index) + rackCpy.substring(index+1);
            } else if (!boardPanel.squareGrid[row][j].tile.isBlank()){
                tileScore = ScoreConstants.letterScore(board.charBoard[row][j]);
            }
            tempSum += tileScore;
        }
        tempSum *= multiplier;
        if (newTiles == 7) { //bingo!
            tempSum += 50;
            //System.out.println("kaller p� bingoMessage");
            //JOptionPane.showMessageDialog(null, "Bingo!");
        }              
        return tempSum;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ScrabbleGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ScrabbleGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ScrabbleGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ScrabbleGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new ScrabbleGame().setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(ScrabbleGame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    
   void initGame() {
        rackString = "";
        rackStringCpy = "";

        playerScore = 0;
        computerScore = 0;
        
        //fyller charBoard med '-'
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                board.charBoard[i][j] = '-';
                //fyller crossChecks med alle bokstaver
                board.crossChecks[i][j] = alphaString;
                //"t�mmer" isAnchor
                board.isAnchor[i][j] = false;
                //t�mmer brettet
                boardPanel.squareGrid[i][j].setIcon(null);
                boardPanel.squareGrid[i][j].tile = null;
                }
            }
        board.isAnchor[7][7] = true;

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
        remainingLabel.setText(tilesLeft);
        
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

        rackPanel.renderRack(rack);

         tilesLeftTitleLabel.setText("<html><body><b><u>Gjenv�rende brikker:</u></b></body></html>");
         firstMove = true;
         bagCountLabel.setText("Brikker igjen i posen: " + bag.tileCount());
         playerIsFirst = !computersTurn;
         playerNotes = "<b><u>" + playerName + ":</u></b><br>";
         cpuNotes = "<u><b>CPU:</b></u><br>";
         updatePlayerNotes("", 0);
         updateCPUNotes("", 0);
         if (computersTurn) {
            computerMove();
         } else {
            (tipsCalculator = new TipsCalculator(this)).execute();
         }
         pointlessTurns = 0;         
   } 
    
   void initBoard() {
       board = new Board();
    }

    //TODO: dette m� ut i CPUThinker,
    // - selve kalkuleringa m� ut i egen metode som ogs� kan brukes av tipslageren.
    void computerAI() {

        //calculating vowelRatio in bag + players rack
        double vowelsLeft = bag.vowelCount() + StringUtil.vowelCount(rack.toString());
        int lettersLeft = bag.tileCount() + rack.tileCount();

        vowelRatioLeft = vowelsLeft / lettersLeft;

        previousRackString = rackString;
        System.out.println("ComputerAI starts.. cpuRack: " + rackString);
        newlyAddedToBoard.clear();
        if (!computersTurn) {
            System.out.println("ikke cpus tur");
            return;
        } else if (rackString.length() == 0) {
            System.out.println("FERDIG!");
            return;
        }
        if (!firstMove) {
            board.updateAnchors();
            board.doCrossChecks(dictionary, alphaString);
        }

        rackStringCpy = rackString;
        possibleWords.clear();

        findAcrossMoves();
        //TRANSPOSING
        board.transposeBoard(boardPanel);
        board.doCrossChecks(dictionary, alphaString);
        //down moves
        findAcrossMoves();

        //TODO: henter n� alle ord ogs� med ny metode, men har ikke fjernet gammel enn�
        //bruker nye metoden for � finne ord
        MoveFinder moveFinder = new MoveFinder();
        ArrayList<PotentialMove> allMoves = moveFinder.findAllMoves(dictionary, board, rackString);

        TreeMap<Double, PotentialMove> newPossibleWords = new TreeMap<>(Collections.reverseOrder());

        ComputerAI computerAI = new ComputerAI(rackStringCpy, bag, vowelRatioLeft, alphaString,
                playerScore, computerScore, pointlessTurns, board.isAnchor, firstMove,
                boardPanel.squareGrid, board.charBoard, dictionary,
                rackString, rack.tileCount());

        allMoves.forEach(potentialMove -> newPossibleWords.put(computerAI.cpuAIScore(potentialMove), potentialMove));

        int count = 0;
        int topSc = 0;
        double topScKey = 0;
        PotentialMove top = null;
        for (Map.Entry<Double,PotentialMove> entry : possibleWords.entrySet()) {
            PotentialMove poss = entry.getValue();
            if (count < 20) {
                System.out.println(entry.getKey() + " " + poss.word + " startsAt " + poss.wordStart + " vertical: " + poss.vertical + " bruker: " + poss.usedFromRack + " har igjen: " + poss.leftOnRack + " score: " + poss.wordScore + "  -> " + poss.AIString);
                count++;
            }
            if (poss.wordScore > topSc || (poss.wordScore == topSc && entry.getKey() > topScKey)) {
                top = poss;
                topSc = poss.wordScore;
                topScKey = entry.getKey();
            }
        }

        //kan ikke legge
        if (possibleWords.isEmpty()) {
            System.out.println("CPU kan ikke legge");
            addedToThisMove.clear();
            newWordsAdded.clear();
            board.transposeBoard(boardPanel);

            //M� BYTTE OM MULIG
            if (bag.tileCount() >= 7) {
                //bytter alle
                computerSwap(rackString);
            } else {
                JOptionPane.showMessageDialog(null, "CPU passer");
                pass();
            }
            computersTurn = false;
            return;
        }

        //velge legg og skrive ut p� skjerm + legge til brikkene i charBoard
        PotentialMove topScoreWord = possibleWords.firstEntry().getValue();

        //TEST
        if (top != null) {
            if (top == topScoreWord) {
                System.out.println("Velger TOPSCORE-word");
            } else {
                System.out.println("TOPSCORE: " + topScKey + " " + top.word + " startsAt " + top.wordStart + " vertical: " + top.vertical + " bruker: " + top.usedFromRack + " har igjen: " + top.leftOnRack + " score: " + top.wordScore + "  -> " + top.AIString);
            }
        }

        //TEST
        System.out.print(possibleWords.firstEntry().getKey() + " (" + topScoreWord.wordScore + ") Velger " + topScoreWord.word + ": " + topScoreWord.AIString);
        System.out.println(", row: " + topScoreWord.row + ", column: " + topScoreWord.wordStart);
        System.out.println("left: " + topScoreWord.leftOnRack);
        for (String s : topScoreWord.words) {
            System.out.println("(" + s + ")");
        }
        //TEST SLUTT

        //hvis beste legg ikke er noe godt legg => bytte brikker
        //har lavere terskel for � bytte om det er f�rste trekk
        if (firstMove) {
            //bytter hvis ordets score er negativ, eller gir mindre enn 10 poeng
            //eller hvis cpu blir sittende igjen med minst tre bokstaver og alle er konsonanter
            if (possibleWords.firstEntry().getKey() < 0 || topScoreWord.wordScore < 10 ||
                    (topScoreWord.leftOnRack.length() >= 3 && !StringUtil.containsVowel(topScoreWord.leftOnRack)) ||
                    (topScoreWord.leftOnRack.length() >= 5 && StringUtil.vowelCount(topScoreWord.leftOnRack) == 1)) {
                System.out.println("bytter ved p� f�rste trekk");
                cpuMakeSwap();
                computersTurn = false;
                addedToThisMove.clear();
                newWordsAdded.clear();
                board.transposeBoard(boardPanel);
                return;
            }
            //hvis det ikker er f�rste legg
        } else {
            //kriterier for � bytte: negativ score eller kun konsonanter
            if (bag.tileCount() >= 7 && possibleWords.firstEntry().getKey() < 0) {
                System.out.println("bytter pga for d�rlig bestelegg");
                cpuMakeSwap();
                computersTurn = false;
                addedToThisMove.clear();
                newWordsAdded.clear();
                board.transposeBoard(boardPanel);
                return;
            }
        }

        cpuLastWord = topScoreWord;
        int topScore = topScoreWord.wordScore;
        if (!topScoreWord.vertical) {
            board.transposeBoard(boardPanel);
        }
        String toRemoveFromRemaining = "";
        for (int i = 0; i < topScoreWord.word.length(); i++) {

            //hvis bokstaven kommer fra racket, plaser p� brettet og fjern fra rack
            if (board.charBoard[topScoreWord.row][topScoreWord.wordStart+i] == '-') {
                char l = topScoreWord.word.charAt(i);
                int index = rackString.indexOf(l);
                boolean blank = false;
                if (index == -1) { //blank
                    index = rackString.indexOf('-');
                    blank = true;
                }
                //fjerner fra rack
                rackString = rackString.substring(0,index) + rackString.substring(index+1);
                Tile t;
                if (blank) {
                    t = new Tile('-');
                    t.letter = l;
                    JOptionPane.showMessageDialog(null, "Blank er " + l);
                    toRemoveFromRemaining += '-';
                } else {
                    t = new Tile(l);
                    toRemoveFromRemaining += l;
                }
                boardPanel.squareGrid[topScoreWord.row][topScoreWord.wordStart+i].placeTile(t);
            }
        }

        if (board.transposed) {
            board.transposeBoard(boardPanel);
        }

        board.updateCharBoard(addedToThisMove);

        for (Square s : addedToThisMove) {
            s.tile.isMovable = false;
        }
        updateComputerScore(topScore);
        if (addedToThisMove.size() == 7) {
            updateCPUNotes("*" + topScoreWord.word, topScore);
        } else {
            updateCPUNotes(topScoreWord.word, topScore);
        }
        updateRemaining(toRemoveFromRemaining);

        addedToThisMove.clear();
        newWordsAdded.clear();

        //trekke nye brikker
        cpuNewlyPicked = "";
        while (rackString.length() != 7 && !bag.isEmpty()) {
            Tile t = bag.pickTile();
            rackString += t.letter;
            cpuNewlyPicked += t.letter;
        }
        computersTurn = false;
        //hvis CPU g�r ut
        if(rackString.length() == 0) {
            System.out.println("kaller finishGame fra CPU");
            finishGame();
        }
        newlyAddedToBoard.clear();
        bagCountLabel.setText("Brikker igjen i posen: " + bag.tileCount());
        playerPassed = false;
    }

    //chooses what tiles to swap - antar at denne metoden kun kalles med nok brikker i posen
    void cpuMakeSwap() {
        System.out.println("cpuMakeSwap kalles med rackString: " + rackString);
        String toSwap = "";
        String tilesKept = "";
        //bytter alle hvis det bare er konsonanter
        if (!StringUtil.containsVowel(rackString)) {
            toSwap = rackString;
        } else {
            //sparer p� vokaler og bingovennlige brikker (maks en av hver) - burde v�rt to av E?
            for (int i = 0; i < 7; i++) {
                char c = rackString.charAt(i);
                if (StringUtil.isVowel(c) && tilesKept.indexOf(c) == -1) {
                    tilesKept += c;
                } else if (!StringUtil.isBingoFriendlyChar(c)) {
                    toSwap += c;
                } else if (tilesKept.indexOf(c) == -1) {
                    tilesKept += c;
                } else {
                    toSwap += c;
                }
            }
            //sjekker konsonant/vokal-ratio p� brikkene som skal spares
            double vowelRatio = StringUtil.vowelRatio(tilesKept);
            while (tilesKept.length() > 1 &&
                    (vowelRatio < 0.33 || vowelRatio > 0.67)) {
                //hvis for mange konsonanter eller vokaler
                char c;
                if (vowelRatio < 0.33) {
                    c = StringUtil.lowestScoringCons(tilesKept);
                } else {
                    c = StringUtil.lowestScoringVowel(tilesKept);
                }
                toSwap += c;
                tilesKept = StringUtil.removeChar(tilesKept, c);

                vowelRatio = StringUtil.vowelRatio(tilesKept);
            }
            if (tilesKept.length() == 1 && !StringUtil.isVowel(tilesKept.charAt(0))) {
                toSwap += tilesKept;
            }
        }
        System.out.println(", bytter disse brikkene: " + toSwap);
        computerSwap(toSwap);
    }
    


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel bagCountLabel;
    javax.swing.JButton challengeButton;
    private javax.swing.JLabel firstPlayerLabel;
    javax.swing.JScrollPane firstPlayerScrollPane;
    private javax.swing.JPanel jPanel1;
    javax.swing.JButton newGameButton;
    javax.swing.JButton passButton;
    javax.swing.JButton playButton;
    private javax.swing.JLabel remainingLabel;
    private javax.swing.JLabel secondPlayerLabel;
    javax.swing.JScrollPane secondPlayerScrollPane;
    javax.swing.JButton swapButton;
    private javax.swing.JLabel tilesLeftTitleLabel;
    javax.swing.JButton tipsButton;
    // End of variables declaration//GEN-END:variables

    private RackPanel rackPanel;
    private BoardPanel boardPanel;

    // My variables
    MDAG dictionary;
    Bag bag = new Bag();
    ArrayList<Square> newlyAddedToBoard = new ArrayList<>();
    String alphaString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ���-";

    //TODO: CPUThinker og TipsCalculator b�r egentlig gj�re det samme...
    //M� lage en egen klasse som tar inn Board og Rack og kalkulerer de beste trekkene ut fra bare det.
    CPUThinker cpuThinker;
    TipsCalculator tipsCalculator;

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
    boolean playerTips = false;
    //Solver variables

    PotentialMove cpuLastWord;
    int currentAnchorI = 0;
    int currentAnchorJ = 0;
    //ArrayList<PossibleWord> possibleWords = new ArrayList<>();
    TreeMap<Double, PotentialMove> tipsWords = new TreeMap<>(Collections.reverseOrder());
    TreeMap<Double, PotentialMove> possibleWords = new TreeMap<>(Collections.reverseOrder());
    ArrayList<String> newCPUWords;

    ArrayList<String> possibleBingos = new ArrayList<>();
    ArrayList<String> impossibleBingos = new ArrayList<>();
    //Innstillinger
    boolean retryIfWordIsNotValid = false;
    String playerName = "player";

}
