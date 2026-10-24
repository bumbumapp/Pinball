package com.bumbumapps.bouncy;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

public class AboutRulesTest {
    @Test
    public void splitsLocalizedRulesIntoBullets() {
        assertEquals(Arrays.asList("Light all rollovers.", "Shoot the ramp."),
                AboutActivity.rulesToBullets("Table Rules:\n- Light all rollovers.\n- Shoot the ramp."));
        assertEquals(Collections.singletonList("Règle"), AboutActivity.rulesToBullets("Règles :\n-  Règle "));
        assertEquals(Collections.emptyList(), AboutActivity.rulesToBullets(null));
    }
}
