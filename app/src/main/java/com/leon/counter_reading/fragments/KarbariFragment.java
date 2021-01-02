package com.leon.counter_reading.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;

import com.leon.counter_reading.adapters.SpinnerCustomAdapter;
import com.leon.counter_reading.databinding.FragmentKarbariBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.tables.KarbariDto;
import com.leon.counter_reading.utils.MyDatabaseClient;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class KarbariFragment extends DialogFragment {

    FragmentKarbariBinding binding;
    String uuid;
    int position;
    Activity activity;
    ArrayList<KarbariDto> karbariDtos;
    ArrayList<String> items = new ArrayList<>();

    public KarbariFragment() {
    }

    public static KarbariFragment newInstance(String uuid, int position) {
        KarbariFragment fragment = new KarbariFragment();
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
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentKarbariBinding.inflate(inflater, container, false);
        activity = getActivity();
        initialize();
        return binding.getRoot();
    }

    void initialize() {
        initializeSpinner();
        setOnButtonClickListener();
    }

    void initializeSpinner() {
        karbariDtos = new ArrayList<>(MyDatabaseClient.getInstance(activity).getMyDatabase().
                karbariDao().getAllKarbariDto());
        for (KarbariDto karbariDto : karbariDtos)
            items.add(karbariDto.title);
        SpinnerCustomAdapter spinnerCustomAdapter = new SpinnerCustomAdapter(activity, items);
        binding.spinnerKarbari.setAdapter(spinnerCustomAdapter);
    }

    void setOnButtonClickListener() {
        binding.buttonClose.setOnClickListener(v -> dismiss());
        binding.buttonSubmit.setOnClickListener(v -> {
            MyDatabaseClient.getInstance(activity).getMyDatabase().onOffLoadDao().
                    updateOnOffLoad(uuid,
                            karbariDtos.get(binding.spinnerKarbari.getSelectedItemPosition()).moshtarakinId);
            dismiss();
        });
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