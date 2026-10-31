package com.bumbumapps.vectorpinball.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class FieldDriverTest {

    /** Field that is always active and never touches its (absent) layout. */
    static class IdleField extends Field {
        IdleField() {
            super(System::currentTimeMillis, null, null);
        }

        @Override public boolean hasActiveElements() { return true; }
        @Override public float getTargetTimeRatio() { return 1f; }
        @Override public void tick(long nanos, int iters) { }
    }

    // Play vitals ANR: FieldDriver.stop() joined the game thread forever from onPause.
    @Test
    public void stopReturnsQuicklyEvenWhenDrawIsStuck() throws Exception {
        CountDownLatch drawing = new CountDownLatch(1);
        FieldDriver driver = new FieldDriver();
        driver.setField(new IdleField());
        driver.setDrawFunction(() -> {
            drawing.countDown();
            long end = System.currentTimeMillis() + 10_000;
            // Busy wait that ignores interrupts, like a draw blocked on the surface.
            while (System.currentTimeMillis() < end) { }
        });
        driver.start();
        assertTrue(drawing.await(2, TimeUnit.SECONDS));

        long t0 = System.nanoTime();
        driver.stop();
        long ms = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - t0);
        assertTrue("stop() blocked for " + ms + " ms", ms < 2000);
        assertFalse(driver.running);
    }

    @Test
    public void stopEndsAnIdleGameThread() throws Exception {
        FieldDriver driver = new FieldDriver();
        driver.start();
        driver.stop();
        driver.gameThread.join(1000);
        assertFalse(driver.gameThread.isAlive());
    }
}
