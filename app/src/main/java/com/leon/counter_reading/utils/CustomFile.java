package com.leon.counter_reading.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.tables.ReadingData;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class CustomFile {

    public static Bitmap loadImage(Context context, String address) {
        try {
            File f = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), context.getString(R.string.camera_folder));
            f = new File(f, address);
            return BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("error", e.toString());
            return null;
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static MultipartBody.Part bitmapToFile(Bitmap bitmap, Context context) {
        String timeStamp = (new SimpleDateFormat(context.getString(R.string.save_format_name))).format(new Date());
        String fileNameToSave = "JPEG_" + timeStamp + ".jpg";
        File f = new File(context.getCacheDir(), fileNameToSave);
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40 /*ignored for PNG*/, bos);
        byte[] bitmapData = bos.toByteArray();
        //write the bytes in file
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(f);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody reqFile = RequestBody.create(f, MediaType.parse("image/jpeg"));
        return MultipartBody.Part.createFormData("File", f.getName(), reqFile);
    }

    public static String bitmapToBinary(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return Arrays.toString(byteArray);
    }

    public static Bitmap binaryToBitmap(String s) {
        byte[] bytes = s.getBytes();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static String saveTempBitmap(Bitmap bitmap, Context context) {
        if (isExternalStorageWritable()) {
            return saveImage(bitmap, context);
        } else {
            new CustomToast().warning(context.getString(R.string.error_external_storage_is_not_writable));
            return context.getString(R.string.error_external_storage_is_not_writable);
        }
    }

    @SuppressLint("SimpleDateFormat")
    static String saveImage(Bitmap bitmapImage, Context context) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + context.getString(R.string.camera_folder));
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = (new SimpleDateFormat(context.getString(R.string.save_format_name))).format(new Date());
        String fileNameToSave = "JPEG_" + timeStamp + ".jpg";
        File file = new File(mediaStorageDir, fileNameToSave);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 40, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("error", Objects.requireNonNull(e.getMessage()));
        }
        MediaScannerConnection.scanFile(context, new String[]{file.getPath()}, new String[]{"image/jpeg"}, null);
        return fileNameToSave;
    }

    @SuppressLint({"SimpleDateFormat"})
    public static File createImageFile(Context context) throws IOException {
        String timeStamp = (new SimpleDateFormat(context.getString(R.string.save_format_name))).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        storageDir.mkdirs();
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        StringBuilder stringBuilder = (new StringBuilder()).append("file:");
        Objects.requireNonNull(image);
        MyApplication.fileName = stringBuilder.append(image.getAbsolutePath()).toString();
        return image;
    }

    static File findFile(File dir, String name) {
        File[] children = dir.listFiles();
        if (children != null) {
            for (File child : children) {
                if (child.isDirectory()) {
                    File found = findFile(child, name);
                    if (found != null) return found;
                } else {
                    if (name.equals(child.getName())) return child;
                }
            }
        }
        return null;
    }

    public static ReadingData readData() {
        File root = Environment.getExternalStorageDirectory();
        File file = findFile(root, "json.txt");

        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException ioException) {
            Log.e("Error", ioException.toString());
        }
        String json = text.toString();
//        Log.e("json", json);

        Gson gson = new GsonBuilder().create();
        return gson.fromJson(json, ReadingData.class);
    }

    @SuppressLint("SimpleDateFormat")
    public static boolean writeResponseBodyToDisk(ResponseBody body, Activity activity) {
        if (isExternalStorageWritable()) {
            try {
                String root = Environment.getExternalStorageDirectory().toString();
                String timeStamp = (new SimpleDateFormat(
                        activity.getString(R.string.save_format_name))).format(new Date());
                String fileName = DifferentCompanyManager.getActiveCompanyName().toString() + "_" +
                        timeStamp + ".apk";
                File futureStudioIconFile = new File(root + "/Download" + File.separator +
                        fileName);
                InputStream inputStream = null;
                OutputStream outputStream = null;
                try {
                    byte[] fileReader = new byte[7168];
                    long fileSize = body.contentLength();
                    long fileSizeDownloaded = 0;
                    inputStream = body.byteStream();
                    outputStream = new FileOutputStream(futureStudioIconFile);
                    while (true) {
                        int read = inputStream.read(fileReader);
                        if (read == -1) {
                            break;
                        }
                        outputStream.write(fileReader, 0, read);
                        fileSizeDownloaded += read;
                        Log.d(".apk file", "file download: " + fileSizeDownloaded + " of " + fileSize);
                    }
                    outputStream.flush();
                    new CustomToast().success(activity.getString(R.string.file_downloaded), Toast.LENGTH_LONG);
                    runFile(activity, fileName);

                    return true;
                } catch (IOException e) {
                    return false;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }

                    if (outputStream != null) {
                        outputStream.close();
                    }
                }
            } catch (IOException e) {
                return false;
            }
        } else {
            new CustomToast().warning(activity.getString(R.string.error_external_storage_is_not_writable));
        }
        return false;
    }

    public static void runFile(Activity activity, String fileName) {
        String root = Environment.getExternalStorageDirectory().toString();
        StrictMode.VmPolicy.Builder newBuilder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(newBuilder.build());
        File futureStudioIconFile = new File(root + "/Download" + File.separator + fileName);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(futureStudioIconFile), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }
}
