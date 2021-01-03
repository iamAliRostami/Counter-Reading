package com.leon.counter_reading.tables;

import android.graphics.Bitmap;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

@Entity(tableName = "ForbiddenDto", indices = @Index(value = "customId", unique = true))
public class ForbiddenDto {
    @PrimaryKey(autoGenerate = true)
    public int customId;
    public int zoneId;
    public String description;
    public String preEshterak;
    public String nextEshterak;
    public String postalCode;
    public int tedadVahed;
    public String x;
    public String y;
    public String gisAccuracy;
    public boolean isSent;

    @Ignore
    @SerializedName("zoneId")
    public RequestBody zoneIdRequestBody;
    @Ignore
    @SerializedName("description")
    public RequestBody descriptionRequestBody;
    @Ignore
    @SerializedName("preEshterak")
    public RequestBody preEshterakRequestBody;
    @Ignore
    @SerializedName("nextEshterak")
    public RequestBody nextEshterakRequestBody;
    @Ignore
    @SerializedName("postalCode")
    public RequestBody postalCodeRequestBody;
    @Ignore
    @SerializedName("tedadVahed")
    public RequestBody tedadVahedRequestBody;
    @Ignore
    @SerializedName("x")
    public RequestBody xRequestBody;
    @Ignore
    @SerializedName("y")
    public RequestBody yRequestBody;
    @Ignore
    @SerializedName("gisAccuracy")
    public RequestBody gisAccuracyRequestBody;

    @Ignore
    public ArrayList<MultipartBody.Part> File;
    @Ignore
    public ArrayList<Bitmap> bitmaps;

    public void prepareToSend(double gisAccuracy, double x, double y, String postalCode,
                              String description, String preEshterak, String nextEshterak,
                              String tedadVahed, int zoneId) {

        this.gisAccuracy = String.valueOf(gisAccuracy);
        this.x = String.valueOf(x);
        this.y = String.valueOf(y);
        this.postalCode = postalCode;
        this.description = description;
        this.preEshterak = preEshterak;
        this.nextEshterak = nextEshterak;
        this.tedadVahed = Integer.parseInt(tedadVahed);
        this.zoneId = zoneId;

        zoneIdRequestBody = RequestBody.create(String.valueOf(zoneId),
                MediaType.parse("text/plain"));
        descriptionRequestBody = RequestBody.create(description, MediaType.parse("text/plain"));
        preEshterakRequestBody = RequestBody.create(preEshterak, MediaType.parse("text/plain"));
        nextEshterakRequestBody = RequestBody.create(nextEshterak, MediaType.parse("text/plain"));
        postalCodeRequestBody = RequestBody.create(postalCode, MediaType.parse("text/plain"));
        tedadVahedRequestBody = RequestBody.create(tedadVahed,
                MediaType.parse("text/plain"));
        xRequestBody = RequestBody.create(this.x, MediaType.parse("text/plain"));
        yRequestBody = RequestBody.create(this.y, MediaType.parse("text/plain"));
        gisAccuracyRequestBody = RequestBody.create(this.gisAccuracy, MediaType.parse("text/plain"));
    }

    public static class ForbiddenDtoResponses {
        public int status;
        public String message;
        public String generationDateTime;
        public boolean isValid;
        public ArrayList<String> targetObject;
    }
}
