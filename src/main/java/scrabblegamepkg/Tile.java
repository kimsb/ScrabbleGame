package scrabblegamepkg;

public class Tile implements Comparable<Tile>{

    char letter;
    int value;
    boolean isMovable = true;

    Tile(char l) {
        letter = l;
        value = ScoreConstants.letterScore(l);
    }

    public boolean isBlank() {
        return letter == '-';
    }

    @Override
    public int compareTo(Tile tile) {
        return Character.compare(letter, tile.letter);
    }

}
