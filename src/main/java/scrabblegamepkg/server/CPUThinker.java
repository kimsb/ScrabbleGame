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
        scrabbleGame.game.computersTurn = false;
    }

    void computerAI() {

        try {
            System.out.println("STARTET COMPUTERAI()");

            Game game = scrabbleGame.game;

            //calculating vowelRatio in bag + players rack
            double vowelsLeft = game.getBag().vowelCount() + StringUtil.vowelCount(game.getPlayer().getRack().toString());
            int lettersLeft = game.getBag().tileCount() + game.getPlayer().getRack().tileCount();

            scrabbleGame.vowelRatioLeft = vowelsLeft / lettersLeft;

            scrabbleGame.previousRackString = scrabbleGame.rackString;
            System.out.println("ComputerAI starts.. cpuRack: " + scrabbleGame.rackString);
            scrabbleGame.newlyAddedToBoard.clear();
            if (!game.computersTurn) {
                System.out.println("ikke cpus tur");
                return;
            } else if (scrabbleGame.rackString.length() == 0) {
                System.out.println("FERDIG!");
                return;
            }

            scrabbleGame.rackStringCpy = scrabbleGame.rackString;

            //TEST - skal fjernes
            sjekkAtBoardErOK();


            //bruker nye metoden for å finne ord
            MoveFinder moveFinder = new MoveFinder();
            ArrayList<Move> allMoves = moveFinder.findAllMoves(scrabbleGame.dictionary, game.getBoard(), scrabbleGame.rackString);

            TreeMap<Double, Move> newPossibleWords = new TreeMap<>(Collections.reverseOrder());

            ComputerAI computerAI = new ComputerAI(scrabbleGame.rackStringCpy, game.getBag(), scrabbleGame.vowelRatioLeft,
                    game.getPlayer().getScore(), game.getComputer().getScore(), game.pointlessTurns, game.getBoard().getAnchors(game.getBoard().getCharBoard()), game.isFirstMove(),
                    scrabbleGame.scrabbleGameFrame.boardPanel.squareGrid, game.getBoard().getCharBoard(), scrabbleGame.dictionary,
                    scrabbleGame.rackString, game.getPlayer().getRack().tileCount());

            allMoves.forEach(potentialMove -> newPossibleWords.put(computerAI.cpuAIScore(potentialMove), potentialMove));

            int count = 0;
            int topSc = 0;
            double topScKey = 0;
            Move top = null;
            for (Map.Entry<Double, Move> entry : newPossibleWords.entrySet()) {
                Move poss = entry.getValue();
                if (count < 20) {
                    System.out.println(entry.getKey() + " " + poss.word + " startsAt " + poss.startColumn + " vertical: " + poss.vertical + " bruker: " + poss.usedFromRack + " har igjen: " + poss.leftOnRack + " score: " + poss.moveScore + "  -> " + poss.AIString);
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

                //MÅ BYTTE OM MULIG
                if (game.getBag().tileCount() >= 7) {
                    //bytter alle
                    computerSwap(scrabbleGame.rackString);
                } else {
                    JOptionPane.showMessageDialog(null, "CPU passer");
                    scrabbleGame.pass(true);
                }
                return;
            }

            //velge legg og skrive ut på skjerm + legge til brikkene i charBoard
            Move topScoreWord = newPossibleWords.firstEntry().getValue();

            //TEST
            if (top != null) {
                if (top == topScoreWord) {
                    System.out.println("Velger TOPSCORE-word");
                } else {
                    System.out.println("TOPSCORE: " + topScKey + " " + top.word + " startsAt " + top.startColumn + " vertical: " + top.vertical + " bruker: " + top.usedFromRack + " har igjen: " + top.leftOnRack + " score: " + top.moveScore + "  -> " + top.AIString);
                }
            }

            //TEST
            System.out.print(newPossibleWords.firstEntry().getKey() + " (" + topScoreWord.moveScore + ") Velger " + topScoreWord.word + ": " + topScoreWord.AIString);
            System.out.println(", row: " + topScoreWord.row + ", column: " + topScoreWord.startColumn);
            System.out.println("left: " + topScoreWord.leftOnRack);
            for (String s : topScoreWord.getWords()) {
                System.out.println("(" + s + ")");
            }
            //TEST SLUTT

            //hvis beste legg ikke er noe godt legg => bytte brikker
            //har lavere terskel for å bytte om det er første trekk
            if (game.isFirstMove()) {
                //bytter hvis ordets score er negativ, eller gir mindre enn 10 poeng
                //eller hvis cpu blir sittende igjen med minst tre bokstaver og alle er konsonanter
                if (newPossibleWords.firstEntry().getKey() < 0 || topScoreWord.moveScore < 10 ||
                        (topScoreWord.leftOnRack.length() >= 3 && !StringUtil.containsVowel(topScoreWord.leftOnRack)) ||
                        (topScoreWord.leftOnRack.length() >= 5 && StringUtil.vowelCount(topScoreWord.leftOnRack) == 1)) {
                    System.out.println("bytter ved på første trekk");
                    cpuMakeSwap();
                    scrabbleGame.addedToThisMove.clear();
                    return;
                }
                //hvis det ikker er første legg
            } else {
                //kriterier for å bytte: negativ score eller kun konsonanter
                if (game.getBag().tileCount() >= 7 && newPossibleWords.firstEntry().getKey() < 0) {
                    System.out.println("bytter pga for dårlig bestelegg");
                    cpuMakeSwap();
                    scrabbleGame.addedToThisMove.clear();
                    return;
                }
            }

            scrabbleGame.cpuLastWord = topScoreWord;
            int topScore = topScoreWord.moveScore;

            //legg brikker på brettet
            //TODO: Move burde ha være en bedre representasjon av et trekk -> "disse brikkene på disse feltene"
            scrabbleGame.scrabbleGameFrame.boardPanel.placeMove(topScoreWord);
            removeFromCPURack(topScoreWord);
            game.getBoard().addToCharBoard(topScoreWord);

            game.getComputer().addTurn(new Turn(Action.MOVE, topScoreWord));

            scrabbleGame.addedToThisMove.clear();

            //trekke nye brikker
            scrabbleGame.cpuNewlyPicked = "";
            while (scrabbleGame.rackString.length() != 7 && !game.getBag().isEmpty()) {
                Tile t = game.getBag().pickTile();
                scrabbleGame.rackString += t.letter;
                scrabbleGame.cpuNewlyPicked += t.letter;
            }
            //hvis CPU går ut
            if (scrabbleGame.rackString.length() == 0) {
                System.out.println("kaller finishGame fra CPU");
                scrabbleGame.finishGame();
            }
            scrabbleGame.newlyAddedToBoard.clear();
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
        char[][] charBoard = scrabbleGame.game.getBoard().getCharBoard();
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
            //sparer på vokaler og bingovennlige brikker (maks en av hver) - burde vært to av E?
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
        scrabbleGame.game.getComputer().addTurn(new Turn(Action.SWAP));
        JOptionPane.showMessageDialog(null, "CPU bytter " + toSwap.length() + " brikker");
        System.out.println("CPU kaller swap med " + toSwap + ", rackString er " + scrabbleGame.rackString + "<-slutt");
        if (scrabbleGame.game.getBag().tileCount() < 7) {
            System.out.println("CPU prøver å bytte med for lite i posen");
        } else {
            //trekker brikker
            for (int i = 0; i < toSwap.length(); i++) {
                Tile t = scrabbleGame.game.getBag().pickTile();
                scrabbleGame.rackString += t.letter;
            }
            System.out.println("etter å ha trukket opp: " + scrabbleGame.rackString);
            //legger gamle brikker tilbake i posen
            for (int i = 0; i < toSwap.length(); i++) {
                char c = toSwap.charAt(i);
                scrabbleGame.rackString = scrabbleGame.rackString.substring(0,scrabbleGame.rackString.indexOf(c)) + scrabbleGame.rackString.substring(scrabbleGame.rackString.indexOf(c)+1);
                scrabbleGame.game.getBag().add(new Tile(c));
            }
            System.out.println("etter å ha lagt tilbake: " + scrabbleGame.rackString);
        }
    }
}
