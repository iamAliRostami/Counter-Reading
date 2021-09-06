package com.leon.counter_reading.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentSettingUpdateBinding;
import com.leon.counter_reading.di.view_model.HttpClientWrapper;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.tables.LastInfo;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.updating.GetUpdateFile;
import com.leon.counter_reading.utils.updating.GetUpdateInfo;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class SettingUpdateFragment extends Fragment {
    private FragmentSettingUpdateBinding binding;
    private Activity activity;
    private boolean firstTime = true;
    private int versionCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingUpdateBinding.inflate(inflater, container, false);
        activity = getActivity();
        initialize();
        return binding.getRoot();
    }

    void initialize() {
        binding.imageViewUpdate.
                setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.img_update));
        setOnButtonReceiveClickListener();
    }

    void setOnButtonReceiveClickListener() {
        binding.buttonReceive.setOnClickListener(v -> {
            if (firstTime) {
                new GetUpdateInfo(activity, this);
            } else if (BuildConfig.VERSION_CODE >= versionCode) {
                new CustomToast().success(getString(R.string.you_are_updated));
            } else {
                new GetUpdateFile(activity);
            }
        });
    }

    public void updateInfoUi(LastInfo lastInfo) {
        activity.runOnUiThread(() -> {
            binding.textViewVersion.setText(lastInfo.versionName);
            MyApplication.getApplicationComponent().SharedPreferenceModel().
                    putData(SharedReferenceKeys.DATE.getValue(), lastInfo.uploadDateJalali);
            binding.textViewDate.setText(lastInfo.uploadDateJalali);
            binding.textViewPossibility.setText(lastInfo.description);
            float size = (float) lastInfo.sizeInByte / (1028 * 1028);
            binding.textViewSize.setText(new DecimalFormat("###.##").format(size).
                    concat(getString(R.string.mega_byte)));

            binding.linearLayout1.setVisibility(View.VISIBLE);
            binding.linearLayout2.setVisibility(View.VISIBLE);
            binding.linearLayout3.setVisibility(View.VISIBLE);
            binding.linearLayout4.setVisibility(View.VISIBLE);
            binding.buttonReceive.setText(getString(R.string.receive_file));
        });
        versionCode = lastInfo.versionCode;
        firstTime = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.imageViewUpdate.setImageDrawable(null);
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (HttpClientWrapper.call != null) {
            HttpClientWrapper.call.cancel();
        }
    }
}