package com.leon.counter_reading.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;

import com.leon.counter_reading.R;
import com.leon.counter_reading.infrastructure.IFlashLightManager;

public final class FlashLightManager implements IFlashLightManager {
    private final Context context;
    private boolean isFlashOn = false;

    public FlashLightManager(Context context) {
        this.context = context;
    }

    public boolean turnOn() {
        boolean hasFlash = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (hasFlash) {
            CameraManager camManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            try {
                String[] cameraId = camManager.getCameraIdList();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    camManager.setTorchMode(cameraId[0], true);
                }
                isFlashOn = true;
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
            CustomToast customToast = new CustomToast();
            customToast.error(context.getString(R.string.has_no_flash));
        }
        return isFlashOn;
    }

    public boolean turnOff() {
        CameraManager camManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] cameraId = camManager.getCameraIdList();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                camManager.setTorchMode(cameraId[0], false);
            }
            isFlashOn = false;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return !isFlashOn;
    }

    public boolean toggleFlash() {
        if (isFlashOn) {
            turnOff();
            return false;
        } else {
            turnOn();
            return true;
        }
    }
}
