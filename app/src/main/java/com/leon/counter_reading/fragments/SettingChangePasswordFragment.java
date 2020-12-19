package com.leon.counter_reading.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentSettingChangePasswordBinding;

import org.jetbrains.annotations.NotNull;

public class SettingChangePasswordFragment extends Fragment {
    FragmentSettingChangePasswordBinding binding;

    public SettingChangePasswordFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingChangePasswordBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void initialize() {
        binding.imageViewPassword.setImageDrawable(getResources().getDrawable(R.drawable.img_change_password));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.imageViewPassword.setImageDrawable(null);
    }
}