package com.leon.counter_reading.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;

import com.google.gson.Gson;
import com.leon.counter_reading.databinding.FragmentPossibleBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.enums.SharedReferenceNames;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.utils.SharedPreferenceManager;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PossibleFragment extends DialogFragment {
    FragmentPossibleBinding binding;
    OnOffLoadDto onOffLoadDto;
    int position;
    Activity activity;
    ISharedPreferenceManager sharedPreferenceManager;

    public PossibleFragment() {
    }

    public static PossibleFragment newInstance(OnOffLoadDto onOffLoadDto, int position) {
        PossibleFragment fragment = new PossibleFragment();
        fragment.setArguments(putBundle(onOffLoadDto, position));
        return fragment;
    }

    static Bundle putBundle(OnOffLoadDto onOffLoadDto, int position) {
        Bundle args = new Bundle();
        Gson gson = new Gson();
        String json1 = gson.toJson(onOffLoadDto);
        args.putString(BundleEnum.ON_OFF_LOAD.getValue(), json1);
        args.putInt(BundleEnum.POSITION.getValue(), position);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBundle();
    }

    void getBundle() {
        if (getArguments() != null) {
            Gson gson = new Gson();
            onOffLoadDto = gson.fromJson(getArguments().getString(
                    BundleEnum.ON_OFF_LOAD.getValue()), OnOffLoadDto.class);
            position = getArguments().getInt(BundleEnum.POSITION.getValue());
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPossibleBinding.inflate(inflater, container, false);
        activity = getActivity();
        initialize();
        return binding.getRoot();
    }

    void initialize() {
        sharedPreferenceManager = new SharedPreferenceManager(activity, SharedReferenceNames.ACCOUNT.getValue());
        initializeTextViews();
    }

    void initializeTextViews() {
        //TODO
        binding.editTextSerial.setVisibility(
                sharedPreferenceManager.getBoolData(SharedReferenceKeys.SERIAL.getValue()) ?
                        View.VISIBLE : View.GONE);
        binding.editTextAddress.setVisibility(
                sharedPreferenceManager.getBoolData(SharedReferenceKeys.ADDRESS.getValue()) ?
                        View.VISIBLE : View.GONE);
        binding.editTextAccount.setVisibility(
                sharedPreferenceManager.getBoolData(SharedReferenceKeys.ACCOUNT.getValue()) ?
                        View.VISIBLE : View.GONE);
        binding.editTextAhadEmpty.setVisibility(
                sharedPreferenceManager.getBoolData(SharedReferenceKeys.AHAD_EMPTY.getValue()) ?
                        View.VISIBLE : View.GONE);
        if (sharedPreferenceManager.getBoolData(SharedReferenceKeys.AHAD_OTHER.getValue())) {

        }
        initializeSpinner();
    }

    void initializeSpinner() {
        binding.spinnerKarbari.setVisibility(
                sharedPreferenceManager.getBoolData(SharedReferenceKeys.KARBARI.getValue()) ?
                        View.VISIBLE : View.GONE);
        if (sharedPreferenceManager.getBoolData(SharedReferenceKeys.KARBARI.getValue())) {

        }
    }

    @Override
    public void onResume() {
        WindowManager.LayoutParams params = Objects.requireNonNull(getDialog()).getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}