package com.leon.counter_reading.activities;

import static com.leon.counter_reading.utils.CustomFile.createImageFile;
import static com.leon.counter_reading.utils.PermissionManager.isNetworkAvailable;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.FragmentTransaction;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.ActivityReportForbidBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.enums.SharedReferenceNames;
import com.leon.counter_reading.fragments.HighQualityFragment;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.tables.ForbiddenDto;
import com.leon.counter_reading.utils.CustomFile;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.DifferentCompanyManager;
import com.leon.counter_reading.utils.LocationTracker;
import com.leon.counter_reading.utils.PermissionManager;
import com.leon.counter_reading.utils.SharedPreferenceManager;
import com.leon.counter_reading.utils.forbid.PrepareForbid;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

public class ReportForbidActivity extends AppCompatActivity {
    private ActivityReportForbidBinding binding;
    private Activity activity;
    private ForbiddenDto forbiddenDto = new ForbiddenDto();
    private int zoneId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ISharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext(),
                SharedReferenceNames.ACCOUNT.getValue());
        int theme = sharedPreferenceManager.getIntData(SharedReferenceKeys.THEME_STABLE.getValue());
        MyApplication.onActivitySetTheme(this, theme, true);
        binding = ActivityReportForbidBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        checkPermissions();
    }

    void checkPermissions() {
        if (PermissionManager.gpsEnabled(this))
            if (PermissionManager.checkLocationPermission(getApplicationContext())) {
                askLocationPermission();
            } else if (!PermissionManager.checkCameraPermission(getApplicationContext())) {
                askCameraPermission();
            } else {
                initialize();
            }
    }

    void askLocationPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                new CustomToast().info(getString(R.string.access_granted));
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

    void askCameraPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                new CustomToast().info(getString(R.string.access_granted));
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

    void initialize() {
        if (getIntent().getExtras() != null)
            zoneId = getIntent().getExtras().getInt(BundleEnum.ZONE_ID.getValue());
        binding.textViewHome.setText(getString(R.string.number).concat(DifferentCompanyManager.getAhad(DifferentCompanyManager.getActiveCompanyName())));

        binding.editTextNextAccount.setFilters(
                new InputFilter[]{
                        new InputFilter.LengthFilter(DifferentCompanyManager.
                                getEshterakMaxLength(DifferentCompanyManager.getActiveCompanyName()))});
        binding.editTextPreAccount.setFilters(
                new InputFilter[]{
                        new InputFilter.LengthFilter(DifferentCompanyManager.
                                getEshterakMaxLength(DifferentCompanyManager.getActiveCompanyName()))});

        forbiddenDto.File = new ArrayList<>();
        forbiddenDto.bitmaps = new ArrayList<>();
        setOnButtonPhotoClickListener();
        setOnButtonSubmitClickListener();
        setOnImageViewDeleteClickListener();
        setOnImageViewTakenClickListener();
        setOnEditTextChangeListener();
    }

    void setOnEditTextChangeListener() {
        binding.editTextPreAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == DifferentCompanyManager.
                        getEshterakMaxLength(DifferentCompanyManager.getActiveCompanyName())) {
                    View view = binding.editTextNextAccount;
                    view.requestFocus();
                }
            }
        });
        binding.editTextNextAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == DifferentCompanyManager.
                        getEshterakMaxLength(DifferentCompanyManager.getActiveCompanyName())) {
                    View view = binding.editTextPostalCode;
                    view.requestFocus();
                }
            }
        });
        binding.editTextPostalCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 10) {
                    View view = binding.editTextAhadNumber;
                    view.requestFocus();
                }
            }
        });
    }

    void setOnImageViewDeleteClickListener() {
        binding.imageViewDelete.setOnClickListener(v -> {
            forbiddenDto.File.remove(forbiddenDto.File.size() - 1);
            forbiddenDto.bitmaps.remove(forbiddenDto.bitmaps.size() - 1);
            if (forbiddenDto.File.size() > 0) {
                binding.imageViewTaken.setImageBitmap(
                        forbiddenDto.bitmaps.get(forbiddenDto.bitmaps.size() - 1));
            } else {
                binding.relativeLayoutImage.setVisibility(View.GONE);
            }
        });
    }

    void setOnButtonSubmitClickListener() {
        binding.buttonSubmit.setOnClickListener(v -> {
            View view = null;
            boolean cancel = false;
            if (binding.editTextPreAccount.getText().length() < DifferentCompanyManager.
                    getEshterakMinLength(DifferentCompanyManager.getActiveCompanyName())) {
                binding.editTextPreAccount.setError(getString(R.string.error_format));
                view = binding.editTextPreAccount;
                cancel = true;
            } else if (binding.editTextNextAccount.getText().length() < DifferentCompanyManager.
                    getEshterakMinLength(DifferentCompanyManager.getActiveCompanyName())) {
                binding.editTextNextAccount.setError(getString(R.string.error_format));
                view = binding.editTextNextAccount;
                cancel = true;
            } else if (binding.editTextPostalCode.getText().length() < 10) {
                binding.editTextPostalCode.setError(getString(R.string.error_format));
                view = binding.editTextPostalCode;
                cancel = true;
            } else if (binding.editTextAhadNumber.getText().toString().isEmpty()) {
                binding.editTextAhadNumber.setError(getString(R.string.error_empty));
                view = binding.editTextAhadNumber;
                cancel = true;
            } else if (binding.editTextDescription.getText().toString().isEmpty()) {
                binding.editTextDescription.setError(getString(R.string.error_empty));
                view = binding.editTextDescription;
                cancel = true;
            }
            if (!cancel)
                sendForbid();
            else view.requestFocus();
        });
    }

    void sendForbid() {
        LocationTracker locationTracker = new LocationTracker(activity);
        forbiddenDto.prepareToSend(
                locationTracker.getAccuracy(), locationTracker.getLongitude(), locationTracker.getLatitude(),
                binding.editTextPostalCode.getText().toString(),
                binding.editTextDescription.getText().toString(),
                binding.editTextPreAccount.getText().toString(),
                binding.editTextNextAccount.getText().toString(),
                binding.editTextAhadNumber.getText().toString(), zoneId);
        new PrepareForbid(activity, forbiddenDto, zoneId).execute(activity);
    }

    void setOnImageViewTakenClickListener() {
        binding.imageViewTaken.setOnClickListener(v -> {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            HighQualityFragment highQualityFragment =
                    HighQualityFragment.newInstance(forbiddenDto.bitmaps.get(forbiddenDto.bitmaps.size() - 1));
            highQualityFragment.show(fragmentTransaction, "Image # 2");
        });
    }

    void setOnButtonPhotoClickListener() {
        binding.buttonPhoto.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(ReportForbidActivity.this, R.style.AlertDialogCustom));
            builder.setTitle(R.string.choose_document);
            builder.setMessage(R.string.select_source);
            builder.setPositiveButton(R.string.gallery, (dialog, which) -> {
                dialog.dismiss();
                Intent intent = new Intent("android.intent.action.PICK");
                intent.setType("image/*");
                startActivityForResult(intent, MyApplication.GALLERY_REQUEST);
            });
            builder.setNegativeButton(R.string.camera, (dialog, which) -> {
                dialog.dismiss();
                Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                if (cameraIntent.resolveActivity(ReportForbidActivity.this.getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile(activity);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (photoFile != null) {
                        StrictMode.VmPolicy.Builder builderTemp = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builderTemp.build());
                        cameraIntent.putExtra("output", Uri.fromFile(photoFile));
                        startActivityForResult(cameraIntent, MyApplication.CAMERA_REQUEST);
                    }
                }
            });
            builder.setNeutralButton("", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });
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

        MyApplication.bitmapSelectedImage = null;
        if (resultCode == RESULT_OK) {
            if (requestCode == MyApplication.GALLERY_REQUEST && data != null) {
                Uri selectedImage = data.getData();
                Bitmap bitmap;
                try {
                    Uri uri = data.getData();
                    Objects.requireNonNull(uri);
                    InputStream inputStream = this.getContentResolver().openInputStream(
                            Objects.requireNonNull(selectedImage));
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    MyApplication.bitmapSelectedImage = bitmap;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == MyApplication.CAMERA_REQUEST) {
                ContentResolver contentResolver = this.getContentResolver();
                try {
                    if (Build.VERSION.SDK_INT > 28) {
                        ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), Uri.parse(MyApplication.fileName));
                        MyApplication.bitmapSelectedImage = ImageDecoder.decodeBitmap(source);
                    } else
                        MyApplication.bitmapSelectedImage = MediaStore.Images.Media.getBitmap(
                                contentResolver, Uri.parse(MyApplication.fileName));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            forbiddenDto.bitmaps.add(MyApplication.bitmapSelectedImage);
            binding.relativeLayoutImage.setVisibility(View.VISIBLE);
            binding.imageViewTaken.setImageBitmap(MyApplication.bitmapSelectedImage);
            forbiddenDto.File.add(CustomFile.bitmapToFile(MyApplication.bitmapSelectedImage, activity));
        }
    }

    @Override
    protected void onStop() {
        Debug.getNativeHeapAllocatedSize();
        System.runFinalization();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Runtime.getRuntime().gc();
        System.gc();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        binding = null;
        forbiddenDto = null;
        Debug.getNativeHeapAllocatedSize();
        System.runFinalization();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Runtime.getRuntime().gc();
        System.gc();
        super.onDestroy();
    }
}