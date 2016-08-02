package scrabblegamepkg;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

import static scrabblegamepkg.StringUtil.isVowel;

public class Bag {

    private final ArrayList<Tile> tiles;

    Bag() {
        tiles = fillBag();
    }

    public int tileCount() {
        return tiles.size();
    }

    public boolean isEmpty() {
        return tiles.isEmpty();
    }

    public boolean add(Tile tile) {
        return tiles.add(tile);
    }

    Tile pickTile() {
        if (!isEmpty()) {
            return tiles.remove(new Random().nextInt(tileCount()));
        }
        System.out.println("prøver å trekke brikke fra tom pose");
        return null;
    }

    private ArrayList<Tile> fillBag() {
        ArrayList<Tile> tiles = new ArrayList<>();

        tiles.addAll(createTiles(7, 'A'));
        tiles.addAll(createTiles(3, 'B'));
        tiles.addAll(createTiles(1, 'C'));
        tiles.addAll(createTiles(5, 'D'));
        tiles.addAll(createTiles(9, 'E'));
        tiles.addAll(createTiles(4, 'F'));
        tiles.addAll(createTiles(4, 'G'));
        tiles.addAll(createTiles(3, 'H'));
        tiles.addAll(createTiles(5, 'I'));
        tiles.addAll(createTiles(2, 'J'));
        tiles.addAll(createTiles(4, 'K'));
        tiles.addAll(createTiles(5, 'L'));
        tiles.addAll(createTiles(3, 'M'));
        tiles.addAll(createTiles(6, 'N'));
        tiles.addAll(createTiles(4, 'O'));
        tiles.addAll(createTiles(2, 'P'));
        tiles.addAll(createTiles(6, 'R'));
        tiles.addAll(createTiles(6, 'S'));
        tiles.addAll(createTiles(6, 'T'));
        tiles.addAll(createTiles(3, 'U'));
        tiles.addAll(createTiles(3, 'V'));
        tiles.addAll(createTiles(1, 'W'));
        tiles.addAll(createTiles(1, 'Y'));
        tiles.addAll(createTiles(1, 'Æ'));
        tiles.addAll(createTiles(2, 'Ø'));
        tiles.addAll(createTiles(2, 'Å'));
        tiles.addAll(createTiles(2, '-'));

        return tiles;
    }

    private ArrayList<Tile> createTiles(int count, char letter) {
        ArrayList<Tile> tiles = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            tiles.add(new Tile(letter));
        }
        return tiles;
    }

    public int letterCount(char letter) {
        return tiles
                .stream()
                .filter(tile -> tile.letter == letter)
                .collect(Collectors.toList())
                .size();
    }

    public boolean contains(char letter) {
        return letterCount(letter) > 0;
    }

    public boolean containsLetterOrBlank(char letter) {
        return contains(letter) || contains('-');
    }

    public int vowelCount() {
        return tiles
                .stream()
                .map(tile -> tile.letter)
                .filter((c) -> isVowel(c))
                .collect(Collectors.toList())
                .size();
    }

    public ArrayList<Tile> pickTiles(int tileCount) {
        ArrayList<Tile> tiles = new ArrayList<>();
        for (int i = 0; i < tileCount; i++) {
            Tile tile = pickTile();
            if (tile != null) {
                tiles.add(tile);
            }
        }
        return tiles;
    }

//    public void swap(ArrayList<Square> toSwap, Bag bag) {
//
//        for (int i = 0; i < 7; i++) {
//            if (squares.get(i).tile == null) {
//                squares.get(i).placeTile(bag.pickTile());
//                tiles.add(squares.get(i).tile);
//            }
//        }
//        for (Square s : toSwap) {
//            if (s.tile.isBlank) {
//                s.tile.letter = '-';
//            }
//            removeTile(s.tile.letter);
//            bag.add(s.tile);
//            s.setIcon(null);
//            s.tile = null;
//        }
//
//    }
}
