package com.leon.counter_reading.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.leon.counter_reading.adapters.ReadingSettingCustomAdapter;
import com.leon.counter_reading.databinding.FragmentReadingSettingBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.tables.ReadingConfigDefaultDto;
import com.leon.counter_reading.tables.TrackingDto;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ReadingSettingFragment extends Fragment {
    FragmentReadingSettingBinding binding;
    ArrayList<TrackingDto> trackingDtos = new ArrayList<>();
    ArrayList<ReadingConfigDefaultDto> readingConfigDefaultDtos = new ArrayList<>();
    ArrayList<Boolean> isActives = new ArrayList<>();
    ArrayList<Integer> zoneIds = new ArrayList<>();
    Context context;

    public ReadingSettingFragment() {
    }

    public static ReadingSettingFragment newInstance(ArrayList<TrackingDto> trackingDtos,
                                                     ArrayList<ReadingConfigDefaultDto>
                                                             readingConfigDefaultDtos) {
        ReadingSettingFragment fragment = new ReadingSettingFragment();
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
        binding = FragmentReadingSettingBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    void initialize() {
        context = getActivity();
        setupListView();
    }

    void setupListView() {
        if (trackingDtos.size() > 0) {
            for (int i = 0; i < trackingDtos.size(); i++) {
                for (ReadingConfigDefaultDto readingConfigDefaultDto : readingConfigDefaultDtos)
                    if (readingConfigDefaultDto.zoneId == trackingDtos.get(i).zoneId) {
                        isActives.add(readingConfigDefaultDto.isActive);
                        zoneIds.add(readingConfigDefaultDto.zoneId);
                    }
            }
            ReadingSettingCustomAdapter readingSettingCustomAdapter =
                    new ReadingSettingCustomAdapter(context, trackingDtos, isActives, zoneIds);
            binding.listViewRead.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            binding.listViewRead.setAdapter(readingSettingCustomAdapter);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}