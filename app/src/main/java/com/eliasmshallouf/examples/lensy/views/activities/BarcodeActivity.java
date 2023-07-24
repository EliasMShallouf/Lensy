package com.eliasmshallouf.examples.lensy.views.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.eliasmshallouf.examples.lensy.R;
import com.eliasmshallouf.examples.lensy.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BarcodeFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BarcodeActivity extends AppCompatActivity {
    public static final String BARCODE_EXTRA = "barcode";
    public static final int REQUEST_CAMERA_PERMISSION = 201;

    private FloatingActionButton autoFocus, flash;
    private boolean isAutoFocus = false, flashOn = false;
    private CodeScannerView scannerView;
    private CodeScanner mCodeScanner;
    private View close;

    private List<BarcodeFormat> formatList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        scannerView = findViewById(R.id.qr_scanner);
        autoFocus = findViewById(R.id.autoFocusBtn);
        flash = findViewById(R.id.flashBtn);
        close = findViewById(R.id.close);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) close.getLayoutParams();
        lp.bottomMargin += Utils.getNavigationBarHeight(getApplicationContext());
        close.setLayoutParams(lp);

        // only 1D barcodes
        formatList.addAll(Arrays.asList(BarcodeFormat.values()));
        formatList.remove(BarcodeFormat.AZTEC);
        formatList.remove(BarcodeFormat.DATA_MATRIX);
        formatList.remove(BarcodeFormat.QR_CODE);
        formatList.remove(BarcodeFormat.MAXICODE);

        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setTouchFocusEnabled(true);
        mCodeScanner.setFormats(formatList);
        mCodeScanner.setDecodeCallback(result -> {
            runOnUiThread(() -> {
                try {
                    String barcode = result.getText();
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if(Build.VERSION.SDK_INT >= 26)
                        vibrator.vibrate(VibrationEffect.createOneShot(250, VibrationEffect.DEFAULT_AMPLITUDE));
                    else
                        vibrator.vibrate(250);
                    setResult(1, new Intent().putExtra(BARCODE_EXTRA, barcode));
                    finish();
                } catch (Exception e){
                    Toast.makeText(BarcodeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    mCodeScanner.startPreview();
                }
            });
        });
        scannerView.setOnClickListener(view -> mCodeScanner.startPreview());

        isAutoFocus = mCodeScanner.isAutoFocusEnabled();
        flashOn = mCodeScanner.isFlashEnabled();

        autoFocus.setOnClickListener(v -> {
            isAutoFocus = !isAutoFocus;
            mCodeScanner.setAutoFocusEnabled(isAutoFocus);
            repaintAutoFocus();
        });

        flash.setOnClickListener(v -> {
            flashOn = !flashOn;
            mCodeScanner.setFlashEnabled(flashOn);
            repaintFlash();
        });

        close.setOnClickListener(v -> {
            setResult(0);
            finish();
        });

        repaintAutoFocus();
        repaintFlash();
        checkPermission();
    }

    private void repaintAutoFocus(){
        autoFocus.setSupportImageTintList(ColorStateList.valueOf(getApplicationContext().getColor(
                isAutoFocus ? R.color.colorPrimary : R.color.black
        )));
    }

    private void repaintFlash(){
        flash.setSupportImageTintList(ColorStateList.valueOf(getApplicationContext().getColor(
                flashOn ? R.color.colorPrimary : R.color.black
        )));
    }

    private void checkPermission(){
        if (ActivityCompat.checkSelfPermission(BarcodeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            mCodeScanner.startPreview();
        } else {
            ActivityCompat.requestPermissions(BarcodeActivity.this, new
                    String[]{ Manifest.permission.CAMERA }, REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mCodeScanner != null)
            mCodeScanner.releaseResources();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mCodeScanner != null)
            mCodeScanner.startPreview();
    }
}
