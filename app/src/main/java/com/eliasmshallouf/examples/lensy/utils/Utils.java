package com.eliasmshallouf.examples.lensy.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.RawRes;
import androidx.paging.PagedList;

import com.eliasmshallouf.examples.lensy.App;
import com.eliasmshallouf.examples.lensy.models.Asset;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class Utils {
    //function to load json row file as String
    public static String loadRawAsText(Context context, @RawRes int raw) {
        InputStream is = context.getResources().openRawResource(raw);
        Scanner scanner = new Scanner(is);
        String res = "";
        while (scanner.hasNextLine()) {
            res += scanner.nextLine() + (scanner.hasNextLine() ? "\n" : "");
        }
        return res;
    }

    //function to convert assets json array object to Asset List
    public static List<Asset> loadAssetsFromJson(String json) {
        Log.d("json", json);
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Asset>>(){}.getType();
        return gson.fromJson(json, listType);
    }

    //function to remove the null values that returned by room db
    public static <E> List<E> filter(PagedList<E> list) {
        List<E> res = new ArrayList<>();
        for(E item : list)
            if(item != null)
                res.add(item);
        return res;
    }

    public static BitMatrix encode(String contents, BarcodeFormat format, int width, int height) throws WriterException {
        try {
            return (new MultiFormatWriter()).encode(contents, format, width, height);
        } catch (WriterException var6) {
            throw var6;
        } catch (Exception var7) {
            throw new WriterException(var7);
        }
    }

    //just extracted function from zxing embedded library to customize the code colors
    public static BitmapDrawable createBitmap(BitMatrix matrix, @ColorInt int fillColor, @ColorInt int backgroundColor) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];

        for(int y = 0; y < height; ++y) {
            int offset = y * width;

            for(int x = 0; x < width; ++x) {
                pixels[offset + x] = matrix.get(x, y) ? fillColor : backgroundColor;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return new BitmapDrawable(App.get().getResources(), bitmap);
    }

    public static String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        if (s.length() == 1) return s.toUpperCase();
        return String.valueOf(s.charAt(0)).toUpperCase() + s.substring(1).toLowerCase();
    }

    public static int dpToPx(Context c, float dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                c.getResources().getDisplayMetrics()
        );
    }

    public static void animateCardBorder(MaterialCardView card, float strokeW) {
        float scale = strokeW > card.getStrokeWidth() ? 1.03f : 1f;
        card.animate().scaleX(scale).scaleY(scale).setDuration(200).start();
        card.setStrokeWidth((int) strokeW);
    }

    public static int getNavigationBarHeight(Context c){
        int id = c.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if(id > 0)
            return c.getResources().getDimensionPixelSize(id);
        return 0;
    }

    //function to highlight specific part of text based on filter
    public static SpannableStringBuilder filterText(String text, String search, Object span) {
        SpannableStringBuilder sb = new SpannableStringBuilder(text);
        int index = text.indexOf(search);
        while(index >= 0){
            sb.setSpan(span, index, index + search.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            index = text.indexOf(search,index + 1);
        }
        return sb;
    }

    //function to make custom touch action with card view - default was ripple effect
    public static void registerCardViewTouchAnimation(View v) {
        if(v instanceof MaterialCardView) {
            MaterialCardView cardView = (MaterialCardView) v;
            int stroke = cardView.getStrokeWidth();
            cardView.setOnTouchListener(new View.OnTouchListener() {
                private static final int MAX_CLICK_DURATION = 400;
                private long startClickTime;

                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    int action = motionEvent.getAction();
                    if (action == MotionEvent.ACTION_DOWN) {
                        startClickTime = Calendar.getInstance().getTimeInMillis();
                        v.animate().scaleXBy(.025f).setDuration(200).start();
                        v.animate().scaleYBy(.025f).setDuration(200).start();
                        if (stroke > 0)
                            cardView.setStrokeWidth(stroke * 2);
                        return true;
                    } else if (action == MotionEvent.ACTION_UP) {
                        v.animate().cancel();
                        v.animate().scaleX(1f).setDuration(200).start();
                        v.animate().scaleY(1f).setDuration(200).start();
                        cardView.setStrokeWidth(stroke);

                        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                        if(clickDuration < MAX_CLICK_DURATION) {
                            v.callOnClick();
                        }

                        return true;
                    }

                    return false;
                }
            });
        }
    }

    private static boolean isNightModeEnabled(Configuration configuration) {
        return  (configuration.uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }
}
