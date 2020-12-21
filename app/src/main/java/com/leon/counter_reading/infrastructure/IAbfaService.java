package com.leon.counter_reading.infrastructure;

import com.leon.counter_reading.tables.Image;
import com.leon.counter_reading.tables.LoginFeedBack;
import com.leon.counter_reading.tables.LoginInfo;
import com.leon.counter_reading.tables.PasswordInfo;
import com.leon.counter_reading.tables.ReadingData;

import java.util.ArrayList;
import java.util.UUID;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface IAbfaService {

    @POST("kontoriNew/V1/Account/Login")
    Call<LoginFeedBack> login(@Body LoginInfo logininfo);

    @POST("kontoriNew/v1/api/ChangePassword")
    Call<Integer> changePassword(@Body PasswordInfo passwordInfo);

    @POST("KontoriNew/V1/Load/Data")
    Call<ReadingData> loadData();

//    @POST("KontoriNew/V1/Upload/Single")
//    Call<Integer> fileUploadSingle(@Body Image image);

    @Multipart
    @POST("KontoriNew/V1/Upload/Single")
    Call<Integer> fileUploadSingle(
            @Part MultipartBody.Part imageFiles,
            @Part("OnOffLoadId") String OnOffLoadId,
            @Part("Description") String Description);

    @POST("KontoriNew/V1/Upload/Grouped")
    Call<Image.ImageUploadResponse> fileUploadGrouped(@Body Image.ImageGrouped image);

    @Multipart
    @POST("KontoriNew/V1/Upload/Grouped")
    Call<Image.ImageUploadResponse> fileUploadGrouped(
            @Part ArrayList<MultipartBody.Part> imageFiles,
            @Part("OnOffLoadId") UUID OnOffLoadId,
            @Part("Description") String Description);

    @Multipart
    @POST("KontoriNew/V1/Upload/Grouped")
    Call<Image.ImageUploadResponse> fileUploadGrouped(
            @Part ArrayList<MultipartBody.Part> imageFiles,
            @Part("OnOffLoadId") String OnOffLoadId,
            @Part("Description") String Description);
    @Multipart
    @POST("KontoriNew/V1/Upload/Grouped")
    Call<Image.ImageUploadResponse> fileUploadGrouped(
            @Part ArrayList<MultipartBody.Part> imageFiles,
            @Part("OnOffLoadId") RequestBody OnOffLoadId,
            @Part("Description") String Description);

    @Multipart
    @POST("KontoriNew/V1/Upload/Multiple")
    Call<Integer> fileUploadMultiple(@Body Image.ImageMultiple image);
}

