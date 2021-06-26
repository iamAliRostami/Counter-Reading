package com.leon.counter_reading.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Debug;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.adapters.SpinnerCustomAdapter;
import com.leon.counter_reading.adapters.ViewPagerAdapterReading;
import com.leon.counter_reading.base_items.BaseActivity;
import com.leon.counter_reading.databinding.ActivityReadingBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.DialogType;
import com.leon.counter_reading.enums.NotificationType;
import com.leon.counter_reading.enums.OffloadStateEnum;
import com.leon.counter_reading.enums.ProgressType;
import com.leon.counter_reading.enums.ReadStatusEnum;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.enums.SharedReferenceNames;
import com.leon.counter_reading.fragments.PossibleFragment;
import com.leon.counter_reading.fragments.SearchFragment;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.infrastructure.IFlashLightManager;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.tables.ReadingData;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.utils.CustomDialog;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomProgressBar;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.DepthPageTransformer;
import com.leon.counter_reading.utils.FlashLightManager;
import com.leon.counter_reading.utils.HttpClientWrapper;
import com.leon.counter_reading.utils.MyDatabase;
import com.leon.counter_reading.utils.MyDatabaseClient;
import com.leon.counter_reading.utils.NetworkHelper;
import com.leon.counter_reading.utils.PermissionManager;
import com.leon.counter_reading.utils.SharedPreferenceManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.leon.counter_reading.MyApplication.SHOW_ERROR;
import static com.leon.counter_reading.utils.MakeNotification.makeRing;
import static com.leon.counter_reading.utils.PermissionManager.isNetworkAvailable;

public class ReadingActivity extends BaseActivity {
    final int[] imageSrc = new int[15];
    ActivityReadingBinding binding;
    Activity activity;
    IFlashLightManager flashLightManager;
    ReadingData readingData, readingDataTemp;
    ViewPagerAdapterReading viewPagerAdapterReading;
    ISharedPreferenceManager sharedPreferenceManager;
    ArrayList<Integer> isMane = new ArrayList<>();
    SpinnerCustomAdapter adapter;
    int readStatus = 0, highLow = 1, errorCounter = 0;
    boolean isNight = false, isReading = false;

    @Override
    protected void initialize() {
        binding = ActivityReadingBinding.inflate(getLayoutInflater());
        View childLayout = binding.getRoot();
        ConstraintLayout parentLayout = findViewById(R.id.base_Content);
        parentLayout.addView(childLayout);
        activity = this;
        sharedPreferenceManager = new SharedPreferenceManager(activity, SharedReferenceNames.ACCOUNT.getValue());
        if (MyApplication.POSITION == 1) {
            if (isNetworkAvailable(activity))
                checkPermissions();
            else PermissionManager.enableNetwork(activity);
        }
    }

    void changePage() {
        if (binding.viewPager.getCurrentItem() + 1 < readingData.onOffLoadDtos.size())
            binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem() + 1);
        else {
            new CustomToast().success(getString(R.string.all_masir_bazdid));
            binding.viewPager.setCurrentItem(0);
        }
    }

    public void updateOnOffLoadAttemptNumber(int position, int attemptNumber) {
        MyDatabaseClient.getInstance(activity).getMyDatabase().onOffLoadDao().
                updateOnOffLoadByAttemptNumber(readingData.onOffLoadDtos.get(position).id, attemptNumber);
        readingData.onOffLoadDtos.get(position).attemptNumber = attemptNumber;
    }

    public void updateTrackingDto(int trackNumber) {
        for (int i = 0; i < readingData.trackingDtos.size(); i++) {
            if (readingData.trackingDtos.get(i).trackNumber == trackNumber) {
                readingData.trackingDtos.get(i).isLocked = true;
            }
        }
        for (int i = 0; i < readingDataTemp.trackingDtos.size(); i++) {
            if (readingDataTemp.trackingDtos.get(i).trackNumber == trackNumber) {
                readingDataTemp.trackingDtos.get(i).isLocked = true;
            }
        }
        for (int i = 0; i < readingDataTemp.onOffLoadDtos.size(); i++) {
            if (readingDataTemp.onOffLoadDtos.get(i).trackNumber == trackNumber)
                readingDataTemp.onOffLoadDtos.get(i).isLocked = true;
        }
        for (int i = 0; i < readingData.onOffLoadDtos.size(); i++) {
            if (readingData.onOffLoadDtos.get(i).trackNumber == trackNumber)
                readingData.onOffLoadDtos.get(i).isLocked = true;
        }
        MyDatabaseClient.getInstance(activity).getMyDatabase().trackingDao().updateTrackingDtoByLock(trackNumber, true);
        MyDatabaseClient.getInstance(activity).getMyDatabase().onOffLoadDao().updateOnOffLoadByLock(trackNumber, true);
    }

    public void updateOnOffLoadByIsShown(int position) {
        readingData.onOffLoadDtos.get(position).isBazdid = true;
        readingData.onOffLoadDtos.get(position).counterNumberShown = true;
        MyDatabaseClient.getInstance(activity).getMyDatabase().onOffLoadDao().
                updateOnOffLoad(readingData.onOffLoadDtos.get(position));
    }

    public void updateOnOffLoad(int position, int counterStateCode, int counterStatePosition) {
        readingData.onOffLoadDtos.get(position).isBazdid = true;
        readingData.onOffLoadDtos.get(position).offLoadStateId = OffloadStateEnum.INSERTED.getValue();
        readingData.onOffLoadDtos.get(position).counterStatePosition = counterStatePosition;
        readingData.onOffLoadDtos.get(position).counterStateId = counterStateCode;
    }

    public void updateOnOffLoadWithoutCounterNumber(int position, int counterStateCode,
                                                    int counterStatePosition) {
        updateOnOffLoad(position, counterStateCode, counterStatePosition);
        attemptSend(position, true, true);
    }

    public void updateOnOffLoadByCounterSerial(int position, int counterStatePosition,
                                               int counterStateCode, String counterSerial) {
        updateOnOffLoad(position, counterStateCode, counterStatePosition);
        readingData.onOffLoadDtos.get(position).possibleCounterSerial = counterSerial;
    }

    public void updateOnOffLoadByCounterNumber(int position, int number, int counterStateCode,
                                               int counterStatePosition) {
        updateOnOffLoad(position, counterStateCode, counterStatePosition);
        readingData.onOffLoadDtos.get(position).counterNumber = number;
        attemptSend(position, true, true);
    }

    public void updateOnOffLoadByCounterNumber(int position, int number, int counterStateCode,
                                               int counterStatePosition, int type) {
        readingData.onOffLoadDtos.get(position).highLowStateId = type;
        updateOnOffLoadByCounterNumber(position, number, counterStateCode, counterStatePosition);
    }

    public void updateOnOffLoadByNavigation(int position, OnOffLoadDto onOffLoadDto) {
        //TODO
        readingData.onOffLoadDtos.get(position).possibleCounterSerial = onOffLoadDto.possibleCounterSerial;
        readingData.onOffLoadDtos.get(position).possibleKarbariCode = onOffLoadDto.possibleKarbariCode;
        readingData.onOffLoadDtos.get(position).possibleAhadTejariOrFari = onOffLoadDto.possibleAhadTejariOrFari;
        readingData.onOffLoadDtos.get(position).possibleAhadMaskooniOrAsli = onOffLoadDto.possibleAhadMaskooniOrAsli;
        readingData.onOffLoadDtos.get(position).possibleAhadSaierOrAbBaha = onOffLoadDto.possibleAhadSaierOrAbBaha;
        readingData.onOffLoadDtos.get(position).description = onOffLoadDto.description;

        readingData.onOffLoadDtos.get(position).possibleEmpty = onOffLoadDto.possibleEmpty;
        readingData.onOffLoadDtos.get(position).possibleMobile = onOffLoadDto.possibleMobile;
        readingData.onOffLoadDtos.get(position).possibleAddress = onOffLoadDto.possibleAddress;
        readingData.onOffLoadDtos.get(position).possibleEshterak = onOffLoadDto.possibleEshterak;
        attemptSend(position, false, true);
    }

    void update(int position) {
        readingData.onOffLoadDtos.get(position).x =
                ((BaseActivity) activity).getGpsTracker().getLongitude();
        readingData.onOffLoadDtos.get(position).y =
                ((BaseActivity) activity).getGpsTracker().getLatitude();
        readingData.onOffLoadDtos.get(position).gisAccuracy =
                ((BaseActivity) activity).getGpsTracker().getAccuracy();
        MyDatabaseClient.getInstance(activity).getMyDatabase().onOffLoadDao().
                updateOnOffLoad(readingData.onOffLoadDtos.get(position));
    }

    void showImage(int position) {
        Intent intent = new Intent(activity, TakePhotoActivity.class);
        intent.putExtra(BundleEnum.BILL_ID.getValue(),
                readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).id);
        intent.putExtra(BundleEnum.TRACKING.getValue(),
                readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).trackNumber);
        intent.putExtra(BundleEnum.POSITION.getValue(), position);
        intent.putExtra(BundleEnum.IMAGE.getValue(), true);
        startActivityForResult(intent, MyApplication.CAMERA);
    }

    void attemptSend(int position, boolean isForm, boolean isImage) {
        //TODO
        if (isForm && (sharedPreferenceManager.getBoolData(SharedReferenceKeys.SERIAL.getValue())
                || sharedPreferenceManager.getBoolData(SharedReferenceKeys.AHAD_2.getValue())
                || sharedPreferenceManager.getBoolData(SharedReferenceKeys.AHAD_1.getValue())
                || sharedPreferenceManager.getBoolData(SharedReferenceKeys.AHAD_TOTAL.getValue())
                || sharedPreferenceManager.getBoolData(SharedReferenceKeys.AHAD_EMPTY.getValue())
                || sharedPreferenceManager.getBoolData(SharedReferenceKeys.KARBARI.getValue())
                || sharedPreferenceManager.getBoolData(SharedReferenceKeys.ADDRESS.getValue())
                || sharedPreferenceManager.getBoolData(SharedReferenceKeys.ACCOUNT.getValue())
                || sharedPreferenceManager.getBoolData(SharedReferenceKeys.READING_REPORT.getValue())
                || sharedPreferenceManager.getBoolData(SharedReferenceKeys.MOBILE.getValue()))) {
            showPossible(position);
        } else if (isImage && sharedPreferenceManager.getBoolData(SharedReferenceKeys.IMAGE.getValue())) {
            showImage(position);
        } else {
            setAboveIconsSrc(position);
            update(position);
            makeRing(activity, NotificationType.SAVE);
            prepareToSend(position);
            changePage();
        }
    }

    void prepareToSend(int position) {
        Retrofit retrofit = NetworkHelper.getInstance(sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue()));
        IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        OnOffLoadDto.OffLoadData offLoadData = new OnOffLoadDto.OffLoadData();

        offLoadData.isFinal = false;

        offLoadData.offLoadReports.addAll(MyDatabaseClient.getInstance(activity).getMyDatabase().
                offLoadReportDao().getAllOffLoadReportById(readingData.onOffLoadDtos.get(position).id));
        //TODO
//        ArrayList<OnOffLoadDto> onOffLoadDtos = new ArrayList<>(
//                MyDatabaseClient.getInstance(activity).getMyDatabase().onOffLoadDao().
//                        getAllOnOffLoadRead(OffloadStateEnum.INSERTED.getValue()));

        ArrayList<OnOffLoadDto> onOffLoadDtos = new ArrayList<>();

        for (TrackingDto trackingDto : readingData.trackingDtos) {
            onOffLoadDtos.addAll(MyDatabaseClient.getInstance(activity).getMyDatabase().onOffLoadDao().
                    getAllOnOffLoadRead(OffloadStateEnum.INSERTED.getValue(), trackingDto.trackNumber));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            onOffLoadDtos.forEach(onOffLoadDto -> {
                offLoadData.offLoads.add(new OnOffLoadDto.OffLoad(onOffLoadDto));
                offLoadData.offLoadReports.addAll(MyDatabaseClient.getInstance(activity).getMyDatabase().
                        offLoadReportDao().getAllOffLoadReportById(onOffLoadDto.id));
            });
        } else
            for (OnOffLoadDto onOffLoadDto : onOffLoadDtos) {
                offLoadData.offLoads.add(new OnOffLoadDto.OffLoad(onOffLoadDto));
                offLoadData.offLoadReports.addAll(MyDatabaseClient.getInstance(activity).getMyDatabase().
                        offLoadReportDao().getAllOffLoadReportById(onOffLoadDto.id));
            }
        Call<OnOffLoadDto.OffLoadResponses> call = iAbfaService.OffLoadData(offLoadData);
        HttpClientWrapper.callHttpAsync(call, ProgressType.NOT_SHOW.getValue(), activity,
                new offLoadData(), new offLoadDataIncomplete(), new offLoadError());
    }

    void showPossible(int position) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        PossibleFragment possibleFragment = PossibleFragment.newInstance(readingData.onOffLoadDtos.get(position), position);
        possibleFragment.show(fragmentTransaction, getString(R.string.dynamic_navigation));
    }

    void setAboveIconsSrc(int position) {
        runOnUiThread(() -> {
            setIsBazdidImage(position);
            setHighLowImage(position);
            setReadStatusImage(position);
            setExceptionImage(position);
        });
    }

    void setExceptionImage(int position) {
        binding.imageViewExceptionState.setVisibility(View.GONE);
        for (int i = 0; i < readingData.counterStateDtos.size(); i++) {
            if (readingData.counterStateDtos.get(i).moshtarakinId ==
                    readingData.onOffLoadDtos.get(position).preCounterStateCode &&
                    readingData.counterStateDtos.get(i).isXarab) {
                binding.imageViewExceptionState.setVisibility(View.VISIBLE);
                binding.imageViewExceptionState.setImageResource(imageSrc[14]);
            }
        }

        for (int i = 0; i < readingData.karbariDtos.size(); i++) {
            if (readingData.karbariDtos.get(i).moshtarakinId ==
                    readingData.onOffLoadDtos.get(position).karbariCode &&
                    readingData.karbariDtos.get(i).isSaxt) {
                binding.imageViewExceptionState.setVisibility(View.VISIBLE);
                binding.imageViewExceptionState.setImageResource(imageSrc[13]);
            }
        }
        if (readingData.onOffLoadDtos.get(position).noeVagozariId == 4) {
            binding.imageViewExceptionState.setVisibility(View.VISIBLE);
            binding.imageViewExceptionState.setImageResource(imageSrc[13]);
        }

        if (readingData.onOffLoadDtos.get(position).hazf > 0) {
            binding.imageViewExceptionState.setVisibility(View.VISIBLE);
            binding.imageViewExceptionState.setImageResource(imageSrc[12]);
        }
    }

    void setIsBazdidImage(int position) {
        if (readingData.onOffLoadDtos.get(position).isBazdid)
            binding.imageViewReadingType.setImageResource(imageSrc[6]);
        else binding.imageViewReadingType.setImageResource(imageSrc[7]);
    }

    void setReadStatusImage(int position) {
        binding.imageViewOffLoadState.setImageResource(
                imageSrc[readingData.onOffLoadDtos.get(position).offLoadStateId]);
        if (readingData.onOffLoadDtos.get(position).offLoadStateId == 0)
            binding.imageViewOffLoadState.setImageResource(imageSrc[8]);
    }

    void setHighLowImage(int position) {
        binding.imageViewHighLowState.setImageResource(
                imageSrc[readingData.onOffLoadDtos.get(position).highLowStateId]);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void setOnImageViewsClickListener() {
        flashLightManager = new FlashLightManager(activity);
        ImageView imageViewFlash = findViewById(R.id.image_view_flash);
        imageViewFlash.setImageDrawable(activity.getDrawable(R.drawable.img_flash_off));
        imageViewFlash.setOnClickListener(v -> {
            boolean isOn = flashLightManager.toggleFlash();
            makeRing(activity, isOn ? NotificationType.LIGHT_ON : NotificationType.LIGHT_OFF);
            imageViewFlash.setImageDrawable(getDrawable(isOn ? R.drawable.img_flash_on : R.drawable.img_flash_off));
        });
        ImageView imageViewReverse = findViewById(R.id.image_view_reverse);
        imageViewReverse.setImageDrawable(activity.getDrawable(R.drawable.img_inverse));
        imageViewReverse.setOnClickListener(v -> {
            AppCompatDelegate.setDefaultNightMode(
                    isNight ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES);
            isNight = !isNight;
        });
        ImageView imageViewCamera = findViewById(R.id.image_view_camera);
        imageViewCamera.setImageDrawable(activity.getDrawable(R.drawable.img_camera));
        imageViewCamera.setOnClickListener(v -> {
            if (readingDataTemp.onOffLoadDtos.isEmpty()) {
                showNoEshterakFound();
            } else {
                Intent intent = new Intent(activity, TakePhotoActivity.class);
                intent.putExtra(BundleEnum.BILL_ID.getValue(),
                        readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).id);
                intent.putExtra(BundleEnum.TRACKING.getValue(),
                        readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).trackNumber);
                startActivity(intent);
            }
        });
        ImageView imageViewCheck = findViewById(R.id.image_view_reading_report);
        imageViewCheck.setImageDrawable(activity.getDrawable(R.drawable.img_checked));
        imageViewCheck.setOnClickListener(v -> {
            if (readingDataTemp.onOffLoadDtos.isEmpty()) {
                showNoEshterakFound();
            } else {
                Intent intent = new Intent(activity, ReadingReportActivity.class);
                intent.putExtra(BundleEnum.BILL_ID.getValue(),
                        readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).id);
                intent.putExtra(BundleEnum.POSITION.getValue(),
                        binding.viewPager.getCurrentItem());
                startActivityForResult(intent, MyApplication.REPORT);
            }
        });
        ImageView imageViewSearch = findViewById(R.id.image_view_search);
        imageViewSearch.setImageDrawable(activity.getDrawable(R.drawable.img_search));
        imageViewSearch.setOnClickListener(v -> {
            if (readingDataTemp.onOffLoadDtos.isEmpty()) {
                showNoEshterakFound();
            } else {
                FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                SearchFragment searchFragment = new SearchFragment();
                searchFragment.show(fragmentTransaction, "");
            }
        });
    }

    public void search(int type, String key, boolean goToPage) {
        switch (type) {
            case 4:
                binding.viewPager.setCurrentItem(Integer.parseInt(key) - 1);
                break;
            case 5:
                readingData.onOffLoadDtos.clear();
                readingData.onOffLoadDtos.addAll(readingDataTemp.onOffLoadDtos);
                runOnUiThread(this::setupViewPager);
                break;
            case 3:
                readingData.onOffLoadDtos.clear();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    readingDataTemp.onOffLoadDtos.forEach(onOffLoadDto -> {
                        if (onOffLoadDto.firstName.toLowerCase().contains(key) ||
                                onOffLoadDto.sureName.toLowerCase().contains(key))
                            readingData.onOffLoadDtos.add(onOffLoadDto);
                    });
                } else
                    for (OnOffLoadDto onOffLoadDto : readingDataTemp.onOffLoadDtos) {
                        if (onOffLoadDto.firstName.toLowerCase().contains(key) ||
                                onOffLoadDto.sureName.toLowerCase().contains(key))
                            readingData.onOffLoadDtos.add(onOffLoadDto);
                    }
                runOnUiThread(this::setupViewPager);
                break;
            default:
                if (goToPage) {
                    switch (type) {
                        case 0://105010600
                            for (int i = 0; i < readingData.onOffLoadDtos.size(); i++) {
                                if (readingData.onOffLoadDtos.get(i).eshterak.contains(key))
                                    binding.viewPager.setCurrentItem(i);
                            }
                            break;
                        case 1://10055024
                            for (int i = 0; i < readingData.onOffLoadDtos.size(); i++) {
                                if (readingData.onOffLoadDtos.get(i).radif == Integer.parseInt(key))
                                    binding.viewPager.setCurrentItem(i);
                            }
                            break;
                        case 2://834519
                            for (int i = 0; i < readingData.onOffLoadDtos.size(); i++) {
                                if (readingData.onOffLoadDtos.get(i).counterSerial.contains(key))
                                    binding.viewPager.setCurrentItem(i);
                            }
                            break;
                    }
                } else {
                    switch (type) {
                        case 0:
                            readingData.onOffLoadDtos.clear();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                readingDataTemp.onOffLoadDtos.forEach(onOffLoadDto -> {
                                    if (onOffLoadDto.eshterak.toLowerCase().contains(key))
                                        readingData.onOffLoadDtos.add(onOffLoadDto);
                                });
                            } else
                                for (OnOffLoadDto onOffLoadDto : readingDataTemp.onOffLoadDtos) {
                                    if (onOffLoadDto.eshterak.toLowerCase().contains(key))
                                        readingData.onOffLoadDtos.add(onOffLoadDto);
                                }
                            break;
                        case 1:
                            readingData.onOffLoadDtos.clear();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                readingDataTemp.onOffLoadDtos.forEach(onOffLoadDto -> {

                                    if (onOffLoadDto.radif == Integer.parseInt(key))
                                        readingData.onOffLoadDtos.add(onOffLoadDto);
                                });
                            } else
                                for (OnOffLoadDto onOffLoadDto : readingDataTemp.onOffLoadDtos) {
                                    if (onOffLoadDto.radif == Integer.parseInt(key))
                                        readingData.onOffLoadDtos.add(onOffLoadDto);
                                }
                            break;
                        case 2:
                            readingData.onOffLoadDtos.clear();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                readingDataTemp.onOffLoadDtos.forEach(onOffLoadDto -> {
                                    if (onOffLoadDto.counterSerial.toLowerCase().contains(key))
                                        readingData.onOffLoadDtos.add(onOffLoadDto);
                                });
                            } else
                                for (OnOffLoadDto onOffLoadDto : readingDataTemp.onOffLoadDtos) {
                                    if (onOffLoadDto.counterSerial.toLowerCase().contains(key))
                                        readingData.onOffLoadDtos.add(onOffLoadDto);
                                }
                            break;

                    }
                    runOnUiThread(this::setupViewPager);
                }
        }
    }

    void showNoEshterakFound() {
        new CustomDialog(DialogType.Yellow, activity, getString(R.string.no_eshterak_found),
                getString(R.string.dear_user), getString(R.string.eshterak),
                getString(R.string.accepted));
    }

    void setAboveIcons() {
        imageSrc[0] = R.drawable.img_default_level;
        imageSrc[1] = R.drawable.img_normal_level;
        imageSrc[2] = R.drawable.img_high_level;
        imageSrc[3] = R.drawable.img_low_level;
        imageSrc[4] = R.drawable.img_low_level;
        imageSrc[5] = R.drawable.img_visit_default;
        imageSrc[6] = R.drawable.img_visit;
        imageSrc[7] = R.drawable.img_writing;
        imageSrc[8] = R.drawable.img_successful_default;
        imageSrc[9] = R.drawable.img_successful;
        imageSrc[10] = R.drawable.img_mistake;
        imageSrc[11] = R.drawable.img_failure;
        imageSrc[12] = R.drawable.img_delete_temp;
        imageSrc[13] = R.drawable.img_construction;
        imageSrc[14] = R.drawable.img_broken_pipe;
    }

    void setupViewPager() {
        ArrayList<String> items = new ArrayList<>();
        for (int i = 0; i < readingData.counterStateDtos.size(); i++) {
            items.add(readingData.counterStateDtos.get(i).title);
        }
        adapter = new SpinnerCustomAdapter(activity, items);

        binding.textViewNotFound.setVisibility(!(readingData.onOffLoadDtos.size() > 0) ? View.VISIBLE : View.GONE);
        binding.linearLayoutAbove.setVisibility(readingData.onOffLoadDtos.size() > 0 ? View.VISIBLE : View.GONE);
        binding.viewPager.setVisibility(readingData.onOffLoadDtos.size() > 0 ? View.VISIBLE : View.GONE);
        viewPagerAdapterReading =
                new ViewPagerAdapterReading(getSupportFragmentManager(),
                        FragmentStatePagerAdapter.POSITION_NONE,
                        readingData);
        binding.viewPager.setAdapter(viewPagerAdapterReading);
        binding.viewPager.setPageTransformer(true, new DepthPageTransformer());
        setOnPageChangeListener();

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (MyApplication.FOCUS_ON_EDIT_TEXT)
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        isReading = true;
    }

    void setOnPageChangeListener() {
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                final String number = (position + 1) + "/" + readingData.onOffLoadDtos.size();
                runOnUiThread(() -> binding.textViewPageNumber.setText(number));
                setAboveIconsSrc(position);
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    void checkPermissions() {
        if (PermissionManager.gpsEnabled(this))
            if (PermissionManager.checkLocationPermission(getApplicationContext())) {
                askLocationPermission();
            } else if (PermissionManager.checkStoragePermission(getApplicationContext())) {
                askStoragePermission();
            } else {
                getBundle();
                setAboveIcons();
                new GetDBData().execute();
                setOnImageViewsClickListener();
            }
    }

    void getBundle() {
        if (getIntent().getExtras() != null) {
            readStatus = getIntent().getIntExtra(BundleEnum.READ_STATUS.getValue(), 0);
            highLow = getIntent().getIntExtra(BundleEnum.TYPE.getValue(), 1);
            Gson gson = new Gson();
            ArrayList<String> json1 = getIntent().getExtras().getStringArrayList(
                    BundleEnum.IS_MANE.getValue());
            if (json1 != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    json1.forEach(s -> isMane.add(gson.fromJson(s, Integer.class)));
                } else
                    for (String s : json1) {
                        isMane.add(gson.fromJson(s, Integer.class));
                    }
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
        new TedPermission(this)
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

    void askLocationPermission() {
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
        new TedPermission(this)
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reading_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        if (id == R.id.menu_navigation) {
            if (readingDataTemp.onOffLoadDtos.isEmpty()) {
                showNoEshterakFound();
            } else {
                intent = new Intent(activity, NavigationActivity.class);
                intent.putExtra(BundleEnum.BILL_ID.getValue(),
                        readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).id);
                intent.putExtra(BundleEnum.POSITION.getValue(), binding.viewPager.getCurrentItem());
                startActivityForResult(intent, MyApplication.NAVIGATION);
            }
        } else if (id == R.id.menu_report_forbid) {
            intent = new Intent(activity, ReportForbidActivity.class);
            if (readingData.onOffLoadDtos.size() > 0)
                intent.putExtra(BundleEnum.ZONE_ID.getValue(), readingData.onOffLoadDtos.
                        get(binding.viewPager.getCurrentItem()).zoneId);
            startActivity(intent);
        } else if (id == R.id.menu_description) {
            if (readingDataTemp.onOffLoadDtos.isEmpty()) {
                showNoEshterakFound();
            } else {
                intent = new Intent(activity, DescriptionActivity.class);
                intent.putExtra(BundleEnum.BILL_ID.getValue(),
                        readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).id);
                intent.putExtra(BundleEnum.TRACKING.getValue(),
                        readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).trackNumber);
                intent.putExtra(BundleEnum.POSITION.getValue(), binding.viewPager.getCurrentItem());
                startActivityForResult(intent, MyApplication.DESCRIPTION);
            }
        }
        if (id == R.id.menu_location) {
            if (readingDataTemp.onOffLoadDtos.isEmpty()) {
                showNoEshterakFound();
            } else {
                intent = new Intent(activity, CounterPlaceActivity.class);
                intent.putExtra(BundleEnum.BILL_ID.getValue(),
                        readingData.onOffLoadDtos.get(binding.viewPager.getCurrentItem()).id);
                intent.putExtra(BundleEnum.POSITION.getValue(), binding.viewPager.getCurrentItem());
                startActivityForResult(intent, MyApplication.COUNTER_LOCATION);
            }
        } else if (id == R.id.menu_keyboard) {
            if (readingData.onOffLoadDtos.isEmpty()) {
                showNoEshterakFound();
            } else {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (MyApplication.FOCUS_ON_EDIT_TEXT) {
                    try {
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception ignored) {
                    }
                    MyApplication.FOCUS_ON_EDIT_TEXT = !MyApplication.FOCUS_ON_EDIT_TEXT;
                } else {
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    MyApplication.FOCUS_ON_EDIT_TEXT = !MyApplication.FOCUS_ON_EDIT_TEXT;
                    viewPagerAdapterReading.notifyDataSetChanged();
                }
            }
        } else if (id == R.id.menu_last) {
            if (readingData.onOffLoadDtos.isEmpty()) {
                showNoEshterakFound();
            } else {
                int currentItem = 0, i = 0;
                for (OnOffLoadDto onOffLoadDto : readingData.onOffLoadDtos) {
                    if (!onOffLoadDto.isBazdid) {
                        currentItem = i;
                        break;
                    }
                    i++;
                }
                binding.viewPager.setCurrentItem(currentItem);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == MyApplication.REPORT || requestCode == MyApplication.NAVIGATION ||
                requestCode == MyApplication.DESCRIPTION ||
                requestCode == MyApplication.COUNTER_LOCATION) && resultCode == RESULT_OK) {
            int position = data.getExtras().getInt(BundleEnum.POSITION.getValue());
            String uuid = data.getExtras().getString(BundleEnum.BILL_ID.getValue());
            MyDatabaseClient.getInstance(activity).getMyDatabase().onOffLoadDao().
                    updateOnOffLoad(true, uuid);
            readingData.onOffLoadDtos.set(position, MyDatabaseClient.getInstance(activity).
                    getMyDatabase().onOffLoadDao().getAllOnOffLoadById(uuid));
            int i = 0;
            for (OnOffLoadDto onOffLoadDto : readingDataTemp.onOffLoadDtos) {
                if (onOffLoadDto.id.equals(uuid))
                    readingDataTemp.onOffLoadDtos.set(i, readingData.onOffLoadDtos.get(position));
                i++;
            }
        } else if (resultCode == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == MyApplication.GPS_CODE)
                checkPermissions();
            if (requestCode == MyApplication.REQUEST_NETWORK_CODE) {
                if (isNetworkAvailable(activity))
                    checkPermissions();
                else PermissionManager.setMobileWifiEnabled(this);
            }
            if (requestCode == MyApplication.REQUEST_WIFI_CODE) {
                if (isNetworkAvailable(activity))
                    checkPermissions();
                else PermissionManager.enableNetwork(this);
            }
        } else if (requestCode == MyApplication.CAMERA && resultCode == RESULT_OK) {
            int position = data.getExtras().getInt(BundleEnum.POSITION.getValue());
            attemptSend(position, false, false);
        }
    }

    class offLoadData implements ICallback<OnOffLoadDto.OffLoadResponses> {
        @Override
        public void execute(Response<OnOffLoadDto.OffLoadResponses> response) {
            if (response.body() != null && response.body().status == 200) {
                MyDatabaseClient.getInstance(activity).getMyDatabase().offLoadReportDao().
                        deleteAllOffLoadReport();
                int state = response.body().isValid ? OffloadStateEnum.SENT.getValue() :
                        OffloadStateEnum.SENT_WITH_ERROR.getValue();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    response.body().targetObject.forEach(s -> {
                        for (int j = 0; j < readingData.onOffLoadDtos.size(); j++) {
                            if (s.equals(readingData.onOffLoadDtos.get(j).id)) {
                                readingData.onOffLoadDtos.get(j).offLoadStateId = state;
                            }
                        }
                        MyDatabaseClient.getInstance(activity).getMyDatabase().onOffLoadDao().
                                updateOnOffLoad(state, s);
                    });
                } else {
                    for (String s : response.body().targetObject) {
                        for (int j = 0; j < readingData.onOffLoadDtos.size(); j++) {
                            if (s.equals(readingData.onOffLoadDtos.get(j).id)) {
                                readingData.onOffLoadDtos.get(j).offLoadStateId = state;
                            }
                        }
                        MyDatabaseClient.getInstance(activity).getMyDatabase().onOffLoadDao().
                                updateOnOffLoad(state, s);
                    }
                }
            } else if (response.body() != null/* && errorCounter < SHOW_ERROR*/) {
                errorCounter++;
                CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(activity);
                String error = customErrorHandlingNew.getErrorMessage(response.body().status);
                new CustomToast().error(error);
            }
        }
    }

    class offLoadDataIncomplete implements ICallbackIncomplete<OnOffLoadDto.OffLoadResponses> {
        @Override
        public void executeIncomplete(Response<OnOffLoadDto.OffLoadResponses> response) {
//            if (errorCounter < SHOW_ERROR) {
//            }
//            errorCounter++;
            if (response != null) {
                Log.e("offLoadDataIncomplete", String.valueOf(response.body()));
                Log.e("offLoadDataIncomplete", response.toString());
                CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(activity);
                String error = customErrorHandlingNew.getErrorMessageDefault(response);
                new CustomToast().error(error);
            }
        }
    }

    class offLoadError implements ICallbackError {
        @Override
        public void executeError(Throwable t) {
            if (errorCounter < SHOW_ERROR) {
                CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(activity);
                String error = customErrorHandlingNew.getErrorMessageTotal(t);
                new CustomToast().error(error);
            }
            errorCounter++;
            Log.e("error", t.toString());
        }
    }

    @SuppressLint("StaticFieldLeak")
    class GetDBData extends AsyncTask<Integer, Integer, Integer> {
        CustomProgressBar customProgressBar;

        public GetDBData() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            customProgressBar = new CustomProgressBar();
            customProgressBar.show(activity, false);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            customProgressBar.getDialog().dismiss();
            super.onPostExecute(integer);
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            readingData = new ReadingData();
            readingDataTemp = new ReadingData();
            MyDatabase myDatabase = MyDatabaseClient.getInstance(activity).getMyDatabase();
            readingData.counterStateDtos.addAll(myDatabase.counterStateDao().getCounterStateDtos());
            readingData.karbariDtos.addAll(myDatabase.karbariDao().getAllKarbariDto());
            readingData.qotrDictionary.addAll(myDatabase.qotrDictionaryDao().getAllQotrDictionaries());
            readingData.trackingDtos.addAll(myDatabase.trackingDao().
                    getTrackingDtosIsActiveNotArchive(true, false));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                readingData.trackingDtos.forEach(trackingDto ->
                        readingData.readingConfigDefaultDtos.addAll(myDatabase.readingConfigDefaultDao().
                                getReadingConfigDefaultDtosByZoneId(trackingDto.zoneId)));
            } else {
                for (TrackingDto dto : readingData.trackingDtos) {
                    readingData.readingConfigDefaultDtos.addAll(myDatabase.readingConfigDefaultDao().
                            getReadingConfigDefaultDtosByZoneId(dto.zoneId));
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                readingData.trackingDtos.forEach(trackingDto -> {
                    if (readStatus == ReadStatusEnum.ALL.getValue()) {
                        readingData.onOffLoadDtos.addAll(myDatabase.onOffLoadDao().
                                getAllOnOffLoadByTracking(trackingDto.trackNumber));
                    } else if (readStatus == ReadStatusEnum.STATE.getValue()) {
                        readingData.onOffLoadDtos.addAll(myDatabase.onOffLoadDao().
                                getAllOnOffLoadByHighLowAndTracking(trackingDto.trackNumber, highLow));
                    } else if (readStatus == ReadStatusEnum.UNREAD.getValue()) {
                        readingData.onOffLoadDtos.addAll(myDatabase.onOffLoadDao().
                                getAllOnOffLoadNotRead(0, trackingDto.trackNumber));
                    } else if (readStatus == ReadStatusEnum.READ.getValue()) {
                        readingData.onOffLoadDtos.addAll(myDatabase.onOffLoadDao().
                                getAllOnOffLoadRead(OffloadStateEnum.SENT.getValue(), trackingDto.trackNumber));
                    } else if (readStatus == ReadStatusEnum.ALL_MANE.getValue()) {
                        isMane.forEach(integer ->
                                readingData.onOffLoadDtos.addAll(myDatabase.onOffLoadDao().
                                        getOnOffLoadReadByIsMane(integer, trackingDto.trackNumber)));

                    }
                });
            else
                for (TrackingDto trackingDto : readingData.trackingDtos) {
                    if (readStatus == ReadStatusEnum.ALL.getValue()) {
                        readingData.onOffLoadDtos.addAll(myDatabase.onOffLoadDao().
                                getAllOnOffLoadByTracking(trackingDto.trackNumber));
                    } else if (readStatus == ReadStatusEnum.STATE.getValue()) {
                        readingData.onOffLoadDtos.addAll(myDatabase.onOffLoadDao().
                                getAllOnOffLoadByHighLowAndTracking(trackingDto.trackNumber, highLow));
                    } else if (readStatus == ReadStatusEnum.UNREAD.getValue()) {
                        readingData.onOffLoadDtos.addAll(myDatabase.onOffLoadDao().
                                getAllOnOffLoadNotRead(0, trackingDto.trackNumber));
                    } else if (readStatus == ReadStatusEnum.READ.getValue()) {
                        readingData.onOffLoadDtos.addAll(myDatabase.onOffLoadDao().
                                getAllOnOffLoadRead(OffloadStateEnum.SENT.getValue(), trackingDto.trackNumber));
                    } else if (readStatus == ReadStatusEnum.ALL_MANE.getValue()) {
                        for (int i : isMane) {
                            readingData.onOffLoadDtos.addAll(myDatabase.onOffLoadDao().
                                    getOnOffLoadReadByIsMane(i, trackingDto.trackNumber));
                        }
                    }
                }
            if (readingData != null && readingData.onOffLoadDtos != null && readingData.onOffLoadDtos.size() > 0) {
                readingDataTemp.onOffLoadDtos.addAll(readingData.onOffLoadDtos);
                readingDataTemp.counterStateDtos.addAll(readingData.counterStateDtos);
                readingDataTemp.qotrDictionary.addAll(readingData.qotrDictionary);
                readingDataTemp.trackingDtos.addAll(readingData.trackingDtos);
                readingDataTemp.karbariDtos.addAll(readingData.karbariDtos);
                readingDataTemp.readingConfigDefaultDtos.addAll(readingData.readingConfigDefaultDtos);
                setAboveIconsSrc(0);
            }
            runOnUiThread(ReadingActivity.this::setupViewPager);
            return null;
        }
    }

    public ReadingData getReadingData() {
        return readingData;
    }

    public SpinnerCustomAdapter getAdapter() {
        return adapter;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isReading && !readingData.onOffLoadDtos.isEmpty()) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (MyApplication.FOCUS_ON_EDIT_TEXT)
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        try {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        try {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception ignored) {
        }
        Debug.getNativeHeapAllocatedSize();
        System.runFinalization();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Runtime.getRuntime().gc();
        System.gc();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        try {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception ignored) {
        }
        ImageView imageViewFlash = findViewById(R.id.image_view_flash);
        imageViewFlash.setImageDrawable(null);
        ImageView imageViewReverse = findViewById(R.id.image_view_reverse);
        imageViewReverse.setImageDrawable(null);
        ImageView imageViewCamera = findViewById(R.id.image_view_camera);
        imageViewCamera.setImageDrawable(null);
        ImageView imageViewSearch = findViewById(R.id.image_view_search);
        imageViewSearch.setImageDrawable(null);
        ImageView imageViewCheck = findViewById(R.id.image_view_reading_report);
        imageViewCheck.setImageDrawable(null);
        binding.imageViewHighLowState.setImageDrawable(null);
        binding.imageViewOffLoadState.setImageDrawable(null);
        binding.imageViewReadingType.setImageDrawable(null);
        binding.imageViewExceptionState.setImageDrawable(null);
        readingData = null;
        readingDataTemp = null;
        viewPagerAdapterReading = null;
        Debug.getNativeHeapAllocatedSize();
        System.runFinalization();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Runtime.getRuntime().gc();
        System.gc();
    }
}