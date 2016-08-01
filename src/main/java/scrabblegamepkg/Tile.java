package scrabblegamepkg;

import javax.swing.*;
import java.awt.*;

public class Tile implements Comparable<Tile>{

    char letter;
    int value;
    ImageIcon icon;
    boolean isMovable = true;
    boolean isBlank = false;

    Tile(char l) {
        letter = l;
        if (l == '-') {
            isBlank = true;
        }
        value = ScoreConstants.letterScore(l);
        icon = new javax.swing.ImageIcon(getClass().getResource("/" + l + ".png"));
        Image img = icon.getImage();
        Image newimg = img.getScaledInstance(35, 35, java.awt.Image.SCALE_SMOOTH); //endre 50, 50 for å endre størrelse
        icon = new ImageIcon(newimg);

    }

    @Override
    public int compareTo(Tile tile) {
        return Character.compare(letter, tile.letter);
    }

}
