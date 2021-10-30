package com.leon.counter_reading.fragments;

import static com.leon.counter_reading.utils.MakeNotification.makeRing;

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
import com.leon.counter_reading.enums.NotificationType;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.tables.KarbariDto;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class KarbariFragment extends DialogFragment {

    private FragmentKarbariBinding binding;
    private String uuid;
    private Activity activity;
    private ArrayList<KarbariDto> karbariDtos;

    public KarbariFragment() {
    }

    public static KarbariFragment newInstance(String uuid) {
        KarbariFragment fragment = new KarbariFragment();
        Bundle args = new Bundle();
        args.putString(BundleEnum.BILL_ID.getValue(), uuid);
        fragment.setArguments(args);
        fragment.setCancelable(false);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uuid = getArguments().getString(BundleEnum.BILL_ID.getValue());
            getArguments().clear();
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
        makeRing(activity, NotificationType.OTHER);
        initializeSpinner();
        setOnButtonClickListener();
    }

    void initializeSpinner() {
        karbariDtos = new ArrayList<>(MyApplication.getApplicationComponent().MyDatabase().
                karbariDao().getAllKarbariDto());
        String[] items = new String[karbariDtos.size()];
        for (int i = 0; i < karbariDtos.size(); i++)
            items[i] = (karbariDtos.get(i).title);
        SpinnerCustomAdapter spinnerCustomAdapter = new SpinnerCustomAdapter(activity, items);
        binding.spinner.setAdapter(spinnerCustomAdapter);
    }

    void setOnButtonClickListener() {
        binding.buttonClose.setOnClickListener(v -> dismiss());
        binding.buttonSubmit.setOnClickListener(v -> {
            MyApplication.getApplicationComponent().MyDatabase().onOffLoadDao().
                    updateOnOffLoad(uuid, karbariDtos.get(
                            binding.spinner.getSelectedItemPosition()).moshtarakinId);
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