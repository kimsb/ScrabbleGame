package scrabblegamepkg.client;

import scrabblegamepkg.server.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class BoardPanel extends JPanel {

    public Square[][] squareGrid = new Square[15][15];

    public BoardPanel(ScrabbleGame scrabbleGame) {
        setBackground(new java.awt.Color(0, 0, 0));
        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        setLayout(new java.awt.GridLayout(15, 15, 2, 2));

        //lager brettet
        setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                Square square = new Square(true, scrabbleGame, i, j);
                squareGrid[i][j] = square;
                add(squareGrid[i][j]);
            }
        }
    }

    public void placeMove(Move move) {
        for (int i = 0; i < move.word.length(); i++) {
            Square square = move.vertical ? squareGrid[move.startColumn + i][move.row] : squareGrid[move.row][move.startColumn + i];

            //hvis bokstaven ikke er på brettet
            if (square.tile == null) {
                char letter = move.word.charAt(i);
                Tile tile;
                if (Character.isLowerCase(letter)) {
                    tile = new Tile('-');
                    tile.letter = letter;
                    JOptionPane.showMessageDialog(null, "Blank er " + letter);
                } else {
                    tile = new Tile(letter);
                }
                tile.isMovable = false;
                square.placeTile(tile);
            }
        }
    }

    public ArrayList<Square> getSquaresWithMovableTiles() {
        ArrayList<Square> squares = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                Square square = squareGrid[i][j];
                if (square.tile != null && square.tile.isMovable) {
                    squares.add(square);
                }
            }
        }
        return squares;
    }

    public void cleanUp() {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                squareGrid[i][j].setIcon(null);
                squareGrid[i][j].tile = null;
            }
        }
    }

    public void lockTiles() {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                Square square = squareGrid[i][j];
                if (square.tile != null && square.tile.isMovable) {
                    square.tile.isMovable = false;

                    //TEST
                    if (!square.onBoard) {
                        System.out.println("Boardpanel - ikke onBoard, row: " + square.row + ", col: " + square.column + ", t: " + square.tile.letter);
                    }

                }
            }
        }
    }
}
