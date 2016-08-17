package scrabblegamepkg.server;

import java.util.ArrayList;

public class Player {

    private final String name;
    private Rack rack;
    private int score;
    private ArrayList<Turn> turns = new ArrayList<>();

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

    public void addTurn(Turn turn) {
        turns.add(turn);
    }

    public ArrayList<Turn> getTurns() {
        return turns;
    }
}