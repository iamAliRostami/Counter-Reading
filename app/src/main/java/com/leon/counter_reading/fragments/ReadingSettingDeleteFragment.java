package com.leon.counter_reading.fragments;

import android.app.Activity;
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
    private FragmentReadingSettingDeleteBinding binding;
    private String[] items;
    private ArrayList<TrackingDto> trackingDtos = new ArrayList<>();
    private ArrayList<String> json;
    private Activity activity;

    public ReadingSettingDeleteFragment() {
    }

    public static ReadingSettingDeleteFragment newInstance(ArrayList<TrackingDto> trackingDtos) {
        ReadingSettingDeleteFragment fragment = new ReadingSettingDeleteFragment();
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
        binding = FragmentReadingSettingDeleteBinding.inflate(inflater, container, false);
        activity = getActivity();
        initialize();
        return binding.getRoot();
    }

    void initialize() {
        Gson gson = new Gson();
        trackingDtos.clear();
        for (int i = 0, jsonSize = json.size(); i < jsonSize; i++) {
            String s = json.get(i);
            trackingDtos.add(gson.fromJson(s, TrackingDto.class));
        }
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
        items = new String[trackingDtos.size() + 1];
        if (trackingDtos.size() > 0) {
            for (int i = 0; i < trackingDtos.size(); i++) {
                items[i + 1] = String.valueOf(trackingDtos.get(i).trackNumber);
            }
        }
        items[0] = getString(R.string.all_items);
        SpinnerCustomAdapter adapter = new SpinnerCustomAdapter(activity, items);
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