package com.leon.counter_reading.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.view.View;

import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.ActivityCounterPlaceBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.enums.SharedReferenceNames;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.utils.MyDatabaseClient;
import com.leon.counter_reading.utils.SharedPreferenceManager;

public class CounterPlaceActivity extends AppCompatActivity {
    ActivityCounterPlaceBinding binding;
    Activity activity;
    ISharedPreferenceManager sharedPreferenceManager;
    String uuid;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext(),
                SharedReferenceNames.ACCOUNT.getValue());
        int theme = sharedPreferenceManager.getIntData(SharedReferenceKeys.THEME_STABLE.getValue());
        MyApplication.onActivitySetTheme(this, theme, true);
        binding = ActivityCounterPlaceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        initialize();
    }

    void initialize() {
        if (getIntent().getExtras() != null) {
            uuid = getIntent().getExtras().getString(BundleEnum.BILL_ID.getValue());
            position = getIntent().getExtras().getInt(BundleEnum.POSITION.getValue());
        }
        binding.imageViewLocation.setImageDrawable(
                ContextCompat.getDrawable(activity, R.drawable.img_location));
        setOnButtonSubmitClickListener();
    }

    void setOnButtonSubmitClickListener() {
        binding.buttonSubmit.setOnClickListener(v -> {
            boolean cancel = false;
            View view = null;
            if (binding.editText1.getText().toString().isEmpty()) {
                cancel = true;
                view = binding.editText1;
                binding.editText1.setError(getString(R.string.error_empty));
            }
            if (!cancel && binding.editText2.getText().toString().isEmpty()) {
                cancel = true;
                view = binding.editText2;
                binding.editText2.setError(getString(R.string.error_empty));
            }
            if (!cancel && binding.editText3.getText().toString().isEmpty()) {
                cancel = true;
                view = binding.editText3;
                binding.editText3.setError(getString(R.string.error_empty));
            }
            if (!cancel && binding.editText4.getText().toString().isEmpty()) {
                cancel = true;
                view = binding.editText4;
                binding.editText4.setError(getString(R.string.error_empty));
            }
            if (cancel) {
                view.requestFocus();
            } else {
                String d1 = binding.editText1.getText().toString().concat(".").
                        concat(binding.editText2.getText().toString());
                String d2 = binding.editText3.getText().toString().concat(".").
                        concat(binding.editText4.getText().toString());
                MyDatabaseClient.getInstance(activity).getMyDatabase().onOffLoadDao().
                        updateOnOffLoadLocation(uuid,d1,d2);
                Intent intent = new Intent();
                intent.putExtra(BundleEnum.POSITION.getValue(), position);
                intent.putExtra(BundleEnum.BILL_ID.getValue(), uuid);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void onStop() {
        Debug.getNativeHeapAllocatedSize();
        System.runFinalization();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Runtime.getRuntime().gc();
        System.gc();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        binding.imageViewLocation.setImageDrawable(null);
        Debug.getNativeHeapAllocatedSize();
        System.runFinalization();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Runtime.getRuntime().gc();
        System.gc();
        super.onDestroy();
    }
}