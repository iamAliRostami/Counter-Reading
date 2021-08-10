package com.leon.counter_reading.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.FragmentSettingUpdateBinding;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.enums.SharedReferenceNames;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.tables.LastInfo;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.HttpClientWrapper;
import com.leon.counter_reading.utils.SharedPreferenceManager;
import com.leon.counter_reading.utils.updating.GetUpdateFile;
import com.leon.counter_reading.utils.updating.GetUpdateInfo;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class SettingUpdateFragment extends Fragment {
    FragmentSettingUpdateBinding binding;
    Activity activity;
    boolean firstTime = true;
    int versionCode;
    ISharedPreferenceManager sharedPreferenceManager;

    public SettingUpdateFragment() {
    }

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
        sharedPreferenceManager = new SharedPreferenceManager(activity, SharedReferenceNames.ACCOUNT.getValue());
        binding.imageViewUpdate.
                setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.img_update));
        setOnButtonReceiveClickListener();
    }

    void setOnButtonReceiveClickListener() {
        binding.buttonReceive.setOnClickListener(v -> {
            if (firstTime) {
                new GetUpdateInfo(activity, this);
            } else if (BuildConfig.VERSION_CODE - 1 >= versionCode) {
                new CustomToast().success(getString(R.string.you_are_updated));
            } else {
                new GetUpdateFile(activity);
            }
        });
    }

    public void updateInfoUi(LastInfo lastInfo) {
        activity.runOnUiThread(() -> {
            binding.textViewVersion.setText(lastInfo.versionName);
            ISharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(
                    activity, SharedReferenceNames.ACCOUNT.getValue());
            sharedPreferenceManager.putData(SharedReferenceKeys.DATE.getValue(),
                    lastInfo.uploadDateJalali);
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
        if (HttpClientWrapper.call != null)
            HttpClientWrapper.call.cancel();
    }
}