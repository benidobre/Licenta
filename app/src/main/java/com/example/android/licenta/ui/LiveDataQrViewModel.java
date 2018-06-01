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
import java.util.Timer;
import java.util.TimerTask;



/**
 * Created by bdobre on 4/22/18.
 */

public class LiveDataQrViewModel extends ViewModel {

    private static final int ONE_SECOND = 1000;
    private MutableLiveData<Bitmap> qrCode = new MutableLiveData<>();
    private int current = 0;
    private byte[] rez;
    private int period = 2*ONE_SECOND;

//    public LiveDataQrViewModel() {
//        File imgFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "fb.jpg");
//        rez = BusinessLogic.fullyReadFileToBytes(imgFile);
//        if(current > rez.length/step) {
//            qrCode.postValue(BusinessLogic.getQR("end"));
//        } else {
//            qrCode.postValue(BusinessLogic.getBytesQR(BusinessLogic.subArray(rez, current * step, current * step + step)));
//        }
//        current++;
//
//    }
//
//    public void next() {
//        if(current > rez.length/step) {
//            qrCode.postValue(BusinessLogic.getQR("end"));
//        } else {
//            qrCode.postValue(BusinessLogic.getBytesQR(BusinessLogic.subArray(rez, current * step, current * step + step)));
//        }
//        current++;
//    }
//
//    public LiveData<Bitmap> getQrCode() {
//        return qrCode;
//    }
}
