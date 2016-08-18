package scrabblegamepkg.client;

import scrabblegamepkg.server.Game;
import scrabblegamepkg.server.Player;
import scrabblegamepkg.server.Turn;

public class Notes {


    static String getRemainingTilesNotes(char[][] charBoard) {

        String tilesLeft = "<html><body>AAAAAAA EEEEEEEEE<br>" +
                "IIIII OOOO UUU<br>" +
                "Y ∆ ÿÿ ≈≈<br>" +
                "BBB C DDDDD FFFF<br>" +
                "GGGG HHH JJ KKKK<br>" +
                "LLLLL MMM NNNNNN<br>" +
                "PP RRRRRR SSSSSS<br>" +
                "TTTTTT VVV W<br>" +
                "[][]</body></html>";


        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                char c = charBoard[i][j];
                if (c != '-') {
                    if (Character.isLowerCase(c)) {
                        tilesLeft = tilesLeft.substring(0, tilesLeft.indexOf('[')) + '-' + tilesLeft.substring(tilesLeft.indexOf('[') + 2);
                    } else {
                        tilesLeft = tilesLeft.substring(0, tilesLeft.indexOf(c)) + '-' + tilesLeft.substring(tilesLeft.indexOf(c) + 1);
                    }
                }
            }
        }
        return tilesLeft;
    }

    static String getPlayerNotes(Player player) {
        String playerNotes = "<b><u>" + player.getName() + ":</u></b><br>";

        for (Turn turn : player.getTurns()) {
            switch (turn.getAction()) {
                case MOVE:
                    playerNotes += turn.getMove().word + " " + turn.getMove().moveScore + "<br>";
                    break;
                case DISALLOWED:
                    playerNotes += "- ikke godkjent -<br>";
                    break;
                case PASS:
                    playerNotes += "- pass -<br>";
                    break;
                case SWAP:
                    playerNotes += "- bytte -<br>";
            }
        }

        return "<html><body>" + playerNotes + "<b>" + player.getScore() + "</b></body></html>";
    }

}
