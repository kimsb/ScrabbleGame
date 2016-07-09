package scrabblegamepkg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class Square extends javax.swing.JLabel {
    int row;
    int column;
    Tile tile;
    String multiplier = "";
    boolean onBoard;
    ScrabbleGame scrabbleGame;

    Square(boolean onBoard, ScrabbleGame scrabbleGame) {
        this.onBoard = onBoard;
        this.scrabbleGame = scrabbleGame;

        setPreferredSize(new Dimension(35, 35)); //endre 50, 50 for å endre størrelse
        setOpaque(true);
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (!scrabbleGame.computersTurn) {
                    squareClicked();
                }
            }
        });
    }

    void setMultiplier(String mult) {
        multiplier = mult;
        if (mult.equals("DL")) {
            setBackground(new Color(153, 221, 255));
        } else if (mult.equals("TL")) {
            setBackground(new Color(77, 121, 255));
        } else if (mult.equals("DW")) {
            setBackground(new Color(255, 204, 153));
        } else if (mult.equals("TW")) {
            setBackground(new Color(255, 0, 0));
        }
    }

    boolean placeTile(Tile t) {
        if (onBoard) {
            if (!scrabbleGame.computersTurn && t.isBlank) {
                //brikken er blank å må få verdi
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
                        t.letter = blankLetter;
                        hasValue = true;
                    } else {
                        JOptionPane.showMessageDialog(null, "Blanke kan bare være en bokstav");
                    }
                }
            }
            scrabbleGame.newlyAddedToBoard.add(this);
            if (scrabbleGame.computersTurn) {
                scrabbleGame.addedToThisMove.add(this);
            }
        }
        if (!onBoard && t.isBlank) {
            t.letter = '-';
        }
        tile = t;
        setIcon(t.icon);
        return true;
    }

    void squareClicked() {
        if (tile != null) {
            if (tile.isMovable) {
                if (scrabbleGame.selectedSquare != null) {
                    Tile temp = tile;
                    if (placeTile(scrabbleGame.selectedSquare.tile)) {
                        scrabbleGame.selectedSquare.setIcon(temp.icon);
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

    public void cleanUp() {
        setIcon(null);
        tile = null;
    }

}