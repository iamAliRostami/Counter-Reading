package com.leon.counter_reading.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentSettingChangeAvatarBinding;

public class SettingChangeAvatarFragment extends Fragment {
    private FragmentSettingChangeAvatarBinding binding;

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
        initialize();
        return binding.getRoot();
    }
    private void initialize() {
    }
}