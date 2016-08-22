package scrabblegamepkg.server;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MoveTest {

    private Board board;

    @Before
    public void setUp() {
        board = new Board();
    }

    @Test
    public void getMoveScore_gives_correct_score_for_first_move() {
        Move firstMove = new Move(7, 7, true, "TRAN", board.getTransposedCharBoard(), "KIM");

        assertThat(firstMove.moveScore).isEqualTo(8);
    }

    @Test
    public void getMoveScore_gives_no_score_for_blank_tile() {
        Move firstMove = new Move(7, 7, true, "TrAN", board.getTransposedCharBoard(), "KIM");

        assertThat(firstMove.moveScore).isEqualTo(6);
    }

    @Test
    public void getMoveScore_gives_correct_score_for_second_move() {
        Move firstMove = new Move(7, 1, true, "PALMING", board.getCharBoard(), "");
        board.addToCharBoard(firstMove);

        Move secondMove = new Move(3, 1, false, "JULEHEG", board.getCharBoard(), "");

        assertThat(secondMove.moveScore).isEqualTo(84);
    }

    @Test
    public void getMoveScore_gives_correct_score_for_second_move_along_edge() {
        Move firstMove = new Move(7, 1, true, "PALMING", board.getCharBoard(), "");
        board.addToCharBoard(firstMove);

        Move secondMove = new Move(8, 0, true, "ECU", board.getTransposedCharBoard(), "KIMB");

        assertThat(secondMove.moveScore).isEqualTo(42);
    }

    @Test
    public void getMoveScore_gives_correct_score_for_adding_to_word() {
        Move firstMove = new Move(7, 1, true, "PALMING", board.getCharBoard(), "");
        board.addToCharBoard(firstMove);

        Move secondMove = new Move(7, 1, true, "A", board.getTransposedCharBoard(), "KIMBOW");

        assertThat(secondMove.moveScore).isEqualTo(13);
    }

    @Test
    public void getMoveScore_doesnt_add_one_letter_words() {

        Move firstMove = new Move(7, 6, false, "BA", board.getCharBoard(), "KIMBO");
        board.addToCharBoard(firstMove);
        Move secondMove = new Move(8, 5, false, "LØP", board.getCharBoard(), "KIMB");
        board.addToCharBoard(secondMove);

        Move move = new Move(6, 7, false, "T", board.getCharBoard(), "KIMBOW");
        Move verticalMove = new Move(7, 6, true, "T", board.getTransposedCharBoard(), "KIMBOW");

        assertThat(move.word).isNotEqualTo("T");
        assertThat(verticalMove.word).isNotEqualTo("T");

        assertThat(move.moveScore).isEqualTo(verticalMove.moveScore);
    }
}