package scrabblegamepkg.server;

public class StringUtil {

    static String removeChar(String s, char c) {
        int index = s.indexOf(c);
        if (index == -1) {
            return s;
        }
        return s.substring(0, index) + s.substring(index+1);
    }

    static double vowelRatio(String s) {
        System.out.print("vowelRatio kalles med: " + s);
        double vowelCount = 0;
        for (int i = 0; i < s.length(); i++) {
            if (isVowel(s.charAt(i))) {
                vowelCount++;
            }
        }
        //velger blank som vokal/kons etter hva som passer best
        if (s.indexOf('-') != -1) {
            if (Math.abs((vowelCount-1/s.length()) - 0.38) < Math.abs((vowelCount/s.length()) - 0.38)) {
                vowelCount--;
            }
        }

        System.out.println(", returnerer med " + (vowelCount / s.length()));
        return vowelCount / s.length();
    }

    //antar at s ikke er tom
    static char lowestScoringVowel(String s) {
        System.out.print("lowestScoringVowel kalles med: " + s);
        int index = 0;
        while (s.length() > 0 && vowelCount(s) < s.length()) {
            if (!isVowel(s.charAt(index))) {
                s = removeChar(s, s.charAt(index));
            } else {
                index++;
            }
        }
        char c = s.charAt(0);
        for (int i = 1; i < s.length(); i++) {
            if (ScoreConstants.letterScore(s.charAt(i)) < ScoreConstants.letterScore(c)) {
                c = s.charAt(i);
            }
        }
        System.out.println(", returnerer: " + c);
        return c;
    }

    //antar at s ikke er tom
    static char lowestScoringCons(String s) {
        System.out.print("lowestScoringCons kalles med: " + s);
        int index = 0;
        while (s.length() > 0 && vowelCount(s) > 0) {
            if (isVowel(s.charAt(index))) {
                s = removeChar(s, s.charAt(index));
            } else {
                index++;
            }
        }
        char c = s.charAt(0);
        for (int i = 1; i < s.length(); i++) {
            if (ScoreConstants.letterScore(s.charAt(i)) < ScoreConstants.letterScore(c)) {
                c = s.charAt(i);
            }
        }
        System.out.println(", returnerer: " + c);
        return c;
    }

    static boolean bingoFriendly(String s) {
        if (s.length() < 3) {
            return false;
        } else if (s.length() < 5 && (vowelCount(s) < 1 || vowelCount(s) > 2)) {
            return false;
        } else if (s.length() >= 5 && (vowelCount(s) < 2 || vowelCount(s) > 3)) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (!isBingoFriendlyChar(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    static boolean isBingoFriendlyChar(char c) {
        if (c != 'E' && c != 'R' && c != 'N' && c != 'A' && c != 'T' && c != 'S' &&
                c != 'L' && c != 'I' && c != '-') {
            return false;
        }
        return true;
    }

    static int vowelCount(String s) {
        int vowels = 0;
        for (int i = 0; i < s.length(); i++) {
            if (isVowel(s.charAt(i))) {
                vowels++;
            }
        }
        return vowels;
    }

    static boolean containsVowel(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (isVowel(s.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    static boolean isVowel(char c) {
        if (c == 'E' || c == 'A' || c == 'I' || c == 'O' || c == 'U' || c == 'Å' ||
                c == 'Ø' || c == 'Æ' || c == 'Y' || c == '-') {
            return true;
        }
        return false;
    }
}
