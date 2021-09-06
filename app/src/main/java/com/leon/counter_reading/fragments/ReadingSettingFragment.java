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
import com.leon.counter_reading.tables.TrackingDto;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ReadingSettingFragment extends Fragment {
    private FragmentReadingSettingBinding binding;
    private ArrayList<TrackingDto> trackingDtos = new ArrayList<>();
    private ArrayList<String> json = new ArrayList<>();
    private Context context;

    public static ReadingSettingFragment newInstance(ArrayList<TrackingDto> trackingDtos) {
        ReadingSettingFragment fragment = new ReadingSettingFragment();
        Bundle args = new Bundle();
        Gson gson = new Gson();
        ArrayList<String> json = new ArrayList<>();
        for (int i = 0, trackingDtosSize = trackingDtos.size(); i < trackingDtosSize; i++) {
            TrackingDto trackingDto = trackingDtos.get(i);
            json.add(gson.toJson(trackingDto));
        }
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
        binding = FragmentReadingSettingBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    void initialize() {
        context = getActivity();
        Gson gson = new Gson();
        trackingDtos.clear();
        for (int i = 0, jsonSize = json.size(); i < jsonSize; i++) {
            String s = json.get(i);
            trackingDtos.add(gson.fromJson(s, TrackingDto.class));
        }
        setupListView();
    }

    void setupListView() {
        if (trackingDtos.size() > 0) {
            ReadingSettingCustomAdapter readingSettingCustomAdapter =
                    new ReadingSettingCustomAdapter(context, trackingDtos);
            binding.listViewRead.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            binding.listViewRead.setAdapter(readingSettingCustomAdapter);
        } else {
            binding.listViewRead.setVisibility(View.GONE);
            binding.textViewNotFound.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        json = null;
        trackingDtos = null;
    }
}