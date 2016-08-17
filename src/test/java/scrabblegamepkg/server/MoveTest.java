package scrabblegamepkg.server;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MoveTest {

    private Board board;

    @Before
    public void setUp() {
        board = new Board();
        board.charBoard = getEmptyCharBoard();
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
        board.charBoard[1][7] = 'P';
        board.charBoard[2][7] = 'A';
        board.charBoard[3][7] = 'L';
        board.charBoard[4][7] = 'M';
        board.charBoard[5][7] = 'I';
        board.charBoard[6][7] = 'N';
        board.charBoard[7][7] = 'G';

        Move secondMove = new Move(3, 1, false, "JULEHEG", board.charBoard, "");

        assertThat(secondMove.moveScore).isEqualTo(84);
    }

    @Test
    public void getMoveScore_gives_correct_score_for_second_move_along_edge() {
        board.charBoard[1][7] = 'P';
        board.charBoard[2][7] = 'A';
        board.charBoard[3][7] = 'L';
        board.charBoard[4][7] = 'M';
        board.charBoard[5][7] = 'I';
        board.charBoard[6][7] = 'N';
        board.charBoard[7][7] = 'G';

        Move secondMove = new Move(8, 0, true, "ECU", board.getTransposedCharBoard(), "KIMB");

        assertThat(secondMove.moveScore).isEqualTo(42);
    }

    @Test
    public void getMoveScore_gives_correct_score_for_adding_to_word() {
        board.charBoard[1][7] = 'P';
        board.charBoard[2][7] = 'A';
        board.charBoard[3][7] = 'L';
        board.charBoard[4][7] = 'M';
        board.charBoard[5][7] = 'I';
        board.charBoard[6][7] = 'N';
        board.charBoard[7][7] = 'G';

        Move secondMove = new Move(7, 1, true, "A", board.getTransposedCharBoard(), "KIMBOW");

        assertThat(secondMove.moveScore).isEqualTo(13);
    }

    @Test
    public void getMoveScore_doesnt_add_one_letter_words() {
        board.charBoard[7][6] = 'B';
        board.charBoard[7][7] = 'A';
        board.charBoard[8][5] = 'L';
        board.charBoard[8][8] = 'Ø';
        board.charBoard[8][7] = 'P';

        Move move = new Move(6, 7, false, "T", board.charBoard, "KIMBOW");
        Move verticalMove = new Move(7, 6, true, "T", board.getTransposedCharBoard(), "KIMBOW");

        assertThat(move.word).isNotEqualTo("T");
        assertThat(verticalMove.word).isNotEqualTo("T");

        assertThat(move.moveScore).isEqualTo(verticalMove.moveScore);
    }

    private char[][] getEmptyCharBoard() {
        char[][] charBoard = new char[15][15];

        //fyller charBoard med '-'
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                charBoard[i][j] = '-';
            }
        }
        return charBoard;
    }
}