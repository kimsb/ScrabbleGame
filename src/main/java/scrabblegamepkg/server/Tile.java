package scrabblegamepkg.server;

public class Tile implements Comparable<Tile>{

    public char letter;
    int value;
    //TODO: isMovable er en GUI-ting og burde ikke være her
    public boolean isMovable = true;

    int row, column;

    public Tile(char letter) {
        this(letter, -1, -1);
    }

    public Tile(char letter, int row, int column) {
        this.letter = letter;
        this.row = row;
        this.column = column;
        value = ScoreConstants.letterScore(letter);
    }

    public boolean isBlank() {
        return letter == '-' || Character.isLowerCase(letter);
    }

    @Override
    public int compareTo(Tile tile) {
        return Character.compare(letter, tile.letter);
    }

}
