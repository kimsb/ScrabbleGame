package scrabblegamepkg.server;

import com.BoxOfC.MDAG.MDAG;
import scrabblegamepkg.client.BoardPanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import static scrabblegamepkg.server.ScrabbleGame.*;

//TODO: her er det mye å ta tak i!
// - skal ikke gjøre frontend-ting
// - selve kalkuleringa av trekk må ut i egen klasse, som også kan brukes ved laging av tips til player1
public class CPUThinker extends SwingWorker<Void, Void> {

    //TODO: disse må fjernes/ryddes
    String previousRackString;
    ScrabbleGame scrabbleGame;
    Bag bag;
    Rack rack;
    double vowelRatioLeft;
    String rackString;
    ArrayList<Square> newlyAddedToBoard;
    boolean computersTurn;
    String rackStringCpy;
    MDAG dictionary;
    Board board;
    int playerScore, computerScore;
    int pointlessTurns;
    boolean firstMove;
    BoardPanel boardPanel;
    ArrayList<Square> addedToThisMove;
    ArrayList<Square[]> newWordsAdded;
    Move cpuLastWord;
    String cpuNewlyPicked;
    JLabel bagCountLabel;
    boolean playerPassed;
    int previousCPUMoveScore;

    public CPUThinker(ScrabbleGame scrabbleGame) {
        this.scrabbleGame = scrabbleGame;
        this.bag = scrabbleGame.bag;
        this.rack = scrabbleGame.rack;
        this.vowelRatioLeft = scrabbleGame.vowelRatioLeft;
        this.previousRackString = scrabbleGame.previousRackString;
        this.rackString = scrabbleGame.rackString;
        this.newlyAddedToBoard = scrabbleGame.newlyAddedToBoard;
        this.computersTurn = scrabbleGame.computersTurn;
        this.rackStringCpy = scrabbleGame.rackStringCpy;
        this.dictionary = scrabbleGame.dictionary;
        this.board = scrabbleGame.board;
        this.playerScore = scrabbleGame.playerScore;
        this.computerScore = scrabbleGame.computerScore;
        this.pointlessTurns = scrabbleGame.pointlessTurns;
        this.firstMove = scrabbleGame.firstMove;
        this.boardPanel = scrabbleGame.boardPanel;
        this.addedToThisMove = scrabbleGame.addedToThisMove;
        this.newWordsAdded = scrabbleGame.newWordsAdded;
        this.cpuLastWord = scrabbleGame.cpuLastWord;
        this.cpuNewlyPicked = scrabbleGame.cpuNewlyPicked;
        this.bagCountLabel = scrabbleGame.bagCountLabel;
        this.playerPassed = scrabbleGame.playerPassed;
        this.previousCPUMoveScore = scrabbleGame.previousCPUMoveScore;
    }

    @Override
    protected Void doInBackground() {
        scrabbleGame.challengeButton.setEnabled(false);
        scrabbleGame.playButton.setEnabled(false);
        scrabbleGame.swapButton.setEnabled(false);
        scrabbleGame.passButton.setEnabled(false);
        scrabbleGame.newGameButton.setEnabled(false);
        scrabbleGame.tipsButton.setEnabled(false);
        computerAI();
        return null;
    }

    @Override
    protected void done() {
        scrabbleGame.challengeButton.setEnabled(true);
        scrabbleGame.playButton.setEnabled(true);
        scrabbleGame.swapButton.setEnabled(true);
        scrabbleGame.passButton.setEnabled(true);
        scrabbleGame.newGameButton.setEnabled(true);
        (scrabbleGame.tipsCalculator = new TipsCalculator(scrabbleGame)).execute();
        JScrollBar verticalScrollBar = scrabbleGame.firstPlayerScrollPane.getVerticalScrollBar();
        verticalScrollBar.setValue(verticalScrollBar.getMaximum());
        verticalScrollBar = scrabbleGame.secondPlayerScrollPane.getVerticalScrollBar();
        verticalScrollBar.setValue(verticalScrollBar.getMaximum());

    }

    void computerAI() {

        try {
            System.out.println("STARET COMPUTERAI()");

            //calculating vowelRatio in bag + players rack
            double vowelsLeft = bag.vowelCount() + StringUtil.vowelCount(rack.toString());
            int lettersLeft = bag.tileCount() + rack.tileCount();

            vowelRatioLeft = vowelsLeft / lettersLeft;

            previousRackString = rackString;
            System.out.println("ComputerAI starts.. cpuRack: " + rackString);
            newlyAddedToBoard.clear();
            if (!computersTurn) {
                System.out.println("ikke cpus tur");
                return;
            } else if (rackString.length() == 0) {
                System.out.println("FERDIG!");
                return;
            }

            rackStringCpy = rackString;

            //TEST - skal fjernes
            sjekkAtBoardErOK();


            //bruker nye metoden for å finne ord
            MoveFinder moveFinder = new MoveFinder();
            ArrayList<Move> allMoves = moveFinder.findAllMoves(dictionary, board, rackString);

            TreeMap<Double, Move> newPossibleWords = new TreeMap<>(Collections.reverseOrder());

            ComputerAI computerAI = new ComputerAI(rackStringCpy, bag, vowelRatioLeft,
                    playerScore, computerScore, pointlessTurns, board.isAnchor, firstMove,
                    boardPanel.squareGrid, board.charBoard, dictionary,
                    rackString, rack.tileCount());

            allMoves.forEach(potentialMove -> newPossibleWords.put(computerAI.cpuAIScore(potentialMove), potentialMove));

            int count = 0;
            int topSc = 0;
            double topScKey = 0;
            Move top = null;
            for (Map.Entry<Double, Move> entry : newPossibleWords.entrySet()) {
                Move poss = entry.getValue();
                if (count < 20) {
                    System.out.println(entry.getKey() + " " + poss.word + " startsAt " + poss.wordStart + " vertical: " + poss.vertical + " bruker: " + poss.usedFromRack + " har igjen: " + poss.leftOnRack + " score: " + poss.wordScore + "  -> " + poss.AIString);
                    count++;
                }
                if (poss.wordScore > topSc || (poss.wordScore == topSc && entry.getKey() > topScKey)) {
                    top = poss;
                    topSc = poss.wordScore;
                    topScKey = entry.getKey();
                }
            }

            //kan ikke legge
            if (newPossibleWords.isEmpty()) {
                System.out.println("CPU kan ikke legge");
                addedToThisMove.clear();
                newWordsAdded.clear();
                board.transposeBoard(boardPanel);

                //MÅ BYTTE OM MULIG
                if (bag.tileCount() >= 7) {
                    //bytter alle
                    computerSwap(rackString);
                } else {
                    JOptionPane.showMessageDialog(null, "CPU passer");
                    scrabbleGame.pass();
                }
                computersTurn = false;
                return;
            }

            //velge legg og skrive ut på skjerm + legge til brikkene i charBoard
            Move topScoreWord = newPossibleWords.firstEntry().getValue();

            //TEST
            if (top != null) {
                if (top == topScoreWord) {
                    System.out.println("Velger TOPSCORE-word");
                } else {
                    System.out.println("TOPSCORE: " + topScKey + " " + top.word + " startsAt " + top.wordStart + " vertical: " + top.vertical + " bruker: " + top.usedFromRack + " har igjen: " + top.leftOnRack + " score: " + top.wordScore + "  -> " + top.AIString);
                }
            }

            //TEST
            System.out.print(newPossibleWords.firstEntry().getKey() + " (" + topScoreWord.wordScore + ") Velger " + topScoreWord.word + ": " + topScoreWord.AIString);
            System.out.println(", row: " + topScoreWord.row + ", column: " + topScoreWord.wordStart);
            System.out.println("left: " + topScoreWord.leftOnRack);
            for (String s : topScoreWord.words) {
                System.out.println("(" + s + ")");
            }
            //TEST SLUTT

            //hvis beste legg ikke er noe godt legg => bytte brikker
            //har lavere terskel for å bytte om det er første trekk
            if (firstMove) {
                //bytter hvis ordets score er negativ, eller gir mindre enn 10 poeng
                //eller hvis cpu blir sittende igjen med minst tre bokstaver og alle er konsonanter
                if (newPossibleWords.firstEntry().getKey() < 0 || topScoreWord.wordScore < 10 ||
                        (topScoreWord.leftOnRack.length() >= 3 && !StringUtil.containsVowel(topScoreWord.leftOnRack)) ||
                        (topScoreWord.leftOnRack.length() >= 5 && StringUtil.vowelCount(topScoreWord.leftOnRack) == 1)) {
                    System.out.println("bytter ved på første trekk");
                    cpuMakeSwap();
                    computersTurn = false;
                    addedToThisMove.clear();
                    newWordsAdded.clear();
                    board.transposeBoard(boardPanel);
                    return;
                }
                //hvis det ikker er første legg
            } else {
                //kriterier for å bytte: negativ score eller kun konsonanter
                if (bag.tileCount() >= 7 && newPossibleWords.firstEntry().getKey() < 0) {
                    System.out.println("bytter pga for dårlig bestelegg");
                    cpuMakeSwap();
                    computersTurn = false;
                    addedToThisMove.clear();
                    newWordsAdded.clear();
                    board.transposeBoard(boardPanel);
                    return;
                }
            }

            cpuLastWord = topScoreWord;
            int topScore = topScoreWord.wordScore;

            //legg brikker på brettet
            //TODO: Move burde ha være en bedre representasjon av et trekk -> "disse brikkene på disse feltene"
            boardPanel.placeMove(topScoreWord);
            removeFromCPURack(topScoreWord);
            board.addToCharBoard(topScoreWord);

            updateComputerScore(topScore);
            if (addedToThisMove.size() == 7) {
                scrabbleGame.updateCPUNotes("*" + topScoreWord.word, topScore);
            } else {
                scrabbleGame.updateCPUNotes(topScoreWord.word, topScore);
            }
            scrabbleGame.updateRemaining(topScoreWord.usedFromRack);

            addedToThisMove.clear();
            newWordsAdded.clear();

            //trekke nye brikker
            cpuNewlyPicked = "";
            while (rackString.length() != 7 && !bag.isEmpty()) {
                Tile t = bag.pickTile();
                rackString += t.letter;
                cpuNewlyPicked += t.letter;
            }
            computersTurn = false;
            //hvis CPU går ut
            if (rackString.length() == 0) {
                System.out.println("kaller finishGame fra CPU");
                scrabbleGame.finishGame();
            }
            newlyAddedToBoard.clear();
            bagCountLabel.setText("Brikker igjen i posen: " + bag.tileCount());
            playerPassed = false;

            System.out.println("AVSLUTTER COMPUTERAI()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeFromCPURack(Move move) {
        String toRemove = move.usedFromRack;
        for (int i = 0; i < toRemove.length(); i++) {
            rackString = StringUtil.removeChar(rackString, toRemove.charAt(i));
        }
    }

    private void sjekkAtBoardErOK() {
        char[][] charBoard = board.charBoard;
        Square[][] squareGrid = boardPanel.squareGrid;

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
        System.out.println("cpuMakeSwap kalles med rackString: " + rackString);
        String toSwap = "";
        String tilesKept = "";
        //bytter alle hvis det bare er konsonanter
        if (!StringUtil.containsVowel(rackString)) {
            toSwap = rackString;
        } else {
            //sparer på vokaler og bingovennlige brikker (maks en av hver) - burde vært to av E?
            for (int i = 0; i < 7; i++) {
                char c = rackString.charAt(i);
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
            //sjekker konsonant/vokal-ratio på brikkene som skal spares
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
        System.out.println("CPU kaller swap med " + toSwap + ", rackString er " + rackString + "<-slutt");
        if (bag.tileCount() < 7) {
            System.out.println("CPU prøver å bytte med for lite i posen");
        } else {
            //trekker brikker
            for (int i = 0; i < toSwap.length(); i++) {
                Tile t = bag.pickTile();
                rackString += t.letter;
            }
            System.out.println("etter å ha trukket opp: " + rackString);
            //legger gamle brikker tilbake i posen
            for (int i = 0; i < toSwap.length(); i++) {
                char c = toSwap.charAt(i);
                rackString = rackString.substring(0,rackString.indexOf(c)) + rackString.substring(rackString.indexOf(c)+1);
                bag.add(new Tile(c));
            }
            System.out.println("etter å ha lagt tilbake: " + rackString);
        }
        scrabbleGame.updateCPUNotes("(bytte)", 0);
    }

    void updateComputerScore(int moveScore) {
        computerScore += moveScore;
        previousCPUMoveScore = moveScore;
    }
}
