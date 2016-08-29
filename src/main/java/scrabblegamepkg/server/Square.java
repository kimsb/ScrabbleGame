package scrabblegamepkg.server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Square extends javax.swing.JLabel {
    public int row;
    public int column;
    public Tile tile;
    public boolean onBoard;
    ScrabbleGame scrabbleGame;

    public Square(boolean onBoard, ScrabbleGame scrabbleGame, int row, int column) {
        this.onBoard = onBoard;
        this.scrabbleGame = scrabbleGame;
        this.row = row;
        this.column = column;

        setBackground();
        setPreferredSize(new Dimension(35, 35)); //endre 50, 50 for å endre størrelse
        setOpaque(true);
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (scrabbleGame.scrabbleGameFrame.isAnalyzing()) {
                    analyzeClick();
                } else {
                    squareClicked();
                }
            }
        });
    }

    private void setBackground() {
        if (BoardConstants.getLetterMultiplier(row, column) == 2) {
            setBackground(new Color(153, 221, 255));
        } else if (BoardConstants.getLetterMultiplier(row, column) == 3) {
            setBackground(new Color(77, 121, 255));
        } else if (BoardConstants.getWordMultiplier(row, column) == 2) {
            setBackground(new Color(255, 204, 153));
        } else if (BoardConstants.getWordMultiplier(row, column) == 3) {
            setBackground(new Color(255, 0, 0));
        } else {
            setBackground(new Color(57, 172, 172));
        }
    }

    public ImageIcon createTileIcon() {
        ImageIcon imageIcon;
        imageIcon = new javax.swing.ImageIcon(getClass().getResource("/" + (tile.isBlank() ? '-' : tile.letter) + ".png"));
        Image img = imageIcon.getImage();
        Image newimg = img.getScaledInstance(35, 35, java.awt.Image.SCALE_SMOOTH); //endre 50, 50 for å endre størrelse
        return new ImageIcon(newimg);
    }

    public boolean placeTile(Tile t) {
        if (onBoard) {
            if (!scrabbleGame.game.computersTurn && t.isBlank()) {
                //brikken er blank og må få verdi
                boolean hasValue = false;
                while (!hasValue) {
                    String blankString = JOptionPane.showInputDialog(null, "Velg bokstav for blank brikke");
                    if (blankString == null) {
                        return false;
                    }
                    blankString = blankString.toUpperCase().replaceAll("\\s","");
                    char blankLetter = blankString.charAt(0);
                    if (blankString.length() == 1 && Character.isLetter(blankLetter)) {
                        System.out.println("blanke er nå " + blankLetter);
                        t.letter = Character.toLowerCase(blankLetter);
                        hasValue = true;
                    } else {
                        JOptionPane.showMessageDialog(null, "Blanke kan bare være en bokstav");
                    }
                }
            }
            scrabbleGame.newlyAddedToBoard.add(this);
            if (scrabbleGame.game.computersTurn) {
                scrabbleGame.addedToThisMove.add(this);
            }
        }
        if (!onBoard && t.isBlank()) {
            t.letter = '-';
        }
        tile = t;
        setIcon(createTileIcon());
        return true;
    }

    //TODO: kan refaktorere denne,
    //men burde uansett heller bruke en form form GUI for Tile som kan trykkes på og flyttes
    void squareClicked() {
        if (tile != null) {
            if (tile.isMovable) {
                if (scrabbleGame.selectedSquare != null) {
                    Tile temp = tile;
                    if (placeTile(scrabbleGame.selectedSquare.tile)) {
                        scrabbleGame.selectedSquare.setIcon(createTileIcon());
                        scrabbleGame.selectedSquare.tile = temp;
                        scrabbleGame.selectedSquare = null;
                    }
                } else {
                    scrabbleGame.selectedSquare = this; //bør markeres
                }
            }
        } else {
            if (scrabbleGame.selectedSquare != null) {
                if (placeTile(scrabbleGame.selectedSquare.tile)) {
                    scrabbleGame.selectedSquare.cleanUp();
                }
                scrabbleGame.selectedSquare = null;
            }
        }
    }

    private void analyzeClick() {
        if (onBoard) {

            Object[] options = {"Avbryt", "Nedover", "Bortover" };

            JPanel panel = new JPanel();
            panel.add(new JLabel("Ord som skal legges:"));
            JTextField textField = new JTextField(15);
            panel.add(textField);

            int result = JOptionPane.showOptionDialog(null, panel, "Legg et ord",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, options,null);
            if (result == 2){
                scrabbleGame.scrabbleGameFrame.boardPanel.addWord(textField.getText().toUpperCase(), row, column, false);
            } else if (result == 1) {
                scrabbleGame.scrabbleGameFrame.boardPanel.addWord(textField.getText().toUpperCase(), row, column, true);
            }
        } else {
            String rackString = JOptionPane.showInputDialog(null, "Velg bokstaver for racket");
            if (rackString == null) {
                return;
            }
            rackString = rackString.toUpperCase();
            if (rackString.length() <= 7) {
                ArrayList<Tile> tiles = new ArrayList<>();
                for (int i = 0; i < rackString.length(); i++) {
                    char letter = rackString.charAt(i);
                    tiles.add(new Tile(letter));
                }
                scrabbleGame.scrabbleGameFrame.rackPanel.renderRack(new Rack(tiles));
            } else {
                JOptionPane.showMessageDialog(null, "Racket kan ha maks 7 brikker");
            }
        }
    }

    public void cleanUp() {
        setIcon(null);
        tile = null;
    }

}