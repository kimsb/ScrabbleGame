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

    public ArrayList<Tile> getNewlyAddedTiles() {
        ArrayList<Tile> tiles = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                Square square = squareGrid[i][j];
                if (square.tile != null && square.tile.isMovable) {
                    tiles.add(new Tile(square.tile.letter, i, j));
                }
            }
        }
        return tiles;
    }

    public void cleanUpUnlockedTiles() {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (squareGrid[i][j].tile.isMovable) {
                    squareGrid[i][j].cleanUp();
                }
            }
        }
    }

    public void cleanUp() {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                squareGrid[i][j].cleanUp();
            }
        }
    }

    public void lockTiles() {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                Square square = squareGrid[i][j];
                if (square.tile != null && square.tile.isMovable) {
                    square.tile.isMovable = false;
                }
            }
        }
    }

    public void render(char[][] charBoard) {

        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                Square square = squareGrid[i][j];
                if (square.tile != null &&  square.tile.letter != charBoard[i][j]) {
                    if (charBoard[i][j] == '-') {
                        square.cleanUp();
                    } else {
                        square.placeTile(new Tile(charBoard[i][j]));
                    }
                }
            }
        }

    }

    public char[][] getCharBoard() {
        char[][] charBoard = new char[15][15];
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (squareGrid[i][j].tile != null) {
                    charBoard[i][j] = squareGrid[i][j].tile.letter;
                } else {
                    charBoard[i][j] = '-';
                }
            }
        }
        return charBoard;
    }

    public void addWord(String feltString, int row, int column, boolean vertical) {
        if (vertical) {
            for (int i = 0; i < 15 && i < feltString.length(); i++) {
                Square square = squareGrid[row+i][column];
                decideTile(square, feltString.charAt(i));
            }
        } else {
            for (int j = 0; j < 15 && j < feltString.length(); j++) {
                Square square = squareGrid[row][column+j];
                decideTile(square, feltString.charAt(j));
            }
        }
    }

    private void decideTile(Square square, char letter) {
        if (letter == ' ') {
            square.cleanUp();
        } else if (Character.isAlphabetic(letter)) {
            square.tile = new Tile(letter);
            square.setIcon(square.createTileIcon());
        }
    }
}
