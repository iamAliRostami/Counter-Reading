package com.leon.counter_reading.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Debug;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.base_items.BaseActivity;
import com.leon.counter_reading.databinding.ActivityHomeBinding;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.PermissionManager;

import java.util.ArrayList;

import static com.leon.counter_reading.utils.PermissionManager.isNetworkAvailable;

public class HomeActivity extends BaseActivity {
    ActivityHomeBinding binding;
    Activity activity;
    @SuppressLint("NonConstantResourceId")
    View.OnClickListener onClickListener = v -> {
        int id = v.getId();
        Intent intent = new Intent();
        switch (id) {
            case R.id.linear_layout_download:
                MyApplication.POSITION = 0;
                intent = new Intent(getApplicationContext(), DownloadActivity.class);
                break;
            case R.id.linear_layout_reading:
                MyApplication.POSITION = 1;
//                MyApplication.isReading = true;
                intent = new Intent(getApplicationContext(), ReadingActivity.class);
                break;
            case R.id.linear_layout_upload:
                MyApplication.POSITION = 2;
                intent = new Intent(getApplicationContext(), UploadActivity.class);
                break;
            case R.id.linear_layout_report:
                MyApplication.POSITION = 3;
                intent = new Intent(getApplicationContext(), ReportActivity.class);
                break;
            case R.id.linear_layout_location:
                MyApplication.POSITION = 4;
                intent = new Intent(getApplicationContext(), LocationActivity.class);
                break;
            case R.id.linear_layout_reading_setting:
                MyApplication.POSITION = 5;
                intent = new Intent(getApplicationContext(), ReadingSettingActivity.class);
                break;
            case R.id.linear_layout_app_setting:
                MyApplication.POSITION = 6;
                intent = new Intent(getApplicationContext(), SettingActivity.class);
                break;
            case R.id.linear_layout_help:
                MyApplication.POSITION = 7;
                intent = new Intent(getApplicationContext(), HelpActivity.class);
                break;
            case R.id.linear_layout_exit:
                finishAffinity();
                break;
        }
        if (id != R.id.linear_layout_exit) {
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void initialize() {
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View childLayout = binding.getRoot();
        ConstraintLayout parentLayout = findViewById(R.id.base_Content);
        parentLayout.addView(childLayout);
        activity = this;

        if (isNetworkAvailable(getApplicationContext()))
            checkPermissions();
        else PermissionManager.enableNetwork(this);
    }

    void checkPermissions() {
        if (PermissionManager.gpsEnabled(this))
            if (!PermissionManager.checkLocationPermission(getApplicationContext())) {
                askLocationPermission();
            } else if (!PermissionManager.checkStoragePermission(getApplicationContext())) {
                askStoragePermission();
            } else {
                initializeImageViews();
                setOnImageViewClickListener();
            }
    }

    void askStoragePermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                CustomToast customToast = new CustomToast();
                customToast.info(getString(R.string.access_granted));
                checkPermissions();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                PermissionManager.forceClose(activity);
            }
        };
        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage(getString(R.string.confirm_permission))
                .setRationaleConfirmText(getString(R.string.allow_permission))
                .setDeniedMessage(getString(R.string.if_reject_permission))
                .setDeniedCloseButtonText(getString(R.string.close))
                .setGotoSettingButtonText(getString(R.string.allow_permission))
                .setPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).check();
    }

    void askLocationPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                CustomToast customToast = new CustomToast();
                customToast.info(getString(R.string.access_granted));
                checkPermissions();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                PermissionManager.forceClose(activity);
            }
        };
        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage(getString(R.string.confirm_permission))
                .setRationaleConfirmText(getString(R.string.allow_permission))
                .setDeniedMessage(getString(R.string.if_reject_permission))
                .setDeniedCloseButtonText(getString(R.string.close))
                .setGotoSettingButtonText(getString(R.string.allow_permission))
                .setPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ).check();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void initializeImageViews() {
        binding.imageViewAppSetting.setImageDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.img_app_settings));
        binding.imageViewDownload.setImageDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.img_download_information));
        binding.imageViewUpload.setImageDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.img_upload));
        binding.imageViewReadingSetting.setImageDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.img_reading_settings));
        binding.imageViewHelp.setImageDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.img_help));
        binding.imageViewExit.setImageDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.img_exit));
        binding.imageViewReading.setImageDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.img_readings));
        binding.imageViewReport.setImageDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.img_reading_report));
        binding.imageViewLocation.setImageDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.img_location));
    }

    void setOnImageViewClickListener() {
        binding.linearLayoutDownload.setOnClickListener(onClickListener);
        binding.linearLayoutReading.setOnClickListener(onClickListener);
        binding.linearLayoutUpload.setOnClickListener(onClickListener);
        binding.linearLayoutReport.setOnClickListener(onClickListener);
        binding.linearLayoutHelp.setOnClickListener(onClickListener);
        binding.linearLayoutLocation.setOnClickListener(onClickListener);
        binding.linearLayoutAppSetting.setOnClickListener(onClickListener);
        binding.linearLayoutReadingSetting.setOnClickListener(onClickListener);
        binding.linearLayoutExit.setOnClickListener(onClickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == MyApplication.GPS_CODE)
                checkPermissions();
            if (requestCode == MyApplication.REQUEST_NETWORK_CODE) {
                if (isNetworkAvailable(getApplicationContext()))
                    checkPermissions();
                else PermissionManager.setMobileWifiEnabled(this);
            }
            if (requestCode == MyApplication.REQUEST_WIFI_CODE) {
                if (isNetworkAvailable(getApplicationContext()))
                    checkPermissions();
                else PermissionManager.enableNetwork(this);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Debug.getNativeHeapAllocatedSize();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.imageViewAppSetting.setImageDrawable(null);
        binding.imageViewDownload.setImageDrawable(null);
        binding.imageViewUpload.setImageDrawable(null);
        binding.imageViewReadingSetting.setImageDrawable(null);
        binding.imageViewHelp.setImageDrawable(null);
        binding.imageViewExit.setImageDrawable(null);
        binding.imageViewReading.setImageDrawable(null);
        binding.imageViewReport.setImageDrawable(null);
        binding.imageViewLocation.setImageDrawable(null);
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Debug.getNativeHeapAllocatedSize();
    }
}