package com.bumbumapps.bouncy.util;

import android.app.Activity;
import android.content.Context;

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
        ad.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                preload(activity);
                after.run();
            }

            @Override
            public void onAdFailed(int code) {
                preload(activity);
                after.run();
            }
        });
        lastShownMs = now;
        ad.show(activity);
    }
}
