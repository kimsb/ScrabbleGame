package scrabblegamepkg.client;

import org.junit.Test;
import scrabblegamepkg.server.Rack;
import scrabblegamepkg.server.ScrabbleGame;
import scrabblegamepkg.server.Tile;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class RackPanelTest {

    ScrabbleGame scrabbleGame = mock(ScrabbleGame.class);

    @Test
    public void toString_returnerer_riktig_string() {
        String rackString = "KIMÆREN";
        RackPanel rackPanel = getRackPanel(rackString);

        char[] rackStringChars = rackString.toCharArray();
        char[] rackPanelChars = rackPanel.toString().toCharArray();
        Arrays.sort(rackPanelChars);
        Arrays.sort(rackStringChars);

        assertThat(Arrays.equals(rackStringChars, rackPanelChars)).isTrue();
    }


    private RackPanel getRackPanel(String letters) {
        ArrayList<Tile> tiles = new ArrayList<>();
        for (char letter : letters.toCharArray()) {
            tiles.add(new Tile(letter));
        }
        Rack rack = new Rack(tiles);
        RackPanel rackPanel = new RackPanel(scrabbleGame);
        rackPanel.renderRack(rack);
        return rackPanel;
    }
}