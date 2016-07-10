package scrabblegamepkg;

import java.util.ArrayList;
import java.util.Arrays;

public class Rack {

    ArrayList<Tile> tiles = new ArrayList<>();
    ArrayList<Square> squares;

    public Rack(Bag bag, ArrayList<Square> squares) {
        this.squares = squares;
        squares.stream().forEach(Square::cleanUp);

        fill(bag);
    }

    public boolean isEmpty() {
        return tiles.isEmpty();
    }

    public boolean addTile(Tile tile) {
        return tiles.add(tile);
    }

    public int tileCount() {
        return tiles.size();
    }

    public void alphabetize() {
        tiles
                .stream()
                .sorted((tile1, tile2) -> Character.compare(tile1.letter, tile2.letter));

        alphabetizeSquares();
    }

    public void removeTile(char letter) {
         tiles.remove(tiles.stream().filter(tile -> tile.letter == letter).findFirst().get());
    }

    public void fill(Bag bag) {
        while (tileCount() < 7 && !bag.isEmpty()) {
            Tile tile = bag.pickTile();

            addTile(tile);

            //Denne m� jeg f� fjerna, men bruker square for � tegne racket n�...
            squares.stream().filter(square -> square.tile == null).findFirst().get().placeTile(tile);

        }
    }

    public void putBack(ArrayList<Square> addedToThisMove) {

        //legger brikkene tilbake p� racken
        for (int i = 0; i < 7; i++) {
            if (squares.get(i).tile == null && !addedToThisMove.isEmpty()) {
                Square s = addedToThisMove.remove(0);
                if (s.tile.isBlank) {
                    squares.get(i).tile = new Tile('-');
                } else {
                    squares.get(i).tile = s.tile;
                }
                squares.get(i).setIcon(s.tile.icon);
                s.setIcon(null);
                s.tile = null;

                tiles.add(squares.get(i).tile);

            }
        }
    }

    void alphabetizeSquares() {
        String s = "";
        for (int i = 0; i < 7; i++) {
            if (squares.get(i).tile != null) {
                s += squares.get(i).tile.letter;
            }
        }
        char[] sorted = s.toCharArray();
        Arrays.sort(sorted);
        for (int i = 0; i < sorted.length; i++) {
            squares.get(i).placeTile(new Tile(sorted[i]));
        }
        for (int i = sorted.length; i < 7; i++) {
            squares.get(i).cleanUp();
        }
    }

    @Override
    public String toString() {
        String string = "";
        for (Tile tile : tiles) {
            string += tile.letter;
        }
        return string;
    }

    public void swap(ArrayList<Square> toSwap, Bag bag) {

        for (int i = 0; i < 7; i++) {
            if (squares.get(i).tile == null) {
                squares.get(i).placeTile(bag.pickTile());
                tiles.add(squares.get(i).tile);
            }
        }
        for (Square s : toSwap) {
            if (s.tile.isBlank) {
                s.tile.letter = '-';
            }
            bag.add(s.tile);
            s.setIcon(null);
            s.tile = null;
        }

    }

    //Denne returnerer false ogs� om racket har blank. usikker p� �nsket oppf�rsel
    public boolean contains(char letter) {
        return tiles
                .stream()
                .anyMatch(tile -> tile.letter == letter);
    }
}