package com.example.android.licenta.bl;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.android.licenta.io.nayuki.qrcodegen.QrCode;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


/**
 * Created by bdobre on 2/22/18.
 */

public class BusinessLogic {

    public static Bitmap getQR(String str){
		QrCode.Ecc errCorLvl = QrCode.Ecc.LOW;  // Error correction level

		QrCode qr = QrCode.encodeText(str, errCorLvl);  // Make the QR Code symbol
        return qr.toBitmap(100,1);
    }

    public static Bitmap getBytesQR(byte[] bytes){
        QrCode.Ecc errCorLvl = QrCode.Ecc.LOW;  // Error correction level

        QrCode qr = QrCode.encodeBinary(bytes, errCorLvl);
        return qr.toBitmap(10,4);
    }

    public static byte[] fullyReadFileToBytes(File f) {
        int size = (int) f.length();
        byte bytes[] = new byte[size];
        byte tmpBuff[] = new byte[size];
        FileInputStream fis;
        try {
            fis = new FileInputStream(f);
            int read = fis.read(bytes, 0, size);
            if (read < size) {
                int remain = size - read;
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain);
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                    remain -= read;
                }
            }
            fis.close();
        }  catch (IOException e){
            Log.i("HELLO", "fullyReadFileToBytes: FAILED");
        }

        return bytes;
    }
}
