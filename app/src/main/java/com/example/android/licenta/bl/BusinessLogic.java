package com.example.android.licenta.bl;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.android.licenta.io.nayuki.qrcodegen.QrCode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;

/**
 * Created by bdobre on 2/22/18.
 */

public class BusinessLogic {

    public static Bitmap getQrCode(String text, int size) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>(2);
            hints.put(EncodeHintType.CHARACTER_SET, "ISO-8859-1");
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, size, size, hints);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return Bitmap.createBitmap(size, size, Bitmap.Config.ALPHA_8);
        }
    }

    public static Bitmap getQR(String str){
        String text = "Hello, world!";          // User-supplied Unicode text
		QrCode.Ecc errCorLvl = QrCode.Ecc.LOW;  // Error correction level

		QrCode qr = QrCode.encodeText(str, errCorLvl);  // Make the QR Code symbol
//        QrCode qr = QrCode.encodeBinary(bytes, errCorLvl);
        return qr.toBitmap(100,4);
    }

    public static byte[] readFile(File file) {
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
            return bytes;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new byte[size];
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new byte[size];
        }
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
