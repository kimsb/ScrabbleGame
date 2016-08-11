package scrabblegamepkg.server;

import com.BoxOfC.MDAG.MDAG;
import scrabblegamepkg.client.BoardPanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import static scrabblegamepkg.server.ScrabbleGame.*;

//TODO: her er det mye � ta tak i!
// - skal ikke gj�re frontend-ting
// - endrer mange variabler i scrabblegame direkte...
public class CPUThinker extends SwingWorker<Void, Void> {

    ScrabbleGame scrabbleGame;

    public CPUThinker(ScrabbleGame scrabbleGame) {
        this.scrabbleGame = scrabbleGame;
    }

    @Override
    protected Void doInBackground() {
        scrabbleGame.scrabbleGameFrame.enableButtons(false);
        computerAI();
        return null;
    }

    @Override
    protected void done() {
        scrabbleGame.scrabbleGameFrame.enableButtons(true);
        scrabbleGame.scrabbleGameFrame.tipsButton.setEnabled(false);
        new TipsCalculator(scrabbleGame).execute();
        JScrollBar verticalScrollBar = scrabbleGame.scrabbleGameFrame.firstPlayerScrollPane.getVerticalScrollBar();
        verticalScrollBar.setValue(verticalScrollBar.getMaximum());
        verticalScrollBar = scrabbleGame.scrabbleGameFrame.secondPlayerScrollPane.getVerticalScrollBar();
        verticalScrollBar.setValue(verticalScrollBar.getMaximum());

    }

    void computerAI() {

        try {
            System.out.println("STARET COMPUTERAI()");

            //calculating vowelRatio in bag + players rack
            double vowelsLeft = scrabbleGame.bag.vowelCount() + StringUtil.vowelCount(scrabbleGame.rack.toString());
            int lettersLeft = scrabbleGame.bag.tileCount() + scrabbleGame.rack.tileCount();

            scrabbleGame.vowelRatioLeft = vowelsLeft / lettersLeft;

            scrabbleGame.previousRackString = scrabbleGame.rackString;
            System.out.println("ComputerAI starts.. cpuRack: " + scrabbleGame.rackString);
            scrabbleGame.newlyAddedToBoard.clear();
            if (!scrabbleGame.computersTurn) {
                System.out.println("ikke cpus tur");
                return;
            } else if (scrabbleGame.rackString.length() == 0) {
                System.out.println("FERDIG!");
                return;
            }

            scrabbleGame.rackStringCpy = scrabbleGame.rackString;

            //TEST - skal fjernes
            sjekkAtBoardErOK();


            //bruker nye metoden for � finne ord
            MoveFinder moveFinder = new MoveFinder();
            ArrayList<Move> allMoves = moveFinder.findAllMoves(scrabbleGame.dictionary, scrabbleGame.board, scrabbleGame.rackString);

            TreeMap<Double, Move> newPossibleWords = new TreeMap<>(Collections.reverseOrder());

            ComputerAI computerAI = new ComputerAI(scrabbleGame.rackStringCpy, scrabbleGame.bag, scrabbleGame.vowelRatioLeft,
                    scrabbleGame.playerScore, scrabbleGame.computerScore, scrabbleGame.pointlessTurns, scrabbleGame.board.isAnchor, scrabbleGame.firstMove,
                    scrabbleGame.scrabbleGameFrame.boardPanel.squareGrid, scrabbleGame.board.charBoard, scrabbleGame.dictionary,
                    scrabbleGame.rackString, scrabbleGame.rack.tileCount());

            allMoves.forEach(potentialMove -> newPossibleWords.put(computerAI.cpuAIScore(potentialMove), potentialMove));

            int count = 0;
            int topSc = 0;
            double topScKey = 0;
            Move top = null;
            for (Map.Entry<Double, Move> entry : newPossibleWords.entrySet()) {
                Move poss = entry.getValue();
                if (count < 20) {
                    System.out.println(entry.getKey() + " " + poss.word + " startsAt " + poss.wordStart + " vertical: " + poss.vertical + " bruker: " + poss.usedFromRack + " har igjen: " + poss.leftOnRack + " score: " + poss.moveScore + "  -> " + poss.AIString);
                    count++;
                }
                if (poss.moveScore > topSc || (poss.moveScore == topSc && entry.getKey() > topScKey)) {
                    top = poss;
                    topSc = poss.moveScore;
                    topScKey = entry.getKey();
                }
            }

            //kan ikke legge
            if (newPossibleWords.isEmpty()) {
                System.out.println("CPU kan ikke legge");
                scrabbleGame.addedToThisMove.clear();
                scrabbleGame.newWordsAdded.clear();

                //M� BYTTE OM MULIG
                if (scrabbleGame.bag.tileCount() >= 7) {
                    //bytter alle
                    computerSwap(scrabbleGame.rackString);
                } else {
                    JOptionPane.showMessageDialog(null, "CPU passer");
                    scrabbleGame.pass();
                }
                scrabbleGame.computersTurn = false;
                return;
            }

            //velge legg og skrive ut p� skjerm + legge til brikkene i charBoard
            Move topScoreWord = newPossibleWords.firstEntry().getValue();

            //TEST
            if (top != null) {
                if (top == topScoreWord) {
                    System.out.println("Velger TOPSCORE-word");
                } else {
                    System.out.println("TOPSCORE: " + topScKey + " " + top.word + " startsAt " + top.wordStart + " vertical: " + top.vertical + " bruker: " + top.usedFromRack + " har igjen: " + top.leftOnRack + " score: " + top.moveScore + "  -> " + top.AIString);
                }
            }

            //TEST
            System.out.print(newPossibleWords.firstEntry().getKey() + " (" + topScoreWord.moveScore + ") Velger " + topScoreWord.word + ": " + topScoreWord.AIString);
            System.out.println(", row: " + topScoreWord.row + ", column: " + topScoreWord.wordStart);
            System.out.println("left: " + topScoreWord.leftOnRack);
            for (String s : topScoreWord.words) {
                System.out.println("(" + s + ")");
            }
            //TEST SLUTT

            //hvis beste legg ikke er noe godt legg => bytte brikker
            //har lavere terskel for � bytte om det er f�rste trekk
            if (scrabbleGame.firstMove) {
                //bytter hvis ordets score er negativ, eller gir mindre enn 10 poeng
                //eller hvis cpu blir sittende igjen med minst tre bokstaver og alle er konsonanter
                if (newPossibleWords.firstEntry().getKey() < 0 || topScoreWord.moveScore < 10 ||
                        (topScoreWord.leftOnRack.length() >= 3 && !StringUtil.containsVowel(topScoreWord.leftOnRack)) ||
                        (topScoreWord.leftOnRack.length() >= 5 && StringUtil.vowelCount(topScoreWord.leftOnRack) == 1)) {
                    System.out.println("bytter ved p� f�rste trekk");
                    cpuMakeSwap();
                    scrabbleGame.computersTurn = false;
                    scrabbleGame.addedToThisMove.clear();
                    scrabbleGame.newWordsAdded.clear();
                    return;
                }
                //hvis det ikker er f�rste legg
            } else {
                //kriterier for � bytte: negativ score eller kun konsonanter
                if (scrabbleGame.bag.tileCount() >= 7 && newPossibleWords.firstEntry().getKey() < 0) {
                    System.out.println("bytter pga for d�rlig bestelegg");
                    cpuMakeSwap();
                    scrabbleGame.computersTurn = false;
                    scrabbleGame.addedToThisMove.clear();
                    scrabbleGame.newWordsAdded.clear();
                    return;
                }
            }

            scrabbleGame.cpuLastWord = topScoreWord;
            int topScore = topScoreWord.moveScore;

            //legg brikker p� brettet
            //TODO: Move burde ha v�re en bedre representasjon av et trekk -> "disse brikkene p� disse feltene"
            scrabbleGame.scrabbleGameFrame.boardPanel.placeMove(topScoreWord);
            removeFromCPURack(topScoreWord);
            scrabbleGame.board.addToCharBoard(topScoreWord);

            updateComputerScore(topScore);
            if (scrabbleGame.addedToThisMove.size() == 7) {
                scrabbleGame.updateCPUNotes("*" + topScoreWord.word, topScore);
            } else {
                scrabbleGame.updateCPUNotes(topScoreWord.word, topScore);
            }
            scrabbleGame.updateRemaining(topScoreWord.usedFromRack);

            scrabbleGame.addedToThisMove.clear();
            scrabbleGame.newWordsAdded.clear();

            //trekke nye brikker
            scrabbleGame.cpuNewlyPicked = "";
            while (scrabbleGame.rackString.length() != 7 && !scrabbleGame.bag.isEmpty()) {
                Tile t = scrabbleGame.bag.pickTile();
                scrabbleGame.rackString += t.letter;
                scrabbleGame.cpuNewlyPicked += t.letter;
            }
            scrabbleGame.computersTurn = false;
            //hvis CPU g�r ut
            if (scrabbleGame.rackString.length() == 0) {
                System.out.println("kaller finishGame fra CPU");
                scrabbleGame.finishGame();
            }
            scrabbleGame.newlyAddedToBoard.clear();
            scrabbleGame.scrabbleGameFrame.bagCountLabel.setText("Brikker igjen i posen: " + scrabbleGame.bag.tileCount());
            scrabbleGame.playerPassed = false;

            System.out.println("AVSLUTTER COMPUTERAI()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeFromCPURack(Move move) {
        String toRemove = move.usedFromRack;
        for (int i = 0; i < toRemove.length(); i++) {
            scrabbleGame.rackString = StringUtil.removeChar(scrabbleGame.rackString, toRemove.charAt(i));
        }
    }

    private void sjekkAtBoardErOK() {
        char[][] charBoard = scrabbleGame.board.charBoard;
        Square[][] squareGrid = scrabbleGame.scrabbleGameFrame.boardPanel.squareGrid;

        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++ ) {
                if(charBoard[i][j] == '-' && squareGrid[i][j].tile != null) {
                    try {
                        throw new Exception("charBoardtrouble");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(charBoard[i][j] != '-' && charBoard[i][j] != squareGrid[i][j].tile.letter) {
                    try {
                        throw new Exception("charBoardtrouble - 2");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    //chooses what tiles to swap - antar at denne metoden kun kalles med nok brikker i posen
    void cpuMakeSwap() {
        System.out.println("cpuMakeSwap kalles med rackString: " + scrabbleGame.rackString);
        String toSwap = "";
        String tilesKept = "";
        //bytter alle hvis det bare er konsonanter
        if (!StringUtil.containsVowel(scrabbleGame.rackString)) {
            toSwap = scrabbleGame.rackString;
        } else {
            //sparer p� vokaler og bingovennlige brikker (maks en av hver) - burde v�rt to av E?
            for (int i = 0; i < 7; i++) {
                char c = scrabbleGame.rackString.charAt(i);
                if (StringUtil.isVowel(c) && tilesKept.indexOf(c) == -1) {
                    tilesKept += c;
                } else if (!StringUtil.isBingoFriendlyChar(c)) {
                    toSwap += c;
                } else if (tilesKept.indexOf(c) == -1) {
                    tilesKept += c;
                } else {
                    toSwap += c;
                }
            }
            //sjekker konsonant/vokal-ratio p� brikkene som skal spares
            double vowelRatio = StringUtil.vowelRatio(tilesKept);
            while (tilesKept.length() > 1 &&
                    (vowelRatio < 0.33 || vowelRatio > 0.67)) {
                //hvis for mange konsonanter eller vokaler
                char c;
                if (vowelRatio < 0.33) {
                    c = StringUtil.lowestScoringCons(tilesKept);
                } else {
                    c = StringUtil.lowestScoringVowel(tilesKept);
                }
                toSwap += c;
                tilesKept = StringUtil.removeChar(tilesKept, c);

                vowelRatio = StringUtil.vowelRatio(tilesKept);
            }
            if (tilesKept.length() == 1 && !StringUtil.isVowel(tilesKept.charAt(0))) {
                toSwap += tilesKept;
            }
        }
        System.out.println(", bytter disse brikkene: " + toSwap);
        computerSwap(toSwap);
    }

    void computerSwap(String toSwap) {
        JOptionPane.showMessageDialog(null, "CPU bytter " + toSwap.length() + " brikker");
        System.out.println("CPU kaller swap med " + toSwap + ", rackString er " + scrabbleGame.rackString + "<-slutt");
        if (scrabbleGame.bag.tileCount() < 7) {
            System.out.println("CPU pr�ver � bytte med for lite i posen");
        } else {
            //trekker brikker
            for (int i = 0; i < toSwap.length(); i++) {
                Tile t = scrabbleGame.bag.pickTile();
                scrabbleGame.rackString += t.letter;
            }
            System.out.println("etter � ha trukket opp: " + scrabbleGame.rackString);
            //legger gamle brikker tilbake i posen
            for (int i = 0; i < toSwap.length(); i++) {
                char c = toSwap.charAt(i);
                scrabbleGame.rackString = scrabbleGame.rackString.substring(0,scrabbleGame.rackString.indexOf(c)) + scrabbleGame.rackString.substring(scrabbleGame.rackString.indexOf(c)+1);
                scrabbleGame.bag.add(new Tile(c));
            }
            System.out.println("etter � ha lagt tilbake: " + scrabbleGame.rackString);
        }
        scrabbleGame.updateCPUNotes("(bytte)", 0);
    }

    void updateComputerScore(int moveScore) {
        scrabbleGame.computerScore += moveScore;
        scrabbleGame.previousCPUMoveScore = moveScore;
    }
}
