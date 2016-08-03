package scrabblegamepkg.client;

import scrabblegamepkg.server.Rack;
import scrabblegamepkg.server.ScrabbleGame;
import scrabblegamepkg.server.Square;
import scrabblegamepkg.server.Tile;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Frontend {

    public JPanel getRackPanel(ScrabbleGame scrabbleGame, ArrayList<Square> squares) {
        JPanel rackPanel = new JPanel();
        rackPanel.setBackground(new java.awt.Color(0, 120, 98));
        rackPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 120, 98), 4));
        rackPanel.setLayout(new java.awt.GridLayout(1, 7, 2, 2));
        rackPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        for(int i = 0; i < 7; i++) {
            Square square = new Square(false, scrabbleGame);
            square.setBackground(new Color(0, 120, 98));
            squares.add(i, square);
            rackPanel.add(squares.get(i));
        }

        return rackPanel;
    }

    public void putBack(ArrayList<Square> squares, ArrayList<Square> addedToThisMove) {

        //legger brikkene tilbake på racken
        for (int i = 0; i < 7; i++) {
            if (squares.get(i).tile == null && !addedToThisMove.isEmpty()) {
                Square s = addedToThisMove.remove(0);
                if (s.tile.isBlank()) {
                    squares.get(i).tile = new Tile('-');
                } else {
                    squares.get(i).tile = s.tile;
                }
                squares.get(i).setIcon(s.createTileIcon());
                s.setIcon(null);
                s.tile = null;
            }
        }
    }

    public void renderRack(ArrayList<Square> squares, Rack rack) {
        squares.forEach(Square::cleanUp);
        rack.alphabetize();
        ArrayList<Tile> tiles = rack.getTiles();
        for (int i = 0; i < rack.tileCount(); i++) {
            squares.get(i).placeTile(tiles.get(i));
        }
    }
}
