package scrabblegamepkg.client;

import com.sun.codemodel.internal.JOp;
import scrabblegamepkg.server.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Map;

public class ScrabbleGameFrame extends JFrame {

    ScrabbleGame scrabbleGame;

    private javax.swing.JLabel bagCountLabel;
    public javax.swing.JButton analyzeButton;
    private javax.swing.JLabel firstPlayerLabel;
    private javax.swing.JScrollPane firstPlayerScrollPane;
    private javax.swing.JPanel jPanel1;
    public javax.swing.JButton newGameButton;
    javax.swing.JButton passButton;
    javax.swing.JButton playButton;
    private javax.swing.JLabel remainingLabel;
    private javax.swing.JLabel secondPlayerLabel;
    private javax.swing.JScrollPane secondPlayerScrollPane;
    javax.swing.JButton swapButton;
    private javax.swing.JLabel tilesLeftTitleLabel;
    public javax.swing.JButton tipsButton;


    public RackPanel rackPanel;
    public BoardPanel boardPanel;
    private boolean isAnalyzing;

    public ScrabbleGameFrame(ScrabbleGame scrabbleGame) {
        this.scrabbleGame = scrabbleGame;

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

        initComponents();

    }

    private void initComponents() {

        boardPanel = new BoardPanel(scrabbleGame);
        rackPanel = new RackPanel(scrabbleGame);
        playButton = new javax.swing.JButton();
        analyzeButton = new javax.swing.JButton();
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
        playButton.setEnabled(false);
        playButton.addActionListener(this::playButtonActionPerformed);

        analyzeButton.setText("Start Analyse");
        analyzeButton.setEnabled(true);
        analyzeButton.addActionListener(this::analyzeButtonActionPerformed);

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
        swapButton.setEnabled(false);
        swapButton.addActionListener(this::swapButtonActionPerformed);

        passButton.setText("Pass");
        passButton.setEnabled(false);
        passButton.addActionListener(this::passButtonActionPerformed);

        newGameButton.setText("Nytt spill");
        newGameButton.setEnabled(true);
        newGameButton.addActionListener(this::newGameButtonActionPerformed);

        tipsButton.setText("Tips");
        tipsButton.setEnabled(false);
        tipsButton.addActionListener(this::tipsButtonActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(0, 22, Short.MAX_VALUE)
                                                .addComponent(analyzeButton)
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
                                                                .addComponent(analyzeButton)
                                                                .addComponent(newGameButton)
                                                                .addComponent(tipsButton)))))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }

    private void playButtonActionPerformed(ActionEvent evt) {
        try {
            ArrayList<Tile> newlyAddedTiles = boardPanel.getNewlyAddedTiles();
            if (!newlyAddedTiles.isEmpty()) {
                renderGame(scrabbleGame.playAction(newlyAddedTiles));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    private void renderGame(Game game) {
        boardPanel.render(game.getBoard().getCharBoard());
        rackPanel.renderRack(game.getPlayer().getRack());
        boardPanel.lockTiles();
        renderNotes(game);
    }

    private void renderNotes(Game game) {

        remainingLabel.setText(Notes.getRemainingTilesNotes(game.getBoard().getCharBoard()));

        firstPlayerLabel.setText(game.playerIsFirst ? Notes.getPlayerNotes(game.getPlayer()) : Notes.getPlayerNotes(game.getComputer()));
        secondPlayerLabel.setText(game.playerIsFirst ? Notes.getPlayerNotes(game.getComputer()) : Notes.getPlayerNotes(game.getPlayer()));

        tilesLeftTitleLabel.setText("<html><body><b><u>Gjenværende brikker:</u></b></body></html>");
        bagCountLabel.setText("Brikker igjen i posen: " + game.getBag().tileCount());

        firstPlayerScrollPane.getVerticalScrollBar().setValue(firstPlayerScrollPane.getVerticalScrollBar().getMaximum());
        secondPlayerScrollPane.getVerticalScrollBar().setValue(secondPlayerScrollPane.getVerticalScrollBar().getMaximum());
    }

    private void clearNotes() {
        remainingLabel.setText("");
        firstPlayerLabel.setText("");
        secondPlayerLabel.setText("");
        tilesLeftTitleLabel.setText("");
        bagCountLabel.setText("");
    }

    private void analyzeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        clearNotes();
        isAnalyzing = true;
        enableGameButtons(false);
        boardPanel.cleanUp();
        rackPanel.cleanUp();
        tipsButton.setEnabled(true);
    }

    private void swapButtonActionPerformed(ActionEvent evt) {

        renderGame(scrabbleGame.swapAction());
    }

    private void passButtonActionPerformed(java.awt.event.ActionEvent evt) {
        renderGame(scrabbleGame.pass(false));
    }

    private void tipsButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (isAnalyzing()) {
            renderTips(scrabbleGame.analyzeTipsAction(boardPanel.getCharBoard(), rackPanel.toString()));
        } else {
            renderTips(scrabbleGame.tipsAction());
        }
    }

    private void renderTips(TipsCalculator tipsCalculator) {
        String tipsString = "<html><body>";
        if (tipsCalculator.tipsWords.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Det finnes ingen mulige legg");
        } else {
            tipsString += "<b><u>Høyest scorende legg:</u></b>";

            int count = 0;
            for (Map.Entry<Double, Move> entry : tipsCalculator.tipsWords.entrySet()) {
                if (count < 10) {
                    Move poss = entry.getValue();
                    tipsString += ("<br>" + poss.moveScore + ": " + poss.word + poss.getTipsPlacementString());
                    count++;
                } else {
                    break;
                }
            }
            tipsString += "</body></html>";

            if (isAnalyzing()) {
                Object[] options = {"Avbryt", "Legg beste"};
                int n = JOptionPane.showOptionDialog(this,
                        tipsString,
                        "Message",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,     //do not use a custom Icon
                        options,  //the titles of buttons
                        options[1]); //default button title
                if (n == 1) {
                    Move move = tipsCalculator.tipsWords.firstEntry().getValue();
                    boardPanel.addWord(move.word, move.vertical ? move.startColumn : move.row,
                            move.vertical ? move.row : move.startColumn, move.vertical);
                }
            } else {
                JOptionPane.showMessageDialog(this, tipsString);
            }
        }
    }

    private void newGameButtonActionPerformed(java.awt.event.ActionEvent evt) {
        isAnalyzing = false;
        if (!"".equals(bagCountLabel.getText())) {
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
                renderGame(scrabbleGame.newGameAction());
            }
        } else {
            renderGame(scrabbleGame.newGameAction());
        }
        enableGameButtons(true);

    }

    public void enableGameButtons(boolean enable) {
        playButton.setEnabled(enable);
        swapButton.setEnabled(enable);
        passButton.setEnabled(enable);
        tipsButton.setEnabled(enable);
    }

    public boolean isAnalyzing() {
        return isAnalyzing;
    }
}
