package com.sun.amy.configs;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PrefSettings {

    private static PrefSettings sSelf;
    private Map<String, Object> mMap = null;
    private SharedPreferences mPreferences;

    public static PrefSettings getSettings(Context context) {
        if (sSelf == null) {
            sSelf = new PrefSettings(context);
        }
        return sSelf;
    }

    private PrefSettings(Context context) {
        this.mMap = new HashMap<>();
        this.mPreferences = context.getSharedPreferences(
                PrefKeys.PREF_SETTINGS, Activity.MODE_PRIVATE);
    }

    public void setValue(String key, Object value) {
        mMap.put(key, value);
    }

    public boolean containsKey(String key) {
        return mPreferences.contains(key);
    }

    public String getValue(String key) {
        String result = mPreferences.getString(key, "");
        return result;
    }

    public void deleteValue(String key){
        SharedPreferences.Editor editor = mPreferences.edit();
        if (containsKey(key)) {
            editor.remove(key);
        }
        editor.commit();
    }

    public void save() {
        SharedPreferences.Editor editor = mPreferences.edit();
        Set<String> keys = mMap.keySet();
        for (String key : keys) {
            editor.putString(key, (String) mMap.get(key));
        }
        editor.commit();
        mMap.clear();
    }
}
