package com.bumbumapps.bouncy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.KeyEvent;

public class BouncyPreferences extends PreferenceActivity {
    Preference sourceCode,licence;

    private static boolean supportsMultitouch() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    private static boolean supportsHapticFeedback(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator == null || !vibrator.hasVibrator()) {
                return false;
            }
        }
        return true;
    }


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().setNavigationBarColor(Color.BLACK);
        }
        licence=(Preference)findPreference("app_licence");
        licence.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/dozingcat/Vector-Pinball/blob/master/COPYING.txt"));
                startActivity(intent);
                return true;
            }
        });
        sourceCode=(Preference)findPreference("source_code");
        sourceCode.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse("https://github.com/bumbumapp/Pinball"));
                startActivity(intent);
                return true;
            }
        });
        // If multitouch or haptic feedback APIs aren't available, disable the preference items.
        if (!supportsMultitouch()) {
            CheckBoxPreference mtPref = (CheckBoxPreference) findPreference("independentFlippers");
            mtPref.setChecked(false);
            mtPref.setEnabled(false);
        }
        if (!supportsHapticFeedback(this)) {
            CheckBoxPreference hapticPref = (CheckBoxPreference) findPreference("haptic");
            hapticPref.setChecked(false);
            hapticPref.setEnabled(false);
        }
    }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Return to main activity when "P" is pressed, to avoid accidentally quitting by hitting
        // "back" multiple times. See https://github.com/dozingcat/Vector-Pinball/issues/103.
        if (keyCode == KeyEvent.KEYCODE_P) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
