package com.bumbumapps.bouncy.util;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.bumbumapps.bouncy.R;
import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.InterstitialAd;

/**
 * Huawei AppGallery build of the game-over interstitial: same API and the same {@link #MIN_GAP_MS} cap
 * as the Play build, served by Huawei Ads. {@code next} always runs exactly once.
 */
public final class InterstitialHelper {

    public static final long MIN_GAP_MS = 90_000L;

    private static long lastShownMs;

    private static InterstitialAd interstitialAd;

    private InterstitialHelper() {
    }

    public static void preload(Context context) {
        if (interstitialAd != null || !AdsConsent.canRequestAds(context)) {
            return;
        }
        Context app = context.getApplicationContext();
        InterstitialAd ad = new InterstitialAd(app);
        ad.setAdId(AdsConsent.slot(app, R.string.hms_interstitial_id, R.string.hms_test_interstitial_id));
        ad.setAdListener(new AdListener() {
            @Override
            public void onAdFailed(int code) {
                if (interstitialAd == ad) {
                    interstitialAd = null;
                }
            }
        });
        interstitialAd = ad;
        ad.loadAd(new AdParam.Builder().build());
    }

    /** Pure frequency rule, unit tested. */
    static boolean gapAllows(long now, long lastShown) {
        return lastShown == 0L || now - lastShown >= MIN_GAP_MS;
    }

    public static void showIfReady(Activity activity, Runnable next) {
        final Runnable after = next != null ? next : () -> { };
        long now = System.currentTimeMillis();
        InterstitialAd ad = interstitialAd;
        if (ad == null || !ad.isLoaded() || activity == null || activity.isFinishing() || !gapAllows(now, lastShownMs)) {
            if (activity != null) {
                preload(activity);
            }
            after.run();
            return;
        }
        interstitialAd = null;
        // Huawei only reports load failures via onAdFailed: if show() never opens the ad, do not wait forever.
        final boolean[] opened = {false};
        final boolean[] done = {false};
        final Runnable finish = () -> {
            if (done[0]) {
                return;
            }
            done[0] = true;
            preload(activity);
            after.run();
        };
        ad.setAdListener(new AdListener() {
            @Override
            public void onAdOpened() {
                opened[0] = true;
            }

            @Override
            public void onAdClosed() {
                finish.run();
            }

            @Override
            public void onAdFailed(int code) {
                finish.run();
            }
        });
        lastShownMs = now;
        ad.show(activity);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!opened[0]) {
                finish.run();
            }
        }, 3000L);
    }
}
