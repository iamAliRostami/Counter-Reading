package com.leon.counter_reading.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.databinding.ActivityDescriptionBinding;
import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.enums.ProgressType;
import com.leon.counter_reading.enums.SharedReferenceKeys;
import com.leon.counter_reading.enums.SharedReferenceNames;
import com.leon.counter_reading.infrastructure.IAbfaService;
import com.leon.counter_reading.infrastructure.ICallback;
import com.leon.counter_reading.infrastructure.ICallbackError;
import com.leon.counter_reading.infrastructure.ICallbackIncomplete;
import com.leon.counter_reading.infrastructure.ISharedPreferenceManager;
import com.leon.counter_reading.tables.Image;
import com.leon.counter_reading.tables.Voice;
import com.leon.counter_reading.utils.CustomErrorHandling;
import com.leon.counter_reading.utils.CustomFile;
import com.leon.counter_reading.utils.CustomProgressBar;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.HttpClientWrapper;
import com.leon.counter_reading.utils.MyDatabaseClient;
import com.leon.counter_reading.utils.NetworkHelper;
import com.leon.counter_reading.utils.PermissionManager;
import com.leon.counter_reading.utils.SharedPreferenceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DescriptionActivity extends AppCompatActivity {
    ActivityDescriptionBinding binding;
    ISharedPreferenceManager sharedPreferenceManager;
    Activity activity;
    Voice voice;
    MediaPlayer mediaPlayer;
    MediaRecorder mediaRecorder;
    String uuid;
    boolean play = false;
    int position, startTime = 0, finalTime = 0;
    Voice.VoiceGrouped voiceGrouped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext(),
                SharedReferenceNames.ACCOUNT.getValue());
        MyApplication.onActivitySetTheme(this,
                sharedPreferenceManager.getIntData(SharedReferenceKeys.THEME_STABLE.getValue()),
                true);
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
        voiceGrouped = new Voice.VoiceGrouped();
        binding.imageViewRecord.setImageDrawable(getDrawable(R.drawable.img_record));
        checkMultimediaAndToggle();
        setImageViewRecordClickListener();
        setImageViewPausePlayClickListener();
        setOnButtonSendClickListener();
        setSeekBarChangeListener();
    }

    @SuppressLint({"ClickableViewAccessibility,SimpleDateFormat", "NewApi"})
    void setImageViewRecordClickListener() {
        binding.imageViewRecord.setLongClickable(true);
        binding.imageViewRecord.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                binding.imageViewPlay.setEnabled(false);
                voice.address = CustomFile.createAudioFile(activity);
                setupMediaRecorder();
                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                    new CustomToast().warning(getString(R.string.error_in_record_voice));
                    mediaRecorder.stop();
                }
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mediaRecorder.stop();
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(voice.address);
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mediaPlayer.getDuration() > 2000) {
                    binding.imageViewPlay.setEnabled(true);
                    binding.imageViewPlay.setImageResource(R.drawable.img_play);
                } else {
                    binding.imageViewPlay.setImageResource(R.drawable.img_play_pause);
                    binding.imageViewPlay.setEnabled(false);
                }
                if (mediaPlayer != null) {
                    try {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return false;
        });
    }

    void stopPlaying() {
        play = false;
        if (voice.id == 0)
            binding.imageViewRecord.setEnabled(true);
        binding.linearLayoutSeek.setVisibility(View.GONE);
        binding.imageViewPlay.setImageResource(R.drawable.img_play);
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(voice.address);
    }

    void setSeekBarChangeListener() {
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                if (progressChangedValue / 100 == finalTime / 100) {
                    stopPlaying();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (play) {
                    mediaPlayer.seekTo(progressChangedValue);
                    startTime = progressChangedValue;
                }
            }
        });
    }

    @SuppressLint("DefaultLocale")
    void setImageViewPausePlayClickListener() {
        binding.imageViewPlay.setOnClickListener(v -> {
            if (!play) {
                try {
                    binding.linearLayoutSeek.setVisibility(View.VISIBLE);
                    play = true;
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(voice.address);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    finalTime = mediaPlayer.getDuration();
                    startTime = mediaPlayer.getCurrentPosition();
                    binding.seekBar.setMax(finalTime);
                    final Handler myHandler = new Handler();
                    binding.seekBar.setProgress(startTime);
                    Runnable UpdateSongTime = new Runnable() {
                        public void run() {
                            if (play) {
                                startTime = mediaPlayer.getCurrentPosition();
                                binding.textViewCurrent.setText(String.format("%d دقیقه، %d ثانیه",
                                        TimeUnit.MILLISECONDS.toMinutes(startTime),
                                        TimeUnit.MILLISECONDS.toSeconds(startTime) -
                                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                                        toMinutes(startTime)))
                                );
                                binding.seekBar.setProgress(startTime);
                                myHandler.postDelayed(this, 1);
                                if (startTime == finalTime) {
                                    stopPlaying();
                                }
                            }
                        }
                    };
                    binding.textViewTotal.setText(String.format("%d دقیقه، %d ثانیه",
                            TimeUnit.MILLISECONDS.toMinutes(finalTime),
                            TimeUnit.MILLISECONDS.toSeconds(finalTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime)))
                    );
                    myHandler.postDelayed(UpdateSongTime, 1);
                    binding.imageViewPlay.setImageResource(R.drawable.img_pause);
                    binding.imageViewRecord.setEnabled(false);
                } catch (IOException e) {
                    e.printStackTrace();
                    new CustomToast().warning(getString(R.string.error_in_play_voice));
                }
            } else {
                stopPlaying();
            }
        });
    }

    void checkMultimediaAndToggle() {
        voice = MyDatabaseClient.getInstance(activity).getMyDatabase().voiceDao().getVoicesByOnOffLoadId(uuid);
        if (voice == null) {
            voice = new Voice();
            binding.buttonSend.setEnabled(true);
            binding.imageViewRecord.setEnabled(true);
            binding.imageViewPlay.setEnabled(false);
            binding.imageViewPlay.setImageResource(R.drawable.img_play_pause);
        } else {
            binding.buttonSend.setEnabled(!voice.isSent);
            binding.editTextMessage.setEnabled(false);
            binding.imageViewRecord.setEnabled(false);
            binding.editTextMessage.setText(voice.Description);
            binding.imageViewPlay.setEnabled(true);
            binding.imageViewPlay.setImageResource(R.drawable.img_play);
        }
    }

    void setOnButtonSendClickListener() {
        binding.buttonSend.setOnClickListener(v -> {
            voice.OnOffLoadId = uuid;
            String message = binding.editTextMessage.getText().toString();
            if (voice.address != null && voice.address.length() > 0)
                new prepareMultiMedia().execute();
            else if (message.length() > 0) {
                finishDescription(message);
            } else {
                new CustomToast().warning(getString(R.string.insert_message));
            }
        });
    }

    void saveVoice(boolean isSent) {
        voice.isSent = isSent;
        if (MyDatabaseClient.getInstance(activity).getMyDatabase().imageDao()
                .getImagesById(voice.id).size() > 0) {
            MyDatabaseClient.getInstance(activity).getMyDatabase().voiceDao().updateVoice(voice);
        } else {
            MyDatabaseClient.getInstance(activity).getMyDatabase().voiceDao().insertVoice(voice);
        }
    }

    void finishDescription(String message) {
        MyDatabaseClient.getInstance(activity).getMyDatabase().onOffLoadDao().
                updateOnOffLoadDescription(uuid, message);
        Intent intent = new Intent();
        intent.putExtra(BundleEnum.POSITION.getValue(), position);
        intent.putExtra(BundleEnum.BILL_ID.getValue(), uuid);
        setResult(RESULT_OK, intent);
        finish();
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
    public void onBackPressed() {
        stopPlaying();
        super.onBackPressed();
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

    @SuppressLint("StaticFieldLeak")
    class prepareMultiMedia extends AsyncTask<Integer, Integer, Integer> {
        CustomProgressBar customProgressBar;

        public prepareMultiMedia() {
            super();
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            voiceGrouped.File.clear();
            voice.File = CustomFile.prepareVoiceToSend(voice.address);
            voiceGrouped.File.add(voice.File);
            if (binding.editTextMessage.getText().toString().isEmpty())
                voice.Description = getString(R.string.description);
            else voice.Description = binding.editTextMessage.getText().toString();

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            customProgressBar = new CustomProgressBar();
            customProgressBar.show(activity, false);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            customProgressBar.getDialog().dismiss();
            uploadVoice();
        }

        void uploadVoice() {
            voiceGrouped.OnOffLoadId = RequestBody.create(
                    voice.OnOffLoadId, MediaType.parse("text/plain"));
            voiceGrouped.Description = RequestBody.create(
                    voice.Description, MediaType.parse("text/plain"));
            Retrofit retrofit = NetworkHelper.getInstance();
            IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
            Call<Image.ImageUploadResponse> call = iAbfaService.fileUploadGrouped(
                    voiceGrouped.File, voiceGrouped.OnOffLoadId, voiceGrouped.Description);
            HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), activity,
                    new upload(), new uploadIncomplete(), new uploadError());
        }
    }

    class upload implements ICallback<Image.ImageUploadResponse> {
        @Override
        public void execute(Response<Image.ImageUploadResponse> response) {
            if (response.body() != null && response.body().status == 200) {
                new CustomToast().success(response.body().message, Toast.LENGTH_LONG);
            } else {
                new CustomToast().warning(activity.getString(R.string.error_upload), Toast.LENGTH_LONG);
            }
            saveVoice(response.body() != null && response.body().status == 200);
            finishDescription(voice.Description);
        }
    }

    class uploadIncomplete implements ICallbackIncomplete<Image.ImageUploadResponse> {

        @Override
        public void executeIncomplete(Response<Image.ImageUploadResponse> response) {
            CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(activity);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            new CustomToast().warning(error, Toast.LENGTH_LONG);
            saveVoice(false);
            finishDescription(voice.Description);
        }
    }

    class uploadError implements ICallbackError {
        @Override
        public void executeError(Throwable t) {
            CustomErrorHandling customErrorHandlingNew = new CustomErrorHandling(activity);
            String error = customErrorHandlingNew.getErrorMessageTotal(t);
            new CustomToast().error(error, Toast.LENGTH_LONG);
            saveVoice(false);
            finishDescription(voice.Description);
        }
    }
}