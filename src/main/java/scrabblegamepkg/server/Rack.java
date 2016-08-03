package scrabblegamepkg.server;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Rack {

    private ArrayList<Tile> tiles;

    public Rack(ArrayList<Tile> letters) {
        this.tiles = letters;
    }

    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    public boolean isEmpty() {
        return tiles.isEmpty();
    }

    public boolean addTile(Tile tile) {
        return tiles.add(tile);
    }

    public void addTiles(ArrayList<Tile> tiles) {
        tiles.forEach(this::addTile);
    }

    public int tileCount() {
        return tiles.size();
    }

    public void alphabetize() {
        tiles = tiles
                .stream()
                .sorted(Tile::compareTo)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void removeTile(char letter) {
        tiles.remove(tiles.stream().filter(tile -> tile.letter == letter).findFirst().get());
    }

    public void removeTiles(ArrayList<Tile> tiles) {
        tiles.forEach(tile -> removeTile(tile.letter));
    }

    @Override
    public String toString() {
        String string = "";
        for (Tile tile : tiles) {
            string += tile.letter;
        }
        return string;
    }

    //Denne returnerer false også om racket har blank. usikker på ønsket oppførsel
    public boolean contains(char letter) {
        return tiles
                .stream()
                .anyMatch(tile -> tile.letter == letter);
    }

    public int rackScore() {
        return tiles.stream().mapToInt(tile -> tile.value).sum();
    }

}
