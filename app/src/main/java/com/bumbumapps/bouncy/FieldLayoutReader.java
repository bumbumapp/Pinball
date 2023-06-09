package com.bumbumapps.bouncy;

import android.content.Context;

import com.bumbumapps.vectorpinball.util.IOUtils;
import com.bumbumapps.bouncy.util.JSONUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FieldLayoutReader {
    static Map<Integer, Map<String, Object>> _layoutMap = new HashMap<>();

    public static int getNumberOfLevels(Context context) {
        try {
            // This would be cleaner with Arrays.stream/filter/count, but that's not supported
            // by Android's desugaring library so it breaks on older devices.
            List<String> tableFiles = Arrays.asList(context.getAssets().list("tables"));
            int count = 0;
            while (tableFiles.contains("table" + (count + 1) + ".json")) {
                count++;
            }
            return count;
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    static Map<String, Object> readFieldLayout(Context context, int level) {
        try {
            String assetPath = "tables/table" + level + ".json";
            InputStream fin = context.getAssets().open(assetPath);
            String s = IOUtils.utf8FromStream(fin);
            return JSONUtils.mapFromJSONString(s);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Map<String, Object> layoutMapForLevel(Context context, int level) {
        Map<String, Object> levelLayout = _layoutMap.get(level);
        if (levelLayout == null) {
            levelLayout = readFieldLayout(context, level);
            _layoutMap.put(level, levelLayout);
        }
        return levelLayout;
    }
}
