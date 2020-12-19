package com.leon.counter_reading.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    ArrayList<TrackingDto> trackingDtos = new ArrayList<>();
    ArrayList<ReadingConfigDefaultDto> readingConfigDefaultDtos = new ArrayList<>();
    ArrayList<String> items = new ArrayList<>();
    SpinnerCustomAdapter adapter;

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
            for (String s : json) {
                trackingDtos.add(gson.fromJson(s, TrackingDto.class));
            }
            json = getArguments().getStringArrayList(BundleEnum.READING_CONFIG.getValue());
            for (String s : json) {
                readingConfigDefaultDtos.add(gson.fromJson(s, ReadingConfigDefaultDto.class));
            }
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReadingSettingDeleteBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void initialize() {
        binding.imageViewDelete.setImageDrawable(getResources().getDrawable(R.drawable.img_delete));
        initializeSpinner();
    }

    void initializeSpinner() {
        for (TrackingDto trackingDto : trackingDtos) {
            items.add(String.valueOf(trackingDto.trackNumber));
        }
        items.add(0, getString(R.string.all_items));
        adapter = new SpinnerCustomAdapter(getActivity(), items);
        binding.spinner.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.imageViewDelete.setImageDrawable(null);
    }
}