package com.leon.counter_reading.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.ImageViewAdapter;
import com.leon.counter_reading.databinding.ActivityTakePhotoBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.tables.Image;
import com.leon.counter_reading.utils.CustomFile;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.DifferentCompanyManager;
import com.leon.counter_reading.utils.PermissionManager;
import com.leon.counter_reading.utils.photo.PrepareMultimedia;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class TakePhotoActivity extends AppCompatActivity {
    public static int replace = 0;
    private boolean result;
    private String uuid;
    private int position, trackNumber;
    private Activity activity;
    private ActivityTakePhotoBinding binding;
    private ArrayList<Image> images;
    private ImageViewAdapter imageViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int theme = MyApplication.getApplicationComponent().SharedPreferenceModel()
                .getIntData(SharedReferenceKeys.THEME_STABLE.getValue());
        MyApplication.onActivitySetTheme(this, theme, true);
        binding = ActivityTakePhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        TextView textViewCompanyName = findViewById(R.id.text_view_company_name);
        textViewCompanyName.setText(DifferentCompanyManager.
                getCompanyName(DifferentCompanyManager.getActiveCompanyName()));

        activity = this;
        if (PermissionManager.checkCameraPermission(getApplicationContext()))
            initialize();
        else askCameraPermission();
    }

    void initialize() {
        if (getIntent().getExtras() != null) {
            uuid = getIntent().getExtras().getString(BundleEnum.BILL_ID.getValue());
            position = getIntent().getExtras().getInt(BundleEnum.POSITION.getValue());
            trackNumber = getIntent().getExtras().getInt(BundleEnum.TRACKING.getValue());
            result = getIntent().getExtras().getBoolean(BundleEnum.IMAGE.getValue());
            binding.textViewNotSent.setVisibility(getIntent().getExtras()
                    .getBoolean(BundleEnum.SENT.getValue()) ? View.GONE : View.VISIBLE);
        }
        imageSetup();
        setOnButtonSendClickListener();
    }

    void imageSetup() {
        images = new ArrayList<>();
        if (!result) {
            images.addAll(MyApplication.getApplicationComponent().MyDatabase()
                    .imageDao().getImagesByOnOffLoadId(uuid));
            for (int i = 0; i < images.size(); i++) {
                images.get(i).bitmap = CustomFile.loadImage(activity, images.get(i).address);
            }
        }
        imageViewAdapter = new ImageViewAdapter(activity, images);
        binding.gridViewImage.setAdapter(imageViewAdapter);
    }

    void setOnButtonSendClickListener() {
        binding.buttonSaveSend.setOnClickListener(v ->
                new PrepareMultimedia(activity, position, result,
                        binding.editTextDescription.getText().toString().isEmpty() ?
                                getString(R.string.description) :
                                binding.editTextDescription.getText().toString(), images)
                        .execute(activity));
    }

    void askCameraPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                new CustomToast().info(getString(R.string.access_granted));
                initialize();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MyApplication.BITMAP_SELECTED_IMAGE = null;
        if (resultCode == RESULT_OK) {
            if (requestCode == MyApplication.GALLERY_REQUEST && data != null) {
                Uri uri = data.getData();
                Bitmap bitmap;
                try {
                    InputStream inputStream = this.getContentResolver().openInputStream(uri);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    MyApplication.BITMAP_SELECTED_IMAGE = bitmap;
                    prepareImage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == MyApplication.CAMERA_REQUEST) {
                if (MyApplication.PHOTO_URI != null) {
                    try {
                        MyApplication.BITMAP_SELECTED_IMAGE = MediaStore.Images.Media.getBitmap(getContentResolver(), MyApplication.PHOTO_URI);
                        prepareImage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    private void prepareImage() {
        Image image = new Image();
        image.OnOffLoadId = uuid;
        image.trackNumber = trackNumber;
        image.bitmap = MyApplication.BITMAP_SELECTED_IMAGE;
        if (replace > 0) {
            MyApplication.getApplicationComponent().MyDatabase()
                    .imageDao().deleteImage(images.get(replace - 1).id);
            images.set(replace - 1, image);
        } else {
            images.add(image);
        }
        imageViewAdapter.notifyDataSetChanged();
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
        MyApplication.BITMAP_SELECTED_IMAGE = null;
        images = null;
        binding = null;
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