package com.bumbumapps.bouncy.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

/** The "large" anchored adaptive banner was about twice as tall as a normal banner and covered the table. */
public class BannerSizeTest {
    @Test
    public void usesTheStandardAnchoredAdaptiveSize() throws Exception {
        String src = new String(Files.readAllBytes(Paths.get(
                "src/main/java/com/bumbumapps/bouncy/util/BannerHelper.java")), StandardCharsets.UTF_8);
        assertFalse(src.contains("getLargeAnchoredAdaptiveBannerAdSize"));
        assertTrue(src.contains("getCurrentOrientationAnchoredAdaptiveBannerAdSize"));
    }
}
