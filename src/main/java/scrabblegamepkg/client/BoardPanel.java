package scrabblegamepkg.client;

import scrabblegamepkg.server.*;

import javax.swing.*;
import java.awt.*;

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
                    //JLabel square = new JLabel("");
                    Square square = new Square(true, scrabbleGame);
                    square.setBackground(new Color(57, 172, 172));
                    square.row = i;
                    square.column = j;
                    //hvis tekst i midten
                    //square.setHorizontalAlignment(SwingConstants.CENTER);
                    squareGrid[i][j] = square;
                    add(squareGrid[i][j]);
                }
            }

            //setter TW-farger
            squareGrid[0][0].setMultiplier("TW");
            squareGrid[0][7].setMultiplier("TW");
            squareGrid[0][14].setMultiplier("TW");
            squareGrid[7][0].setMultiplier("TW");
            squareGrid[7][14].setMultiplier("TW");
            squareGrid[14][0].setMultiplier("TW");
            squareGrid[14][7].setMultiplier("TW");
            squareGrid[14][14].setMultiplier("TW");

            //setter DW-farger
            squareGrid[1][1].setMultiplier("DW");
            squareGrid[2][2].setMultiplier("DW");
            squareGrid[3][3].setMultiplier("DW");
            squareGrid[4][4].setMultiplier("DW");
            squareGrid[10][10].setMultiplier("DW");
            squareGrid[11][11].setMultiplier("DW");
            squareGrid[12][12].setMultiplier("DW");
            squareGrid[13][13].setMultiplier("DW");
            squareGrid[1][13].setMultiplier("DW");
            squareGrid[2][12].setMultiplier("DW");
            squareGrid[3][11].setMultiplier("DW");
            squareGrid[4][10].setMultiplier("DW");
            squareGrid[10][4].setMultiplier("DW");
            squareGrid[11][3].setMultiplier("DW");
            squareGrid[12][2].setMultiplier("DW");
            squareGrid[13][1].setMultiplier("DW");
            //stjerne
            squareGrid[7][7].setMultiplier("DW");

            //setter TL-farger
            squareGrid[1][5].setMultiplier("TL");
            squareGrid[1][9].setMultiplier("TL");
            squareGrid[5][1].setMultiplier("TL");
            squareGrid[5][5].setMultiplier("TL");
            squareGrid[5][9].setMultiplier("TL");
            squareGrid[5][13].setMultiplier("TL");
            squareGrid[9][1].setMultiplier("TL");
            squareGrid[9][5].setMultiplier("TL");
            squareGrid[9][9].setMultiplier("TL");
            squareGrid[9][13].setMultiplier("TL");
            squareGrid[13][5].setMultiplier("TL");
            squareGrid[13][9].setMultiplier("TL");

            //setter DL-farger
            squareGrid[0][3].setMultiplier("DL");
            squareGrid[0][11].setMultiplier("DL");
            squareGrid[2][6].setMultiplier("DL");
            squareGrid[2][8].setMultiplier("DL");
            squareGrid[3][0].setMultiplier("DL");
            squareGrid[3][7].setMultiplier("DL");
            squareGrid[3][14].setMultiplier("DL");
            squareGrid[6][2].setMultiplier("DL");
            squareGrid[6][6].setMultiplier("DL");
            squareGrid[6][8].setMultiplier("DL");
            squareGrid[6][12].setMultiplier("DL");
            squareGrid[7][3].setMultiplier("DL");
            squareGrid[7][11].setMultiplier("DL");
            squareGrid[8][2].setMultiplier("DL");
            squareGrid[8][6].setMultiplier("DL");
            squareGrid[8][8].setMultiplier("DL");
            squareGrid[8][12].setMultiplier("DL");
            squareGrid[11][0].setMultiplier("DL");
            squareGrid[11][7].setMultiplier("DL");
            squareGrid[11][14].setMultiplier("DL");
            squareGrid[12][6].setMultiplier("DL");
            squareGrid[12][8].setMultiplier("DL");
            squareGrid[14][3].setMultiplier("DL");
            squareGrid[14][11].setMultiplier("DL");
        }

    public void placeMove(Move move) {
        for (int i = 0; i < move.word.length(); i++) {
            Square square = move.vertical ? squareGrid[move.wordStart+i][move.row] : squareGrid[move.row][move.wordStart+i];

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
}
