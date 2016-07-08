package scrabblegamepkg;

import javax.swing.*;
import java.awt.*;

public class Tile {

    char letter;
    int value;
    ImageIcon icon;
    boolean isMovable = true;
    boolean isBlank = false;

    Tile(char l, int v) {
        letter = l;
        if (l == '-') {
            isBlank = true;
        }
        value = v;
        icon = new javax.swing.ImageIcon(getClass().getResource("/TileImages/" + l + ".png"));
        Image img = icon.getImage();
        Image newimg = img.getScaledInstance(35, 35, java.awt.Image.SCALE_SMOOTH); //endre 50, 50 for å endre størrelse
        icon = new ImageIcon(newimg);

    }

}
