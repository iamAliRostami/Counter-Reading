package com.leon.counter_reading.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.ActivityDescriptionBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.enums.SharedReferenceNames;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.tables.Voice;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.MyDatabaseClient;
import com.leon.counter_reading.utils.PermissionManager;
import com.leon.counter_reading.utils.RecordVoice;
import com.leon.counter_reading.utils.SharedPreferenceManager;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class DescriptionActivity extends AppCompatActivity {
    ActivityDescriptionBinding binding;
    ISharedPreferenceManager sharedPreferenceManager;
    String uuid;
    int position;
    Activity activity;
    Voice voice;
    boolean play = false;
    boolean hasVoice = false;
    RecordVoice recordVoice;
    final double[] startTime = {0};
    final double[] finalTime = {0};
    final int[] oneTimeOnly = {0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext(),
                SharedReferenceNames.ACCOUNT.getValue());
        int theme = sharedPreferenceManager.getIntData(SharedReferenceKeys.THEME_STABLE.getValue());
        MyApplication.onActivitySetTheme(this, theme, true);
        binding = ActivityDescriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;

        if (PermissionManager.checkRecorderPermission(getApplicationContext())) {
            initialize();
        }
        else askRecorderPermission();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void initialize() {
        if (getIntent().getExtras() != null) {
            uuid = getIntent().getExtras().getString(BundleEnum.BILL_ID.getValue());
            position = getIntent().getExtras().getInt(BundleEnum.POSITION.getValue());
        }
        recordVoice = new RecordVoice(activity);
        binding.imageViewRecord.setImageDrawable(getDrawable(R.drawable.img_record));
        checkMultimediaAndToggle();
        setImageViewRecordClickListener();
        setImageViewPausePlayClickListener();
        setSeekBarChangeListener();
        setOnButtonSendClickListener();
    }

    @SuppressLint("DefaultLocale")
    void setImageViewPausePlayClickListener() {
        binding.imageViewPlay.setOnClickListener(v -> {
            if (!play) {
                recordVoice.onPlay(true);
                finalTime[0] = recordVoice.Player.getDuration();
                startTime[0] = recordVoice.Player.getCurrentPosition();
                if (oneTimeOnly[0] == 0) {
                    binding.seekBar.setMax((int) finalTime[0]);
                    oneTimeOnly[0] = 1;
                }
                final Handler myHandler = new Handler();
                binding.seekBar.setProgress((int) startTime[0]);
                Runnable UpdateSongTime = new Runnable() {
                    public void run() {
                        startTime[0] = recordVoice.Player.getCurrentPosition();
                        binding.textViewCurrent.setText(String.format("%d min, %d sec",
                                TimeUnit.MILLISECONDS.toMinutes((long) startTime[0]),
                                TimeUnit.MILLISECONDS.toSeconds((long) startTime[0]) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                                toMinutes((long) startTime[0])))
                        );
                        binding.seekBar.setProgress((int) startTime[0]);
                        myHandler.postDelayed(this, 100);
                        if (startTime[0] == finalTime[0]) {
                            final ImageView imageView = findViewById(R.id.image_view_play);
                            imageView.setImageResource(R.drawable.img_pause);
                            play = false;

                        }
                    }
                };
                binding.textViewTotal.setText(String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes((long) finalTime[0]),
                        TimeUnit.MILLISECONDS.toSeconds((long) finalTime[0]) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                        finalTime[0])))
                );
                binding.textViewCurrent.setText(String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime[0]),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime[0]) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                        startTime[0])))
                );
                myHandler.postDelayed(UpdateSongTime, 100);

                binding.imageViewPlay.setImageResource(R.drawable.img_pause);
                play = true;
            } else {
//                    recordVoice.Player.pause();
                recordVoice.onPlay(false);
                binding.imageViewPlay.setImageResource(R.drawable.img_play);
                play = false;
            }
        });
    }

    void setSeekBarChangeListener() {
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                recordVoice.Player.seekTo(progressChangedValue);
                startTime[0] = progressChangedValue;
                if (progressChangedValue == finalTime[0]) {
                    binding.imageViewPlay.setImageResource(R.drawable.img_play);
                    play = false;
                }
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    void setImageViewRecordClickListener() {
        binding.imageViewRecord.setOnTouchListener((v, event) -> {
            play = false;
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                binding.buttonSend.setEnabled(false);
                binding.imageViewPlay.setEnabled(false);
                recordVoice.onRecord(true);
                binding.imageViewPlay.setImageResource(R.drawable.img_play_pause);
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                recordVoice.onRecord(false);
                binding.buttonSend.setEnabled(true);
                binding.imageViewPlay.setEnabled(true);
                binding.imageViewPlay.setImageResource(R.drawable.img_play);
                hasVoice = true;
            }
            return true;
        });
    }

    void checkMultimediaAndToggle() {
        voice = MyDatabaseClient.getInstance(activity).getMyDatabase().voiceDao().getVoicesByOnOffLoadId(uuid);
        if (voice == null) {
            binding.buttonSend.setEnabled(true);
            binding.imageViewRecord.setEnabled(true);
            binding.imageViewPlay.setEnabled(false);
            binding.imageViewPlay.setImageResource(R.drawable.img_play_pause);
        } else {
            RecordVoice.FileName = voice.address;
            binding.buttonSend.setEnabled(!voice.isSent);
            binding.imageViewRecord.setEnabled(false);
            binding.imageViewPlay.setEnabled(true);
            binding.imageViewPlay.setImageResource(R.drawable.img_play);
        }
    }

    void setOnButtonSendClickListener() {
        binding.buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    void askRecorderPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                CustomToast customToast = new CustomToast();
                customToast.info(getString(R.string.access_granted));
                initialize();
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
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).check();
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
        binding.imageViewPlay.setImageDrawable(null);
        binding.imageViewRecord.setImageDrawable(null);
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Debug.getNativeHeapAllocatedSize();
    }
}