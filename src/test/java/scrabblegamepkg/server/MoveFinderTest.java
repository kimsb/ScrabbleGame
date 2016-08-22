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

    }

    @Test
    public void finds_ecu_for_fourty_two_points() {

        Move firstMove = new Move(7, 1, true, "PALMING", board.getCharBoard(), "");
        board.addToCharBoard(firstMove);

        MoveFinder moveFinder = new MoveFinder();

        ArrayList<Move> allMoves = moveFinder.findAllMoves(dictionary, board, "CFELSTU");

        Move ecuMove = allMoves.stream().filter(move -> move.word.equals("ECU") && move.moveScore == 42).findFirst().get();

        assertThat(ecuMove).isNotNull();
    }

    @Test
    public void finds_both_bingos_with_blank() {
        MoveFinder moveFinder = new MoveFinder();

        ArrayList<Move> allMoves = moveFinder.findAllMoves(dictionary, board, "HÅTASK-");

        assertThat(allMoves.stream().filter(move -> move.word.equals("HÅTASKe")).findAny().get()).isNotNull();
        assertThat(allMoves.stream().filter(move -> move.word.equals("HÅTASKa")).findAny().get()).isNotNull();

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

}