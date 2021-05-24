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
import com.leon.counter_reading.tables.ReadingConfigDefaultDto;
import com.leon.counter_reading.tables.TrackingDto;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ReadingSettingDeleteFragment extends Fragment {
    FragmentReadingSettingDeleteBinding binding;
    final ArrayList<TrackingDto> trackingDtos = new ArrayList<>();
    final ArrayList<ReadingConfigDefaultDto> readingConfigDefaultDtos = new ArrayList<>();
    ArrayList<String> items = new ArrayList<>();
    SpinnerCustomAdapter adapter;
    Activity activity;

    public ReadingSettingDeleteFragment() {
    }

    public static ReadingSettingDeleteFragment newInstance(ArrayList<TrackingDto> trackingDtos,
                                                           ArrayList<ReadingConfigDefaultDto>
                                                                   readingConfigDefaultDtos) {
        ReadingSettingDeleteFragment fragment = new ReadingSettingDeleteFragment();
        Bundle args = new Bundle();
        Gson gson = new Gson();
        ArrayList<String> json1 = new ArrayList<>();
        for (TrackingDto trackingDto : trackingDtos) {
            json1.add(gson.toJson(trackingDto));
        }
        args.putStringArrayList(BundleEnum.TRACKING.getValue(), json1);
        ArrayList<String> json2 = new ArrayList<>();
        for (ReadingConfigDefaultDto readingConfigDefaultDto : readingConfigDefaultDtos) {
            json2.add(gson.toJson(readingConfigDefaultDto));
        }
        args.putStringArrayList(BundleEnum.READING_CONFIG.getValue(), json2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Gson gson = new Gson();
            ArrayList<String> json = getArguments().getStringArrayList(
                    BundleEnum.TRACKING.getValue());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                json.forEach(s -> trackingDtos.add(gson.fromJson(s, TrackingDto.class)));
            } else
                for (String s : json) trackingDtos.add(gson.fromJson(s, TrackingDto.class));

            json = getArguments().getStringArrayList(BundleEnum.READING_CONFIG.getValue());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                json.forEach(s -> readingConfigDefaultDtos.add(gson.fromJson(s, ReadingConfigDefaultDto.class)));
            } else
                for (String s : json) {
                    readingConfigDefaultDtos.add(gson.fromJson(s, ReadingConfigDefaultDto.class));
                }
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
        binding.imageViewDelete.setImageDrawable(
                ContextCompat.getDrawable(activity, R.drawable.img_delete));
        initializeSpinner();
        setOnButtonDeleteClickListener();
    }

    void setOnButtonDeleteClickListener() {
        binding.buttonDelete.setOnClickListener(v -> {
            if (binding.spinner.getSelectedItemPosition() == 0) {
                DeleteFragment deleteFragment = DeleteFragment.newInstance("");
                if (getFragmentManager() != null) {
                    deleteFragment.show(getFragmentManager(), "");
                }
            } else {
                DeleteFragment deleteFragment = DeleteFragment.newInstance(
                        trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).id);
                if (getFragmentManager() != null) {
                    deleteFragment.show(getFragmentManager(), "");
                }
            }
        });
    }

    void initializeSpinner() {
        if (trackingDtos.size() > 0 && readingConfigDefaultDtos.size() > 0) {
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
    }
}