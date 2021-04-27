package com.leon.counter_reading.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.leon.counter_reading.databinding.FragmentReadingPossibleSettingBinding;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.enums.SharedReferenceNames;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.utils.DifferentCompanyManager;
import com.leon.counter_reading.utils.SharedPreferenceManager;

import org.jetbrains.annotations.NotNull;

public class ReadingPossibleSettingFragment extends Fragment {
    ISharedPreferenceManager sharedPreferenceManager;
    FragmentReadingPossibleSettingBinding binding;
    Context context;

    public ReadingPossibleSettingFragment() {
    }

    public static ReadingPossibleSettingFragment newInstance() {
        return new ReadingPossibleSettingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReadingPossibleSettingBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    void initialize() {
        context = getContext();
        sharedPreferenceManager = new SharedPreferenceManager(context, SharedReferenceNames.ACCOUNT.getValue());
        initializeCheckBoxes();
    }

    void initializeCheckBoxes() {
        binding.checkBoxAhad1.setChecked(sharedPreferenceManager.getBoolData(SharedReferenceKeys.AHAD_1.getValue()));
        binding.checkBoxAhad1.setText(DifferentCompanyManager.getAhad1(DifferentCompanyManager.getActiveCompanyName()));
        binding.checkBoxAhad2.setChecked(sharedPreferenceManager.getBoolData(SharedReferenceKeys.AHAD_2.getValue()));
        binding.checkBoxAhad2.setText(DifferentCompanyManager.getAhad2(DifferentCompanyManager.getActiveCompanyName()));
        binding.checkBoxAhadTotal.setChecked(sharedPreferenceManager.getBoolData(SharedReferenceKeys.AHAD_TOTAL.getValue()));
        binding.checkBoxAhadTotal.setText(DifferentCompanyManager.getAhadTotal(DifferentCompanyManager.getActiveCompanyName()));
        binding.checkBoxAccount.setChecked(sharedPreferenceManager.getBoolData(SharedReferenceKeys.ACCOUNT.getValue()));
        binding.checkBoxAddress.setChecked(sharedPreferenceManager.getBoolData(SharedReferenceKeys.ADDRESS.getValue()));
        binding.checkBoxAhad.setChecked(sharedPreferenceManager.getBoolData(SharedReferenceKeys.AHAD_EMPTY.getValue()));
        binding.checkBoxMobile.setChecked(sharedPreferenceManager.getBoolData(SharedReferenceKeys.MOBILE.getValue()));
        binding.checkBoxKarbari.setChecked(sharedPreferenceManager.getBoolData(SharedReferenceKeys.KARBARI.getValue()));
        binding.checkBoxSerial.setChecked(sharedPreferenceManager.getBoolData(SharedReferenceKeys.SERIAL.getValue()));
        binding.checkBoxImage.setChecked(sharedPreferenceManager.getBoolData(SharedReferenceKeys.IMAGE.getValue()));
        binding.checkBoxDescription.setChecked(sharedPreferenceManager.getBoolData(SharedReferenceKeys.DESCRIPTION.getValue()));
        setCheckBoxClickListener();
    }

    void setCheckBoxClickListener() {
        binding.checkBoxSerial.setOnClickListener(v -> sharedPreferenceManager.putData(
                SharedReferenceKeys.SERIAL.getValue(), binding.checkBoxSerial.isChecked()));
        binding.checkBoxAhad.setOnClickListener(v -> sharedPreferenceManager.putData(
                SharedReferenceKeys.AHAD_EMPTY.getValue(), binding.checkBoxAhad.isChecked()));
        binding.checkBoxAddress.setOnClickListener(v -> sharedPreferenceManager.putData(
                SharedReferenceKeys.ADDRESS.getValue(), binding.checkBoxAddress.isChecked()));
        binding.checkBoxAccount.setOnClickListener(v -> sharedPreferenceManager.putData(
                SharedReferenceKeys.ACCOUNT.getValue(), binding.checkBoxAccount.isChecked()));
        binding.checkBoxAhad2.setOnClickListener(v -> sharedPreferenceManager.putData(
                SharedReferenceKeys.AHAD_2.getValue(), binding.checkBoxAhad2.isChecked()));
        binding.checkBoxAhad1.setOnClickListener(v -> sharedPreferenceManager.putData(
                SharedReferenceKeys.AHAD_1.getValue(), binding.checkBoxAhad1.isChecked()));
        binding.checkBoxAhadTotal.setOnClickListener(v -> sharedPreferenceManager.putData(
                SharedReferenceKeys.AHAD_TOTAL.getValue(), binding.checkBoxAhadTotal.isChecked()));
        binding.checkBoxMobile.setOnClickListener(v -> sharedPreferenceManager.putData(
                SharedReferenceKeys.MOBILE.getValue(), binding.checkBoxMobile.isChecked()));
        binding.checkBoxKarbari.setOnClickListener(v -> sharedPreferenceManager.putData(
                SharedReferenceKeys.KARBARI.getValue(), binding.checkBoxKarbari.isChecked()));
        binding.checkBoxImage.setOnClickListener(v -> sharedPreferenceManager.putData(
                SharedReferenceKeys.IMAGE.getValue(), binding.checkBoxImage.isChecked()));
        binding.checkBoxDescription.setOnClickListener(v -> sharedPreferenceManager.putData(
                SharedReferenceKeys.DESCRIPTION.getValue(), binding.checkBoxDescription.isChecked()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}