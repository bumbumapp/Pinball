package com.bumbumapps.bouncy.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class InterstitialGapTest {
    private static final long T0 = 1_700_000_000_000L;

    @Test
    public void firstGameOverMayShow() {
        assertTrue(InterstitialHelper.gapAllows(T0, 0L));
    }

    @Test
    public void quickNextGameOverIsSkipped() {
        assertFalse(InterstitialHelper.gapAllows(T0 + 89_999L, T0));
        assertTrue(InterstitialHelper.gapAllows(T0 + 90_000L, T0));
    }
}
