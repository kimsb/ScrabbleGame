package scrabblegamepkg.server;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BoardTest {

    @Test
    public void transposeCharBoard_fungerer() {
        Board board = new Board();
        board.charBoard = getEmptyCharBoard();
        board.charBoard[1][3] = 'K';
        board.charBoard[1][4] = 'I';
        board.charBoard[1][5] = 'M';
        board.charBoard[1][6] = 'B';
        board.charBoard[1][7] = 'O';

        char[][] transposedCharBoard = board.getTransposedCharBoard();

        assertThat(transposedCharBoard[3][1] == 'K').isTrue();
        assertThat(transposedCharBoard[4][1] == 'I').isTrue();
        assertThat(transposedCharBoard[5][1] == 'M').isTrue();
        assertThat(transposedCharBoard[6][1] == 'B').isTrue();
        assertThat(transposedCharBoard[7][1] == 'O').isTrue();

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