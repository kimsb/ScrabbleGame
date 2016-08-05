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
        Move firstMove = new Move("TRAN", 7, 9, true, 8, "TRAN", board, "KIM");

        assertThat(firstMove.moveScore).isEqualTo(8);
    }

    @Test
    public void getMoveScore_gives_no_score_for_blank_tile() {
        Move firstMove = new Move("TrAN", 7, 9, true, 8, "T-AN", board, "KIM");

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

        Move secondMove = new Move("JULEHELG", 3, 8, false, 89, "JULEHEG", board, "");

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

        Move secondMove = new Move("ECU", 8, 2, true, 99, "ECU", board, "KIMB");

        assertThat(secondMove.moveScore).isEqualTo(42);
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