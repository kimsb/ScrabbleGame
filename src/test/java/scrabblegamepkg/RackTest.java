package scrabblegamepkg;

import scrabblegamepkg.server.ScrabbleGame;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class RackTest {

    private ScrabbleGame scrabbleGame = mock(ScrabbleGame.class);
//    private ArrayList<Square> squares = getSquareList();

    /*@Test
    public void isEmpty_returns_false_when_not_empty() {
        Rack rack = new Rack(new Bag(), squares);
        assertThat(rack.isEmpty()).isFalse();

        assertThat(tiles_er_lik_squares(rack)).isTrue();
    }

    @Test
    public void isEmpty_returns_true_when_empty() {
        Rack rack = emptyRack();
        assertThat(rack.isEmpty()).isTrue();

        assertThat(tiles_er_lik_squares(rack)).isTrue();
    }

    @Test
    public void addTile_adds_tile() {
        Rack rack = emptyRack();
        rack.addTile(new Tile('K'));
        assertThat(rack.tileCount()).isEqualTo(1);

        assertThat(tiles_er_lik_squares(rack)).isTrue();
    }

    @Test
    public void rack_contains_tile_after_tile_is_added() {
        Rack rack = emptyRack();
        rack.addTile(new Tile('K'));
        assertThat(rack.contains('K'));

        assertThat(tiles_er_lik_squares(rack)).isTrue();
    }

    @Test
    public void new_rack_has_seven_tiles() {
        Rack rack = new Rack(new Bag(), squares);
        assertThat(rack.tileCount()).isEqualTo(7);

        assertThat(tiles_er_lik_squares(rack)).isTrue();
    }

    @Test
    public void removeTile_removes_a_tile() {
        Rack rack = new Rack(getBagWith("ABCDEFG"), squares);
        assertThat(rack.tileCount()).isEqualTo(7);

        rack.removeTile('A');
        assertThat(rack.tileCount()).isEqualTo(6);

        //TODO: spillflyten antar at tile har blitt lagt på brett (er altså allerede fjernet fra square)
        //assertThat(tiles_er_lik_squares(rack)).isTrue();
    }

    @Test
    public void removeTile_removes_the_correct_tile() {
        Rack rack = new Rack(getBagWith("ABCDEFG"), squares);
        assertThat(rack.contains('A')).isTrue();

        rack.removeTile('A');
        assertThat(rack.contains('A')).isFalse();

        //TODO: spillflyten antar at tile har blitt lagt på brett (er altså allerede fjernet fra square)
        //assertThat(tiles_er_lik_squares(rack)).isTrue();
    }

    @Test
    public void alphabetize_puts_letters_in_right_order() {
        Rack rack = new Rack(getBagWith("FEDGCBA"), squares);
        rack.alphabetize();

        assertThat(rack.toString()).isEqualTo("ABCDEFG");

        assertThat(tiles_er_lik_squares(rack)).isTrue();
    }

    //TODO: fjerne denne testen når square er fjernet fra Rack
    private boolean tiles_er_lik_squares(Rack rack) {
        int squaresWithTiles = (int) rack.squares.stream().filter(square -> square.tile != null).count();
        if (rack.tiles.size() != squaresWithTiles) {
            return false;
        }
        for (Square square : rack.squares) {
            if (square.tile != null) {
                if (!rack.contains(square.tile.letter)) {
                    return false;
                }
            }
        }
        return true;
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

    private Bag getBagWith(String letters) {
        Bag bag = new Bag();
        while(!bag.isEmpty()) {
            bag.pickTile();
        }
        for (int i = 0; i < letters.length(); i++) {
            bag.add(new Tile(letters.charAt(i)));
        }
        return bag;
    }

    private Rack emptyRack() {
        return new Rack(getBagWith(""), squares);
    }*/
}