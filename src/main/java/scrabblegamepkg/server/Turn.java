package scrabblegamepkg.server;

public class Turn {

    Action action;
    Move move;

    public Turn(Action action) {
        this.action = action;
    }

    public Turn(Action action, Move move) {
        this(action);
        this.move = move;
    }
}
