package com.example.android.licenta.ui;

import android.Manifest;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.example.android.licenta.bl.BusinessLogic;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;



/**
 * Created by bdobre on 4/22/18.
 */

public class LiveDataQrViewModel extends ViewModel {

    public int ONE_SECOND = 1000;
    private MutableLiveData<Bitmap> qrCode = new MutableLiveData<>();
    private int current = 0;
    private byte[] rez;
    private int period = 2*ONE_SECOND;
    private int step = 106;
    private int progress = 0;
    public static int STEP_ENCODING_LENGTH = 4;
    private int[] versions = {17, 32, 53, 78, 106};

    public LiveDataQrViewModel() {
        File imgFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "fb.jpg");
        rez = BusinessLogic.fullyReadFileToBytes(imgFile);
        if(progress > rez.length) {
            qrCode.postValue(BusinessLogic.getQR("end"));
        } else {
            byte[] bytes = Arrays.copyOfRange(rez, progress, progress + step);
            byte[] stepInBytes = ByteBuffer.allocate(STEP_ENCODING_LENGTH).putInt(current).array();
            byte[] destination = new byte[bytes.length + stepInBytes.length];
            System.arraycopy(bytes, 0, destination, 0, bytes.length);
            System.arraycopy(stepInBytes, 0, destination, bytes.length, stepInBytes.length);
            qrCode.postValue(BusinessLogic.getBytesQR(destination));
        }
        progress += step;

    }

    public void next() {
        current++;
        if(progress > rez.length) {
            qrCode.postValue(BusinessLogic.getQR("end"));
        } else {
            byte[] bytes = Arrays.copyOfRange(rez, progress, progress + step);
            byte[] stepInBytes = ByteBuffer.allocate(STEP_ENCODING_LENGTH).putInt(current).array();
            byte[] destination = new byte[bytes.length + stepInBytes.length];
            System.arraycopy(bytes, 0, destination, 0, bytes.length);
            System.arraycopy(stepInBytes, 0, destination, bytes.length, stepInBytes.length);
            qrCode.postValue(BusinessLogic.getBytesQR(destination));
        }
        progress += step;

    }

    public LiveData<Bitmap> getQrCode() {
        return qrCode;
    }

    public int getCurrent() {
        return current;
    }

    public void downScale() {
        progress -= step;
        int i;
        for (i = 0; i < versions.length; ++i) {
            if (step == versions[i]) {
                break;
            }
        }
        if (i > 0) {
            step = versions[i-1];
        }

        byte[] bytes = Arrays.copyOfRange(rez, progress, progress + step);
        byte[] stepInBytes = ByteBuffer.allocate(STEP_ENCODING_LENGTH).putInt(current).array();
        byte[] destination = new byte[bytes.length + stepInBytes.length];
        System.arraycopy(bytes, 0, destination, 0, bytes.length);
        System.arraycopy(stepInBytes, 0, destination, bytes.length, stepInBytes.length);
        qrCode.postValue(BusinessLogic.getBytesQR(destination));
        progress += step;
    }

    public void upScale() {
        int i;
        for (i = 0; i < versions.length; ++i) {
            if (step == versions[i]) {
                break;
            }
        }
        if (i < versions.length - 1) {
            step = versions[i+1];
        }
    }
}
