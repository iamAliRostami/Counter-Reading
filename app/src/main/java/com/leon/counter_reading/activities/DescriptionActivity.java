package com.leon.counter_reading.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DescriptionActivity extends AppCompatActivity {
    ActivityDescriptionBinding binding;
    ISharedPreferenceManager sharedPreferenceManager;
    String uuid;
    int position;
    Activity activity;
    Voice voice;
    String pathSave;
    MediaPlayer mediaPlayer;
    MediaRecorder mediaRecorder;
    boolean play = false;
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

        if (PermissionManager.checkRecorderPermission(getApplicationContext()))
            initialize();
        else askRecorderPermission();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void initialize() {
        if (getIntent().getExtras() != null) {
            uuid = getIntent().getExtras().getString(BundleEnum.BILL_ID.getValue());
            position = getIntent().getExtras().getInt(BundleEnum.POSITION.getValue());
        }
        binding.imageViewRecord.setImageDrawable(getDrawable(R.drawable.img_record));
        checkMultimediaAndToggle();
        setImageViewRecordClickListener();
        setImageViewPausePlayClickListener();
        setOnButtonSendClickListener();
        setSeekBarChangeListener();
    }

    @SuppressLint("ClickableViewAccessibility,SimpleDateFormat")
    void setImageViewRecordClickListener() {
        binding.imageViewRecord.setLongClickable(true);
        binding.imageViewRecord.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                String timeStamp = (
                        new SimpleDateFormat(getString(R.string.save_format_name))).format(new Date());
                String audioFileName = "audio_" + timeStamp;
                pathSave = Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/" + audioFileName + ".amr";
                setupMediaRecorder();
                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                mediaRecorder.stop();
                binding.imageViewPlay.setImageResource(R.drawable.img_play);
                binding.imageViewPlay.setEnabled(true);
            }
            return false;
        });
    }

    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);
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
                mediaPlayer.seekTo(progressChangedValue);
                startTime[0] = progressChangedValue;
                if (progressChangedValue == finalTime[0]) {
                    binding.imageViewPlay.setImageResource(R.drawable.img_play);
                    play = false;
                }
            }
        });
    }

    @SuppressLint("DefaultLocale")
    void setImageViewPausePlayClickListener() {
        binding.imageViewPlay.setOnClickListener(v -> {
            Log.e("status", String.valueOf(play));
            if (!play) {
                Log.e("here", "false");
                play = !play;
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(pathSave);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
                //TODO
                finalTime[0] = mediaPlayer.getDuration();
                startTime[0] = mediaPlayer.getCurrentPosition();
                if (oneTimeOnly[0] == 0) {
                    binding.seekBar.setMax((int) finalTime[0]);
                    oneTimeOnly[0] = 1;
                }
                final Handler myHandler = new Handler();
                binding.seekBar.setProgress((int) startTime[0]);
                Runnable UpdateSongTime = new Runnable() {
                    public void run() {
                        if (play) {
                            startTime[0] = mediaPlayer.getCurrentPosition();
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
                //TODO
                binding.imageViewPlay.setImageResource(R.drawable.img_pause);
            } else {
                Log.e("here", "true");
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    setupMediaRecorder();
                }
                binding.imageViewPlay.setImageResource(R.drawable.img_play);
                play = !play;
            }
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