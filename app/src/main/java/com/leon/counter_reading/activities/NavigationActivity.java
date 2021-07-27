package com.leon.counter_reading.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Debug;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.ActivityNavigationBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.enums.SharedReferenceNames;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.utils.DifferentCompanyManager;
import com.leon.counter_reading.utils.SharedPreferenceManager;
import com.leon.counter_reading.utils.navigation.Navigating;

public class NavigationActivity extends AppCompatActivity {
    private ActivityNavigationBinding binding;
    private Activity activity;
    private String uuid;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ISharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext(),
                SharedReferenceNames.ACCOUNT.getValue());
        int theme = sharedPreferenceManager.getIntData(SharedReferenceKeys.THEME_STABLE.getValue());
        MyApplication.onActivitySetTheme(this, theme, true);
        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        initialize();
    }

    void initialize() {
        if (getIntent().getExtras() != null) {
            uuid = getIntent().getExtras().getString(BundleEnum.BILL_ID.getValue());
            position = getIntent().getExtras().getInt(BundleEnum.POSITION.getValue());
        }
        binding.textViewEmpty.setText(DifferentCompanyManager.getAhad(
                DifferentCompanyManager.getActiveCompanyName()).concat(getString(R.string.empty)));
        initializeImageViews();
        setOnButtonNavigationClickListener();
        setOnEditTextChangeListener();
    }

    void setOnButtonNavigationClickListener() {
        binding.buttonNavigation.setOnClickListener(v -> {
            View view = null;
            boolean cancel = false;
            if (binding.editTextAccount.getText().toString().length() > 0 &&
                    binding.editTextAccount.getText().toString().length() < 7) {
                binding.editTextAccount.setError(getString(R.string.error_format));
                view = binding.editTextAccount;
                cancel = true;
            } else if (binding.editTextPhone.getText().toString().length() > 0 &&
                    binding.editTextPhone.getText().toString().length() < 8) {
                binding.editTextPhone.setError(getString(R.string.error_format));
                view = binding.editTextPhone;
                cancel = true;
            } else if (binding.editTextMobile.getText().toString().length() > 0 &&
                    (binding.editTextMobile.getText().toString().length() < 11 ||
                            !binding.editTextMobile.getText().toString().substring(0, 2).contains("09"))) {
                binding.editTextMobile.setError(getString(R.string.error_format));
                view = binding.editTextMobile;
                cancel = true;
            } else if (binding.editTextSerialCounter.getText().toString().length() > 0 &&
                    binding.editTextSerialCounter.getText().toString().length() < 3) {
                binding.editTextSerialCounter.setError(getString(R.string.error_format));
                view = binding.editTextSerialCounter;
                cancel = true;
            }
            if (cancel) {
                view.requestFocus();
            } else {
                int possibleEmpty = binding.editTextEmpty.getText().length() > 0 ?
                        Integer.parseInt(binding.editTextEmpty.getText().toString()) : 0;
                new Navigating(position, uuid, possibleEmpty,
                        binding.editTextAccount.getText().toString(),
                        binding.editTextMobile.getText().toString(),
                        binding.editTextPhone.getText().toString(),
                        binding.editTextSerialCounter.getText().toString(),
                        binding.editTextAddress.getText().toString()).execute(activity);
            }
        });
    }

    void setOnEditTextChangeListener() {
        binding.editTextAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 15) {
                    View view = binding.editTextPhone;
                    view.requestFocus();
                }
            }
        });
        binding.editTextPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 8) {
                    View view = binding.editTextMobile;
                    view.requestFocus();
                }
            }
        });

        binding.editTextMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 11 && s.toString().substring(0, 2).contains("09")) {
                    View view = binding.editTextSerialCounter;
                    view.requestFocus();
                } else binding.editTextMobile.setError(getString(R.string.error_format));
            }
        });
        binding.editTextSerialCounter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 15) {
                    View view = binding.editTextAddress;
                    view.requestFocus();
                }
            }
        });
    }

    void initializeImageViews() {
        binding.imageViewAccount.
                setImageDrawable(AppCompatResources.getDrawable(activity, R.drawable.img_subscribe));
        binding.imageViewAddress.
                setImageDrawable(AppCompatResources.getDrawable(activity, R.drawable.img_address));
        binding.imageViewCounterSerial.
                setImageDrawable(AppCompatResources.getDrawable(activity, R.drawable.img_counter));
        binding.imageViewPhoneNumber.
                setImageDrawable(AppCompatResources.getDrawable(activity, R.drawable.img_phone));
        binding.imageViewMobile.
                setImageDrawable(AppCompatResources.getDrawable(activity, R.drawable.img_mobile));
        binding.imageViewEmpty.
                setImageDrawable(AppCompatResources.getDrawable(activity, R.drawable.img_home));
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
        binding.imageViewAccount.setImageDrawable(null);
        binding.imageViewAddress.setImageDrawable(null);
        binding.imageViewCounterSerial.setImageDrawable(null);
        binding.imageViewPhoneNumber.setImageDrawable(null);
        binding.imageViewMobile.setImageDrawable(null);
        binding.imageViewEmpty.setImageDrawable(null);
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