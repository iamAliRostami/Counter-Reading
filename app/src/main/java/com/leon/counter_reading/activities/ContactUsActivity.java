package com.leon.counter_reading.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Debug;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.ActivityContactUsBinding;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.utils.DifferentCompanyManager;

public class ContactUsActivity extends AppCompatActivity {
    private ActivityContactUsBinding binding;
    private ISharedPreferenceManager sharedPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferenceManager = MyApplication.getApplicationComponent().SharedPreferenceModel();
        MyApplication.onActivitySetTheme(this, sharedPreferenceManager.getIntData(SharedReferenceKeys.THEME_STABLE.getValue()), true);
        super.onCreate(savedInstanceState);
        binding = ActivityContactUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialize();
    }

    @SuppressLint("SetTextI18n")
    void initialize() {
        TextView textViewCompanyName = findViewById(R.id.text_view_company_name);
        textViewCompanyName.setText(DifferentCompanyManager.getCompanyName(DifferentCompanyManager.getActiveCompanyName()));
        //TODO
        if (sharedPreferenceManager.checkIsNotEmpty(SharedReferenceKeys.USERNAME_TEMP.getValue()))
            binding.textViewDate.setText(sharedPreferenceManager
                    .getStringData(SharedReferenceKeys.DATE.getValue()));
        binding.textViewVersion.setText(BuildConfig.VERSION_NAME);
        binding.textViewSite.setText("tarnamesep.com");
        binding.imageViewLogo.setImageDrawable(AppCompatResources.getDrawable(
                getApplicationContext(), R.drawable.img_logo));
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
        binding.imageViewLogo.setImageDrawable(null);
        binding = null;
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