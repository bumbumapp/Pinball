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
 * Single preloaded interstitial shown after every finished game (owner rule for Pinball; banners stay on
 * all screens). Call {@link #preload(Context)} once ads can be requested and
 * {@link #showIfReady(Activity, Runnable)} when a game ends; {@code next} always runs exactly once (after
 * the ad is dismissed, or immediately if no ad is ready).
 */
public final class InterstitialHelper {

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

    public static void showIfReady(Activity activity, Runnable next) {
        final Runnable after = next != null ? next : () -> { };
        if (interstitialAd == null || activity == null || activity.isFinishing()) {
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
        ad.show(activity);
    }
}
