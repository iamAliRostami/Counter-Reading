package com.leon.counter_reading.activities;

import android.os.Bundle;
import android.os.Debug;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.ActivityDistributionBillBinding;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.utils.DifferentCompanyManager;

public class DistributionBillActivity extends AppCompatActivity {
    private ActivityDistributionBillBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApplication.onActivitySetTheme(this, MyApplication.getApplicationComponent()
                        .SharedPreferenceModel().getIntData(SharedReferenceKeys.THEME_STABLE.getValue()),
                true);
        super.onCreate(savedInstanceState);
        binding = ActivityDistributionBillBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialize();
    }

    void initialize() {
        TextView textViewCompanyName = findViewById(R.id.text_view_company_name);
        textViewCompanyName.setText(DifferentCompanyManager.getCompanyName(DifferentCompanyManager.getActiveCompanyName()));

        binding.imageViewBill.setImageDrawable(
                AppCompatResources.getDrawable(getApplicationContext(), R.drawable.img_temporary));
        startAnimationOnTextViewCounter();
    }

    private void startAnimationOnTextViewCounter() {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(300);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        binding.textViewCounter.startAnimation(anim);
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
        binding.imageViewBill.setImageDrawable(null);
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