package com.leon.counter_reading.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.SpinnerCustomAdapter;
import com.leon.counter_reading.databinding.FragmentReadingSettingDeleteBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.tables.TrackingDto;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ReadingSettingDeleteFragment extends Fragment {
    FragmentReadingSettingDeleteBinding binding;
    ArrayList<String> items = new ArrayList<>();
    ArrayList<TrackingDto> trackingDtos = new ArrayList<>();
    ArrayList<String> json;
    SpinnerCustomAdapter adapter;
    Activity activity;

    public ReadingSettingDeleteFragment() {
    }

    public static ReadingSettingDeleteFragment newInstance(ArrayList<TrackingDto> trackingDtos) {
        ReadingSettingDeleteFragment fragment = new ReadingSettingDeleteFragment();
        Bundle args = new Bundle();
        Gson gson = new Gson();
        ArrayList<String> json = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            trackingDtos.forEach(trackingDto -> json.add(gson.toJson(trackingDto)));
        else
            for (TrackingDto trackingDto : trackingDtos)
                json.add(gson.toJson(trackingDto));
        args.putStringArrayList(BundleEnum.TRACKING.getValue(), json);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            json = getArguments().getStringArrayList(
                    BundleEnum.TRACKING.getValue());
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReadingSettingDeleteBinding.inflate(inflater, container, false);
        activity = getActivity();
        initialize();
        return binding.getRoot();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void initialize() {
        Gson gson = new Gson();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            json.forEach(s -> trackingDtos.add(gson.fromJson(s, TrackingDto.class)));
        } else
            for (String s : json) trackingDtos.add(gson.fromJson(s, TrackingDto.class));
        initializeSpinner();
        setOnButtonDeleteClickListener();
        binding.imageViewDelete.setImageDrawable(
                ContextCompat.getDrawable(activity, R.drawable.img_delete));
    }

    void setOnButtonDeleteClickListener() {
        binding.buttonDelete.setOnClickListener(v -> {
            DeleteFragment deleteFragment;
            if (binding.spinner.getSelectedItemPosition() == 0) {
                deleteFragment = DeleteFragment.newInstance("");
            } else {
                deleteFragment = DeleteFragment.newInstance(
                        trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).id);
            }
            deleteFragment.show(getParentFragmentManager(), "");
        });
    }

    void initializeSpinner() {
        if (trackingDtos.size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                trackingDtos.forEach(trackingDto -> items.add(String.valueOf(trackingDto.trackNumber)));
            } else
                for (TrackingDto trackingDto : trackingDtos) {
                    items.add(String.valueOf(trackingDto.trackNumber));
                }
        }
        items.add(0, getString(R.string.all_items));
        adapter = new SpinnerCustomAdapter(activity, items);
        binding.spinner.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.imageViewDelete.setImageDrawable(null);
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        items = null;
        trackingDtos = null;
    }
}