package com.leon.counter_reading.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.UploadActivity;
import com.leon.counter_reading.adapters.SpinnerCustomAdapter;
import com.leon.counter_reading.databinding.FragmentUploadBinding;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.DialogType;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.MyDatabase;
import com.leon.counter_reading.utils.uploading.PrepareMultimedia;
import com.leon.counter_reading.utils.uploading.PrepareOffLoad;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class UploadFragment extends Fragment {
    private int[] imageSrc = {
            R.drawable.img_upload_on,
            R.drawable.img_upload_off,
            R.drawable.img_multimedia};
    private int type;
    private FragmentUploadBinding binding;
    private Activity activity;
    private String[] items;
    private ArrayList<TrackingDto> trackingDtos = new ArrayList<>();
    private TextView textView;

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

    private void initialize() {
        if (type == 3) {
            binding.spinner.setVisibility(View.GONE);
            binding.textViewMultimedia.setVisibility(View.VISIBLE);
            textView = binding.getRoot().findViewById(R.id.text_view_multimedia);
            setMultimediaInfo(activity);
        } else {
            items = TrackingDto.getTrackingDtoItems(trackingDtos, getString(R.string.select_one));
            setupSpinner();
        }
        binding.imageViewUpload.setImageResource(imageSrc[type - 1]);
        setOnButtonUploadClickListener();
    }

    public void setMultimediaInfo(Activity activity) {
        int imagesCount = MyApplication.getApplicationComponent().MyDatabase().imageDao().getUnsentImageCount(false);
        int voicesCount = MyApplication.getApplicationComponent().MyDatabase().voiceDao().getUnsentVoiceCount(false);
        String message = String.format(activity.getString(R.string.unuploaded_multimedia), imagesCount, voicesCount);
        activity.runOnUiThread(() -> binding.textViewMultimedia.setText(message));

    }

    private void setupSpinner() {
        SpinnerCustomAdapter spinnerCustomAdapter = new SpinnerCustomAdapter(activity, items);
        binding.spinner.setAdapter(spinnerCustomAdapter);
    }

    private boolean checkOnOffLoad() {
        int total, mane = 0, unread, alalPercent, imagesCount, voicesCount, trackNumber;
        double alalMane;
        MyDatabase myDatabase = MyApplication.getApplicationComponent().MyDatabase();
        if (binding.spinner.getSelectedItemPosition() != 0) {
            trackNumber = trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).trackNumber;
            total = myDatabase.onOffLoadDao().getOnOffLoadCount(trackNumber);
            unread = myDatabase.onOffLoadDao().getOnOffLoadUnreadCount(0, trackNumber);
            //TODO
            ArrayList<Integer> isManes = new ArrayList<>(myDatabase.counterStateDao().
                    getCounterStateDtosIsMane(true,
                            trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).zoneId));
            for (int i = 0; i < isManes.size(); i++) {
                mane += myDatabase.onOffLoadDao().getOnOffLoadIsManeCount(isManes.get(i), trackNumber);
            }
            alalPercent = myDatabase.trackingDao().getAlalHesabByZoneId(trackingDtos
                    .get(binding.spinner.getSelectedItemPosition() - 1).zoneId);
            alalMane = (double) mane / total * 100;
            imagesCount = myDatabase.imageDao().getUnsentImageCountByTrackNumber(trackNumber, false);
            voicesCount = myDatabase.voiceDao().getUnsentVoiceCountByTrackNumber(trackNumber, false);

        } else return false;
        if (unread > 0) {
            String message = String.format(getString(R.string.unread_number), unread);
            new CustomToast().info(message, Toast.LENGTH_LONG);
            return false;
        } else if (mane > 0 && alalMane > (double) alalPercent) {
            new CustomToast().info(getString(R.string.darsad_alal), Toast.LENGTH_LONG);
            return false;
        } else if (imagesCount > 0 || voicesCount > 0) {
            String message = String.format(getString(R.string.unuploaded_multimedia),
                    imagesCount, voicesCount).concat("\n")
                    .concat(getString(R.string.recommend_multimedia));
            //TODO
            new CustomDialogModel(DialogType.YellowRedirect, activity, message,
                    getString(R.string.dear_user),
                    getString(R.string.upload), getString(R.string.confirm), new Inline());
            return false;
        }
        return true;
    }

    private void setOnButtonUploadClickListener() {
        binding.buttonUpload.setOnClickListener(v -> {
            if (type == 1 || type == 2) {
                if (checkOnOffLoad())
                    new PrepareOffLoad(activity,
                            trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).trackNumber,
                            trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).id).
                            execute(activity);
            } else if (type == 3) {
                new PrepareMultimedia(activity, this, false).execute(activity);
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

    private class Inline implements CustomDialogModel.Inline {
        @Override
        public void inline() {
            new PrepareOffLoad(activity,
                    trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).trackNumber,
                    trackingDtos.get(binding.spinner.getSelectedItemPosition() - 1).id).
                    execute(activity);
        }
    }
}