package com.bumbumapps.bouncy;

import java.util.List;

/** Pure rules for the end-of-game card and the table card (unit tested). */
public final class GameSummary {
    private GameSummary() {
    }

    /** Best stored score for a table, or 0 when nothing has been scored yet. */
    public static long bestOf(List<Long> highScores) {
        long best = 0;
        if (highScores != null) {
            for (Long s : highScores) {
                if (s != null && s > best) best = s;
            }
        }
        return best;
    }

    /**
     * A finished game is a new best when it scored something, beat the previous best and was not
     * played with unlimited balls (practice games never count for high scores).
     */
    public static boolean isNewBest(long score, long previousBest, boolean unlimitedBalls) {
        return !unlimitedBalls && score > 0 && score > previousBest;
    }
}
