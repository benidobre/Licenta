package com.example.android.licenta.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.android.licenta.R;
import com.example.android.licenta.bl.BusinessLogic;
import com.google.zxing.Result;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import me.dm7.barcodescanner.zbar.ZBarScannerView;
//import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class SecondDecodeActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler{
//    private ZXingScannerView zXingScannerView;
    private ZBarScannerView mScannerView;
    private ImageView qrImageView;
    private File photo ;
    private FileOutputStream fos;
    private Vibrator v;
    AlertDialog dialog;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 123:
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    break;

                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_decode);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    356);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    123);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.VIBRATE},
                    546);
        }

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        photo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "bb.jpg");
        qrImageView = findViewById(R.id.qr_image_view);
        mScannerView = new ZBarScannerView(this);           // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View ackView = getLayoutInflater().inflate(R.layout.ack_dialog, null);
        builder.setView(ackView);
        dialog = builder.create();
        dialog.show();

        mHandler.sendEmptyMessageDelayed(123, 2000);


    }

//    public void scan(View view){
//        zXingScannerView =new ZXingScannerView(getApplicationContext());
//        setContentView(zXingScannerView);
//        zXingScannerView.setResultHandler(this);
//        zXingScannerView.startCamera();
//
//    }



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

    byte[] last = new byte[10];
    @Override
    public void handleResult(me.dm7.barcodescanner.zbar.Result result) {
//        do something with barcode data returned
        Toast.makeText(this, "DONE", Toast.LENGTH_SHORT).show();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            if(result.getContents().equalsIgnoreCase("end")) {
                fos.close();
                fos = null;
                finish();
                return;
            }

            if(result.getContents().equalsIgnoreCase("focus")) {
                return;
            }

            byte[] rez = result.getContents().getBytes(StandardCharsets.ISO_8859_1);

            if(Arrays.equals(BusinessLogic.subArray(rez,0,10),last)) {
                return;
            }
            last = BusinessLogic.subArray(rez,0,10);

            if(fos == null) {
                if (photo.exists()) {
                    photo.delete();
                }
                fos = new FileOutputStream(photo.getPath(), true);
            }
            fos.write(rez);


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (java.io.IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        } finally {
            mScannerView.resumeCameraPreview(this);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.encode, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.encode_qr:
                Intent intent = new Intent(this, EncodeActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
