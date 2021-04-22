package com.leon.counter_reading.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;

import com.google.gson.Gson;
import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.adapters.SpinnerCustomAdapter;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.enums.SharedReferenceNames;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.tables.KarbariDto;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.utils.MyDatabaseClient;
import com.leon.counter_reading.utils.SharedPreferenceManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class PossibleFragment extends DialogFragment {
    com.leon.counter_reading.databinding.FragmentPossibleBinding binding;
    OnOffLoadDto onOffLoadDto;
    int position;
    Activity activity;
    ISharedPreferenceManager sharedPreferenceManager;
    ArrayList<KarbariDto> karbariDtos = new ArrayList<>();
    ArrayList<String> items = new ArrayList<>();
    SpinnerCustomAdapter spinnerCustomAdapter;

    public PossibleFragment() {
    }

    public static PossibleFragment newInstance(OnOffLoadDto onOffLoadDto, int position) {
        PossibleFragment fragment = new PossibleFragment();
        fragment.setArguments(putBundle(onOffLoadDto, position));
        return fragment;
    }

    static Bundle putBundle(OnOffLoadDto onOffLoadDto, int position) {
        Bundle args = new Bundle();
        Gson gson = new Gson();
        String json1 = gson.toJson(onOffLoadDto);
        args.putString(BundleEnum.ON_OFF_LOAD.getValue(), json1);
        args.putInt(BundleEnum.POSITION.getValue(), position);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBundle();
    }

    void getBundle() {
        if (getArguments() != null) {
            Gson gson = new Gson();
            onOffLoadDto = gson.fromJson(getArguments().getString(
                    BundleEnum.ON_OFF_LOAD.getValue()), OnOffLoadDto.class);
            position = getArguments().getInt(BundleEnum.POSITION.getValue());
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = com.leon.counter_reading.databinding.FragmentPossibleBinding.inflate(inflater, container, false);
        activity = getActivity();

        initialize();
        return binding.getRoot();
    }

    void initialize() {
        sharedPreferenceManager = new SharedPreferenceManager(activity, SharedReferenceNames.ACCOUNT.getValue());
        initializeTextViews();
        setOnButtonsClickListener();
    }

    void setOnButtonsClickListener() {
        binding.buttonSubmit.setOnClickListener(v -> {
            boolean cancel = false;
            View view = null;
            if (sharedPreferenceManager.getBoolData(SharedReferenceKeys.KARBARI.getValue()))
                onOffLoadDto.possibleKarbariCode = karbariDtos.get(
                        binding.spinnerKarbari.getSelectedItemPosition()).moshtarakinId;
            if (binding.editTextMobile.getText().length() > 0) {
                if (binding.editTextMobile.getText().length() < 11 ||
                        !binding.editTextMobile.getText().toString().substring(0, 2).contains("09")) {
                    binding.editTextMobile.setError(getString(R.string.error_format));
                    view = binding.editTextMobile;
                    cancel = true;
                } else
                    onOffLoadDto.possibleMobile = binding.editTextMobile.getText().toString();
            }
            if (binding.editTextSerial.getText().length() > 0) {
                if (binding.editTextSerial.getText().toString().length() < 3) {
                    binding.editTextSerial.setError(getString(R.string.error_format));
                    view = binding.editTextSerial;
                    cancel = true;
                } else
                    onOffLoadDto.possibleCounterSerial = binding.editTextSerial.getText().toString();
            }
            if (binding.editTextAccount.getText().length() > 0) {
                if (binding.editTextAccount.getText().toString().length() < 7) {
                    binding.editTextAccount.setError(getString(R.string.error_format));
                    view = binding.editTextAccount;
                    cancel = true;
                } else onOffLoadDto.possibleEshterak = binding.editTextAccount.getText().toString();
            }
            if (binding.editTextDescription.getText().length() > 0) {
                onOffLoadDto.description = binding.editTextDescription.getText().toString();
            }
            if (binding.editTextAddress.getText().length() > 0)
                onOffLoadDto.possibleAddress = binding.editTextAddress.getText().toString();

            if (binding.editTextAhadOther.getText().length() > 0)
                onOffLoadDto.possibleAhadSaierOrAbBaha = Integer.parseInt(binding.editTextAhadOther.getText().toString());

            if (binding.editTextAhadFari.getText().length() > 0)
                onOffLoadDto.possibleAhadTejariOrFari = Integer.parseInt(binding.editTextAhadFari.getText().toString());

            if (binding.editTextAhadAsli.getText().length() > 0)
                onOffLoadDto.possibleAhadMaskooniOrAsli = Integer.parseInt(binding.editTextAhadAsli.getText().toString());

            if (binding.editTextAhadEmpty.getText().length() > 0)
                onOffLoadDto.possibleEmpty = Integer.parseInt(binding.editTextAhadEmpty.getText().toString());

            if (cancel)
                view.requestFocus();
            else {
                ((ReadingActivity) activity).updateOnOffLoadByNavigation(position, onOffLoadDto);
                dismiss();

            }
        });
        binding.buttonClose.setOnClickListener(v -> dismiss());
    }

    void initializeTextViews() {
        //TODO
        binding.editTextSerial.setVisibility(sharedPreferenceManager.
                getBoolData(SharedReferenceKeys.SERIAL.getValue()) ? View.VISIBLE : View.GONE);
        binding.editTextAddress.setVisibility(sharedPreferenceManager.
                getBoolData(SharedReferenceKeys.ADDRESS.getValue()) ? View.VISIBLE : View.GONE);
        binding.editTextAccount.setVisibility(sharedPreferenceManager.
                getBoolData(SharedReferenceKeys.ACCOUNT.getValue()) ? View.VISIBLE : View.GONE);
        binding.editTextAhadEmpty.setVisibility(sharedPreferenceManager.
                getBoolData(SharedReferenceKeys.AHAD_EMPTY.getValue()) ? View.VISIBLE : View.GONE);

        binding.editTextDescription.setVisibility(sharedPreferenceManager.
                getBoolData(SharedReferenceKeys.DESCRIPTION.getValue()) ? View.VISIBLE : View.GONE);

        binding.linearLayoutAhadAsli.setVisibility(sharedPreferenceManager.
                getBoolData(SharedReferenceKeys.AHAD_ASLI.getValue()) ? View.VISIBLE : View.GONE);

        binding.editTextAhadAsli.setVisibility(sharedPreferenceManager.
                getBoolData(SharedReferenceKeys.AHAD_ASLI.getValue()) ? View.VISIBLE : View.GONE);
        binding.textViewAhadAsli.setVisibility(sharedPreferenceManager.
                getBoolData(SharedReferenceKeys.AHAD_ASLI.getValue()) ? View.VISIBLE : View.GONE);
        binding.textViewAhadAsli.setText(String.valueOf(onOffLoadDto.ahadMaskooniOrAsli));

        binding.linearLayoutAhadFari.setVisibility(sharedPreferenceManager.
                getBoolData(SharedReferenceKeys.AHAD_FARI.getValue()) ? View.VISIBLE : View.GONE);

        binding.editTextAhadFari.setVisibility(sharedPreferenceManager.
                getBoolData(SharedReferenceKeys.AHAD_FARI.getValue()) ? View.VISIBLE : View.GONE);
        binding.textViewAhadFari.setVisibility(sharedPreferenceManager.
                getBoolData(SharedReferenceKeys.AHAD_FARI.getValue()) ? View.VISIBLE : View.GONE);
        binding.textViewAhadFari.setText(String.valueOf(onOffLoadDto.ahadTejariOrFari));

        binding.linearLayoutAhadOther.setVisibility(sharedPreferenceManager.
                getBoolData(SharedReferenceKeys.AHAD_OTHER.getValue()) ? View.VISIBLE : View.GONE);
        binding.editTextAhadOther.setVisibility(sharedPreferenceManager.
                getBoolData(SharedReferenceKeys.AHAD_OTHER.getValue()) ? View.VISIBLE : View.GONE);
        binding.textViewAhadOther.setVisibility(sharedPreferenceManager.
                getBoolData(SharedReferenceKeys.AHAD_OTHER.getValue()) ? View.VISIBLE : View.GONE);
        binding.textViewAhadOther.setText(String.valueOf(onOffLoadDto.ahadSaierOrAbBaha));

        binding.linearLayoutMobile.setVisibility(sharedPreferenceManager.
                getBoolData(SharedReferenceKeys.MOBILE.getValue()) ? View.VISIBLE : View.GONE);
        binding.editTextMobile.setVisibility(sharedPreferenceManager.
                getBoolData(SharedReferenceKeys.MOBILE.getValue()) ? View.VISIBLE : View.GONE);
        binding.textViewMobile.setVisibility(sharedPreferenceManager.
                getBoolData(SharedReferenceKeys.MOBILE.getValue()) ? View.VISIBLE : View.GONE);
        binding.textViewMobile.setText(String.valueOf(onOffLoadDto.mobile));

        initializeSpinner();
    }

    void initializeSpinner() {
        binding.linearLayoutKarbari.setVisibility(sharedPreferenceManager.
                getBoolData(SharedReferenceKeys.KARBARI.getValue()) ? View.VISIBLE : View.GONE);
        if (sharedPreferenceManager.getBoolData(SharedReferenceKeys.KARBARI.getValue())) {
            karbariDtos.addAll(MyDatabaseClient.
                    getInstance(activity).getMyDatabase().karbariDao().getAllKarbariDto());
            for (KarbariDto karbariDto : karbariDtos) {
                items.add(karbariDto.title);
            }
            spinnerCustomAdapter = new SpinnerCustomAdapter(activity, items);
            binding.spinnerKarbari.setAdapter(spinnerCustomAdapter);
            binding.spinnerKarbari.setSelection(onOffLoadDto.counterStatePosition);
        }
    }

    @Override
    public void onResume() {
        WindowManager.LayoutParams params = Objects.requireNonNull(getDialog()).getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}