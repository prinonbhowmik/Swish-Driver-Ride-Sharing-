package com.example.swishbddriver.Api;

import android.database.Observable;

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
    @POST("driversave")
    Call<ResponseBody> register(@Part("car_owner") int  car_owner,
                                      @Part("details") String  details,
                                      @Part("date")String  date,
                                      @Part("full_name") String  name,
                                      @Part("gender")String  gender,
                                      @Part("email")String  email,
                                      @Part("driver_address")String  driver_address,
                                      @Part("phone")String  phone,
                                      @Part("password")String  password,
                                      @Part("remember_token")String  remember_token,
                                      @Part("status")String  status,
                                      @Part("carType")String  carType,
                                      @Part MultipartBody.Part image,
                                      @Part("rating")float  rating,
                                      @Part("ratingCount")int  ratingCount,
                                      @Part("rideCount")int  rideCount,
                                      @Part("token")String  token,
                                      @Part("editable")String  editable);

    @GET("driver?")
    Call<List<ProfileModel>> getData(@Query("id") String id);
}
