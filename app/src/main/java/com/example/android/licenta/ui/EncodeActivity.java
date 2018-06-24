package com.example.android.licenta.ui;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.android.licenta.R;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;


public class EncodeActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler {
    private ImageView qrImageView;
    private ZBarScannerView mScannerView;
    LiveDataQrViewModel liveDataQrViewModel;
    Vibrator v;
    private long timestamp;
    private long PERIOD = 5000;
    private int nrOfACKToUpgrade = 3;
    private int nrOfACK;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        File imgFile = new  File("/sdcard/Images/test_image.jpg");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    234);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

        }

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        qrImageView = findViewById(R.id.qr_image_view_2);

        mScannerView = new ZBarScannerView(this);           // Programmatically initialize the scanner view
        LinearLayout linearLayout = findViewById(R.id.activity_main_2);
        linearLayout.addView(mScannerView);

        liveDataQrViewModel = ViewModelProviders.of(EncodeActivity.this).get(LiveDataQrViewModel.class);
        liveDataQrViewModel.getQrCode().observe(EncodeActivity.this, new Observer<Bitmap>() {
            @Override
            public void onChanged(@Nullable Bitmap bitmap) {
                qrImageView.setImageBitmap(bitmap);
            }
        });

        Timer timer = new Timer();
        timestamp = new Date().getTime();
        nrOfACK = 0;
        // Update the elapsed time every second.
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (new Date().getTime() - timestamp > PERIOD) {
                    liveDataQrViewModel.downScale();
                    nrOfACK = 0;
                }


            }
        }, 0, PERIOD);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.decode, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.decode_qr:
                Intent intent = new Intent(this, SecondDecodeActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void handleResult(Result rawResult) {
        String s = rawResult.getContents();
        v.vibrate(300);
        byte[] stepInBytes = ByteBuffer.allocate(4).putInt(liveDataQrViewModel.getCurrent()).array();
        byte[] ackStep = rawResult.getContents().getBytes(StandardCharsets.ISO_8859_1);
        if(Arrays.equals(ackStep,stepInBytes)) {
            long auxTimestamp = new Date().getTime();

            if (auxTimestamp - timestamp <= PERIOD) {
                nrOfACK++;
                if (nrOfACK >= nrOfACKToUpgrade) {
                    liveDataQrViewModel.upScale();
                }
            }

            timestamp = auxTimestamp;
            liveDataQrViewModel.next();
        }
        mScannerView.resumeCameraPreview(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mScannerView != null) {
            mScannerView.stopCamera();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }
}
