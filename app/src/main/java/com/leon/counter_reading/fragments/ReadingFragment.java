package com.leon.counter_reading.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
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
import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.databinding.FragmentReadingBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.HighLowStateEnum;
import com.leon.counter_reading.enums.NotificationType;
import com.leon.counter_reading.enums.SharedReferenceNames;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.tables.CounterStateDto;
import com.leon.counter_reading.tables.KarbariDto;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.tables.ReadingConfigDefaultDto;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.utils.Counting;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.DifferentCompanyManager;
import com.leon.counter_reading.utils.PermissionManager;
import com.leon.counter_reading.utils.SharedPreferenceManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.leon.counter_reading.MyApplication.LOCK_NUMBER;
import static com.leon.counter_reading.utils.MakeNotification.makeRing;

public class ReadingFragment extends Fragment {
    FragmentReadingBinding binding;
    ISharedPreferenceManager sharedPreferenceManager;

    ArrayList<CounterStateDto> counterStateDtos = new ArrayList<>();
    OnOffLoadDto onOffLoadDto;
    ReadingConfigDefaultDto readingConfigDefaultDto;
    KarbariDto karbariDto;
    TrackingDto trackingDto;

    String qotr;
    int position, counterStateCode, counterStatePosition;
    boolean canBeEmpty, canLessThanPre, isMakoos, isMane;

    Activity activity;

    public ReadingFragment() {
    }

    public static ReadingFragment newInstance(
            /*OnOffLoadDto onOffLoadDto,*/
            ReadingConfigDefaultDto readingConfigDefaultDto,
            KarbariDto karbariDto,
            TrackingDto trackingDto,
            String qotr,
            /*ArrayList<String> items,
            ArrayList<CounterStateDto> counterStateDtos,*/
            int position) {
        ReadingFragment fragment = new ReadingFragment();
//        Bundle bundle = new Bundle();
//        bundle.putInt(BundleEnum.POSITION.getValue(), position);
//        fragment.setArguments(bundle);
        fragment.setArguments(putBundle(/*onOffLoadDto,*/ readingConfigDefaultDto, karbariDto,
                trackingDto, qotr/*, items, counterStateDtos*/, position));
        return fragment;
    }

    static Bundle putBundle(/*OnOffLoadDto onOffLoadDto,*/
            ReadingConfigDefaultDto readingConfigDefaultDto,
            KarbariDto karbariDto,
            TrackingDto trackingDto,
            String qotr,
                            /*ArrayList<String> items,
                            ArrayList<CounterStateDto> counterStateDtos,*/
            int position) {
        Bundle args = new Bundle();
        Gson gson = new Gson();
//        String json1 = gson.toJson(onOffLoadDto);
//        args.putString(BundleEnum.ON_OFF_LOAD.getValue(), json1);
        String json2 = gson.toJson(readingConfigDefaultDto);
        args.putString(BundleEnum.READING_CONFIG.getValue(), json2);
        String json3 = gson.toJson(karbariDto);
        args.putString(BundleEnum.KARBARI_DICTONARY.getValue(), json3);

        String json4 = gson.toJson(trackingDto);
        args.putString(BundleEnum.TRACKING.getValue(), json4);

        args.putString(BundleEnum.QOTR_DICTIONARY.getValue(), qotr);

//        ArrayList<String> json6 = new ArrayList<>();
//        for (String s : items) {
//            String json = gson.toJson(s);
//            json6.add(json);
//        }
//        args.putStringArrayList(BundleEnum.Item.getValue(), json6);

//        ArrayList<String> json7 = new ArrayList<>();
//        for (CounterStateDto s : counterStateDtos) {
//            String json = gson.toJson(s);
//            json7.add(json);
//        }
//        args.putStringArrayList(BundleEnum.COUNTER_STATE.getValue(), json7);
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

    void initialize() {
        sharedPreferenceManager = new SharedPreferenceManager(activity, SharedReferenceNames.ACCOUNT.getValue());
        binding.editTextNumber.setOnLongClickListener(view -> {
            binding.editTextNumber.setText("");
            return false;
        });
        if (onOffLoadDto.isLocked) {
            new CustomToast().error(getString(R.string.by_mistakes).concat(onOffLoadDto.eshterak).concat(getString(R.string.is_locked)), Toast.LENGTH_LONG);
            binding.editTextNumber.setFocusable(false);
            binding.editTextNumber.setEnabled(false);
        }
        initializeViews();
        initializeSpinner();
        onButtonSubmitClickListener();
    }

    void initializeViews() {
        binding.textViewAhad1Title.setText(DifferentCompanyManager.getAhad1(DifferentCompanyManager.getActiveCompanyName()).concat(" : "));
        binding.textViewAhad2Title.setText(DifferentCompanyManager.getAhad2(DifferentCompanyManager.getActiveCompanyName()).concat(" : "));
        binding.textViewAhadTotalTitle.setText(DifferentCompanyManager.getAhadTotal(DifferentCompanyManager.getActiveCompanyName()).concat(" : "));
        binding.textViewAddress.setText(onOffLoadDto.address);
        binding.textViewName.setText(onOffLoadDto.firstName.concat(" ")
                .concat(onOffLoadDto.sureName));
        binding.textViewPreDate.setText(onOffLoadDto.preDate);
        binding.textViewSerial.setText(onOffLoadDto.counterSerial);

//        if (readingConfigDefaultDto.displayRadif)
        if (trackingDto.displayRadif)
            binding.textViewRadif.setText(String.valueOf(onOffLoadDto.radif));

//        if (readingConfigDefaultDto.displayBillId)
        if (trackingDto.displayBillId)
            binding.textViewRadif.setText(String.valueOf(onOffLoadDto.billId));

        binding.textViewAhad1.setText(String.valueOf(onOffLoadDto.ahadMaskooniOrAsli));

        if (onOffLoadDto.counterNumber != 0)
            binding.editTextNumber.setText(String.valueOf(onOffLoadDto.counterNumber));
        binding.textViewAhad2.setText(String.valueOf(onOffLoadDto.ahadTejariOrFari));
        binding.textViewAhadTotal.setText(String.valueOf(onOffLoadDto.ahadSaierOrAbBaha));

        if (readingConfigDefaultDto.isOnQeraatCode) {
            binding.textViewCode.setText(onOffLoadDto.qeraatCode);
        } else binding.textViewCode.setText(onOffLoadDto.eshterak);

        binding.textViewKarbari.setText(karbariDto.title);
        binding.textViewBranch.setText(qotr);

//        if (readingConfigDefaultDto.defaultHasPreNumber)
//        if (trackingDto.hasPreNumber)
//            binding.textViewPreNumber.setText(String.valueOf(onOffLoadDto.preNumber));
        binding.textViewPreNumber.setOnClickListener(v -> {
            if (trackingDto.hasPreNumber) {
                activity.runOnUiThread(() -> binding.textViewPreNumber.setText(String.valueOf(onOffLoadDto.preNumber)));
//            if (!readingConfigDefaultDto.defaultHasPreNumber)
                if (trackingDto.hasPreNumber)
                    ((ReadingActivity) activity).updateOnOffLoadByIsShown(position);
            } else {
                new CustomToast().warning(getString(R.string.can_not_show_pre));
            }
        });
    }

    void initializeSpinner() {
//        adapter = new SpinnerCustomAdapter(activity, items);
        binding.spinner.setAdapter(((ReadingActivity) activity).getAdapter());
        if (onOffLoadDto.counterStatePosition != null)
            binding.spinner.setSelection(onOffLoadDto.counterStatePosition);
        else {
            for (int i = 0; i < counterStateDtos.size(); i++)
                if (counterStateDtos.get(i).id == onOffLoadDto.preCounterStateCode)
                    binding.spinner.setSelection(i);
        }
        setOnSpinnerSelectedListener();
    }

    void setOnSpinnerSelectedListener() {
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                counterStatePosition = i;
                counterStateCode = counterStateDtos.get(counterStatePosition).id;
                CounterStateDto counterStateDto = counterStateDtos.get(counterStatePosition);
                binding.editTextNumber.setEnabled(counterStateDto.canEnterNumber
                        || counterStateDto.shouldEnterNumber);
                isMane = counterStateDto.isMane;
                canBeEmpty = !counterStateDto.shouldEnterNumber;
                canLessThanPre = counterStateDto.canNumberBeLessThanPre;
                isMakoos = counterStateDto.title.equals("معکوس");
                if (onOffLoadDto.counterStatePosition == null ||
                        onOffLoadDto.counterStatePosition != binding.spinner.getSelectedItemPosition()) {
                    if ((counterStateDto.isTavizi || counterStateDto.isXarab) &&
                            counterStateDto.id != onOffLoadDto.preCounterStateCode) {
                        SerialFragment serialFragment = SerialFragment.newInstance(position,
                                counterStateDto.id, counterStatePosition);
                        serialFragment.show(getChildFragmentManager(), getString(R.string.counter_serial));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    void askLocationPermission() {
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
                .setPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ).check();
    }

    void checkPermissions() {
        if (PermissionManager.gpsEnabledNew(activity))
            if (PermissionManager.checkLocationPermission(getContext())) {
                askLocationPermission();
                //TODO
            } else if (PermissionManager.checkStoragePermission(getContext())) {
                askStoragePermission();
            } else {
                //TODO
                onOffLoadDto.attemptNumber++;
                if (!onOffLoadDto.isLocked && onOffLoadDto.attemptNumber + 1 == LOCK_NUMBER)
                    new CustomToast().warning(getString(R.string.mistakes_error), Toast.LENGTH_LONG);
                if (!onOffLoadDto.isLocked && onOffLoadDto.attemptNumber == LOCK_NUMBER)
                    new CustomToast().error(getString(R.string.by_mistakes).concat(onOffLoadDto.eshterak).concat(getString(R.string.is_locked)), Toast.LENGTH_LONG);
//                MyDatabaseClient.getInstance(activity).getMyDatabase().onOffLoadDao().updateOnOffLoadByAttemptNumber(onOffLoadDto.id, onOffLoadDto.attemptNumber);
                ((ReadingActivity) activity).updateOnOffLoadAttemptNumber(position, onOffLoadDto.attemptNumber);
                if (!onOffLoadDto.isLocked && onOffLoadDto.attemptNumber == LOCK_NUMBER) {
                    ((ReadingActivity) activity).updateTrackingDto(onOffLoadDto.id, onOffLoadDto.trackNumber, position);
                    Intent intent = activity.getIntent();
                    activity.finish();
                    startActivity(intent);
                } else /*if (!onOffLoadDto.isLocked)*/ {
                    attemptSend();
                }
            }
    }

    void askStoragePermission() {
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
                .setPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).check();
    }

    void onButtonSubmitClickListener() {
        binding.buttonSubmit.setOnClickListener(v -> checkPermissions());
    }

    public void attemptSend() {
        if (canBeEmpty) {
            canBeEmpty();
        } else {
            canNotBeEmpty();
        }
    }

    void canBeEmpty() {//TODO
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

    void canNotBeEmpty() {
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

    void lessThanPre(int currentNumber) {
        if (!isMakoos)
            ((ReadingActivity) activity).updateOnOffLoadByCounterNumber(position, currentNumber,
                    counterStateCode, counterStatePosition);
        else {
            notEmptyIsMakoos(currentNumber);
        }
    }

    void notEmptyIsMakoos(int currentNumber) {
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

    void notEmpty(int currentNumber) {
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

    void getBundle() {
//        if ((ReadingActivity) activity == null)
//            Log.e("activity", "null");
        if (getArguments() != null) {
            position = getArguments().getInt(BundleEnum.POSITION.getValue());
//            items = ((ReadingActivity) activity).getItems();
//            adapter = ((ReadingActivity) activity).getAdapter();

            Gson gson = new Gson();
//            onOffLoadDto = gson.fromJson(getArguments().getString(
//                    BundleEnum.ON_OFF_LOAD.getValue()), OnOffLoadDto.class);
            readingConfigDefaultDto = gson.fromJson(getArguments().getString(
                    BundleEnum.READING_CONFIG.getValue()),
                    ReadingConfigDefaultDto.class);
            karbariDto = gson.fromJson(getArguments().getString(
                    BundleEnum.KARBARI_DICTONARY.getValue()),
                    KarbariDto.class);
            trackingDto = gson.fromJson(getArguments().getString(
                    BundleEnum.TRACKING.getValue()),
                    TrackingDto.class);
            qotr = getArguments().getString(BundleEnum.QOTR_DICTIONARY.getValue());
//            ArrayList<String> json2 = getArguments().getStringArrayList(
//                    BundleEnum.Item.getValue());
//            for (String s : json2) {
//                items.add(gson.fromJson(s, String.class));
//            }
//            ArrayList<String> json1 = getArguments().getStringArrayList(
//                    BundleEnum.COUNTER_STATE.getValue());
//            counterStateDtos.clear();
//            for (String s : json1) {
//                counterStateDtos.add(gson.fromJson(s, CounterStateDto.class));
//            }
            onOffLoadDto = ((ReadingActivity) activity).getReadingData().onOffLoadDtos.get(position);
            counterStateDtos = ((ReadingActivity) activity).getReadingData().counterStateDtos;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MyApplication.FOCUS_ON_EDIT_TEXT) {
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
//        karbariDto = null;
//        trackingDto = null;
//        readingConfigDefaultDto = null;
//        counterStateDtos = null;
    }
}