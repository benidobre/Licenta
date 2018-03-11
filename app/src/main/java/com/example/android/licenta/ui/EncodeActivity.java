package com.example.android.licenta.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.android.licenta.R;
import com.example.android.licenta.bl.BusinessLogic;

import java.io.File;
import java.io.UnsupportedEncodingException;


public class EncodeActivity extends AppCompatActivity {
    private ImageView qrImageView;
    private Button button;
    String text = "/storage/emulated/0/Download/images.jpg";
    String str;
    int frame = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        File imgFile = new  File("/sdcard/Images/test_image.jpg");
        File imgFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "bb.txt");

        byte[] rez = {0x1B, 0x2A, 0x7F, -0x80};
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    234);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            rez = BusinessLogic.fullyReadFileToBytes(imgFile);
        }

        str = "BENI";
        try {
            str = new String(rez,"ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        qrImageView = findViewById(R.id.qr_image_view);
        if(100*frame +100 > str.length()) {
            qrImageView.setImageBitmap(BusinessLogic.getQR(str.substring(100 * frame)));
        } else {
            qrImageView.setImageBitmap(BusinessLogic.getQR(str.substring(100 * frame, 100 * frame + 100)));
        }

        button = findViewById(R.id.button_next_qr);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frame++;

                if(100*frame > str.length()) {
                    qrImageView.setImageBitmap(BusinessLogic.getQR("end"));
                    return;
                }
                if(100*frame +100 > str.length()) {
                    qrImageView.setImageBitmap(BusinessLogic.getQR(str.substring(100 * frame)));
                } else {
                    qrImageView.setImageBitmap(BusinessLogic.getQR(str.substring(100 * frame, 100 * frame + 100)));
                }
            }
        });
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
