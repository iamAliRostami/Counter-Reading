package com.leon.counter_reading.infrastructure;

import com.leon.counter_reading.tables.ForbiddenDto;
import com.leon.counter_reading.tables.Image;
import com.leon.counter_reading.tables.LastInfo;
import com.leon.counter_reading.tables.LoginFeedBack;
import com.leon.counter_reading.tables.LoginInfo;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.tables.PasswordInfo;
import com.leon.counter_reading.tables.ReadingData;
import com.leon.counter_reading.tables.SimpleResponse;
import com.leon.counter_reading.tables.Voice;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface IAbfaService {

    @POST("kontoriNew/V1/Account/Login")
    Call<LoginFeedBack> login(@Body LoginInfo logininfo);

    @POST("kontoriNew/V1/User/RegisterDevice")
    Call<LoginFeedBack> signSerial(@Body LoginInfo logininfo);

    @POST("kontoriNew/V1/Account/ChangePassword")
    Call<SimpleResponse> changePassword(@Body PasswordInfo passwordInfo);

    @POST("KontoriNew/V1/Load/Data")
    Call<ReadingData> loadData();

    @Multipart
    @POST("KontoriNew/V1/Upload/Single")
    Call<Integer> fileUploadSingle(
            @Part MultipartBody.Part voice,
            @Part("OnOffLoadId") String OnOffLoadId,
            @Part("Description") String Description);

    @Multipart
    @POST("KontoriNew/V1/Upload/Grouped")
    Call<Image.ImageUploadResponse> fileUploadGrouped(
            @Part ArrayList<MultipartBody.Part> imageFiles,
            @Part("OnOffLoadId") RequestBody OnOffLoadId,
            @Part("Description") RequestBody Description);

    @Multipart
    @POST("KontoriNew/V1/Upload/Grouped")
    Call<Image.ImageUploadResponse> fileUploadGrouped(
            @Part("OnOffLoadId") RequestBody OnOffLoadId,
            @Part("Description") RequestBody Description);

    @Multipart
    @POST("KontoriNew/V1/Upload/Grouped")
    Call<Image.ImageUploadResponse> fileUploadGrouped(
            @Part ArrayList<MultipartBody.Part> imageFiles,
            @Part("OnOffLoadId") RequestBody OnOffLoadId);

    @Multipart
    @POST("KontoriNew/V1/Upload/Multiple")
    Call<Image.ImageUploadResponse> fileUploadMultiple(
            @Part ArrayList<MultipartBody.Part> imageFiles,
            @Part("OnOffLoadId") ArrayList<RequestBody> OnOffLoadId,
            @Part("Description") ArrayList<RequestBody> Description);

    @Multipart
    @POST("KontoriNew/V1/Upload/Multiple")
    Call<Voice.VoiceUploadResponse> voiceUploadMultiple(
            @Part ArrayList<MultipartBody.Part> imageFiles,
            @Part("OnOffLoadId") ArrayList<RequestBody> OnOffLoadId,
            @Part("Description") ArrayList<RequestBody> Description);

    @POST("KontoriNew/V1/OffLoad/Data")
    Call<OnOffLoadDto.OffLoadResponses> OffLoadData(
            @Part("isFinal") RequestBody isFinal,
            @Part("offLoads") RequestBody offLoads,
            @Part("offLoadReports") RequestBody offLoadReports);


    @POST("KontoriNew/V1/OffLoad/Data")
    Call<OnOffLoadDto.OffLoadResponses> OffLoadData(@Body OnOffLoadDto.OffLoadData offLoads);

    @GET("KontoriNew/V1/Apk/Last")
    Call<ResponseBody> getLastApk();

    @GET("KontoriNew/V1/Apk/LastInfo")
    Call<LastInfo> getLastInfo();


    @Multipart
    @POST("KontoriNew/V1/Forbidden/Single")
    Call<ForbiddenDto.ForbiddenDtoResponses> singleForbidden(
            @Part ArrayList<MultipartBody.Part> files,
            @Part("zoneId") RequestBody zoneId,
            @Part("Description") RequestBody Description,
            @Part("PreEshterak") RequestBody preEshterak,
            @Part("NextEshterak") RequestBody nextEshterak,
            @Part("PostalCode") RequestBody postalCode,
            @Part("TedadVahed") RequestBody TedadVahed,
            @Part("x") RequestBody x,
            @Part("y") RequestBody y,
            @Part("GisAccuracy") RequestBody gisAccuracy);

    @Multipart
    @POST("KontoriNew/V1/Forbidden/Single")
    Call<ForbiddenDto.ForbiddenDtoResponses> singleForbidden(
            @Part("zoneId") RequestBody zoneId,
            @Part("Description") RequestBody Description,
            @Part("PreEshterak") RequestBody preEshterak,
            @Part("NextEshterak") RequestBody nextEshterak,
            @Part("PostalCode") RequestBody postalCode,
            @Part("TedadVahed") RequestBody TedadVahed,
            @Part("x") RequestBody x,
            @Part("y") RequestBody y,
            @Part("GisAccuracy") RequestBody gisAccuracy);

    @Multipart
    @POST("KontoriNew/V1/Forbidden/Single")
    Call<ForbiddenDto.ForbiddenDtoResponses> singleForbidden(
            @Part ArrayList<MultipartBody.Part> files,
            @Part("Description") RequestBody Description,
            @Part("PreEshterak") RequestBody preEshterak,
            @Part("NextEshterak") RequestBody nextEshterak,
            @Part("PostalCode") RequestBody postalCode,
            @Part("TedadVahed") RequestBody TedadVahed,
            @Part("x") RequestBody x,
            @Part("y") RequestBody y,
            @Part("GisAccuracy") RequestBody gisAccuracy);

    @Multipart
    @POST("KontoriNew/V1/Forbidden/Single")
    Call<ForbiddenDto.ForbiddenDtoResponses> singleForbidden(
            @Part("Description") RequestBody Description,
            @Part("PreEshterak") RequestBody preEshterak,
            @Part("NextEshterak") RequestBody nextEshterak,
            @Part("PostalCode") RequestBody postalCode,
            @Part("TedadVahed") RequestBody TedadVahed,
            @Part("x") RequestBody x,
            @Part("y") RequestBody y,
            @Part("GisAccuracy") RequestBody gisAccuracy);

    //    @Multipart
    @POST("KontoriNew/V1/Forbidden/Multiple")
    Call<ForbiddenDto.ForbiddenDtoResponses> multipleForbidden(@Body ForbiddenDto.ForbiddenDtoRequestMultiple forbiddenDto);
}

