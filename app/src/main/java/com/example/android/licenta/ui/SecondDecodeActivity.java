package com.example.android.licenta.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.android.licenta.R;
import com.example.android.licenta.bl.BusinessLogic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import me.dm7.barcodescanner.zbar.ZBarScannerView;

import static com.example.android.licenta.ui.LiveDataQrViewModel.STEP_ENCODING_LENGTH;

public class SecondDecodeActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler{
    private ZBarScannerView mScannerView;
    private ImageView qrImageView;
    private File photo ;
    private FileOutputStream fos;
    private Vibrator v;
    private int current = 0;
    private int progress = 0;
    private byte[] destination;
    byte[] last;

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
        LinearLayout linearLayout = findViewById(R.id.activity_main);
        linearLayout.addView(mScannerView);

        destination = new byte[1500+500];


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

    @Override
    public void handleResult(me.dm7.barcodescanner.zbar.Result result) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        v.vibrate(300);
        try {
            if(result.getContents().equalsIgnoreCase("end")) {
                fos.write(destination,0, progress);
                fos.close();
                fos = null;
                finish();
                return;
            }

            byte[] received = result.getContents().getBytes(StandardCharsets.ISO_8859_1);
            int dataLength = received.length - STEP_ENCODING_LENGTH;

            if(Arrays.equals(received,last)) {
                return;
            } else {
                byte[] stepInBytes = ByteBuffer.allocate(4).putInt(current).array();
                byte[] previousStepInBytes = ByteBuffer.allocate(4).putInt(current-1).array();
                byte[] currentStep = Arrays.copyOfRange(received, dataLength , dataLength + STEP_ENCODING_LENGTH);
                if(Arrays.equals(currentStep,stepInBytes)) {
                    current++;


                    System.arraycopy(received, 0, destination, progress, dataLength);


                    progress += dataLength;
                    qrImageView.setImageBitmap(BusinessLogic.getBytesQR(currentStep));


                }
                else if(Arrays.equals(currentStep,previousStepInBytes)){
                    progress -= (last.length - STEP_ENCODING_LENGTH);
                    System.arraycopy(received, 0, destination, progress, dataLength);

                    progress += dataLength;
//                    progress -= (last.length - received.length);

                    qrImageView.setImageBitmap(BusinessLogic.getBytesQR(currentStep));
                }
            }
            last = received;

            if(fos == null) {
                if (photo.exists()) {
                    photo.delete();
                }
                fos = new FileOutputStream(photo.getPath(), true);
            }


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
