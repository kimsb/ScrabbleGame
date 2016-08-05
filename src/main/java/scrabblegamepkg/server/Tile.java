package scrabblegamepkg.server;

public class Tile implements Comparable<Tile>{

    public char letter;
    int value;
    public boolean isMovable = true;

    public Tile(char l) {
        letter = l;
        value = ScoreConstants.letterScore(l);
    }

    public boolean isBlank() {
        return letter == '-' || Character.isLowerCase(letter);
    }

    @Override
    public int compareTo(Tile tile) {
        return Character.compare(letter, tile.letter);
    }

}
