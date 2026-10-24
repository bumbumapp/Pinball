package com.bumbumapps.bouncy.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ReviewPromptTest {
    private static final long NOW = 1_800_000_000_000L;

    @Test
    public void asksOnANewBestFromTheThirdGameOfASecondLaunch() {
        assertTrue(ReviewPrompt.shouldRequest(3, 2, 0, NOW, true));
    }

    @Test
    public void neverAsksWithoutANewBestOrTooEarly() {
        assertFalse(ReviewPrompt.shouldRequest(10, 5, 0, NOW, false));
        assertFalse(ReviewPrompt.shouldRequest(2, 5, 0, NOW, true));
        assertFalse(ReviewPrompt.shouldRequest(10, 1, 0, NOW, true));
    }

    @Test
    public void respectsTheCooldown() {
        assertFalse(ReviewPrompt.shouldRequest(10, 5, NOW - 1000, NOW, true));
        assertTrue(ReviewPrompt.shouldRequest(10, 5, NOW - ReviewPrompt.COOLDOWN_MS, NOW, true));
    }
}
