package scrabblegamepkg;

import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class RackTest {

    private ScrabbleGame scrabbleGame = mock(ScrabbleGame.class);
    private Bag bag = new Bag();

    @Test
    public void test() {
        ArrayList<Square> rackSquares = getSquareList();
        Rack rack = new Rack(bag, rackSquares);
        assertThat(rack.isEmpty()).isFalse();
    }

    private ArrayList<Square> getSquareList() {
        ArrayList<Square> squares = new ArrayList<>(7);
        for(int i = 0; i < 7; i++) {
            Square square = new Square(false, scrabbleGame);
            square.setBackground(new Color(0, 120, 98));
            squares.add(i, square);
        }
        return squares;
    }
}