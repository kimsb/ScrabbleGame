package scrabblegamepkg.server;

import java.util.Random;

public class Game {

    private Board board;
    private Bag bag;
    private Player player, computer;

    ScrabbleGame scrabbleGame;

    //TODO: disse burde jeg vel kunne unngå?
    public boolean playerIsFirst;
    boolean computersTurn;
    boolean firstMove = true;
    int pointlessTurns = 0;

    public Game(Board board, Bag bag, String playerName, ScrabbleGame scrabbleGame) {
        this.board = board;
        this.bag = bag;
        this.scrabbleGame = scrabbleGame;

        //hvis computer starter
        if (new Random().nextInt(2) == 0) {
            computer = new Player("CPU", new Rack(bag.pickTiles(7)));
            scrabbleGame.rackString = computer.getRack().toString();
            player = new Player(playerName, new Rack(bag.pickTiles(7)));
            computersTurn = true;
        } else { //hvis pl1 starter
            playerIsFirst = true;
            player = new Player(playerName, new Rack(bag.pickTiles(7)));
            computer = new Player("CPU", new Rack(bag.pickTiles(7)));
            scrabbleGame.rackString = computer.getRack().toString();
            new TipsCalculator(scrabbleGame).execute();
        }
    }

    public Board getBoard() {
        return board;
    }

    public Player getPlayer() {
        return player;
    }

    public Player getComputer() {
        return computer;
    }

    public Bag getBag() {
        return bag;
    }

    public boolean isFirstMove() {
        return board.getCharBoard()[7][7] == '-';
    }

    //TODO: lage Turn-objekt.
    //bruke dette for å avgjøre om det har vært 6 poengløse Turns på rad
}
