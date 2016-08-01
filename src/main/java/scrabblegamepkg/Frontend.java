package scrabblegamepkg;

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
}
