package scrabblegamepkg.server;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BoardTest {

    @Test
    public void transposeCharBoard_fungerer() {
        Board board = new Board();

        Move firstMove = new Move(1, 3, false, "KIMBO", board.getCharBoard(), "BO");
        board.addToCharBoard(firstMove);

        char[][] transposedCharBoard = board.getTransposedCharBoard();

        assertThat(transposedCharBoard[3][1] == 'K').isTrue();
        assertThat(transposedCharBoard[4][1] == 'I').isTrue();
        assertThat(transposedCharBoard[5][1] == 'M').isTrue();
        assertThat(transposedCharBoard[6][1] == 'B').isTrue();
        assertThat(transposedCharBoard[7][1] == 'O').isTrue();

    }

}