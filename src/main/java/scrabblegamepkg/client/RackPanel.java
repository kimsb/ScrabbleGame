package scrabblegamepkg.client;

import scrabblegamepkg.server.Rack;
import scrabblegamepkg.server.ScrabbleGame;
import scrabblegamepkg.server.Square;
import scrabblegamepkg.server.Tile;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class RackPanel extends JPanel {

    private ArrayList<Square> squares = new ArrayList<>(7);

    public RackPanel(ScrabbleGame scrabbleGame) {
        setBackground(new java.awt.Color(0, 120, 98));
        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 120, 98), 4));
        setLayout(new java.awt.GridLayout(1, 7, 2, 2));
        setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        for(int i = 0; i < 7; i++) {
            Square square = new Square(false, scrabbleGame, 0, 0);
            square.setBackground(new Color(0, 120, 98));
            squares.add(i, square);
            add(squares.get(i));
        }
    }

    public void putBack(ArrayList<Square> addedToThisMove) {

        //legger brikkene tilbake p� racken
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

    public void renderRack(Rack rack) {
        squares.forEach(Square::cleanUp);
        rack.alphabetize();
        ArrayList<Tile> tiles = rack.getTiles();
        for (int i = 0; i < rack.tileCount(); i++) {
            squares.get(i).placeTile(tiles.get(i));
        }
    }

    public void cleanUp() {
        squares.forEach(Square::cleanUp);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        squares.stream().filter(square -> square.tile != null).forEach(square -> stringBuilder.append(square.tile.letter));
        return stringBuilder.toString();
    }
}
