package com.leon.counter_reading.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.SettingActivity;
import com.leon.counter_reading.databinding.FragmentSettingChangeThemeBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.utils.CustomToast;

import org.jetbrains.annotations.NotNull;

public class SettingChangeThemeFragment extends Fragment {
    private static int theme;
    private FragmentSettingChangeThemeBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingChangeThemeBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    void initialize() {
        binding.imageViewBlack.setImageDrawable(ContextCompat
                .getDrawable(MyApplication.getContext(), R.drawable.img_black));
        binding.imageViewBlue.setImageDrawable(ContextCompat
                .getDrawable(MyApplication.getContext(), R.drawable.img_blue));
        binding.imageViewLightBlue.setImageDrawable(ContextCompat
                .getDrawable(MyApplication.getContext(), R.drawable.img_pale_blue));
        binding.imageViewGreen.setImageDrawable(ContextCompat
                .getDrawable(MyApplication.getContext(), R.drawable.img_green));
        binding.imageViewTheme.setImageDrawable(ContextCompat
                .getDrawable(MyApplication.getContext(), R.drawable.img_change_theme));
        setOnChangeThemeClickListener();
        setButtonChangeThemeClickListener();
    }

    void setOnChangeThemeClickListener() {
        binding.linearLayoutBlue.setOnClickListener(view -> {
            theme = 1;
            changeTheme();
        });
        binding.linearLayoutGreen.setOnClickListener(view -> {
            theme = 2;
            changeTheme();
        });
        binding.linearLayoutLightBlue.setOnClickListener(view -> {
            theme = 3;
            changeTheme();
        });
        binding.linearLayoutBlack.setOnClickListener(view -> {
            theme = 4;
            changeTheme();
        });
    }

    void setButtonChangeThemeClickListener() {
        binding.buttonChangeTheme.setOnClickListener(view -> {
            MyApplication.getApplicationComponent().SharedPreferenceModel().putData(SharedReferenceKeys.THEME_STABLE.getValue(), theme);
            new CustomToast().success(getString(R.string.theme_changed));
            changeTheme();
        });
    }

    void changeTheme() {
        Intent intent = new Intent(getActivity(), SettingActivity.class);
        intent.putExtra(BundleEnum.THEME.getValue(), theme);
        requireActivity().finish();
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.imageViewBlack.setImageDrawable(null);
        binding.imageViewBlue.setImageDrawable(null);
        binding.imageViewLightBlue.setImageDrawable(null);
        binding.imageViewGreen.setImageDrawable(null);
        binding.imageViewTheme.setImageDrawable(null);
    }
}