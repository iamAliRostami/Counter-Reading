package com.leon.counter_reading.fragments;

import static android.app.Activity.RESULT_OK;
import static com.leon.counter_reading.helpers.Constants.BITMAP_SELECTED_IMAGE;
import static com.leon.counter_reading.helpers.Constants.CAMERA_REQUEST;
import static com.leon.counter_reading.helpers.Constants.GALLERY_REQUEST;
import static com.leon.counter_reading.helpers.Constants.PHOTO_URI;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.utils.CustomFile.createImageFile;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentSettingChangeAvatarBinding;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.utils.CustomFile;
import com.leon.counter_reading.utils.CustomToast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SettingChangeAvatarFragment extends Fragment {
    private FragmentSettingChangeAvatarBinding binding;
    private Activity activity;

    public SettingChangeAvatarFragment() {
    }

    public static SettingChangeAvatarFragment newInstance() {
        return new SettingChangeAvatarFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingChangeAvatarBinding.inflate(inflater, container, false);
        activity = getActivity();
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        if (getApplicationComponent().SharedPreferenceModel().checkIsNotEmpty(SharedReferenceKeys.AVATAR.getValue())) {
            binding.imageViewAvatar.setImageBitmap(CustomFile.loadImage(activity, getApplicationComponent().SharedPreferenceModel().getStringData(SharedReferenceKeys.AVATAR.getValue())));
        } else {
            binding.imageViewAvatar.setImageDrawable(ContextCompat
                    .getDrawable(activity, R.drawable.img_profile));
            binding.buttonChangeDelete.setVisibility(View.GONE);
        }
        setOnButtonChangeAvatarClickListener();
        setOnButtonDeleteClickListener();
        setOnImageViewAvatarClickListener();
    }

    private void setOnButtonChangeAvatarClickListener() {
        binding.buttonChangeAvatar.setOnClickListener(view -> {
            if (BITMAP_SELECTED_IMAGE != null) {
                String address = CustomFile.saveTempBitmap(BITMAP_SELECTED_IMAGE, activity);
                if (!address.equals(activity.getString(R.string.error_external_storage_is_not_writable))) {
                    getApplicationComponent().SharedPreferenceModel().putData(SharedReferenceKeys.AVATAR.getValue(), address);
                    new CustomToast().success(getString(R.string.profile_changed));
                }
            } else {
                new CustomToast().warning(getString(R.string.there_is_no_images));
            }
        });
    }

    private void setOnButtonDeleteClickListener() {
        binding.buttonChangeDelete.setOnClickListener(view -> {
            BITMAP_SELECTED_IMAGE = null;
            getApplicationComponent().SharedPreferenceModel().putData(SharedReferenceKeys.AVATAR.getValue(), null);
            initialize();
        });
    }

    private void setOnImageViewAvatarClickListener() {
        binding.imageViewAvatar.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AlertDialogCustom));
            builder.setTitle(R.string.choose_document);
            builder.setMessage(R.string.select_source);
            builder.setPositiveButton(R.string.gallery, (dialog, which) -> {
                dialog.dismiss();
                Intent intent = new Intent("android.intent.action.PICK");
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_REQUEST);
            });
            builder.setNegativeButton(R.string.camera, (dialog, which) -> {
                dialog.dismiss();
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(activity.getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile(activity);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (photoFile != null) {
                        PHOTO_URI = FileProvider.getUriForFile(activity,
                                BuildConfig.APPLICATION_ID.concat(".provider"),
                                photoFile);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, PHOTO_URI);
                        try {
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            builder.setNeutralButton("", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        BITMAP_SELECTED_IMAGE = null;
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST && data != null) {
                Uri uri = data.getData();
                Bitmap bitmap;
                try {
                    InputStream inputStream = activity.getContentResolver().openInputStream(uri);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    prepareImage(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == CAMERA_REQUEST) {
                if (PHOTO_URI != null) {
                    try {
                        prepareImage(CustomFile.rotateImage(MediaStore.Images.Media.getBitmap(activity.getContentResolver(), PHOTO_URI), 90));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void prepareImage(Bitmap bitmap) {
        BITMAP_SELECTED_IMAGE = bitmap;
        binding.imageViewAvatar.setImageBitmap(bitmap);
        binding.buttonChangeDelete.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.imageViewAvatar.setImageDrawable(null);
    }
}