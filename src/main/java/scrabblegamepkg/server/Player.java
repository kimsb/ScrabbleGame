package scrabblegamepkg.server;

public class Player {

    private final String name;
    private Rack rack;
    private int score;

    public Player(String name, Rack rack) {
        this.name = name;
        this.rack = rack;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public int addScore(int toAdd) {
        return score += toAdd;
    }

    public int removeScore(int toRemove) {
        return score -= toRemove;
    }

    public Rack getRack() {
        return rack;
    }
}
