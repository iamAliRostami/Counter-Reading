package com.leon.counter_reading.di.view_model;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;

import com.leon.counter_reading.R;
import com.leon.counter_reading.infrastructure.IFlashLightManager;
import com.leon.counter_reading.utils.CustomToast;

import javax.inject.Inject;

public class FlashViewModel implements IFlashLightManager {

    private final Context context;
    private boolean isFlashOn = false;

    @Inject
    public FlashViewModel(Context context) {
        this.context = context;
    }

    @Override
    public boolean turnOn() {
        boolean hasFlash = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (hasFlash) {
            CameraManager camManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            try {
                String[] cameraId = camManager.getCameraIdList();
                try {
                    camManager.setTorchMode(cameraId[0], true);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
                isFlashOn = true;
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
            new CustomToast().error(context.getString(R.string.has_no_flash));
        }
        return isFlashOn;
    }

    @Override
    public boolean turnOff() {
        CameraManager camManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] cameraId = camManager.getCameraIdList();
            camManager.setTorchMode(cameraId[0], false);
            isFlashOn = false;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return isFlashOn;
    }

    @Override
    public boolean toggleFlash() {
        if (isFlashOn) {
            return turnOff();
        } else {
            return turnOn();
        }
    }
}
