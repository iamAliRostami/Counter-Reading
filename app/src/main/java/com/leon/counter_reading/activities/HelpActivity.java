package com.leon.counter_reading.activities;

import android.os.Debug;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.R;
import com.leon.counter_reading.base_items.BaseActivity;
import com.leon.counter_reading.databinding.ActivityHelpBinding;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.enums.SharedReferenceNames;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.utils.SharedPreferenceManager;

public class HelpActivity extends BaseActivity {
    ActivityHelpBinding binding;
    ISharedPreferenceManager sharedPreferenceManager;

    @Override
    protected void initialize() {
        binding = ActivityHelpBinding.inflate(getLayoutInflater());
        View childLayout = binding.getRoot();
        ConstraintLayout parentLayout = findViewById(R.id.base_Content);
        parentLayout.addView(childLayout);
        sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext(),
                SharedReferenceNames.ACCOUNT.getValue());
        if (sharedPreferenceManager.checkIsNotEmpty(SharedReferenceKeys.USERNAME_TEMP.getValue()))
            binding.textViewDate.setText(sharedPreferenceManager.getStringData(SharedReferenceKeys.DATE.getValue()));
        binding.textViewVersion.setText(getString(R.string.version).concat(" ")
                .concat(BuildConfig.VERSION_NAME));
    }


    @Override
    protected void onStop() {
        super.onStop();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Debug.getNativeHeapAllocatedSize();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Debug.getNativeHeapAllocatedSize();
    }
}