package com.bumbumapps.bouncy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.ViewGroup;

public class AboutActivity extends Activity {
    /** Called when the activity is first created. */
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.about);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().setNavigationBarColor(Color.BLACK);
        }

        int level = getIntent().getIntExtra("level", 1);
        int levels = FieldLayoutReader.getNumberOfLevels(this);
        Object name = FieldLayoutReader.layoutMapForLevel(this, level).get("name");
        ((TextView) findViewById(R.id.aboutTableIndex)).setText(getString(R.string.arcade_table_index, level, levels));
        ((TextView) findViewById(R.id.aboutTableName)).setText(name != null ? name.toString() : "");
        findViewById(R.id.aboutBackButton).setOnClickListener(v -> finish());

        // Rules come from string resource table[level]_rules: "Table Rules:\n- rule\n- rule".
        int rulesId = getResources().getIdentifier("table" + level + "_rules", "string", getPackageName());
        String rules = rulesId != 0 ? getString(rulesId) : "";
        ViewGroup list = findViewById(R.id.aboutRulesList);
        float density = getResources().getDisplayMetrics().density;
        for (String rule : rulesToBullets(rules)) {
            TextView item = new TextView(this);
            item.setText(rule);
            item.setTextColor(getResources().getColor(R.color.arcade_text));
            item.setTextSize(15);
            item.setLineSpacing(0, 1.2f);
            item.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.bg_arcade_bullet, 0, 0, 0);
            item.setCompoundDrawablePadding((int) (12 * density));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = (int) (12 * density);
            list.addView(item, lp);
        }
    }

    /** Splits "Table Rules:\n- a\n- b" into ["a", "b"] (unit tested). */
    static java.util.List<String> rulesToBullets(String rules) {
        java.util.List<String> out = new java.util.ArrayList<>();
        if (rules == null) return out;
        for (String line : rules.split("\n")) {
            String t = line.trim();
            if (!t.startsWith("-")) continue; // the localized "Table Rules:" heading
            t = t.substring(1).trim();
            if (!t.isEmpty()) out.add(t);
        }
        return out;
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

    public static Intent startForLevel(Context context, int level) {
        Intent aboutIntent = new Intent(context, AboutActivity.class);
        aboutIntent.putExtra("level", level);
        context.startActivity(aboutIntent);
        return aboutIntent;
    }
}
