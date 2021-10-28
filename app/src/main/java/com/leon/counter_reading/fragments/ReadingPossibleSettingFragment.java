package com.leon.counter_reading.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentReadingPossibleSettingBinding;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.utils.DifferentCompanyManager;

import org.jetbrains.annotations.NotNull;

public class ReadingPossibleSettingFragment extends Fragment {
    private ISharedPreferenceManager sharedPreferenceManager;
    private FragmentReadingPossibleSettingBinding binding;

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
        sharedPreferenceManager = MyApplication.getApplicationComponent().SharedPreferenceModel();
        initializeCheckBoxes();
    }

    void initializeCheckBoxes() {
        binding.checkBoxAhadEmpty.setChecked(sharedPreferenceManager.getBoolData(SharedReferenceKeys.AHAD_EMPTY.getValue()));
        binding.checkBoxAhadEmpty.setText(DifferentCompanyManager.getAhad(
                DifferentCompanyManager.getActiveCompanyName()).concat(getString(R.string.empty)));

        binding.checkBoxAhad1.setChecked(sharedPreferenceManager.getBoolData(SharedReferenceKeys.AHAD_1.getValue()));
        binding.checkBoxAhad1.setText(DifferentCompanyManager.getAhad1(DifferentCompanyManager.getActiveCompanyName()));
        binding.checkBoxAhad2.setChecked(sharedPreferenceManager.getBoolData(SharedReferenceKeys.AHAD_2.getValue()));
        binding.checkBoxAhad2.setText(DifferentCompanyManager.getAhad2(DifferentCompanyManager.getActiveCompanyName()));
        binding.checkBoxAhadShowTitle.setChecked(sharedPreferenceManager.getBoolData(SharedReferenceKeys.SHOW_AHAD_TITLE.getValue()));
        binding.checkBoxAhadShowTitle.setText(getString(R.string.show).concat(DifferentCompanyManager.getAhad(
                DifferentCompanyManager.getActiveCompanyName())));
        binding.checkBoxAhadTotal.setChecked(sharedPreferenceManager.getBoolData(SharedReferenceKeys.AHAD_TOTAL.getValue()));
        binding.checkBoxAhadTotal.setText(DifferentCompanyManager.getAhadTotal(DifferentCompanyManager.getActiveCompanyName()));
        binding.checkBoxAccount.setChecked(sharedPreferenceManager.getBoolData(SharedReferenceKeys.ACCOUNT.getValue()));
        binding.checkBoxAddress.setChecked(sharedPreferenceManager.getBoolData(SharedReferenceKeys.ADDRESS.getValue()));
        binding.checkBoxMobile.setChecked(sharedPreferenceManager.getBoolData(SharedReferenceKeys.MOBILE.getValue()));
        binding.checkBoxKarbari.setChecked(sharedPreferenceManager.getBoolData(SharedReferenceKeys.KARBARI.getValue()));
        binding.checkBoxSerial.setChecked(sharedPreferenceManager.getBoolData(SharedReferenceKeys.SERIAL.getValue()));
        binding.checkBoxImage.setChecked(sharedPreferenceManager.getBoolData(SharedReferenceKeys.IMAGE.getValue()));
        binding.checkBoxDescription.setChecked(sharedPreferenceManager.getBoolData(SharedReferenceKeys.DESCRIPTION.getValue()));
        binding.checkBoxReadingReport.setChecked(sharedPreferenceManager.getBoolData(SharedReferenceKeys.READING_REPORT.getValue()));
        setCheckBoxClickListener();
    }

    void setCheckBoxClickListener() {
        binding.checkBoxSerial.setOnClickListener(v -> sharedPreferenceManager.putData(
                SharedReferenceKeys.SERIAL.getValue(), binding.checkBoxSerial.isChecked()));
        binding.checkBoxAhadEmpty.setOnClickListener(v -> sharedPreferenceManager.putData(
                SharedReferenceKeys.AHAD_EMPTY.getValue(), binding.checkBoxAhadEmpty.isChecked()));
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
        binding.checkBoxAhadShowTitle.setOnClickListener(v -> sharedPreferenceManager.putData(
                SharedReferenceKeys.SHOW_AHAD_TITLE.getValue(), binding.checkBoxAhadShowTitle.isChecked()));
        binding.checkBoxMobile.setOnClickListener(v -> sharedPreferenceManager.putData(
                SharedReferenceKeys.MOBILE.getValue(), binding.checkBoxMobile.isChecked()));
        binding.checkBoxKarbari.setOnClickListener(v -> sharedPreferenceManager.putData(
                SharedReferenceKeys.KARBARI.getValue(), binding.checkBoxKarbari.isChecked()));
        binding.checkBoxImage.setOnClickListener(v -> sharedPreferenceManager.putData(
                SharedReferenceKeys.IMAGE.getValue(), binding.checkBoxImage.isChecked()));
        binding.checkBoxDescription.setOnClickListener(v -> sharedPreferenceManager.putData(
                SharedReferenceKeys.DESCRIPTION.getValue(), binding.checkBoxDescription.isChecked()));

        binding.checkBoxReadingReport.setOnClickListener(v -> sharedPreferenceManager.putData(
                SharedReferenceKeys.READING_REPORT.getValue(), binding.checkBoxReadingReport.isChecked()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sharedPreferenceManager = null;
    }
}