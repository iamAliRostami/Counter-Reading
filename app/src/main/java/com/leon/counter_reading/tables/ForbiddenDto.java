package com.leon.counter_reading.tables;

import android.graphics.Bitmap;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

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
    public ArrayList<MultipartBody.Part> File;
    @Ignore
    public ArrayList<Bitmap> bitmaps;
    @Ignore
    public ForbiddenDtoRequest forbiddenDtoRequest;

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
        forbiddenDtoRequest = new ForbiddenDtoRequest();
        prepareRequestBody(File, zoneId, description, preEshterak, nextEshterak, tedadVahed, x, y, gisAccuracy);
    }

    void prepareRequestBody(ArrayList<MultipartBody.Part> file, int zoneId, String description,
                            String preEshterak, String nextEshterak, String tedadVahed,
                            double x, double y, double gisAccuracy) {
        File.addAll(file);
        forbiddenDtoRequest.description = RequestBody.create(description, MediaType.parse("text/plain"));
        forbiddenDtoRequest.preEshterak = RequestBody.create(preEshterak, MediaType.parse("text/plain"));
        forbiddenDtoRequest.nextEshterak = RequestBody.create(nextEshterak, MediaType.parse("text/plain"));
        forbiddenDtoRequest.postalCode = RequestBody.create(postalCode, MediaType.parse("text/plain"));
        forbiddenDtoRequest.tedadVahed = RequestBody.create(tedadVahed,
                MediaType.parse("text/plain"));
        forbiddenDtoRequest.x = RequestBody.create(String.valueOf(x), MediaType.parse("text/plain"));
        forbiddenDtoRequest.y = RequestBody.create(String.valueOf(y), MediaType.parse("text/plain"));
        forbiddenDtoRequest.gisAccuracy = RequestBody.create(String.valueOf(gisAccuracy), MediaType.parse("text/plain"));
        forbiddenDtoRequest.zoneId = RequestBody.create(String.valueOf(zoneId),
                MediaType.parse("text/plain"));
    }

    public ForbiddenDtoRequest prepareRequestBody() {
        forbiddenDtoRequest = new ForbiddenDtoRequest();
        forbiddenDtoRequest.description = RequestBody.create(description, MediaType.parse("text/plain"));
        forbiddenDtoRequest.preEshterak = RequestBody.create(preEshterak, MediaType.parse("text/plain"));
        forbiddenDtoRequest.nextEshterak = RequestBody.create(nextEshterak, MediaType.parse("text/plain"));
        forbiddenDtoRequest.postalCode = RequestBody.create(postalCode, MediaType.parse("text/plain"));
        forbiddenDtoRequest.tedadVahed = RequestBody.create(String.valueOf(tedadVahed),
                MediaType.parse("text/plain"));
        forbiddenDtoRequest.x = RequestBody.create(String.valueOf(x), MediaType.parse("text/plain"));
        forbiddenDtoRequest.y = RequestBody.create(String.valueOf(y), MediaType.parse("text/plain"));
        forbiddenDtoRequest.gisAccuracy = RequestBody.create(String.valueOf(gisAccuracy), MediaType.parse("text/plain"));
        forbiddenDtoRequest.zoneId = RequestBody.create(String.valueOf(zoneId),
                MediaType.parse("text/plain"));
        return forbiddenDtoRequest;
    }

    public static class ForbiddenDtoResponses {
        public int status;
        public String message;
        public String generationDateTime;
        public boolean isValid;
        public ArrayList<String> targetObject;
    }

    public static class ForbiddenDtoRequest {
        public RequestBody zoneId;
        public RequestBody description;
        public RequestBody preEshterak;
        public RequestBody nextEshterak;
        public RequestBody postalCode;
        public RequestBody tedadVahed;
        public RequestBody x;
        public RequestBody y;
        public RequestBody gisAccuracy;

        public ForbiddenDtoRequest() {
        }
    }

    public static class ForbiddenDtoMultiple {
        public Integer zoneId;
        public final String description;
        public final String preEshterak;
        public final String nextEshterak;
        public final String postalCode;
        public final int tedadVahed;
        public final String x;
        public final String y;
        public final String gisAccuracy;

        public ForbiddenDtoMultiple(Integer zoneId, String description, String preEshterak,
                                    String nextEshterak, String postalCode, int tedadVahed,
                                    String x, String y, String gisAccuracy) {
            if (zoneId != 0)
                this.zoneId = zoneId;
            this.description = description;
            this.preEshterak = preEshterak;
            this.nextEshterak = nextEshterak;
            this.postalCode = postalCode;
            this.tedadVahed = tedadVahed;
            this.x = x;
            this.y = y;
            this.gisAccuracy = gisAccuracy;
        }
    }

    public static class ForbiddenDtoRequestMultiple {
        public final ArrayList<ForbiddenDtoMultiple> forbiddenDtos;

        public ForbiddenDtoRequestMultiple() {
            forbiddenDtos = new ArrayList<>();
        }
    }
}
