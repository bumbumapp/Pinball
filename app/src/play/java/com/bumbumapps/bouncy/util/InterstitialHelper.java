package com.bumbumapps.bouncy.util;

import android.app.Activity;
import com.bumbumapps.bouncy.R;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

/**
 * Single preloaded interstitial shown after a finished game (banners stay on all screens), at most once
 * per {@link #MIN_GAP_MS} so quick back-to-back games don't get an ad each time. Call {@link #preload(Context)} once ads can be requested and
 * {@link #showIfReady(Activity, Runnable)} when a game ends; {@code next} always runs exactly once (after
 * the ad is dismissed, or immediately if no ad is ready).
 */
public final class InterstitialHelper {

    public static final long MIN_GAP_MS = 90_000L;

    private static long lastShownMs;

    private static InterstitialAd interstitialAd;
    private static boolean loading;

    private InterstitialHelper() {
    }

    public static void preload(Context context) {
        if (interstitialAd != null || loading || !AdsConsent.canRequestAds(context)) {
            return;
        }
        loading = true;
        InterstitialAd.load(context.getApplicationContext(), context.getString(R.string.admob_interstitial_id),
                new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd ad) {
                        loading = false;
                        interstitialAd = ad;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError error) {
                        loading = false;
                        interstitialAd = null;
                    }
                });
    }

    /** Pure frequency rule, unit tested. */
    static boolean gapAllows(long now, long lastShown) {
        return lastShown == 0L || now - lastShown >= MIN_GAP_MS;
    }

    public static void showIfReady(Activity activity, Runnable next) {
        final Runnable after = next != null ? next : () -> { };
        long now = System.currentTimeMillis();
        if (interstitialAd == null || activity == null || activity.isFinishing() || !gapAllows(now, lastShownMs)) {
            if (activity != null) {
                preload(activity);
            }
            after.run();
            return;
        }
        InterstitialAd ad = interstitialAd;
        interstitialAd = null;
        ad.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                preload(activity);
                after.run();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                preload(activity);
                after.run();
            }
        });
        lastShownMs = now;
        ad.show(activity);
    }
}
