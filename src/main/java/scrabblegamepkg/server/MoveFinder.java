package scrabblegamepkg.server;

import com.BoxOfC.MDAG.MDAG;
import com.BoxOfC.MDAG.MDAGNode;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class MoveFinder {

    ArrayList<Move> allMoves;

    public ArrayList<Move> findAllMoves(MDAG dictionary, Board board, String rack) {

        allMoves = new ArrayList<>();

        findMoves(dictionary, board, rack);

        return allMoves;
    }

    private void findMoves(MDAG dictionary, Board board, String rackString) {

        transposed = false;
        String[][] crossChecks = findCrossChecks(dictionary, board.getCharBoard());
        findAcrossMoves(board, dictionary, board.getCharBoard(), crossChecks, board.getAnchors(board.getCharBoard()), rackString);

        //down-moves
        transposed = true;
        char[][] transposedCharBoard = board.getTransposedCharBoard();
        String[][] transposedCrossChecks = findCrossChecks(dictionary, transposedCharBoard);
        findAcrossMoves(board, dictionary, transposedCharBoard, transposedCrossChecks, board.getAnchors(transposedCharBoard), rackString);
    }

    //TODO: bli kvitt disse globale
    private int currentAnchorI, currentAnchorJ;
    private boolean transposed;

    private void findAcrossMoves(Board board, MDAG dictionary, char[][] charBoard, String[][] crossChecks, boolean[][] isAnchor, String rackString) {
        //for all anchorSquares
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {

                if (isAnchor[i][j]) {
                    currentAnchorI = i;
                    currentAnchorJ = j;
                    String partialWord = "";
                    int k = 0;
                    while (j - k != 0 && !isAnchor[i][j-(k+1)]) {
                        k++;
                    }
                    //hvis left part er fra brettet
                    if (k != 0 && charBoard[i][j-1] != '-') {
                        MDAGNode n = (MDAGNode) dictionary.getSourceNode();
                        for (int l = 0; l < k; l++) {
                            partialWord += charBoard[i][j - (k-l)];
                            n = n.transition(Character.toUpperCase(charBoard[i][j - (k-l)]));
                        }
                        extendRight(board, charBoard, rackString, crossChecks, partialWord, n, j, "");

                    } else {
                        leftPart(board, charBoard, rackString, crossChecks, "", (MDAGNode) dictionary.getSourceNode(), k, "");
                    }
                }
            }
        }
    }

    private void extendRight(Board board, char[][] charBoard, String rackString, String[][] crossChecks, String partialWord, MDAGNode n, int squareJ, String usedFromRack) {
        //if square is vacant
        if (squareJ == 15 || charBoard[currentAnchorI][squareJ] == '-') {
            //if N si a terminal node
            if (squareJ != currentAnchorJ && n.isAcceptNode()) {
                Move newPos = new Move(currentAnchorI, (squareJ-partialWord.length()), transposed,
                        usedFromRack, (transposed ? board.getTransposedCharBoard() : board.getCharBoard()), rackString);

                allMoves.add(newPos);
            }
            if (squareJ < 15) {

                //for each edge E out of N
                TreeMap<Character,MDAGNode> outGoingEdges = n.getOutgoingTransitions();
                for (Map.Entry<Character,MDAGNode> entry : outGoingEdges.entrySet()) {
                    //if the letter l labeling edge e is in our rack
                    char l = entry.getKey();
                    int index = rackString.indexOf(l);
                    if (index != -1) {
                        //and l is in the crossCheck set of square
                        if (crossChecks[currentAnchorI][squareJ].indexOf(l) != -1) {
                            //then remove a tile labeled l from the rack
                            rackString = rackString.substring(0,index) + rackString.substring(index+1);
                            //let N' be the node reached by following edge E
                            MDAGNode nNext = entry.getValue();
                            //let next-square be the square to the right of square
                            //if (squareJ != 14) {
                            extendRight(board, charBoard, rackString, crossChecks, (partialWord + l), nNext, squareJ+1, usedFromRack + l);
                            //}
                            //put the tile back in the rack
                            rackString += l;
                        }
                    } else { // check for blank
                        index = rackString.indexOf('-');
                        if (index != -1) {
                            //and l is in the crossCheck set of square
                            if (crossChecks[currentAnchorI][squareJ].indexOf(l) != -1) {
                                //then remove blank tile from the rack
                                rackString = rackString.substring(0,index) + rackString.substring(index+1);
                                //let N' be the node reached by following edge E
                                MDAGNode nNext = entry.getValue();
                                //let next-square be the square to the right of square
                                if (squareJ != 14) {
                                    extendRight(board, charBoard, rackString, crossChecks, (partialWord + Character.toLowerCase(l)), nNext, squareJ+1, usedFromRack + Character.toLowerCase(l));
                                }
                                //put the blank tile back in the rack
                                rackString += '-';
                            }
                        }
                    }
                }
            }
            //}
        } else { //if square not vacant
            //let l be the letter occupying square
            char l = charBoard[currentAnchorI][squareJ];
            //if N has an edge labeled by l that leads to some node N'
            if (n.hasOutgoingTransition(l)) {
                //let next-square be the square to the right of square
                //if (squareJ != 14) {
                extendRight(board, charBoard, rackString, crossChecks, (partialWord + l), n.transition(l), squareJ+1, usedFromRack);
                //}
            }
        }
    }

    private void leftPart(Board board, char[][] charBoard, String rackString, String[][] crossChecks, String partialWord, MDAGNode n, int limit, String usedFromRack) {
        extendRight(board, charBoard, rackString, crossChecks, partialWord, n, currentAnchorJ, usedFromRack);
        if (limit > 0) {
            //for each edge E out of N
            TreeMap<Character,MDAGNode> outGoingEdges = n.getOutgoingTransitions();
            for (Map.Entry<Character,MDAGNode> entry : outGoingEdges.entrySet()) {
                //if the letter l labeling edge e is in our rack
                char l = entry.getKey();
                int index = rackString.indexOf(l);
                if (index != -1) {
                    //then remove a tile labeled l from the rack
                    rackString = rackString.substring(0,index) + rackString.substring(index+1);
                    //let N' be the node reached by following edge E
                    MDAGNode nNext = entry.getValue();
                    //leftPart(...)
                    leftPart(board, charBoard, rackString, crossChecks, (partialWord + l), nNext, limit-1, usedFromRack + l);
                    //put the tile back in the rack
                    rackString += l;
                } else { //if not on rack, check for blanks
                    index = rackString.indexOf('-');
                    if (index != -1) {
                        //then remove blank tile from the rack
                        rackString = rackString.substring(0,index) + rackString.substring(index+1);
                        //let N' be the node reached by following edge E
                        MDAGNode nNext = entry.getValue();
                        //leftPart(...)
                        leftPart(board, charBoard, rackString, crossChecks, (partialWord + Character.toLowerCase(l)), nNext, limit-1, usedFromRack + Character.toLowerCase(l));
                        //put the blank tile back in the rack
                        rackString += '-';
                    }
                }

            }
        }
    }

    //denne kan gjøres raskere, nå sjekker jeg alle felter
    private String[][] findCrossChecks(MDAG dictionary, char[][] charBoard) {

        String alphaString = "ABCDEFGHIJKLMNOPQRSTUVWXYZÆØÅ-";
        String[][] crossChecks = new String[15][15];

        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                //fyller crossChecks med alle bokstaver
                crossChecks[i][j] = alphaString;
            }
        }

        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (charBoard[i][j] == '-') {
                    int tilesOver = 0;
                    int tilesUnder = 0;
                    while(i - tilesOver != 0 && charBoard[i-(tilesOver+1)][j] != '-') {
                        tilesOver++;
                    }
                    while(i + tilesUnder != 14 && charBoard[i+(tilesUnder+1)][j] != '-') {
                        tilesUnder++;
                    }
                    if (tilesOver != 0 || tilesUnder != 0) {
                        crossChecks[i][j] = "";
                        String lettersOver = "";
                        String lettersUnder = "";
                        for (int k = tilesOver; k > 0; k--) {
                            lettersOver += charBoard[i-k][j];
                        }
                        for (int k = 1; k <= tilesUnder; k++) {
                            lettersUnder += charBoard[i+k][j];
                        }
                        //sjekker alle bokstaver i alfabetet
                        for (int k = 0; k < 29; k++) {
                            if (dictionary.contains(lettersOver + alphaString.charAt(k) + lettersUnder)) {
                                crossChecks[i][j] += alphaString.charAt(k);
                            }
                        }
                    }
                }
            }
        }
        return crossChecks;
    }

}
