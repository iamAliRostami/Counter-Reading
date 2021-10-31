package com.leon.counter_reading.fragments;

import static com.leon.counter_reading.helpers.Constants.FOCUS_ON_EDIT_TEXT;
import static com.leon.counter_reading.utils.MakeNotification.makeRing;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.adapters.SpinnerCustomAdapter;
import com.leon.counter_reading.databinding.FragmentReadingBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.HighLowStateEnum;
import com.leon.counter_reading.enums.NotificationType;
import com.leon.counter_reading.tables.CounterStateDto;
import com.leon.counter_reading.tables.KarbariDto;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.tables.ReadingConfigDefaultDto;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.DifferentCompanyManager;
import com.leon.counter_reading.utils.PermissionManager;
import com.leon.counter_reading.utils.reading.Counting;
import com.leon.counter_reading.utils.reading.UpdateOnOffLoadByAttemptNumber;
import com.leon.counter_reading.utils.reading.UpdateOnOffLoadByIsShown;
import com.leon.counter_reading.utils.reading.UpdateOnOffLoadDtoByLock;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ReadingFragment extends Fragment {
    private static SpinnerCustomAdapter adapter;
    private FragmentReadingBinding binding;
    private ArrayList<CounterStateDto> counterStateDtos = new ArrayList<>();
    private OnOffLoadDto onOffLoadDto;
    private ReadingConfigDefaultDto readingConfigDefaultDto;
    private KarbariDto karbariDto;
    private int position, counterStateCode, counterStatePosition;
    private boolean canBeEmpty, canLessThanPre, isMakoos, isMane;
    private Activity activity;

    public static ReadingFragment newInstance(
            OnOffLoadDto onOffLoadDto,
            ReadingConfigDefaultDto readingConfigDefaultDto,
            KarbariDto karbariDto,
            ArrayList<CounterStateDto> counterStateDtos,
            SpinnerCustomAdapter adapter,
            int position) {
        ReadingFragment fragment = new ReadingFragment();
        fragment.setArguments(putBundle(onOffLoadDto, readingConfigDefaultDto, karbariDto,
                counterStateDtos, adapter, position));
        return fragment;
    }

    private static Bundle putBundle(OnOffLoadDto onOffLoadDto,
                                    ReadingConfigDefaultDto readingConfigDefaultDto,
                                    KarbariDto karbariDto,
                                    ArrayList<CounterStateDto> counterStateDtos,
                                    SpinnerCustomAdapter adapterTemp,
                                    int position) {
        Bundle args = new Bundle();
        Gson gson = new Gson();
        String json1 = gson.toJson(onOffLoadDto);
        args.putString(BundleEnum.ON_OFF_LOAD.getValue(), json1);
        String json2 = gson.toJson(readingConfigDefaultDto);
        args.putString(BundleEnum.READING_CONFIG.getValue(), json2);
        String json3 = gson.toJson(karbariDto);
        args.putString(BundleEnum.KARBARI_DICTONARY.getValue(), json3);

        ArrayList<String> json7 = new ArrayList<>();
        for (int i = 0, counterStateDtosSize = counterStateDtos.size(); i < counterStateDtosSize; i++) {
            CounterStateDto s = counterStateDtos.get(i);
            String json = gson.toJson(s);
            json7.add(json);
        }
        args.putStringArrayList(BundleEnum.COUNTER_STATE.getValue(), json7);
        adapter = adapterTemp;
        args.putInt(BundleEnum.POSITION.getValue(), position);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        getBundle();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReadingBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        binding.editTextNumber.setOnLongClickListener(view -> {
            binding.editTextNumber.setText("");
            return false;
        });
        if (onOffLoadDto.isLocked) {
            new CustomToast().error(getString(R.string.by_mistakes).concat(onOffLoadDto.eshterak).
                    concat(getString(R.string.is_locked)), Toast.LENGTH_SHORT);
            binding.editTextNumber.setFocusable(false);
            binding.editTextNumber.setEnabled(false);
        }
        initializeViews();
        initializeSpinner();
        onButtonSubmitClickListener();
    }

    private void initializeViews() {
        binding.textViewAhad1Title.setText(DifferentCompanyManager.getAhad1(
                DifferentCompanyManager.getActiveCompanyName()).concat(" : "));
        binding.textViewAhad2Title.setText(DifferentCompanyManager.getAhad2(
                DifferentCompanyManager.getActiveCompanyName()).concat(" : "));
        binding.textViewAhadTotalTitle.setText(DifferentCompanyManager.getAhadTotal(
                DifferentCompanyManager.getActiveCompanyName()).concat(" : "));
        binding.textViewAddress.setText(onOffLoadDto.address);
        binding.textViewName.setText(onOffLoadDto.firstName.concat(" ")
                .concat(onOffLoadDto.sureName));
        binding.textViewPreDate.setText(onOffLoadDto.preDate);
        binding.textViewSerial.setText(onOffLoadDto.counterSerial);

        if (onOffLoadDto.displayRadif)
            binding.textViewRadif.setText(String.valueOf(onOffLoadDto.radif));
        else if (onOffLoadDto.displayBillId)
            binding.textViewRadif.setText(String.valueOf(onOffLoadDto.billId));
        else binding.textViewRadif.setVisibility(View.GONE);

        binding.textViewAhad1.setText(String.valueOf(onOffLoadDto.ahadMaskooniOrAsli));

        if (onOffLoadDto.counterNumber != null)
            binding.editTextNumber.setText(String.valueOf(onOffLoadDto.counterNumber));
        binding.textViewAhad2.setText(String.valueOf(onOffLoadDto.ahadTejariOrFari));
        binding.textViewAhadTotal.setText(String.valueOf(onOffLoadDto.ahadSaierOrAbBaha));

        if (readingConfigDefaultDto.isOnQeraatCode) {
            binding.textViewCode.setText(onOffLoadDto.qeraatCode);
        } else binding.textViewCode.setText(onOffLoadDto.eshterak);

        binding.textViewKarbari.setText(karbariDto.title);
        binding.textViewBranch.setText(onOffLoadDto.qotr.equals("مشخص نشده") ? "-" : onOffLoadDto.qotr);
        binding.textViewSiphon.setText(onOffLoadDto.sifoonQotr.equals("مشخص نشده") ? "-" : onOffLoadDto.sifoonQotr);

        binding.textViewPreNumber.setOnClickListener(v -> {
            if (onOffLoadDto.hasPreNumber) {
                activity.runOnUiThread(() ->
                        binding.textViewPreNumber.setText(String.valueOf(onOffLoadDto.preNumber)));
                new UpdateOnOffLoadByIsShown(position).execute(activity);
            } else {
                new CustomToast().warning(getString(R.string.can_not_show_pre));
            }
        });
        binding.textViewAddress.setOnLongClickListener(v -> {
            PossibleFragment possibleFragment = PossibleFragment.newInstance(onOffLoadDto,
                    position, true);
            possibleFragment.show(getChildFragmentManager(), getString(R.string.dynamic_navigation));
            return false;
        });
    }

    private void initializeSpinner() {
        binding.spinner.setAdapter(adapter);
        boolean found = false;
        int i;
        for (i = 0; i < counterStateDtos.size() && !found; i++)
            if (counterStateDtos.get(i).id == onOffLoadDto.counterStateId) {
                found = true;
            }
        binding.spinner.setSelection(found ? i - 1 : 0);
        setOnSpinnerSelectedListener();
    }

    private void setOnSpinnerSelectedListener() {
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                counterStatePosition = i;
                counterStateCode = counterStateDtos.get(counterStatePosition).id;
                CounterStateDto counterStateDto = counterStateDtos.get(counterStatePosition);
                binding.editTextNumber.setEnabled(counterStateDto.canEnterNumber
                        || counterStateDto.shouldEnterNumber);
                //TODO
                if (!(counterStateDto.canEnterNumber || counterStateDto.shouldEnterNumber))
                    binding.editTextNumber.setText("");
                isMane = counterStateDto.isMane;
                canBeEmpty = !counterStateDto.shouldEnterNumber;
                canLessThanPre = counterStateDto.canNumberBeLessThanPre;
                isMakoos = counterStateDto.title.equals("معکوس");
//                if (onOffLoadDto.counterStatePosition == null ||
//                        onOffLoadDto.counterStatePosition != binding.spinner.getSelectedItemPosition()) {
//                    if ((counterStateDto.isTavizi || counterStateDto.isXarab) &&
//                            counterStateDto.moshtarakinId != onOffLoadDto.preCounterStateCode) {
//                        SerialFragment serialFragment = SerialFragment.newInstance(position,
//                                counterStateDto.id, counterStatePosition);
//                        serialFragment.show(getChildFragmentManager(), getString(R.string.counter_serial));
//                    }
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void onButtonSubmitClickListener() {
        binding.buttonSubmit.setOnClickListener(v -> checkPermissions());
    }

    private void askLocationPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                new CustomToast().info(getString(R.string.access_granted));
                checkPermissions();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                new CustomToast().warning("به علت عدم دسترسی به مکان یابی، امکان ثبت وجود ندارد.");
            }
        };
        new TedPermission(activity)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage(getString(R.string.confirm_permission))
                .setRationaleConfirmText(getString(R.string.allow_permission))
                .setDeniedMessage(getString(R.string.if_reject_permission))
                .setDeniedCloseButtonText(getString(R.string.close))
                .setGotoSettingButtonText(getString(R.string.allow_permission))
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION).check();
    }

    private void askStoragePermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                new CustomToast().info(getString(R.string.access_granted));
                checkPermissions();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                PermissionManager.forceClose(activity);
            }
        };
        new TedPermission(activity)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage(getString(R.string.confirm_permission))
                .setRationaleConfirmText(getString(R.string.allow_permission))
                .setDeniedMessage(getString(R.string.if_reject_permission))
                .setDeniedCloseButtonText(getString(R.string.close))
                .setGotoSettingButtonText(getString(R.string.allow_permission))
                .setPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE).check();
    }

    private void checkPermissions() {
        if (PermissionManager.gpsEnabledNew(activity))
            if (PermissionManager.checkLocationPermission(getContext())) {
                askLocationPermission();
            } else if (PermissionManager.checkStoragePermission(getContext())) {
                askStoragePermission();
            } else {
                //TODO
                onOffLoadDto.attemptCount++;
                if (!onOffLoadDto.isLocked && onOffLoadDto.attemptCount + 1 == DifferentCompanyManager.getLockNumber(DifferentCompanyManager.getActiveCompanyName()))
                    new CustomToast().warning(getString(R.string.mistakes_error), Toast.LENGTH_LONG);
                if (!onOffLoadDto.isLocked && onOffLoadDto.attemptCount == DifferentCompanyManager.getLockNumber(DifferentCompanyManager.getActiveCompanyName()))
                    new CustomToast().error(getString(R.string.by_mistakes).
                            concat(onOffLoadDto.eshterak).concat(getString(R.string.is_locked)), Toast.LENGTH_LONG);
                new UpdateOnOffLoadByAttemptNumber(position, onOffLoadDto.attemptCount).execute(activity);
                if (!onOffLoadDto.isLocked && onOffLoadDto.attemptCount >= DifferentCompanyManager.getLockNumber(DifferentCompanyManager.getActiveCompanyName())) {
                    new UpdateOnOffLoadDtoByLock(position, onOffLoadDto.trackNumber, onOffLoadDto.id).execute(activity);
                } else {
                    attemptSend();
                }
            }
    }

    public void attemptSend() {
        if (canBeEmpty) {
            canBeEmpty();
        } else {
            canNotBeEmpty();
        }
    }

    private void canBeEmpty() {//TODO
        if (binding.editTextNumber.getText().toString().isEmpty() || isMane) {
            ((ReadingActivity) activity).updateOnOffLoadWithoutCounterNumber(position,
                    counterStateCode, counterStatePosition);
        } else {
            View view = binding.editTextNumber;
            int currentNumber = Integer.parseInt(binding.editTextNumber.getText().toString());
            int use = currentNumber - onOffLoadDto.preNumber;
            if (canLessThanPre) {
                lessThanPre(currentNumber);
            } else if (use < 0) {
                makeRing(activity, NotificationType.NOT_SAVE);
                binding.editTextNumber.setError(getString(R.string.less_than_pre));
                view.requestFocus();
            }
        }
    }

    private void canNotBeEmpty() {
        View view = binding.editTextNumber;
        if (binding.editTextNumber.getText().toString().isEmpty()) {
            makeRing(activity, NotificationType.NOT_SAVE);
            binding.editTextNumber.setError(getString(R.string.counter_empty));
            view.requestFocus();
        } else {
            int currentNumber = Integer.parseInt(binding.editTextNumber.getText().toString());
            int use = currentNumber - onOffLoadDto.preNumber;
            if (canLessThanPre) {
                lessThanPre(currentNumber);
            } else if (use < 0) {
                makeRing(activity, NotificationType.NOT_SAVE);
                binding.editTextNumber.setError(getString(R.string.less_than_pre));
                view.requestFocus();
            } else {
                notEmpty(currentNumber);
            }
        }
    }

    private void lessThanPre(int currentNumber) {
        if (!isMakoos)
            ((ReadingActivity) activity).updateOnOffLoadByCounterNumber(position, currentNumber,
                    counterStateCode, counterStatePosition);
        else {
            notEmptyIsMakoos(currentNumber);
        }
    }

    private void notEmptyIsMakoos(int currentNumber) {
        FragmentTransaction fragmentTransaction = requireActivity().
                getSupportFragmentManager().beginTransaction();
        AreYouSureFragment areYouSureFragment;
        if (currentNumber == onOffLoadDto.preNumber) {
            areYouSureFragment = AreYouSureFragment.newInstance(
                    position, currentNumber, HighLowStateEnum.ZERO.getValue(),
                    counterStateCode, counterStatePosition);
            areYouSureFragment.show(fragmentTransaction, getString(R.string.use_out_of_range));
        } else {
            int status = Counting.checkHighLowMakoos(onOffLoadDto, karbariDto, readingConfigDefaultDto,
                    currentNumber);
            switch (status) {
                case 1:
                    areYouSureFragment = AreYouSureFragment.newInstance(
                            position, currentNumber, HighLowStateEnum.HIGH.getValue(),
                            counterStateCode, counterStatePosition);
                    areYouSureFragment.show(fragmentTransaction, getString(R.string.use_out_of_range));
                    break;
                case -1:
                    areYouSureFragment = AreYouSureFragment.newInstance(
                            position, currentNumber, HighLowStateEnum.LOW.getValue(),
                            counterStateCode, counterStatePosition);
                    areYouSureFragment.show(fragmentTransaction, getString(R.string.use_out_of_range));
                    break;
                case 0:
                    ((ReadingActivity) activity).updateOnOffLoadByCounterNumber(position,
                            currentNumber, counterStateCode, counterStatePosition,
                            HighLowStateEnum.NORMAL.getValue());
                    break;
            }
        }
    }

    private void notEmpty(int currentNumber) {
        FragmentTransaction fragmentTransaction = requireActivity().
                getSupportFragmentManager().beginTransaction();
        AreYouSureFragment areYouSureFragment;
        if (currentNumber == onOffLoadDto.preNumber) {
            areYouSureFragment = AreYouSureFragment.newInstance(
                    position, currentNumber, HighLowStateEnum.ZERO.getValue(),
                    counterStateCode, counterStatePosition);
            areYouSureFragment.show(fragmentTransaction, getString(R.string.use_out_of_range));
        } else {
            int status = Counting.checkHighLow(onOffLoadDto, karbariDto, readingConfigDefaultDto,
                    currentNumber);
            switch (status) {
                case 1:
                    areYouSureFragment = AreYouSureFragment.newInstance(
                            position, currentNumber, HighLowStateEnum.HIGH.getValue(),
                            counterStateCode, counterStatePosition);
                    areYouSureFragment.show(fragmentTransaction, getString(R.string.use_out_of_range));
                    break;
                case -1:
                    areYouSureFragment = AreYouSureFragment.newInstance(
                            position, currentNumber, HighLowStateEnum.LOW.getValue(),
                            counterStateCode, counterStatePosition);
                    areYouSureFragment.show(fragmentTransaction, getString(R.string.use_out_of_range));
                    break;
                case 0:
                    ((ReadingActivity) activity).updateOnOffLoadByCounterNumber(position,
                            currentNumber, counterStateCode, counterStatePosition,
                            HighLowStateEnum.NORMAL.getValue());
                    break;
            }
        }
    }

    private void getBundle() {
        if (getArguments() != null) {
            position = getArguments().getInt(BundleEnum.POSITION.getValue());

            Gson gson = new Gson();
            onOffLoadDto = gson.fromJson(getArguments().getString(BundleEnum.ON_OFF_LOAD.getValue()),
                    OnOffLoadDto.class);
            readingConfigDefaultDto = gson.fromJson(getArguments()
                            .getString(BundleEnum.READING_CONFIG.getValue()),
                    ReadingConfigDefaultDto.class);
            karbariDto = gson.fromJson(getArguments()
                    .getString(BundleEnum.KARBARI_DICTONARY.getValue()), KarbariDto.class);
            ArrayList<String> json1 = getArguments()
                    .getStringArrayList(BundleEnum.COUNTER_STATE.getValue());
            counterStateDtos.clear();
            for (String s : json1) {
                counterStateDtos.add(gson.fromJson(s, CounterStateDto.class));
            }
//            getArguments().clear();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (FOCUS_ON_EDIT_TEXT) {
            View viewFocus = binding.editTextNumber;
            viewFocus.requestFocus();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        karbariDto = null;
        readingConfigDefaultDto = null;
        counterStateDtos = null;
    }
}