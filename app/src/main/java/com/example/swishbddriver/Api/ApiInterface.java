package com.example.swishbddriver.Api;

import com.example.swishbddriver.Model.CheckModel;
import com.example.swishbddriver.Model.ProfileModel;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiInterface {

    @POST("phonenocheck")
    @FormUrlEncoded
    Call<List<CheckModel>> checkNo(@Field("phone_no") String phone_no);

    @Multipart
    @POST("driverall")
    Call<ResponseBody> uploadImage(@Part RequestBody file,
                                   @Part("title") RequestBody requestBody);

    @POST("driverall")
    @FormUrlEncoded
    Call<List<ProfileModel>> register(@Field("car_owner") int car_owner,
                                      @Field("details") String details,
                                      @Field("date")String date,
                                      @Field("full_name") String name,
                                      @Field("gender")String gender,
                                      @Field("email")String email,
                                      @Field("driver_address")String driver_address,
                                      @Field("phone")String phone,
                                      @Field("password")String password,
                                      @Field("remember_token")String remember_token,
                                      @Field("status")String status,
                                      @Field("carType")String carType,
                                      @Field("image") String image,
                                      @Field("rating")float rating,
                                      @Field("ratingCount")int ratingCount,
                                      @Field("rideCount")int rideCount,
                                      @Field("token")String token,
                                      @Field("editable")String editable);

    @GET("driver?")
    Call<List<ProfileModel>> getData(@Query("id") String id);
}
