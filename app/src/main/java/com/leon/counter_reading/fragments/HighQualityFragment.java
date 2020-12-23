package com.leon.counter_reading.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;

import com.leon.counter_reading.databinding.FragmentHighQualityBinding;
import com.leon.counter_reading.enums.BundleEnum;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class HighQualityFragment extends DialogFragment {
    FragmentHighQualityBinding binding;
    private Bitmap bitmap;

    public HighQualityFragment() {
    }

    public static HighQualityFragment newInstance(Bitmap bitmap) {
        HighQualityFragment fragment = new HighQualityFragment();
        Bundle args = new Bundle();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        args.putByteArray(BundleEnum.IMAGE_BITMAP.getValue(), bos.toByteArray());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            byte[] bytes = getArguments().getByteArray((BundleEnum.IMAGE_BITMAP.getValue()));
            if (bytes != null) {
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHighQualityBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    void initialize() {
        binding.photoView.setImageBitmap(bitmap);
    }

    @Override
    public void onResume() {
        WindowManager.LayoutParams params = Objects.requireNonNull(
                Objects.requireNonNull(getDialog()).getWindow()).getAttributes();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        Objects.requireNonNull(getDialog().getWindow()).setAttributes(params);
        super.onResume();
    }
}