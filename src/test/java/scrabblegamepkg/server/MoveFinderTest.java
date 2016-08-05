package scrabblegamepkg.server;

import com.BoxOfC.MDAG.MDAG;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

public class MoveFinderTest {

    MDAG dictionary;
    Board board;

    @Before
    public void setUp() {
        try {
            createDictionary();
        } catch (IOException e) {
            e.printStackTrace();
        }

        board = new Board();
        board.charBoard = getEmptyCharBoard();
        board.charBoard[1][7] = 'P';
        board.charBoard[2][7] = 'A';
        board.charBoard[3][7] = 'L';
        board.charBoard[4][7] = 'M';
        board.charBoard[5][7] = 'I';
        board.charBoard[6][7] = 'N';
        board.charBoard[7][7] = 'G';
    }

    @Test
    public void finds_ecu_for_fourty_two_points() {
        MoveFinder moveFinder = new MoveFinder();

        ArrayList<Move> allMoves = moveFinder.findAllMoves(dictionary, board, "CFELSTU");

        Move ecuMove = allMoves.stream().filter(move -> move.word.equals("ECU") && move.moveScore == 42).findFirst().get();

        assertThat(ecuMove).isNotNull();
    }

    void createDictionary() throws IOException {
        try {
            File file = new File(this.getClass().getResource("/yeslist.txt").toURI());
            dictionary = new MDAG(file);
            //fjerner ikke godkjente ord fra dictionary
            try {
                Scanner ikkeGodkjent = new Scanner(new File("ikkeGodkjent.txt"));
                while (ikkeGodkjent.hasNext()) {
                    dictionary.removeString(ikkeGodkjent.next());
                }
                ikkeGodkjent.close();
            } catch (FileNotFoundException ex) {
                System.out.println("Finner ikke ikkeGodkjent.txt");
            }
            //legger til godkjente ord til dictionary
            try {
                Scanner godkjent = new Scanner(new File("godkjent.txt"));
                while (godkjent.hasNext()) {
                    dictionary.addString(godkjent.next());
                }
                godkjent.close();
            } catch (FileNotFoundException ex) {
                System.out.println("Finner ikke godkjent.txt");
            }
        } catch (Exception e) {
            System.out.println("Laging av dictionary feiler");
            e.printStackTrace();
        }
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