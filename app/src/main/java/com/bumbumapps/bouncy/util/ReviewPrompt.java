package com.bumbumapps.bouncy.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

/**
 * Asks for a Play Store rating with the In-App Review API at a happy moment: a new best score,
 * from the 3rd finished game on, never in the session of the first app launch, and at most once per
 * {@link #COOLDOWN_MS} locally (Google applies its own quota on top). Never bound to a button.
 */
public final class ReviewPrompt {
    static final int MIN_GAMES = 3;
    static final int MIN_LAUNCHES = 2;
    static final long COOLDOWN_MS = 30L * 24 * 60 * 60 * 1000;

    private static final String TAG = "ReviewPrompt";
    private static final String PREFS = "review_prompt";
    private static final String KEY_LAUNCHES = "launches";
    private static final String KEY_GAMES = "games";
    private static final String KEY_LAST_REQUEST = "last_request";

    private ReviewPrompt() {
    }

    /** Pure decision logic (unit tested). */
    static boolean shouldRequest(int games, int launches, long lastRequestMs, long nowMs, boolean newBest) {
        if (!newBest || games < MIN_GAMES || launches < MIN_LAUNCHES) return false;
        return lastRequestMs <= 0 || nowMs - lastRequestMs >= COOLDOWN_MS;
    }

    private static SharedPreferences prefs(Context c) {
        return c.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    /** Call once per process start (Application.onCreate). */
    public static void onAppLaunch(Context c) {
        SharedPreferences p = prefs(c);
        p.edit().putInt(KEY_LAUNCHES, p.getInt(KEY_LAUNCHES, 0) + 1).apply();
    }

    /** Records a finished game; true if the review flow should start now (request time stored at once). */
    public static boolean recordFinishedGame(Context c, boolean newBest) {
        SharedPreferences p = prefs(c);
        int games = p.getInt(KEY_GAMES, 0) + 1;
        long now = System.currentTimeMillis();
        boolean ask = shouldRequest(games, p.getInt(KEY_LAUNCHES, 0), p.getLong(KEY_LAST_REQUEST, 0L), now, newBest);
        SharedPreferences.Editor e = p.edit().putInt(KEY_GAMES, games);
        if (ask) e.putLong(KEY_LAST_REQUEST, now);
        e.apply();
        Log.i(TAG, "game #" + games + ", new best: " + newBest + ", request review: " + ask);
        return ask;
    }

    /** Starts Play's in-app review flow; Google decides whether the sheet is actually shown. */
    public static void launch(Activity activity) {
        try {
            ReviewManager manager = ReviewManagerFactory.create(activity);
            manager.requestReviewFlow().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "requestReviewFlow failed", task.getException());
                    return;
                }
                if (activity.isFinishing() || activity.isDestroyed()) return;
                ReviewInfo info = task.getResult();
                manager.launchReviewFlow(activity, info);
            });
        } catch (RuntimeException e) {
            Log.w(TAG, "In-app review unavailable", e);
        }
    }
}
