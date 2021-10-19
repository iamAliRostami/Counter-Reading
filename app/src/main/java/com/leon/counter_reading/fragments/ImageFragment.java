package com.leon.counter_reading.fragments;

import static com.leon.counter_reading.utils.CustomFile.createImageFileOld;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentImageBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.tables.Image;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class ImageFragment extends DialogFragment {

    FragmentImageBinding binding;
    String uuid;
    int position;
    Activity activity;
    ArrayList<Bitmap> bitmaps;
    ArrayList<Image> images;
    int imageNumber = 1, imageNumberTemp = 0;
    boolean replace = false;
    @SuppressLint("NonConstantResourceId")
    View.OnLongClickListener onLongClickListener = v -> {
        Bitmap bitmap = null;
        switch (v.getId()) {
            case R.id.image_View_1:
                if (bitmaps.size() > 0)
                    bitmap = bitmaps.get(0);
                break;
            case R.id.image_View_2:
                if (bitmaps.size() > 1)
                    bitmap = bitmaps.get(1);
                break;
            case R.id.image_View_3:
                if (bitmaps.size() > 2)
                    bitmap = bitmaps.get(2);
                break;
            case R.id.image_View_4:
                if (bitmaps.size() > 3)
                    bitmap = bitmaps.get(3);
                break;
        }
        if (bitmap != null) {
            HighQualityFragment highQualityFragment =
                    HighQualityFragment.newInstance(bitmap);
            if (getFragmentManager() != null) {
                highQualityFragment.show(getFragmentManager(), "Image # 2");
            }
        }
        return false;
    };
    @SuppressLint("NonConstantResourceId")
    View.OnClickListener onPickerClickListener = v -> {
        switch (v.getId()) {
            case R.id.image_View_1:
                replace = imageNumber > 1;
                if (replace) {
                    imageNumberTemp = 1;
                }
                break;
            case R.id.image_View_2:
                replace = imageNumber > 2;
                if (replace) {
                    imageNumberTemp = 2;
                }
                break;
            case R.id.image_View_3:
                replace = imageNumber > 3;
                if (replace) {
                    imageNumberTemp = 3;
                }
                break;
            case R.id.image_View_4:
                replace = imageNumber > 4;
                if (replace) {
                    imageNumberTemp = 4;
                }
                break;
        }
        imagePicker();
    };
    @SuppressLint("NonConstantResourceId")
    View.OnClickListener onDeleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean b = false;
            switch (v.getId()) {
                case R.id.image_View_delete_1:
                    if (imageNumber > 1) {
                        removeImage(0);
                        b = true;
                        binding.imageView1.setImageBitmap(((BitmapDrawable)
                                binding.imageView2.getDrawable()).getBitmap());
                        binding.imageView2.setImageBitmap(((BitmapDrawable)
                                binding.imageView3.getDrawable()).getBitmap());
                        binding.imageView3.setImageBitmap(((BitmapDrawable)
                                binding.imageView4.getDrawable()).getBitmap());
                    }
                    break;
                case R.id.image_View_delete_2:
                    if (imageNumber > 2) {
                        removeImage(1);
                        b = true;
                        binding.imageView2.setImageBitmap(((BitmapDrawable)
                                binding.imageView3.getDrawable()).getBitmap());
                        binding.imageView3.setImageBitmap(((BitmapDrawable)
                                binding.imageView4.getDrawable()).getBitmap());
                    }
                    break;
                case R.id.image_View_delete_3:
                    if (imageNumber > 3) {
                        removeImage(2);
                        b = true;
                        binding.imageView3.setImageBitmap(((BitmapDrawable)
                                binding.imageView4.getDrawable()).getBitmap());
                    }
                    break;
                case R.id.image_View_delete_4:
                    if (imageNumber > 4) {
                        removeImage(3);
                        b = true;
                    }
                    break;
            }
            if (b) {
                if (imageNumber == 1) {
                    binding.imageViewDelete1.setVisibility(View.GONE);
                    binding.imageViewSent1.setVisibility(View.GONE);
                } else if (imageNumber == 2) {
                    binding.imageViewDelete2.setVisibility(View.GONE);
                    binding.imageViewSent2.setVisibility(View.GONE);
                } else if (imageNumber == 3) {
                    binding.imageViewDelete3.setVisibility(View.GONE);
                    binding.imageViewSent3.setVisibility(View.GONE);
                } else if (imageNumber == 4) {
                    binding.imageViewDelete4.setVisibility(View.GONE);
                    binding.imageViewSent4.setVisibility(View.GONE);
                }
                binding.imageView4.setImageDrawable(ContextCompat.getDrawable(activity,
                        R.drawable.img_take_photo));
            }
        }
    };

    public static ImageFragment newInstance(String uuid, int position) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString(BundleEnum.BILL_ID.getValue(), uuid);
        args.putInt(BundleEnum.POSITION.getValue(), position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uuid = getArguments().getString(BundleEnum.BILL_ID.getValue());
            position = getArguments().getInt(BundleEnum.POSITION.getValue());
            getArguments().clear();
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentImageBinding.inflate(inflater, container, false);
        activity = getActivity();
        initialize();
        return binding.getRoot();
    }

    void initialize() {
        setOnButtonClickListener();
        setOnImageViewPickerClickListener();
        setOnImageViewDeleteClickListener();
    }

    void setOnButtonClickListener() {
        binding.buttonSaveSend.setOnClickListener(v -> {
//            ((ReadingActivity) activity).updateOnOffLoadImage(position);
            dismiss();
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void setOnImageViewPickerClickListener() {
        binding.imageView1.setOnClickListener(onPickerClickListener);
        binding.imageView2.setOnClickListener(onPickerClickListener);
        binding.imageView3.setOnClickListener(onPickerClickListener);
        binding.imageView4.setOnClickListener(onPickerClickListener);

        binding.imageView1.setOnLongClickListener(onLongClickListener);
        binding.imageView2.setOnLongClickListener(onLongClickListener);
        binding.imageView3.setOnLongClickListener(onLongClickListener);
        binding.imageView4.setOnLongClickListener(onLongClickListener);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void setOnImageViewDeleteClickListener() {
        binding.imageViewDelete1.setOnClickListener(onDeleteClickListener);
        binding.imageViewDelete2.setOnClickListener(onDeleteClickListener);
        binding.imageViewDelete3.setOnClickListener(onDeleteClickListener);
        binding.imageViewDelete4.setOnClickListener(onDeleteClickListener);
    }

    void removeImage(int index) {
        imageNumber = imageNumber - 1;
        bitmaps.remove(index);
        MyApplication.getApplicationComponent().MyDatabase()
                .imageDao().deleteImage(images.get(index).id);
        images.remove(index);
    }

    void imagePicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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
            if (cameraIntent.resolveActivity(activity.getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFileOld(activity);
                } catch (IOException e) {
                    Log.e("Main", e.toString());
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
    }

    @Override
    public void onResume() {
        WindowManager.LayoutParams params = Objects.requireNonNull(getDialog()).getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);
        super.onResume();
    }
}