package com.example.android.licenta.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.android.licenta.R;
import com.example.android.licenta.bl.BusinessLogic;

import java.io.File;
import java.io.UnsupportedEncodingException;


public class EncodeActivity extends AppCompatActivity {
    private ImageView qrImageView;
    String text = "/storage/emulated/0/Download/images.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        File imgFile = new  File("/sdcard/Images/test_image.jpg");
//        File imgFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Orar3CC.xls");

        byte[] rez = {0x1B, 0x2A, 0x7F, -0x80};
        String str = "BENI";
        try {
            str = new String(rez,"ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        byte[] rez2;
//        try {
//            rez2 = str.getBytes("ISO-8859-1");
//        } catch (UnsupportedEncodingException e) {
//            Log.i("BENI", "file not present");
//        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        qrImageView = findViewById(R.id.qr_image_view);
        qrImageView.setImageBitmap(BusinessLogic.getQR(str));
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
                Intent intent = new Intent(this, DecodeActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
