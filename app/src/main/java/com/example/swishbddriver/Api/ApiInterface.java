package com.example.swishbddriver.Api;

import android.database.Observable;

import com.example.swishbddriver.Activity.DriverProfile;
import com.example.swishbddriver.Model.BookForLaterModel;
import com.example.swishbddriver.Model.CheckModel;
import com.example.swishbddriver.Model.CustomerProfile;
import com.example.swishbddriver.Model.ProfileModel;
import com.example.swishbddriver.Model.RidingRate;

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
                                        @Field("gender") String gender,
                                        @Field("date") String date);

    @FormUrlEncoded
    @PUT("driverpassword/{driver_id}")
    Call<List<ProfileModel>> changePassword(@Path("driver_id") String driver_id,
                                            @Field("password") String password);

    @FormUrlEncoded
    @PUT("rating/{driver_id}")
    Call<List<ProfileModel>> updateRating(@Path("driver_id") String driver_id,
                                          @Field("rating") float rating,
                                          @Field("ratingCount") int ratingCount);

    @FormUrlEncoded
    @PUT("bookingconfirm/{bookingId}")
    Call<List<BookForLaterModel>> confirmRide(@Path("bookingId") String bookingId,
                                              @Field("bookingStatus") String bookingStatus,
                                              @Field("driverId") String driverId);

    @FormUrlEncoded
    @PUT("bookingtripstart/{bookingId}")
    Call<List<BookForLaterModel>> startTripData(@Path("bookingId") String bookingId,
                                                @Field("pickupTime") String pickupTime,
                                                @Field("pickupLat") String pickupLat,
                                                @Field("pickupLon") String pickupLon,
                                                @Field("pickupPlace") String pickupPlace,
                                                @Field("rideStatus") String rideStatus);

    @FormUrlEncoded
    @PUT("bookingtripend/{bookingId}")
    Call<List<BookForLaterModel>> endTripData(@Path("bookingId") String bookingId,
                                              @Field("rideStatus") String rideStatus,
                                              @Field("destinationLat") String destinationLat,
                                              @Field("destinationLon") String destinationLon,
                                              @Field("destinationPlace") String destinationPlace,
                                              @Field("endTime") String endTime);

    @GET("bookingrate?")
    Call<List<RidingRate>> getPrice(@Query("id") String car_type);

    @FormUrlEncoded
    @PUT("priceupdate/{bookingId}")
    Call<List<BookForLaterModel>> priceUpdate(@Path("bookingId") String bookingId,
                                              @Field("price") String price);

    @FormUrlEncoded
    @PUT("rideCountUpdate/{driver_id}")
    Call<List<ProfileModel>> rideCountUpdate(@Path("driver_id") String driver_id,
                                              @Field("rideCount") int rideCount);

    @FormUrlEncoded
    @PUT("detailsupdate/{driver_id}")
    Call<List<ProfileModel>> updateBio(@Path("driver_id") String driver_id,
                                            @Field("details") String details);


}
