package scrabblegamepkg.client;

public class Notes {

    /*
    void updateRemaining(String toRemoveString) {
        previousTilesLeft = tilesLeft;
        for (int i = 0; i < toRemoveString.length(); i++) {
            char c = toRemoveString.charAt(i);
            if (Character.isLowerCase(c)) {
                tilesLeft = tilesLeft.substring(0, tilesLeft.indexOf('[')) + '-' + tilesLeft.substring(tilesLeft.indexOf('[') + 2);
            } else {
                tilesLeft = tilesLeft.substring(0, tilesLeft.indexOf(c)) + '-' + tilesLeft.substring(tilesLeft.indexOf(c) + 1);
            }
        }
        scrabbleGameFrame.remainingLabel.setText(tilesLeft);
    }

    void updateCPUNotes(String word, int score) {
        previousCPUNotes = cpuNotes;
        JLabel noteLabel = scrabbleGameFrame.firstPlayerLabel;
        if (game.playerIsFirst) {
            noteLabel = scrabbleGameFrame.secondPlayerLabel;
        }
        cpuNotes += word + " ";
        if (score != 0) {
            cpuNotes += score;
        }
        cpuNotes += "<br>";
        noteLabel.setText("<html><body>" + cpuNotes + "<b>" + game.getComputer().getScore() + "</b></body></html>");
    }

    void updatePlayerNotes(String word, int score) {
        JLabel noteLabel = scrabbleGameFrame.firstPlayerLabel;
        if (!game.playerIsFirst) {
            noteLabel = scrabbleGameFrame.secondPlayerLabel;
        }
        playerNotes += word + " ";
        if (score != 0) {
            playerNotes += score;
            if (score == bestTipScore) {
                playerNotes += "!";
            }
            Move poss = tipsWords.firstEntry().getValue();
            if (score < bestTipScore) {
                String message = "<html><body><u><b>Du kunne lagt:</u></b><br>";
                message += (poss.moveScore + ", " + poss.word);
                JOptionPane.showMessageDialog(null, message);
            } else if (score > bestTipScore && !newWordAdded) {
                String message = ("BUG - høyeste CPU fant var: " +
                        poss.moveScore + ", " + poss.word);
                JOptionPane.showMessageDialog(null, message);
                playerNotes += "!";
            }
        }
        playerNotes += "<br>";
        noteLabel.setText("<html><body>" + playerNotes + "<b>" + game.getPlayer().getScore() + "</b></body></html>");
    }



    //init
    tilesLeft = "<html><body>AAAAAAA EEEEEEEEE<br>" +
                "IIIII OOOO UUU<br>" +
                "Y Æ ØØ ÅÅ<br>" +
                "BBB C DDDDD FFFF<br>" +
                "GGGG HHH JJ KKKK<br>" +
                "LLLLL MMM NNNNNN<br>" +
                "PP RRRRRR SSSSSS<br>" +
                "TTTTTT VVV W<br>" +
                "[][]</body></html>";
        scrabbleGameFrame.remainingLabel.setText(tilesLeft);
    scrabbleGameFrame.tilesLeftTitleLabel.setText("<html><body><b><u>Gjenværende brikker:</u></b></body></html>");
        scrabbleGameFrame.bagCountLabel.setText("Brikker igjen i posen: " + bag.tileCount());
        playerNotes = "<b><u>" + playerName + ":</u></b><br>";
        cpuNotes = "<u><b>CPU:</b></u><br>";


        scrabbleGameFrame.bagCountLabel.setText("Brikker igjen i posen: " + game.getBag().tileCount());
     */

}
