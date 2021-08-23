package com.leon.counter_reading.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.UploadActivity;
import com.leon.counter_reading.adapters.SpinnerCustomAdapter;
import com.leon.counter_reading.databinding.FragmentUploadBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.DialogType;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.MyDatabase;
import com.leon.counter_reading.utils.custom_dialogue.CustomDialog;
import com.leon.counter_reading.utils.uploading.PrepareMultimediaToUpload;
import com.leon.counter_reading.utils.uploading.PrepareOffLoadToUpload;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class UploadFragment extends Fragment {
    int[] imageSrc = {
            R.drawable.img_upload_on,
            R.drawable.img_upload_off,
            R.drawable.img_multimedia};
    int type;
    FragmentUploadBinding binding;
    Activity activity;
    ArrayList<String> items = new ArrayList<>();
    ArrayList<TrackingDto> trackingDtos = new ArrayList<>();

    public UploadFragment() {
    }

    public static UploadFragment newInstance(int type) {
        UploadFragment fragment = new UploadFragment();
        Bundle args = new Bundle();
        args.putInt(BundleEnum.TYPE.getValue(), type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        getBundle();
    }

    void getBundle() {
        trackingDtos = new ArrayList<>(((UploadActivity) activity).getTrackingDtos());
        if (getArguments() != null) {
            type = getArguments().getInt(BundleEnum.TYPE.getValue());
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUploadBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    void initialize() {
        if (type == 3) {
            binding.linearLayoutSpinner.setVisibility(View.GONE);
        } else {
            items.clear();
            items.addAll(TrackingDto.getTrackingDtoItems(trackingDtos));
            items.add(0, getString(R.string.select_one));
            setupSpinner();
        }
        binding.imageViewUpload.setImageResource(imageSrc[type - 1]);
        setOnButtonUploadClickListener();
    }

    void setupSpinner() {
        SpinnerCustomAdapter spinnerCustomAdapter = new SpinnerCustomAdapter(activity, items);
        binding.spinner.setAdapter(spinnerCustomAdapter);
    }

    boolean checkOnOffLoad() {
        int total, mane = 0, unread, alalPercent, imagesCount, voicesCount, trackNumber;
        double alalMane;
        MyDatabase myDatabase = MyApplication.getApplicationComponent().MyDatabase();
        if (binding.spinner.getSelectedItemPosition() != 0) {
            trackNumber = trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).trackNumber;
            total = myDatabase.onOffLoadDao().getOnOffLoadCount(trackNumber);
            unread = myDatabase.onOffLoadDao().getOnOffLoadUnreadCount(0, trackNumber);
            ArrayList<Integer> isManes = new ArrayList<>(myDatabase.counterStateDao().
                    getCounterStateDtosIsMane(true));
            for (int i = 0; i < isManes.size(); i++) {
                mane = mane + myDatabase.onOffLoadDao().getOnOffLoadIsManeCount(isManes.get(i), trackNumber);
            }
            alalPercent = myDatabase.trackingDao().getAlalHesabByZoneId(
                    trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).zoneId);
            alalMane = (double) mane / total * 100;
            imagesCount = myDatabase.imageDao().getUnsentImageCount(trackNumber, false);
            voicesCount = myDatabase.voiceDao().getUnsentVoiceCount(trackNumber, false);

        } else return false;
        if (unread > 0) {
            String message = String.format(getString(R.string.unread_number), unread);
            new CustomToast().info(message, Toast.LENGTH_LONG);
            return false;
        } else if (mane > 0 && alalMane > (double) alalPercent) {
            new CustomToast().info(getString(R.string.darsad_alal), Toast.LENGTH_LONG);
            return false;
        } else if (imagesCount > 0 || voicesCount > 0) {
            String message = String.format(getString(R.string.unuploaded_multimedia), imagesCount, voicesCount);
            //TODO
            new CustomDialog(DialogType.YellowRedirect, activity, message,
                    getString(R.string.dear_user),
                    getString(R.string.upload), getString(R.string.confirm), new Inline());
            return false;
        }
        return true;
    }

    void setOnButtonUploadClickListener() {
        binding.buttonUpload.setOnClickListener(v -> {
            if (type == 1 || type == 2) {
                if (checkOnOffLoad())
                    new PrepareOffLoadToUpload(activity,
                            trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).trackNumber,
                            trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).id).
                            execute(activity);
            } else if (type == 3) {
                new PrepareMultimediaToUpload(activity).execute(activity);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.imageViewUpload.setImageDrawable(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        imageSrc = null;
        trackingDtos = null;
        items = null;
    }

    class Inline implements CustomDialog.Inline {
        @Override
        public void inline() {
            new PrepareOffLoadToUpload(activity,
                    trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).trackNumber,
                    trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).id).
                    execute(activity);
        }
    }
}