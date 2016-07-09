package scrabblegamepkg;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

import static scrabblegamepkg.ComputerAI.*;

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

    public ArrayList<Tile> getTiles() {
        return tiles;
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

        tiles.addAll(createTiles(7, 'A', 1));
        tiles.addAll(createTiles(3, 'B', 4));
        tiles.addAll(createTiles(1, 'C', 10));
        tiles.addAll(createTiles(5, 'D', 1));
        tiles.addAll(createTiles(9, 'E', 1));
        tiles.addAll(createTiles(4, 'F', 2));
        tiles.addAll(createTiles(4, 'G', 2));
        tiles.addAll(createTiles(3, 'H', 3));
        tiles.addAll(createTiles(5, 'I', 1));
        tiles.addAll(createTiles(2, 'J', 4));
        tiles.addAll(createTiles(4, 'K', 2));
        tiles.addAll(createTiles(5, 'L', 1));
        tiles.addAll(createTiles(3, 'M', 2));
        tiles.addAll(createTiles(6, 'N', 1));
        tiles.addAll(createTiles(4, 'O', 2));
        tiles.addAll(createTiles(2, 'P', 4));
        tiles.addAll(createTiles(6, 'R', 1));
        tiles.addAll(createTiles(6, 'S', 1));
        tiles.addAll(createTiles(6, 'T', 1));
        tiles.addAll(createTiles(3, 'U', 4));
        tiles.addAll(createTiles(3, 'V', 4));
        tiles.addAll(createTiles(1, 'W', 8));
        tiles.addAll(createTiles(1, 'Y', 6));
        tiles.addAll(createTiles(1, 'Æ', 6));
        tiles.addAll(createTiles(2, 'Ø', 5));
        tiles.addAll(createTiles(2, 'Å', 4));
        tiles.addAll(createTiles(2, '-', 0));

        return tiles;
    }

    private ArrayList<Tile> createTiles(int count, char letter, int score) {
        ArrayList<Tile> tiles = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            tiles.add(new Tile(letter, score));
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
                .filter((c) -> ComputerAI.isVowel(c))
                .collect(Collectors.toList())
                .size();
    }
}
