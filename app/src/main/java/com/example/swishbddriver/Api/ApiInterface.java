package com.example.swishbddriver.Api;

import android.database.Observable;

import com.example.swishbddriver.Activity.DriverProfile;
import com.example.swishbddriver.Model.CheckModel;
import com.example.swishbddriver.Model.CustomerProfile;
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
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @POST("phonenocheck")
    @FormUrlEncoded
    Call<List<CheckModel>> checkNo(@Field("phone_no") String phone_no);

    @Multipart
    @POST("driversave")
    Call<ResponseBody> register(@Part("car_owner") int car_owner,
                                @Part("details") RequestBody details,
                                @Part("date") RequestBody date,
                                @Part("full_name") RequestBody name,
                                @Part("gender") RequestBody gender,
                                @Part("email") RequestBody email,
                                @Part("driver_address") RequestBody driver_address,
                                @Part("phone") RequestBody phone,
                                @Part("password") RequestBody password,
                                @Part("remember_token") RequestBody remember_token,
                                @Part("status") RequestBody status,
                                @Part("carType") RequestBody carType,
                                @Part MultipartBody.Part image,
                                @Part("rating") int rating,
                                @Part("ratingCount") int ratingCount,
                                @Part("rideCount") int rideCount,
                                @Part("token") RequestBody token,
                                @Part("editable") RequestBody editable);

    @GET("driver?")
    Call<List<ProfileModel>> getData(@Query("id") String id);

    @GET("customer?")
    Call<List<CustomerProfile>> getCustomerData(@Query("id") String customer_id);

    @FormUrlEncoded
    @PUT("driverupdate/{driver_id}")
    Call<List<ProfileModel>> updateData(@Path("driver_id") String driver_id,
                                        @Field("full_name") String full_name,
                                        @Field("email") String email,
                                        @Field("gender") String gender);

    @FormUrlEncoded
    @PUT("driverpassword/{driver_id}")
    Call<List<ProfileModel>> changePassword(@Path("driver_id") String driver_id,
                                            @Field("password") String password);

    @FormUrlEncoded
    @PUT("rating/{driver_id}")
    Call<List<ProfileModel>> updateRating(@Path("driver_id") String driver_id,
                                          @Field("rating") float rating,
                                          @Field("ratingCount") int ratingCount);

}
