package com.bumbumapps.bouncy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

public class GameSummaryTest {
    @Test
    public void bestOfIsTheHighestStoredScore() {
        assertEquals(48250L, GameSummary.bestOf(Arrays.asList(1200L, 48250L, 0L)));
        assertEquals(0L, GameSummary.bestOf(Collections.singletonList(0L)));
        assertEquals(0L, GameSummary.bestOf(null));
    }

    @Test
    public void newBestNeedsAHigherScoreWithLimitedBalls() {
        assertTrue(GameSummary.isNewBest(5000, 4000, false));
        assertTrue(GameSummary.isNewBest(10, 0, false));
        assertFalse(GameSummary.isNewBest(4000, 4000, false));
        assertFalse(GameSummary.isNewBest(0, 0, false));
        assertFalse("practice games never count", GameSummary.isNewBest(9000, 100, true));
    }
}
