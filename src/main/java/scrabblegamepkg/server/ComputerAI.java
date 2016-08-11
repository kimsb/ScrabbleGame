package scrabblegamepkg.server;

import com.BoxOfC.MDAG.MDAG;

public class ComputerAI {
    
    String rackStringCpy;
    Bag bag;
    double vowelRatioLeft;
    int playerScore, computerScore;
    int pointlessTurns;
    boolean[][] isAnchor;
    boolean firstMove;
    Square[][] squareGrid;
    char[][] charBoard;
    MDAG dictionary;
    String rackString;
    int onPlayersRack;

    public ComputerAI(String rackStringCpy, Bag bag, double vowelRatioLeft,
                      int playerScore, int computerScore, int pointlessTurns, boolean[][] isAnchor, boolean firstMove,
                      Square[][] squareGrid, char[][] charBoard, MDAG dictionary,
                      String rackString, int onPlayersRack) {
        this.rackStringCpy = rackStringCpy;
        this.bag = bag;
        this.vowelRatioLeft = vowelRatioLeft;
        this.playerScore = playerScore;
        this.computerScore = computerScore;
        this.pointlessTurns = pointlessTurns;
        this.isAnchor = isAnchor;
        this.firstMove = firstMove;
        this.squareGrid = squareGrid;
        this.charBoard = charBoard;
        this.dictionary = dictionary;
        this.rackString = rackString;
        this.onPlayersRack = onPlayersRack;
    }

    //gjøres nå for alle ord, bør det bare gjøres for topp 10, 20, 30?
    double cpuAIScore(Move posWord) {
        String casingCopy = posWord.word;
        posWord.word = posWord.word.toUpperCase();
        double score = posWord.moveScore;

        //hvis blank brukes - trekker fra 20p (straffes for max en)
        for (int i = 0; i < posWord.usedFromRack.length(); i++) {
            if (posWord.usedFromRack.charAt(i) == '-') {
                score -= 20;
                //stats
                posWord.AIString += "-20 for bruk av blank, ";
                break;
            }
        }

        //gir poeng for brikkene som blir igjen på racket (ok poengsum?)
        double leftScore = 0;
        String leftOnRack = rackStringCpy;
        for (int i = 0; i < posWord.usedFromRack.length(); i++) {
            int index = leftOnRack.indexOf(posWord.usedFromRack.charAt(i));
            leftOnRack = leftOnRack.substring(0,index) + leftOnRack.substring(index+1);
        }
        posWord.leftOnRack = leftOnRack;
        for (int i = 0; i < leftOnRack.length(); i++) {
            score += ScoreConstants.relativeLetterScore(leftOnRack.charAt(i));
            //stats
            leftScore += ScoreConstants.relativeLetterScore(leftOnRack.charAt(i));
        }
        //stats
        posWord.AIString += "+" + leftScore + " for brikkene som er igjen på racket, ";

        //gir poeng for å bruke mange bokstaver om man har få vokaler
        if(StringUtil.vowelCount(posWord.leftOnRack) + StringUtil.vowelCount(posWord.usedFromRack) <= 1 &&
                posWord.usedFromRack.length() >= 4) {
            score += 5;
            posWord.AIString += "+5 for bruk av mange brikker når man har få vokaler, ";
        }

        //trekk for å bruke få bokstaver med W eller C på hånda
        if (leftOnRack.length() >= 5 && (leftOnRack.indexOf('W') != -1 || leftOnRack.indexOf('C') != -1)) {
            score -= 10;
            posWord.AIString += "-10 for bruk av få bokstaver med W/C på hånda, ";
        }

        //trekk for mange brikker igjen, men få vokaler
        if (leftOnRack.length() >= 5 && StringUtil.vowelCount(leftOnRack) <= 1) {
            score -= 7.5;
            posWord.AIString += "-7.5 for mange brikker, men få vokaler, ";
        }

        if (leftScore == 0 && rackStringCpy.length() == 7) {
            score += 25;
            posWord.AIString += "+25 for å legge bingo, ";
        }

        //favorisere ing, ene - gir 10p ekstra
        if (leftOnRack.length() >= 3 &&
                leftOnRack.indexOf('I') != -1 &&
                leftOnRack.indexOf('N') != -1 &&
                leftOnRack.indexOf('G') != -1) {
            score += 10;
            //stats
            posWord.AIString += "+10 for å ha igjen ING på racket, ";
        }
        boolean ene = false;
        if (leftOnRack.length() >= 3 &&
                leftOnRack.indexOf('E') != -1 &&
                leftOnRack.indexOf('N') != -1 &&
                leftOnRack.substring(leftOnRack.indexOf('E')+1).indexOf('E') != -1) {
            score += 10;
            //stats
            posWord.AIString += "+10 for å ha igjen ENE på racket, ";
            ene = true;
        }

        //favorisere ulike brikker på racket - trekker fra 5p for hver like brikke
        boolean twoEs = false;
        boolean doubles = false;
        for (int i = 0; i < leftOnRack.length() - 1; i++) {
            for (int j = i+1; j < leftOnRack.length(); j++) {
                if (leftOnRack.charAt(i) == leftOnRack.charAt(j)) {
                    doubles = true;
                    if (leftOnRack.charAt(i) == 'E' && !twoEs) {
                        if (!ene) {
                            twoEs = true;
                            score += 5;
                            posWord.AIString += "+5 for akkurat 2stk E igjen på racket, ";
                        }
                    } else {
                        score -= 5;
                        //stats
                        posWord.AIString += "-5 for 2stk " + leftOnRack.charAt(i) + " igjen på racket, ";
                    }
                }
            }
        }

        //plusspoeng for bingovennlige brikker igjen på rack
        if (!doubles) {
            if (StringUtil.bingoFriendly(leftOnRack)) {
                score += 7;
                posWord.AIString += "+7 for bingovennlige brikker igjen, ";
            }
        }

        //vokal/konsonant-ratio - nå bare dersom det er minst 7 brikker i posen
        double optimalVowelCount = 2.63;
        double vowelCount = 0;
        boolean hasBlank = false;
        if (bag.tileCount() >= 7) {
            for (int i = 0; i < leftOnRack.length(); i++) {
                if (leftOnRack.charAt(i) == '-') {
                    hasBlank = true;
                    vowelCount += 0.5;
                } else if (StringUtil.isVowel(leftOnRack.charAt(i))) {
                    vowelCount += 1;
                }
            }
            for (int i = leftOnRack.length(); i < 7; i++) {
                vowelCount += vowelRatioLeft;
            }
            double ratio = Math.abs(vowelCount - optimalVowelCount);
            if (hasBlank) {
                if (ratio <= 0.5) {
                    ratio = 0;
                } else {
                    ratio -= 0.5;
                }
            }
            score -= 5 * ratio;
            //stats
            posWord.AIString += "-" + (5 * ratio) + " for vokal/konsonant-ratio, ";
        }

        //gå ut om det gjør at cpu vinner (med mindre player ikke kan legge)
        if (bag.isEmpty() && posWord.leftOnRack.length() == 0 &&
                (computerScore + posWord.moveScore) > playerScore &&
                pointlessTurns == 0) {
            score += 100;
            //stats
            posWord.AIString += "går ut fordi cpu vinner (+100), ";
        } else if (bag.isEmpty()) {
            //gir ekstra poeng for brikkene som brukes (så ikke motstander får dem som pluss)
            int extraScore = 0;
            for (int i = 0; i < posWord.usedFromRack.length(); i++) {
                extraScore += ScoreConstants.relativeLetterScore(posWord.usedFromRack.charAt(i)) * 2;
            }
            score += extraScore;
            posWord.AIString += "+" + extraScore + " for å kvitte seg med bokstaver når posen er tom, ";
        }

        //bonus for bruk av toveis DL, TL, DW, TW
        int i = posWord.row;
        for (int j = posWord.wordStart; j < posWord.wordStart + posWord.word.length(); j++) {
            if (isAnchor[i][j] && !firstMove) {
                if (BoardConstants.getLetterMultiplier(i, j) == 2) {
                    score += 2.5;
                    posWord.AIString += "+2.5 for bruk av toveis DL, ";
                } else if (BoardConstants.getLetterMultiplier(i, j) == 3) {
                    score += 5;
                    posWord.AIString += "+5 for bruk av toveis TL, ";
                } else if (BoardConstants.getWordMultiplier(i, j) == 2) {
                    score += 5;
                    posWord.AIString += "+5 for bruk av toveis DW, ";
                } else if (BoardConstants.getWordMultiplier(i, j) == 3) {
                    score += 10;
                    posWord.AIString += "+10 for bruk av toveis TW, ";
                }
            }
        }

        //trekk for å legge opp toveis DL, TL, DW, TW (om de kan brukes)
        //denne sjekker nå om en enkelt bokstav kan brukes på MultiplierFeltet for å lage ord begge veier
        score += toWayMultiplierSetUpPenalty(posWord);

        //trekk for å lage åpninger langs TW-radene + DW * DW
        score += openingTWPenalty(posWord);

        //sluttspilltaktikk

        //bytte om mulige trekk er for dårlige

        //større forskjell på brikker som er igjen på racket

        //minus for å legge åpen e

        //lavere terskel for å bytte første legg

        posWord.word = casingCopy;
        return score;
    }

    double openingTWPenalty(Move posWord) {
        if (posWord.row == 0 || posWord.row == 14) {
            return 0;
        }
        double score = 0;

        //hvis starter på første rad
        if (posWord.wordStart == 0) {
            boolean upperOpening = false;
            boolean upperMiddleOpening = false;
            boolean lowerMiddleOpening = false;
            boolean lowerOpening = false;

            //øverst hjørne
            if (posWord.row > 0 && posWord.row < 5) {
                upperOpening = true;
                for (int i = 0; i <= posWord.row+1; i++) {
                    if (charBoard[i][0] != '-') {
                        upperOpening = false;
                        break;
                    }
                }
            } if (posWord.row > 3 && posWord.row < 7) {
                upperMiddleOpening = true;
                for (int i = posWord.row-1; i <= 8; i++) {
                    if (charBoard[i][0] != '-') {
                        upperMiddleOpening = false;
                        break;
                    }
                }
            } if (posWord.row > 7 && posWord.row < 11) {
                lowerMiddleOpening = true;
                for (int i = posWord.row+1; i >= 6; i--) {
                    if (charBoard[i][0] != '-') {
                        lowerMiddleOpening = false;
                        break;
                    }
                }
            } if (posWord.row > 9 && posWord.row < 14) {
                lowerOpening = true;
                for (int i = 14; i >= posWord.row-1; i--) {
                    if (charBoard[i][0] != '-') {
                        lowerOpening = false;
                        break;
                    }
                }
            }
            //har laget åpning på venstre side
            if (upperOpening || upperMiddleOpening || lowerMiddleOpening || lowerOpening) {
                score -= 15;
                posWord.AIString += "-15 for å legge på første av TW-rad, ";
            }
        }

        //hvis ender på siste rad
        if (posWord.wordStart + posWord.word.length() == 15) {
            boolean upperOpening = false;
            boolean upperMiddleOpening = false;
            boolean lowerMiddleOpening = false;
            boolean lowerOpening = false;

            //øverst hjørne
            if (posWord.row > 0 && posWord.row < 5) {
                upperOpening = true;
                for (int i = 0; i <= posWord.row+1; i++) {
                    if (charBoard[i][14] != '-') {
                        upperOpening = false;
                        break;
                    }
                }
            } if (posWord.row > 3 && posWord.row < 7) {
                upperMiddleOpening = true;
                for (int i = posWord.row-1; i <= 8; i++) {
                    if (charBoard[i][14] != '-') {
                        upperMiddleOpening = false;
                        break;
                    }
                }
            } if (posWord.row > 7 && posWord.row < 11) {
                lowerMiddleOpening = true;
                for (int i = posWord.row+1; i >= 6; i--) {
                    if (charBoard[i][14] != '-') {
                        lowerMiddleOpening = false;
                        break;
                    }
                }
            } if (posWord.row > 9 && posWord.row < 14) {
                lowerOpening = true;
                for (int i = 14; i >= posWord.row-1; i--) {
                    if (charBoard[i][14] != '-') {
                        lowerOpening = false;
                        break;
                    }
                }
            }
            //har laget åpning på høyre side
            if (upperOpening || upperMiddleOpening || lowerMiddleOpening || lowerOpening) {
                score -= 15;
                posWord.AIString += "-15 for å legge på siste av TW-rad, ";
            }
        }

        //for åpninger på venstre side
        if (posWord.wordStart == 1) {
            int playerOpenings = 0;
            int cpuOpenings = 0;
            for (int i = 0; i < 29; i++) {
                if (dictionary.contains(StringUtil.alphaString().charAt(i) + posWord.word)) {
                    //straff for åpning
                    if (bag.containsLetterOrBlank(StringUtil.alphaString().charAt(i))) {
                        playerOpenings++;
                        //bonus for åpning som ikke kan brukes av player
                    } else if (rackString.indexOf(StringUtil.alphaString().charAt(i)) != -1) {
                        cpuOpenings++;
                    }
                }
            }
            if (playerOpenings > 0 || cpuOpenings > 0) {
                boolean upperOpening = false;
                boolean upperMiddleOpening = false;
                boolean lowerMiddleOpening = false;
                boolean lowerOpening = false;
                //øverst hjørne
                if (posWord.row < 7) {
                    upperOpening = true;
                    for (int i = 0; i <= posWord.row +1; i++) {
                        if (charBoard[i][0] != '-') {
                            upperOpening = false;
                            break;
                        }
                    }
                    upperMiddleOpening = true;
                    for (int i = posWord.row-1; i <= 8; i++) {
                        if (charBoard[i][0] != '-') {
                            upperMiddleOpening = false;
                            break;
                        }
                    }
                } else if (posWord.row > 7) {
                    lowerMiddleOpening = true;
                    for (int i = posWord.row+1; i >= 6; i--) {
                        if (charBoard[i][0] != '-') {
                            lowerMiddleOpening = false;
                            break;
                        }
                    }
                    lowerOpening = true;
                    for (int i = 14; i >= posWord.row-1; i--) {
                        if (charBoard[i][0] != '-') {
                            lowerOpening = false;
                            break;
                        }
                    }
                }
                //har funnet åpning på venstre side
                if (upperOpening || upperMiddleOpening || lowerMiddleOpening || lowerOpening) {
                    if (cpuOpenings > playerOpenings) {
                        score += 15;
                        posWord.AIString += "+15 for CPU-åpning av TW-rad (foran), ";
                    } else if (playerOpenings > 0) {
                        score -= 15;
                        posWord.AIString += "-15 for player-åpning av TW-rad (foran), ";
                    }
                }
            }
        }

        // for åpninger på høyre side
        if (posWord.wordStart + posWord.word.length() == 14) {
            int playerOpenings = 0;
            int cpuOpenings = 0;
            for (int i = 0; i < 29; i++) {
                if (dictionary.contains(posWord.word + StringUtil.alphaString().charAt(i))) {
                    //straff for åpning
                    if (bag.containsLetterOrBlank(StringUtil.alphaString().charAt(i))) {
                        playerOpenings++;
                        //bonus for åpning som ikke kan brukes av player
                    } else if (rackString.indexOf(StringUtil.alphaString().charAt(i)) != -1) {
                        cpuOpenings++;
                    }
                }
            }
            if (playerOpenings > 0 || cpuOpenings > 0) {
                boolean upperOpening = false;
                boolean upperMiddleOpening = false;
                boolean lowerMiddleOpening = false;
                boolean lowerOpening = false;
                //øverst hjørne
                if (posWord.row < 7) {
                    upperOpening = true;
                    for (int i = 0; i <= posWord.row +1; i++) {
                        if (charBoard[i][14] != '-') {
                            upperOpening = false;
                            break;
                        }
                    }
                    upperMiddleOpening = true;
                    for (int i = posWord.row-1; i <= 8; i++) {
                        if (charBoard[i][14] != '-') {
                            upperMiddleOpening = false;
                            break;
                        }
                    }
                } else if (posWord.row > 7) {
                    lowerMiddleOpening = true;
                    for (int i = posWord.row+1; i >= 6; i--) {
                        if (charBoard[i][14] != '-') {
                            lowerMiddleOpening = false;
                            break;
                        }
                    }
                    lowerOpening = true;
                    for (int i = 14; i >= posWord.row-1; i--) {
                        if (charBoard[i][14] != '-') {
                            lowerOpening = false;
                            break;
                        }
                    }
                }
                //har funnet åpning på høyre side
                if (upperOpening || upperMiddleOpening || lowerMiddleOpening || lowerOpening) {
                    if (cpuOpenings > playerOpenings) {
                        score += 15;
                        posWord.AIString += "+15 for CPU-åpning av TW-rad (bak), ";
                    } else if (playerOpenings > 0) {
                        score -= 15;
                        posWord.AIString += "-15 for player-åpning av TW-rad (bak), ";
                    }
                }
            }
        }

        // for åpninger med ord foran midtlinja
        if (posWord.wordStart + posWord.word.length() == 7) {
            int playerOpenings = 0;
            int cpuOpenings = 0;
            for (int i = 0; i < 29; i++) {
                if (dictionary.contains(posWord.word + StringUtil.alphaString().charAt(i))) {
                    //straff for åpning
                    if (bag.containsLetterOrBlank(StringUtil.alphaString().charAt(i))) {
                        playerOpenings++;
                        //bonus for åpning som ikke kan brukes av player
                    } else if (rackString.indexOf(StringUtil.alphaString().charAt(i)) != -1) {
                        cpuOpenings++;
                    }
                }
            }
            if (playerOpenings > 0 || cpuOpenings > 0) {
                boolean upperOpening = false;
                boolean lowerOpening = false;
                if (posWord.row < 7) {
                    upperOpening = true;
                    for (int i = 0; i <= posWord.row+1; i++) {
                        if (charBoard[i][7] != '-') {
                            upperOpening = false;
                            break;
                        }
                    }
                } else if (posWord.row > 7) {
                    lowerOpening = true;
                    for (int i = 14; i >= posWord.row-1; i--) {
                        if (charBoard[i][7] != '-') {
                            lowerOpening = false;
                            break;
                        }
                    }
                }

                //har funnet åpning på midtlinja med ord foran
                if (upperOpening || lowerOpening) {
                    if (cpuOpenings > playerOpenings) {
                        score += 15;
                        posWord.AIString += "+15 for CPU-åpning av TW-rad (midten, ord foran), ";
                    } else if (playerOpenings > 0) {
                        score -= 15;
                        posWord.AIString += "-15 for player-åpning av TW-rad (midten, ord foran), ";
                    }
                }
            }
        }
        // for åpninger med ord bak midtlinja
        if (posWord.wordStart == 8) {
            int playerOpenings = 0;
            int cpuOpenings = 0;
            for (int i = 0; i < 29; i++) {
                if (dictionary.contains(StringUtil.alphaString().charAt(i) + posWord.word)) {
                    //straff for åpning
                    if (bag.containsLetterOrBlank(StringUtil.alphaString().charAt(i))) {
                        playerOpenings++;
                        //bonus for åpning som ikke kan brukes av player
                    } else if (rackString.indexOf(StringUtil.alphaString().charAt(i)) != -1) {
                        cpuOpenings++;
                    }
                }
            }
            if (playerOpenings > 0 || cpuOpenings > 0) {
                boolean upperOpening = false;
                boolean lowerOpening = false;
                if (posWord.row < 7) {
                    upperOpening = true;
                    for (int i = 0; i <= posWord.row +1; i++) {
                        if (charBoard[i][7] != '-') {
                            upperOpening = false;
                            break;
                        }
                    }
                } else if (posWord.row > 7) {
                    lowerOpening = true;
                    for (int i = 14; i >= posWord.row-1; i--) {
                        if (charBoard[i][7] != '-') {
                            lowerOpening = false;
                            break;
                        }
                    }
                }
                //har funnet åpning på midtlinja med ord bak
                if (upperOpening || lowerOpening) {
                    if (cpuOpenings > playerOpenings) {
                        score += 15;
                        posWord.AIString += "+15 for CPU-åpning av TW-rad (midten, ord bak), ";
                    } else if (playerOpenings > 0) {
                        score -= 15;
                        posWord.AIString += "-15 for player-åpning av TW-rad (midten, ord bak), ";
                    }
                }
            }
        }

        // hvis legges på rad 1 (og åpner for øverste rad)
        if (posWord.row == 1) {
            if (posWord.wordStart >= 0 && posWord.wordStart <= 6) {
                boolean leftOpening = false;
                boolean middleOpening = false;
                //sjekker mot venstre
                if (charBoard[0][posWord.wordStart] == '-' && charBoard[0][posWord.wordStart+1] == '-') {
                    String vertSuffix = "" + posWord.word.charAt(1);
                    int k = 2;
                    while (k < 15 && charBoard[k][posWord.wordStart] != '-') {
                        vertSuffix += charBoard[k][posWord.wordStart];
                        k++;
                    }
                    boolean hasPrefix = false;
                    for (int i = 0; i < 29; i++) {
                        if (dictionary.contains(StringUtil.alphaString().charAt(i) + vertSuffix) &&
                                bag.contains(StringUtil.alphaString().charAt(i))) {
                            hasPrefix = true;
                            break;
                        }
                    }
                    if (hasPrefix) {
                        leftOpening = true;
                        for (int j = 0; j < posWord.wordStart; j++) {
                            if (charBoard[0][j] != '-') {
                                leftOpening = false;
                            }
                        }
                    }
                }
                if (posWord.wordStart + posWord.word.length() <= 7) {
                    //sjekker mot midten
                    int wordStop = posWord.wordStart + posWord.word.length() - 1;
                    if (charBoard[0][wordStop] == '-' && charBoard[0][wordStop+1] == '-') {
                        String vertSuffix = "" + posWord.word.charAt(posWord.word.length()-1);
                        int k = 2;
                        while (k < 15 && charBoard[k][wordStop] != '-') {
                            vertSuffix += charBoard[k][wordStop];
                            k++;
                        }
                        boolean hasPrefix = false;
                        for (int i = 0; i < 29; i++) {
                            if (dictionary.contains(StringUtil.alphaString().charAt(i) + vertSuffix) &&
                                    bag.contains(StringUtil.alphaString().charAt(i))) {
                                hasPrefix = true;
                                break;
                            }
                        }
                        if (hasPrefix) {
                            middleOpening = true;
                            for (int j = wordStop + 1; j <= 8; j++) {
                                if (charBoard[0][j] != '-') {
                                    leftOpening = false;
                                }
                            }
                        }
                    }
                }
                if (leftOpening || middleOpening) {
                    score -= 15;
                    posWord.AIString += "-15 for åpning av TW-rad (rad 1, venstre eller midten), ";
                }
            }
            //sjekker mot midten og mot høyre
            if ((posWord.wordStart + posWord.word.length()) >= 9 && (posWord.wordStart + posWord.word.length()) <= 15) {
                boolean middleOpening = false;
                boolean rightOpening = false;
                //sjekker mot midten
                if (charBoard[0][posWord.wordStart] == '-' && charBoard[0][posWord.wordStart-1] == '-') {
                    String vertSuffix = "" + posWord.word.charAt(1);
                    int k = 2;
                    while (k < 15 && charBoard[k][posWord.wordStart] != '-') {
                        vertSuffix += charBoard[k][posWord.wordStart];
                        k++;
                    }
                    boolean hasPrefix = false;
                    for (int i = 0; i < 29; i++) {
                        if (dictionary.contains(StringUtil.alphaString().charAt(i) + vertSuffix) &&
                                bag.contains(StringUtil.alphaString().charAt(i))) {
                            hasPrefix = true;
                            break;
                        }
                    }
                    if (hasPrefix) {
                        middleOpening = true;
                        for (int j = 7; j < posWord.wordStart; j++) {
                            if (charBoard[0][j] != '-') {
                                middleOpening = false;
                            }
                        }
                    }
                }
                //sjekker mot høyre
                int wordStop = posWord.wordStart + posWord.word.length() - 1;
                if (charBoard[0][wordStop] == '-' &&  charBoard[0][wordStop-1] == '-') {
                    String vertSuffix = "" + posWord.word.charAt(posWord.word.length()-1);
                    int k = 2;
                    while (k < 15 && charBoard[k][wordStop] != '-') {
                        vertSuffix += charBoard[k][wordStop];
                        k++;
                    }
                    boolean hasPrefix = false;
                    for (int i = 0; i < 29; i++) {
                        if (dictionary.contains(StringUtil.alphaString().charAt(i) + vertSuffix) &&
                                bag.contains(StringUtil.alphaString().charAt(i))) {
                            hasPrefix = true;
                            break;
                        }
                    }
                    if (hasPrefix) {
                        rightOpening = true;
                        for (int j = wordStop + 1; j <= 14; j++) {
                            if (charBoard[0][j] != '-') {
                                rightOpening = false;
                            }
                        }
                    }
                }
                if (middleOpening || rightOpening) {
                    score -= 15;
                    posWord.AIString += "-15 for åpning av TW-rad (rad 1, midten eller høyre), ";
                }
            }
        }

        // hvis legges på rad 8 (og åpner for midterste rad)
        if (posWord.row == 8) {
            if (posWord.wordStart >= 0 && posWord.wordStart < 7) {
                boolean leftOpening = false;
                //sjekker mot venstre
                if (charBoard[7][posWord.wordStart] == '-' && charBoard[7][posWord.wordStart+1] == '-') {
                    String vertSuffix = "" + posWord.word.charAt(1);
                    int k = 9;
                    while (k < 15 && charBoard[k][posWord.wordStart] != '-') {
                        vertSuffix += charBoard[k][posWord.wordStart];
                        k++;
                    }
                    String vertPrefix = "";
                    k = 6;
                    while (k >= 0 && charBoard[k][posWord.wordStart] != '-') {
                        vertPrefix = charBoard[k][posWord.wordStart] + vertPrefix;
                        k--;
                    }
                    boolean hasWord = false;
                    for (int i = 0; i < 29; i++) {
                        if (dictionary.contains(vertPrefix + StringUtil.alphaString().charAt(i) + vertSuffix) &&
                                bag.contains(StringUtil.alphaString().charAt(i))) {
                            hasWord = true;
                            break;
                        }
                    }
                    if (hasWord) {
                        leftOpening = true;
                        for (int j = 0; j < posWord.wordStart; j++) {
                            if (charBoard[7][j] != '-') {
                                leftOpening = false;
                            }
                        }
                    }
                }
                if (leftOpening) {
                    score -= 15;
                    posWord.AIString += "-15 for åpning av TW-rad (rad 8, venstre), ";
                }
            }
            //sjekker mot høyre
            if ((posWord.wordStart + posWord.word.length()) >= 9 && (posWord.wordStart + posWord.word.length()) <= 14) {
                boolean rightOpening = false;
                //sjekker mot høyre
                int wordStop = posWord.wordStart + posWord.word.length() - 1;
                if (charBoard[7][wordStop] == '-' && charBoard[7][wordStop-1] == '-') {
                    String vertSuffix = "" + posWord.word.charAt(posWord.word.length()-1);
                    int k = 9;
                    while (k < 15 && charBoard[k][wordStop] != '-') {
                        vertSuffix += charBoard[k][wordStop];
                        k++;
                    }
                    String vertPrefix = "";
                    k = 6;
                    while (k >= 0 && charBoard[k][wordStop] != '-') {
                        vertPrefix = charBoard[k][wordStop] + vertPrefix;
                        k--;
                    }
                    boolean hasWord = false;
                    for (int i = 0; i < 29; i++) {
                        if (dictionary.contains(vertPrefix + StringUtil.alphaString().charAt(i) + vertSuffix) &&
                                bag.contains(StringUtil.alphaString().charAt(i))) {
                            hasWord = true;
                            break;
                        }
                    }
                    if (hasWord) {
                        rightOpening = true;
                        for (int j = wordStop + 1; j <= 14; j++) {
                            if (charBoard[7][j] != '-') {
                                rightOpening = false;
                            }
                        }
                    }
                }
                if (rightOpening) {
                    score -= 15;
                    posWord.AIString += "-15 for åpning av TW-rad (rad 8, høyre), ";
                }
            }

        }
        // hvis legges på rad 6 (og åpner for midterste rad)
        if (posWord.row == 6) {
            if (posWord.wordStart >= 1 && posWord.wordStart < 7) {
                boolean leftOpening = false;
                //sjekker mot venstre
                if (charBoard[7][posWord.wordStart] == '-' && charBoard[7][posWord.wordStart+1] == '-') {
                    String vertSuffix = "";
                    int k = 8;
                    while (k < 15 && charBoard[k][posWord.wordStart] != '-') {
                        vertSuffix += charBoard[k][posWord.wordStart];
                        k++;
                    }
                    String vertPrefix = "" + posWord.word.charAt(1);
                    k = 5;
                    while (k >= 0 && charBoard[k][posWord.wordStart] != '-') {
                        vertPrefix = charBoard[k][posWord.wordStart] + vertPrefix;
                        k--;
                    }
                    boolean hasWord = false;
                    for (int i = 0; i < 29; i++) {
                        if (dictionary.contains(vertPrefix + StringUtil.alphaString().charAt(i) + vertSuffix) &&
                                bag.contains(StringUtil.alphaString().charAt(i))) {
                            hasWord = true;
                            break;
                        }
                    }
                    if (hasWord) {
                        leftOpening = true;
                        for (int j = 0; j < posWord.wordStart; j++) {
                            if (charBoard[7][j] != '-') {
                                leftOpening = false;
                            }
                        }
                    }
                }
                if (leftOpening) {
                    score -= 15;
                    posWord.AIString += "-15 for åpning av TW-rad (rad 6, venstre), ";
                }
            }
            //sjekker mot høyre
            if ((posWord.wordStart + posWord.word.length()) >= 9 && (posWord.wordStart + posWord.word.length()) <= 14) {
                boolean rightOpening = false;
                //sjekker mot høyre
                int wordStop = posWord.wordStart + posWord.word.length() - 1;
                if (charBoard[7][wordStop] == '-' && charBoard[7][wordStop-1] == '-') {
                    String vertSuffix = "";
                    int k = 8;
                    while (k < 15 && charBoard[k][wordStop] != '-') {
                        vertSuffix += charBoard[k][wordStop];
                        k++;
                    }
                    String vertPrefix = "" + posWord.word.charAt(posWord.word.length()-1);
                    k = 5;
                    while (k >= 0 && charBoard[k][wordStop] != '-') {
                        vertPrefix = charBoard[k][wordStop] + vertPrefix;
                        k--;
                    }
                    boolean hasWord = false;
                    for (int i = 0; i < 29; i++) {
                        if (dictionary.contains(vertPrefix + StringUtil.alphaString().charAt(i) + vertSuffix) &&
                                bag.contains(StringUtil.alphaString().charAt(i))) {
                            hasWord = true;
                            break;
                        }
                    }
                    if (hasWord) {
                        rightOpening = true;
                        for (int j = wordStop + 1; j <= 14; j++) {
                            if (charBoard[7][j] != '-') {
                                rightOpening = false;
                            }
                        }
                    }
                }
                if (rightOpening) {
                    score -= 15;
                    posWord.AIString += "-15 for åpning av TW-rad (rad 6, høyre), ";
                }
            }
        }
        // hvis legges på rad 13 (og åpner for nederst rad)
        if (posWord.row == 13) {
            if (posWord.wordStart >= 0 && posWord.wordStart < 7) {
                boolean leftOpening = false;
                boolean middleOpening = false;
                //sjekker mot venstre
                if (charBoard[14][posWord.wordStart] == '-' && charBoard[14][posWord.wordStart+1] == '-') {
                    String vertPrefix = "" + posWord.word.charAt(1);
                    int k = 12;
                    while (k >= 0 && charBoard[k][posWord.wordStart] != '-') {
                        vertPrefix = charBoard[k][posWord.wordStart] + vertPrefix;
                        k--;
                    }
                    boolean hasSuffix = false;
                    for (int i = 0; i < 29; i++) {
                        if (dictionary.contains(vertPrefix + StringUtil.alphaString().charAt(i)) &&
                                bag.contains(StringUtil.alphaString().charAt(i))) {
                            hasSuffix = true;
                            break;
                        }
                    }
                    if (hasSuffix) {
                        leftOpening = true;
                        for (int j = 0; j < posWord.wordStart; j++) {
                            if (charBoard[14][j] != '-') {
                                leftOpening = false;
                            }
                        }
                    }
                }
                //sjekker mot midten
                if  (posWord.wordStart + posWord.word.length() <= 7) {
                    int wordStop = posWord.wordStart + posWord.word.length() - 1;
                    if (charBoard[14][wordStop] == '-' && charBoard[14][wordStop-1] == '-') {
                        String vertPrefix = "" + posWord.word.charAt(posWord.word.length()-1);
                        int k = 12;
                        while (k >= 0 && charBoard[k][wordStop] != '-') {
                            vertPrefix = charBoard[k][wordStop] + vertPrefix;
                            k--;
                        }
                        boolean hasSuffix = false;
                        for (int i = 0; i < 29; i++) {
                            if (dictionary.contains(vertPrefix + StringUtil.alphaString().charAt(i)) &&
                                    bag.contains(StringUtil.alphaString().charAt(i))) {
                                hasSuffix = true;
                                break;
                            }
                        }
                        if (hasSuffix) {
                            middleOpening = true;
                            for (int j = wordStop + 1; j <= 8; j++) {
                                if (charBoard[14][j] != '-') {
                                    leftOpening = false;
                                }
                            }
                        }
                    }
                }
                if (leftOpening || middleOpening) {
                    score -= 15;
                    posWord.AIString += "-15 for åpning av TW-rad (rad 13, venstre eller midten), ";
                }
            }
            //sjekker mot midten og mot høyre
            if ((posWord.wordStart + posWord.word.length()) >= 9 && (posWord.wordStart + posWord.word.length()) <= 15) {
                boolean middleOpening = false;
                boolean rightOpening = false;
                //sjekker mot midten
                if (charBoard[14][posWord.wordStart] == '-' && charBoard[14][posWord.wordStart+1] == '-') {
                    String vertPrefix = "" + posWord.word.charAt(1);
                    int k = 12;
                    while (k >= 0 && charBoard[k][posWord.wordStart] != '-') {
                        vertPrefix = charBoard[k][posWord.wordStart] + vertPrefix;
                        k--;
                    }
                    boolean hasSuffix = false;
                    for (int i = 0; i < 29; i++) {
                        if (dictionary.contains(vertPrefix + StringUtil.alphaString().charAt(i)) &&
                                bag.contains(StringUtil.alphaString().charAt(i))) {
                            hasSuffix = true;
                            break;
                        }
                    }
                    if (hasSuffix) {
                        middleOpening = true;
                        for (int j = 7; j < posWord.wordStart; j++) {
                            if (charBoard[14][j] != '-') {
                                middleOpening = false;
                            }
                        }
                    }
                }
                //sjekker mot høyre
                int wordStop = posWord.wordStart + posWord.word.length() - 1;
                if (charBoard[14][wordStop] == '-' && charBoard[14][wordStop-1] == '-') {
                    String vertPrefix = "" + posWord.word.charAt(posWord.word.length()-1);
                    int k = 12;
                    while (k >= 0 && charBoard[k][wordStop] != '-') {
                        vertPrefix = charBoard[k][wordStop] + vertPrefix;
                        k--;
                    }
                    boolean hasSuffix = false;
                    for (int i = 0; i < 29; i++) {
                        if (dictionary.contains(vertPrefix + StringUtil.alphaString().charAt(i)) &&
                                bag.contains(StringUtil.alphaString().charAt(i))) {
                            hasSuffix = true;
                            break;
                        }
                    }
                    if (hasSuffix) {
                        rightOpening = true;
                        for (int j = wordStop + 1; j <= 14; j++) {
                            if (charBoard[14][j] != '-') {
                                rightOpening = false;
                            }
                        }
                    }
                }
                if (middleOpening || rightOpening) {
                    score -= 15;
                    posWord.AIString += "-15 for åpning av TW-rad (rad 13, midten eller høyre), ";
                }
            }
        }

        //sjekker om åpner for midtTW og dobbel
        //oppe
        if (posWord.row == 2 && posWord.wordStart <= 7 &&
                posWord.wordStart + posWord.word.length() >= 8) {
            if (charBoard[0][6] == '-' && charBoard[0][7] == '-' && charBoard[0][8] == '-' &&
                    charBoard[1][7] == '-' && charBoard[3][7] == '-' && charBoard[4][7] == '-') {
                score -= 15;
                posWord.AIString += "-15 for åpning av øverste TW og dobbel, ";
            }
        }
        if (posWord.row == 4 && posWord.wordStart <= 7 &&
                posWord.wordStart + posWord.word.length() >= 8) {
            if (charBoard[0][6] == '-' && charBoard[0][7] == '-' && charBoard[0][8] == '-' &&
                    charBoard[1][7] == '-' && charBoard[2][7] == '-' && charBoard[3][7] == '-' && charBoard[5][7] == '-') {
                score -= 15;
                posWord.AIString += "-15 for åpning av øverste TW og dobbel, ";
            }
        }
        //nede
        if (posWord.row == 12 && posWord.wordStart <= 7 &&
                posWord.wordStart + posWord.word.length() >= 8) {
            if (charBoard[14][6] == '-' && charBoard[14][7] == '-' && charBoard[14][8] == '-' &&
                    charBoard[10][7] == '-' && charBoard[11][7] == '-' && charBoard[13][7] == '-') {
                score -= 15;
                posWord.AIString += "-15 for åpning av nederste TW og dobbel, ";
            }
        }
        if (posWord.row == 10 && posWord.wordStart <= 7 &&
                posWord.wordStart + posWord.word.length() >= 8) {
            if (charBoard[14][6] == '-' && charBoard[14][7] == '-' && charBoard[14][8] == '-' &&
                    charBoard[9][7] == '-' && charBoard[11][7] == '-' && charBoard[12][7] == '-' && charBoard[13][7] == '-') {
                score -= 15;
                posWord.AIString += "-15 for åpning av øverste TW og dobbel, ";
            }
        }

        //sjekker om legger gjennom DW*DW
        //trekker 10p bare hvis bokstaven som åpner er bingofriendly
        if (posWord.wordStart <= 4 && posWord.wordStart + posWord.word.length() >= 5 &&
                posWord.row >= 5 && posWord.row <= 9) {
            if (charBoard[4][4] == '-' && charBoard[5][4] == '-' && charBoard[6][4] == '-' &&
                    charBoard[7][4] == '-' && charBoard[8][4] == '-' && charBoard[9][4] == '-' &&
                    charBoard[10][4] == '-') {
                if (StringUtil.isBingoFriendlyChar(posWord.word.charAt(4 - posWord.wordStart))) {
                    score -= 10;
                    posWord.AIString += "-10 for åpning av DW * DW, ";
                }
            }
        }
        if (posWord.wordStart == 5 && posWord.row >= 5 && posWord.row <= 9) {
            if (charBoard[4][4] == '-' && charBoard[5][4] == '-' && charBoard[6][4] == '-' &&
                    charBoard[7][4] == '-' && charBoard[8][4] == '-' && charBoard[9][4] == '-' &&
                    charBoard[10][4] == '-') {
                String horPrefix = "";
                int k = 3;
                while (k >= 0 && charBoard[posWord.row][k] != '-') {
                    horPrefix = charBoard[posWord.row][k] + horPrefix;
                    k--;
                }
                boolean hasWord = false;
                for (int i = 0; i < 29; i++) {
                    if (dictionary.contains(horPrefix + StringUtil.alphaString().charAt(i) + posWord.word) &&
                            bag.contains(StringUtil.alphaString().charAt(i))) {
                        if (StringUtil.isBingoFriendlyChar(StringUtil.alphaString().charAt(i))) {
                            hasWord = true;
                            break;
                        }
                    }
                }
                if (hasWord) {
                    score -= 10;
                    posWord.AIString += "-10 for åpning bak DW * DW, ";
                }
            }
        }
        if (posWord.wordStart <= 10 && posWord.wordStart + posWord.word.length() >= 11 &&
                posWord.row >= 5 && posWord.row <= 9) {
            if (charBoard[4][10] == '-' && charBoard[5][10] == '-' && charBoard[6][10] == '-' &&
                    charBoard[7][10] == '-' && charBoard[8][10] == '-' && charBoard[9][10] == '-' &&
                    charBoard[10][10] == '-') {
                if (StringUtil.isBingoFriendlyChar(posWord.word.charAt(10 - posWord.wordStart))) {
                    score -= 10;
                    posWord.AIString += "-10 for åpning av DW * DW, ";
                }
            }
        }
        if (posWord.wordStart + posWord.word.length() == 10 && posWord.row >= 5 && posWord.row <= 9) {
            if (charBoard[4][10] == '-' && charBoard[5][10] == '-' && charBoard[6][10] == '-' &&
                    charBoard[7][10] == '-' && charBoard[8][10] == '-' && charBoard[9][10] == '-' &&
                    charBoard[10][10] == '-') {
                String horSuffix = "";
                int k = 11;
                while (k <= 14 && charBoard[posWord.row][k] != '-') {
                    horSuffix += charBoard[posWord.row][k];
                    k++;
                }
                boolean hasWord = false;
                for (int i = 0; i < 29; i++) {
                    if (dictionary.contains(posWord.word + StringUtil.alphaString().charAt(i) + horSuffix) &&
                            bag.contains(StringUtil.alphaString().charAt(i))) {
                        if (StringUtil.isBingoFriendlyChar(StringUtil.alphaString().charAt(i))) {
                            hasWord = true;
                            break;
                        }
                    }
                }
                if (hasWord) {
                    score -= 10;
                    posWord.AIString += "-10 for åpning foran DW * DW, ";
                }
            }
        }
        return score;
    }

    double toWayMultiplierSetUpPenalty(Move posWord) {
        double score = 0;
        //foran nye ord
        if (posWord.wordStart != 0 && (BoardConstants.getLetterMultiplier(posWord.row, posWord.wordStart-1) != 1
        || BoardConstants.getWordMultiplier(posWord.row, posWord.wordStart-1) != 1)) {
            String horPrefix = "";
            int k = 0;
            while (posWord.wordStart - 2 - k >= 0 && charBoard[posWord.row][posWord.wordStart - 2 - k] != '-') {
                horPrefix = charBoard[posWord.row][posWord.wordStart - 2 - k] + horPrefix;
                k++;
            }
            String vertPrefix = "";
            String vertSuffix = "";
            k = posWord.row - 1;
            while (k >= 0 && charBoard[k][posWord.wordStart - 1] != '-') {
                vertPrefix = charBoard[k][posWord.wordStart - 1] + vertPrefix;
                k--;
            }
            k = posWord.row + 1;
            while (k < 15 && charBoard[k][posWord.wordStart - 1] != '-') {
                vertSuffix += charBoard[k][posWord.wordStart - 1];
                k++;
            }
            double openingScore = 0;
            for (int i = 0; i < 29; i++) {
                if (dictionary.contains(horPrefix + StringUtil.alphaString().charAt(i) + posWord.word)) {
                    if (vertPrefix.length() == 0 && vertSuffix.length() == 0 ||
                            dictionary.contains(vertPrefix + StringUtil.alphaString().charAt(i) + vertSuffix)) {
                        char c = StringUtil.alphaString().charAt(i);
                        if (ScoreConstants.letterScore(c) != 0) {
                            openingScore += 2 * ScoreConstants.letterScore(c) * ScoreConstants.letterScore(c) * ((double)bag.letterCount(c) / (bag.tileCount() + onPlayersRack));
                        }

                    }
                }
            }
            if (openingScore > 0) {
                double addedScore = multiplierPenalty(posWord.row, posWord.wordStart, openingScore);
                if (addedScore > 0) {
                    posWord.AIString += "-" + addedScore + " for åpning foran, ";
                    score -= addedScore;
                }
            }

        }
        //bak nye ord
        if (posWord.wordStart+posWord.word.length() != 15 && (BoardConstants.getLetterMultiplier(posWord.row, posWord.wordStart+posWord.word.length()) != 1 ||
                                                                BoardConstants.getWordMultiplier(posWord.row, posWord.wordStart+posWord.word.length()) != 1)) {
            String horSuffix = "";
            int k = 0;
            while (posWord.wordStart+posWord.word.length() + 2 + k <= 14 && charBoard[posWord.row][posWord.wordStart+posWord.word.length() + 2 + k] != '-') {
                horSuffix += charBoard[posWord.row][posWord.wordStart+posWord.word.length() + 2 + k];
                k++;
            }
            String vertPrefix = "";
            String vertSuffix = "";
            k = posWord.row - 1;
            while (k >= 0 && charBoard[k][posWord.wordStart+posWord.word.length()] != '-') {
                vertPrefix = charBoard[k][posWord.wordStart+posWord.word.length()] + vertPrefix;
                k--;
            }
            k = posWord.row + 1;
            while (k < 15 && charBoard[k][posWord.wordStart+posWord.word.length()] != '-') {
                vertSuffix += charBoard[k][posWord.wordStart+posWord.word.length()];
                k++;
            }
            double openingScore = 0;
            for (int i = 0; i < 29; i++) {
                if (dictionary.contains(posWord.word + StringUtil.alphaString().charAt(i) + horSuffix)) {
                    if (vertPrefix.length() == 0 && vertSuffix.length() == 0 ||
                            dictionary.contains(vertPrefix + StringUtil.alphaString().charAt(i) + vertSuffix)) {
                        char c = StringUtil.alphaString().charAt(i);
                        if (ScoreConstants.letterScore(c) != 0) {
                            openingScore += 2 * ScoreConstants.letterScore(c) * ScoreConstants.letterScore(c) * ((double)bag.letterCount(c) / (bag.tileCount() + onPlayersRack));
                        }
                    }
                }
            }
            if (openingScore > 0) {
                double addedScore = multiplierPenalty(posWord.row, posWord.wordStart+posWord.word.length(), openingScore);
                if (addedScore > 0) {
                    posWord.AIString += "-" + addedScore + " for åpning bak, ";
                    score -= addedScore;
                }
            }

        }
        //over hver nye bokstav
        for (int j = posWord.wordStart; j < posWord.wordStart + posWord.word.length(); j++) {
            if (charBoard[posWord.row][j] == '-' ) {
                int l = 1;
                String vertSuffix = "" + posWord.word.charAt(j - posWord.wordStart);
                while (posWord.row - l > 0 && charBoard[posWord.row-l][j] != '-') {
                    vertSuffix = charBoard[posWord.row-l][j] + vertSuffix;
                    l++;
                }
                if (posWord.row > 0 && charBoard[posWord.row-l][j] == '-' &&
                        (BoardConstants.getLetterMultiplier(posWord.row-l, j) != 1 || BoardConstants.getWordMultiplier(posWord.row-l, j) != 1)) {
                    String vertPrefix = "";

                    int k = posWord.row - l - 1;
                    while (k > 0 && charBoard[k][j] != '-') {
                        vertPrefix = charBoard[k][j] + vertPrefix;
                        k--;
                    }
                    k = posWord.row + 1;
                    while(k < 15 && charBoard[k][j] != '-') {
                        vertSuffix += charBoard[k][j];
                        k++;
                    }
                    String horPrefix = "";
                    String horSuffix = "";
                    k = j - 1;
                    while (k > 0 && charBoard[posWord.row-l][k] != '-') {
                        horPrefix = charBoard[posWord.row-l][k] + horPrefix;
                        k--;
                    }
                    k = j + 1;
                    while (k < 15 && charBoard[posWord.row-l][k] != '-') {
                        horSuffix += charBoard[posWord.row-l][k];
                        k++;
                    }
                    double openingScore = 0;
                    for (int i = 0; i < 29; i++) {

                        if (dictionary.contains(vertPrefix + StringUtil.alphaString().charAt(i) + vertSuffix)) {
                            if (horPrefix.length() == 0 && horSuffix.length() == 0 ||
                                    dictionary.contains(horPrefix + StringUtil.alphaString().charAt(i) + horSuffix)) {
                                char c = StringUtil.alphaString().charAt(i);
                                if (ScoreConstants.letterScore(c) != 0) {
                                    openingScore += 2 * ScoreConstants.letterScore(c) * ScoreConstants.letterScore(c) * ((double)bag.letterCount(c) / (bag.tileCount() + onPlayersRack));
                                }
                            }
                        }
                    }
                    if (openingScore > 0) {
                        if (rackStringCpy.indexOf(posWord.word.charAt(j - posWord.wordStart)) != -1) {
                            double letterScore = (double) ScoreConstants.letterScore(posWord.word.charAt(j - posWord.wordStart)) / 2;
                            score -= letterScore;
                            posWord.AIString += "-" + letterScore + " for bokstaven som åpner, ";
                        }
                        double addedScore = multiplierPenalty(posWord.row-l, j, openingScore);
                        if (addedScore > 0) {
                            posWord.AIString += "-" + addedScore + " for åpning over, ";
                            score -= addedScore;
                        }
                    }


                }
            }
        }

        //under hver nye bokstav
        for (int j = posWord.wordStart; j < posWord.wordStart + posWord.word.length(); j++) {
            if (charBoard[posWord.row][j] == '-') {
                int l = 1;
                String vertPrefix = "" + posWord.word.charAt(j - posWord.wordStart);
                while (posWord.row + l < 14 && charBoard[posWord.row+l][j] != '-') {
                    vertPrefix += charBoard[posWord.row+l][j];
                    l++;
                }
                if (posWord.row < 14 && charBoard[posWord.row+l][j] == '-' &&
                        (BoardConstants.getLetterMultiplier(posWord.row+l, j) != 1 || BoardConstants.getWordMultiplier(posWord.row+1, j) != 1)) {
                    String vertSuffix = "";
                    int k = posWord.row - 1;
                    while (k > 0 && charBoard[k][j] != '-') {
                        vertPrefix = charBoard[k][j] + vertPrefix;
                        k--;
                    }
                    k = posWord.row + l + 1;
                    while(k < 15 && charBoard[k][j] != '-') {
                        vertSuffix += charBoard[k][j];
                        k++;
                    }
                    String horPrefix = "";
                    String horSuffix = "";
                    k = j - 1;
                    while (k > 0 && charBoard[posWord.row+l][k] != '-') {
                        horPrefix = charBoard[posWord.row+l][k] + horPrefix;
                        k--;
                    }
                    k = j + 1;
                    while (k < 15 && charBoard[posWord.row+l][k] != '-') {
                        horSuffix += charBoard[posWord.row+l][k];
                        k++;
                    }
                    double openingScore = 0;
                    for (int i = 0; i < 29; i++) {
                        if (dictionary.contains(vertPrefix + StringUtil.alphaString().charAt(i) + vertSuffix)) {
                            if (horPrefix.length() == 0 && horSuffix.length() == 0 ||
                                    dictionary.contains(horPrefix + StringUtil.alphaString().charAt(i) + horSuffix)) {
                                char c = StringUtil.alphaString().charAt(i);
                                if (ScoreConstants.letterScore(c) != 0) {
                                    openingScore += 2 * ScoreConstants.letterScore(c) * ScoreConstants.letterScore(c) * ((double)bag.letterCount(c) / (bag.tileCount() + onPlayersRack));
                                }
                            }
                        }
                    }
                    if (openingScore > 0) {
                        if (rackStringCpy.indexOf(posWord.word.charAt(j - posWord.wordStart)) != -1) {
                            double letterScore = (double) ScoreConstants.letterScore(posWord.word.charAt(j - posWord.wordStart)) / 2;
                            score -= letterScore;
                            posWord.AIString += "-" + letterScore + " for bokstaven som åpner, ";
                        }
                        double addedScore = multiplierPenalty(posWord.row+l, j, openingScore);
                        if (addedScore > 0) {
                            posWord.AIString += "-" + addedScore + " for åpning under, ";
                            score -= addedScore;
                        }
                    }

                }
            }
        }

        return score;
    }

    private double multiplierPenalty(int row, int column, double openingScore) {
        if (BoardConstants.getLetterMultiplier(row, column) == 2) {
            return (openingScore > 2.5) ? openingScore : 2.5;
        } else if (BoardConstants.getLetterMultiplier(row, column) == 3) {
            return ((1.5 * openingScore) > 5) ? (1.5 * openingScore) : 5;
        } else if (BoardConstants.getWordMultiplier(row, column) == 2) {
            return ((2 * openingScore) > 7.5) ? (2 * openingScore) : 7.5;
        } else if (BoardConstants.getWordMultiplier(row, column) == 3) {
            return ((3 * openingScore) > 15) ? (3 * openingScore) : 15;
        }
        return 0;
    }

}
