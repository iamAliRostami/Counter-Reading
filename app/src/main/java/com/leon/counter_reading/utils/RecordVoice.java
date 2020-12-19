package com.leon.counter_reading.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;

import com.leon.counter_reading.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public final class RecordVoice {
    public static String FileName = null;
    private final Context context;
    public MediaPlayer Player = null;
    private MediaRecorder Recorder = null;

    @SuppressLint("SimpleDateFormat")
    public RecordVoice(Context context) {
        this.context = context;
        FileName = context.getExternalCacheDir().getAbsolutePath() + context.getString(R.string.audio_folder) +
                new SimpleDateFormat(context.getString(R.string.save_format_name)).format(new Date()) + ".amr";
    }

    public static MultipartBody.Part prepareVoiceToSend(File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse(("multipart/form-data")), file);
        return MultipartBody.Part.createFormData("voice", file.getName(), requestFile);
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public File getFile() {
        File mediaStorageDir = new File(FileName);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        return mediaStorageDir;
    }

    public void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    public void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        Player = new MediaPlayer();
        try {
            Player.setDataSource(FileName);
            Player.prepare();
            Player.start();
        } catch (IOException e) {
//            new CustomDialog(DialogType.Yellow, context,
//                    context.getString(R.string.error_in_play_voice),
//                    context.getString(R.string.dear_user),
//                    context.getString(R.string.error),
//                    context.getString(R.string.accepted));
            new CustomToast().warning(context.getString(R.string.error_in_play_voice));
        }
    }

    private void stopPlaying() {
        Player.pause();
//        Player.release();
//        Player = null;
    }

    private void startRecording() {
        Recorder = new MediaRecorder();
        Recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        Recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        Recorder.setOutputFile(FileName);
        Recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            Recorder.prepare();
        } catch (IOException e) {
//            new CustomDialog(DialogType.Yellow, context,
//                    context.getString(R.string.error_in_record_voice),
//                    context.getString(R.string.dear_user),
//                    context.getString(R.string.error),
//                    context.getString(R.string.accepted));
            new CustomToast().warning(context.getString(R.string.error_in_record_voice));
        }
        Recorder.start();
    }

    private void stopRecording() {
        try {
            Recorder.stop();
            Recorder.release();
            Recorder = null;
        } catch (Exception e) {
            Log.d("error", e.getMessage());
        }
    }

    public MediaPlayer getPlayer() {
        return Player;
    }

    public void setPlayer(MediaPlayer player) {
        Player = player;
    }

    public MediaRecorder getRecorder() {
        return Recorder;
    }

    public void setRecorder(MediaRecorder recorder) {
        Recorder = recorder;
    }
}
