package com.eliasmshallouf.examples.lensy.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

public class SettingsManager {
    @Setting public final static String THEME = "theme";

    @StringDef({
        THEME
    })
    private @interface Setting{}

    @IntDef({
        Theme.Light,
        Theme.Night,
        Theme.Auto
    })
    public @interface Theme{
        @Theme int Light = 0;
        @Theme int Night = 1;
        @Theme int Auto = 2;
    }


    private static SettingsManager instance;

    public static SettingsManager getInstance(Context context) {
        if(instance == null)
            instance = new SettingsManager(context);
        return instance;
    }

    public static SettingsManager getInstance() {
        return instance;
    }

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private SettingsManager(Context context) {
        this.preferences = context.getSharedPreferences("lensy_settings", Context.MODE_PRIVATE);
        this.editor = this.preferences.edit();
    }

    public void setTheme(@Theme int theme) {
        this.editor.putInt(THEME, theme);
        this.editor.commit();
    }

    public @Theme int getTheme() {
        return this.preferences.getInt(THEME,Theme.Auto);
    }
}
