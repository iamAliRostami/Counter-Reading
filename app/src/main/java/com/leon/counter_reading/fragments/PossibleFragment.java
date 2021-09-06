package com.leon.counter_reading.fragments;

import static com.leon.counter_reading.utils.MakeNotification.makeRing;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;

import com.google.gson.Gson;
import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.adapters.SpinnerCustomAdapter;
import com.leon.counter_reading.databinding.FragmentPossibleBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.NotificationType;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.tables.CounterReportDto;
import com.leon.counter_reading.tables.KarbariDto;
import com.leon.counter_reading.tables.OffLoadReport;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.utils.DifferentCompanyManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class PossibleFragment extends DialogFragment {
    static boolean justMobile = false;
    private FragmentPossibleBinding binding;
    private OnOffLoadDto onOffLoadDto;
    private int position;
    private Activity activity;
    private ISharedPreferenceManager sharedPreferenceManager;
    private ArrayList<KarbariDto> karbariDtos = new ArrayList<>();
    private ArrayList<CounterReportDto> counterReportDtos;

    public PossibleFragment() {
    }

    public static PossibleFragment newInstance(OnOffLoadDto onOffLoadDto, int position, boolean justMobile) {
        PossibleFragment.justMobile = justMobile;
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
        binding = FragmentPossibleBinding.inflate(inflater, container, false);
        activity = getActivity();
        initialize();
        return binding.getRoot();
    }

    void initialize() {
        makeRing(activity, NotificationType.OTHER);
        sharedPreferenceManager = MyApplication.getApplicationComponent().SharedPreferenceModel();
        if (justMobile) {
            binding.linearLayoutMobile.setVisibility(View.VISIBLE);
            binding.editTextMobile.setVisibility(View.VISIBLE);
            binding.textViewMobile.setVisibility(View.VISIBLE);

            binding.textViewMobile.setText(String.valueOf(onOffLoadDto.mobile));
            binding.editTextMobile.setText(onOffLoadDto.possibleMobile);


            binding.editTextSerial.setVisibility(View.GONE);
            binding.editTextAddress.setVisibility(View.GONE);
            binding.editTextAccount.setVisibility(View.GONE);
            binding.editTextAhadEmpty.setVisibility(View.GONE);
            binding.editTextDescription.setVisibility(View.GONE);
            binding.linearLayoutAhad.setVisibility(View.GONE);

            binding.editTextAhad1.setVisibility(View.GONE);
            binding.editTextAhad2.setVisibility(View.GONE);
            binding.editTextAhadTotal.setVisibility(View.GONE);

            binding.linearLayoutReadingReport.setVisibility(View.GONE);
            binding.linearLayoutKarbari.setVisibility(View.GONE);

        } else
            initializeTextViews();
        setOnButtonsClickListener();
    }


    void initializeTextViews() {
        //TODO
        binding.editTextAccount.setFilters(
                new InputFilter[]{
                        new InputFilter.LengthFilter(DifferentCompanyManager.
                                getEshterakMaxLength(DifferentCompanyManager.getActiveCompanyName()))});

        binding.textViewAhad1Title.setText(DifferentCompanyManager.getAhad1(DifferentCompanyManager.getActiveCompanyName()).concat(":"));
        binding.textViewAhad2Title.setText(DifferentCompanyManager.getAhad2(DifferentCompanyManager.getActiveCompanyName()).replaceFirst("آحاد ", "").concat(":"));
        binding.textViewAhadTotalTitle.setText(DifferentCompanyManager.getAhadTotal(DifferentCompanyManager.getActiveCompanyName()).replaceFirst("آحاد ", "").concat(":"));

        binding.editTextAhadEmpty.setHint(DifferentCompanyManager.getAhad(
                DifferentCompanyManager.getActiveCompanyName()).concat(getString(R.string.empty)));

        binding.editTextAhad1.setHint(DifferentCompanyManager.getAhad1(DifferentCompanyManager.getActiveCompanyName()));
        binding.editTextAhad2.setHint(DifferentCompanyManager.getAhad2(DifferentCompanyManager.getActiveCompanyName()));
        binding.editTextAhadTotal.setHint(DifferentCompanyManager.getAhadTotal(DifferentCompanyManager.getActiveCompanyName()));

        binding.editTextMobile.setText(onOffLoadDto.possibleMobile);
        binding.editTextAddress.setText(onOffLoadDto.possibleAddress);
        binding.editTextAccount.setText(onOffLoadDto.possibleEshterak);
        binding.editTextSerial.setText(onOffLoadDto.possibleCounterSerial);

        binding.editTextAhadEmpty.setText(String.valueOf(onOffLoadDto.possibleEmpty));
        binding.editTextAhad1.setText(String.valueOf(onOffLoadDto.possibleAhadMaskooniOrAsli));
        binding.editTextAhad2.setText(String.valueOf(onOffLoadDto.possibleAhadTejariOrFari));
        binding.editTextAhadTotal.setText(String.valueOf(onOffLoadDto.possibleAhadSaierOrAbBaha));

        binding.editTextDescription.setText(onOffLoadDto.description);


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
        binding.linearLayoutAhad.setVisibility(sharedPreferenceManager.
                getBoolData(SharedReferenceKeys.SHOW_AHAD_TITLE.getValue()) ? View.VISIBLE : View.GONE);

        binding.textViewAhad1.setText(String.valueOf(onOffLoadDto.ahadMaskooniOrAsli));
        binding.textViewAhad2.setText(String.valueOf(onOffLoadDto.ahadTejariOrFari));
        binding.textViewAhadTotal.setText(String.valueOf(onOffLoadDto.ahadSaierOrAbBaha));

        binding.editTextAhad1.setVisibility(sharedPreferenceManager.
                getBoolData(SharedReferenceKeys.AHAD_1.getValue()) ? View.VISIBLE : View.GONE);
        binding.editTextAhad2.setVisibility(sharedPreferenceManager.
                getBoolData(SharedReferenceKeys.AHAD_2.getValue()) ? View.VISIBLE : View.GONE);
        binding.editTextAhadTotal.setVisibility(sharedPreferenceManager.
                getBoolData(SharedReferenceKeys.AHAD_TOTAL.getValue()) ? View.VISIBLE : View.GONE);

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
            karbariDtos = new ArrayList<>(MyApplication.getApplicationComponent().MyDatabase().karbariDao().getAllKarbariDto());
            String[] items1 = new String[karbariDtos.size()];
            for (int i = 0; i < karbariDtos.size(); i++) {
                items1[i] = karbariDtos.get(i).title;
            }
            SpinnerCustomAdapter spinnerCustomAdapterKarbari = new SpinnerCustomAdapter(activity, items1);
            binding.spinnerKarbari.setAdapter(spinnerCustomAdapterKarbari);
            binding.spinnerKarbari.setSelection(onOffLoadDto.counterStatePosition);
        }

        binding.linearLayoutReadingReport.setVisibility(sharedPreferenceManager.
                getBoolData(SharedReferenceKeys.READING_REPORT.getValue()) ? View.VISIBLE : View.GONE);
        if (sharedPreferenceManager.getBoolData(SharedReferenceKeys.READING_REPORT.getValue())) {
            counterReportDtos = new ArrayList<>(MyApplication.getApplicationComponent().MyDatabase()
                    .counterReportDao().getAllCounterStateReport());
            String[] items2 = new String[counterReportDtos.size() + 1];
            for (int i = 0; i < counterReportDtos.size(); i++) {
                items2[i + 1] = counterReportDtos.get(i).title;
            }
            items2[0] = getString(R.string.reports);
            SpinnerCustomAdapter spinnerCustomAdapterReadingReport = new SpinnerCustomAdapter(activity, items2);
            binding.spinnerReadingReport.setAdapter(spinnerCustomAdapterReadingReport);
        }
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
                if (binding.editTextAccount.getText().toString().length() < DifferentCompanyManager.
                        getEshterakMinLength(DifferentCompanyManager.getActiveCompanyName())) {
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

            if (binding.editTextAhadTotal.getText().length() > 0)
                onOffLoadDto.possibleAhadSaierOrAbBaha = Integer.parseInt(binding.editTextAhadTotal.getText().toString());

            if (binding.editTextAhad2.getText().length() > 0)
                onOffLoadDto.possibleAhadTejariOrFari = Integer.parseInt(binding.editTextAhad2.getText().toString());

            if (binding.editTextAhad1.getText().length() > 0)
                onOffLoadDto.possibleAhadMaskooniOrAsli = Integer.parseInt(binding.editTextAhad1.getText().toString());

            if (binding.editTextAhadEmpty.getText().length() > 0)
                onOffLoadDto.possibleEmpty = Integer.parseInt(binding.editTextAhadEmpty.getText().toString());

            if (cancel)
                view.requestFocus();
            else {
                if (sharedPreferenceManager.getBoolData(SharedReferenceKeys.READING_REPORT.getValue())
                        && binding.spinnerReadingReport.getSelectedItemPosition() != 0) {
                    OffLoadReport offLoadReport = new OffLoadReport();
                    offLoadReport.reportId = counterReportDtos.get(binding.spinnerReadingReport.getSelectedItemPosition() - 1).id;
                    offLoadReport.onOffLoadId = onOffLoadDto.id;
                    offLoadReport.trackNumber = onOffLoadDto.trackNumber;
                    MyApplication.getApplicationComponent().MyDatabase()
                            .offLoadReportDao().insertOffLoadReport(offLoadReport);
                }
                ((ReadingActivity) activity).updateOnOffLoadByNavigation(position, onOffLoadDto, justMobile);
                dismiss();

            }
        });
        binding.buttonClose.setOnClickListener(v -> dismiss());
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