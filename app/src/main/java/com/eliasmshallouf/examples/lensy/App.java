package com.eliasmshallouf.examples.lensy;

import android.content.res.TypedArray;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDexApplication;

import com.eliasmshallouf.examples.lensy.utils.SettingsManager;

public class App extends MultiDexApplication {
    private static App instance;
    private SettingsManager settingsManager;

    public static App get() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        settingsManager = SettingsManager.getInstance(this);
        refreshTheme();
    }

    public void refreshTheme() {
        int theme = settingsManager.getTheme();
        if(theme == 0) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if(theme == 1) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    public @ColorInt int getAttrColor(@AttrRes int attr) {
        TypedArray arr = getTheme().obtainStyledAttributes(R.style.AppTheme_Main, new int[]{ attr });
        return arr.getColor(0, 0);
    }
}
