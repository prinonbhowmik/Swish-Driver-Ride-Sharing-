package com.example.swishbddriver.Api;


import com.example.swishbddriver.Model.ApiDeviceToken;
import com.example.swishbddriver.Model.BookRegularModel;
import com.example.swishbddriver.Model.Car;
import com.example.swishbddriver.Model.CarModel;
import com.example.swishbddriver.Model.CarModleYear;
import com.example.swishbddriver.Model.CheckModel;
import com.example.swishbddriver.Model.CouponShow;
import com.example.swishbddriver.Model.CustomerProfile;
import com.example.swishbddriver.Model.DriverAllRidePrice;
import com.example.swishbddriver.Model.DriverEarnings;
import com.example.swishbddriver.Model.DriverInfo;
import com.example.swishbddriver.Model.EarningsShowModel;
import com.example.swishbddriver.Model.HourlyRideModel;
import com.example.swishbddriver.Model.NotificationModel;
import com.example.swishbddriver.Model.PayHistoryModel;
import com.example.swishbddriver.Model.ProfileModel;
import com.example.swishbddriver.Model.RidingRate;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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

    @POST("dforgotpassword")
    @FormUrlEncoded
    Call<List<CheckModel>> forgotPassword(@Field("phone_no") String phone_no);

    @GET("referral-commission?")
    Call<List<CustomerProfile>> getReffarelCommision(@Query("customer_id") String customer_id);


    @PUT("dpasswordupdate/{customer_id}")
    @FormUrlEncoded
    Call<List<ProfileModel>> resetPassword(@Path("customer_id") String customer_id,
                                           @Field("password") String password);

    @Multipart
    @POST("driversave")
    Call<List<ProfileModel>> register(@Part("details") RequestBody details,
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
                                      @Part("rating") float rating,
                                      @Part("ratingCount") int ratingCount,
                                      @Part("rideCount") int rideCount,
                                      @Part("token") RequestBody token,
                                      @Part("editable") RequestBody editable,
                                      @Part("referral") RequestBody referral);


    @GET("driver?")
    Call<List<ProfileModel>> getData(@Query("id") String id);

    @GET("driverinfo?")
    Call<List<DriverInfo>> getRegistrationData(@Query("id") String id);

    @GET("customer?")
    Call<List<CustomerProfile>> getCustomerData(@Query("id") String customer_id);

    @Multipart
    @POST("driverupdate")
    Call<List<ProfileModel>> updateData(@Part("driver_id") RequestBody driver_id,
                                        @Part MultipartBody.Part image,
                                        @Part("full_name") RequestBody full_name,
                                        @Part("email") RequestBody email,
                                        @Part("gender") RequestBody gender,
                                        @Part("date") RequestBody date);

    @Multipart
    @POST("driverupdate")
    Call<List<ProfileModel>> updateDatawithoutimage(@Part("driver_id") RequestBody driver_id,
                                                    @Part("full_name") RequestBody full_name,
                                                    @Part("email") RequestBody email,
                                                    @Part("gender") RequestBody gender,
                                                    @Part("date") RequestBody date);

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
    Call<List<BookRegularModel>> confirmRide(@Path("bookingId") String bookingId,
                                             @Field("bookingStatus") String bookingStatus,
                                             @Field("driverId") String driverId);

    @FormUrlEncoded
    @PUT("hourlyrideconfirm/{bookingId}")
    Call<List<HourlyRideModel>> confirmHourRide(@Path("bookingId") String bookingId,
                                                @Field("bookingStatus") String bookingStatus,
                                                @Field("driverId") String driverId);

    @FormUrlEncoded
    @PUT("bookingtripstart/{bookingId}")
    Call<List<BookRegularModel>> startTripData(@Path("bookingId") String bookingId,
                                               @Field("pickUpTime") String pickupTime,
                                               @Field("pickUpDate") String pickupDate,
                                               @Field("pickUpLat") String pickupLat,
                                               @Field("pickUpLon") String pickupLon,
                                               @Field("pickUpPlace") String pickupPlace,
                                               @Field("rideStatus") String rideStatus);

    @FormUrlEncoded
    @PUT("hourlyridestart/{bookingId}")
    Call<List<HourlyRideModel>> startHourTripData(@Path("bookingId") String bookingId,
                                                  @Field("pickUpTime") String pickupTime,
                                                  @Field("pickUpDate") String pickupDate,
                                                  @Field("pickUpLat") String pickupLat,
                                                  @Field("pickUpLon") String pickupLon,
                                                  @Field("pickUpPlace") String pickupPlace,
                                                  @Field("rideStatus") String rideStatus);

    @FormUrlEncoded
    @PUT("bookingtripend/{bookingId}")
    Call<List<BookRegularModel>> endTripData(@Path("bookingId") String bookingId,
                                             @Field("rideStatus") String rideStatus,
                                             @Field("destinationLat") String destinationLat,
                                             @Field("destinationLon") String destinationLon,
                                             @Field("destinationPlace") String destinationPlace,
                                             @Field("endTime") String endTime);

    @FormUrlEncoded
    @PUT("hourlytripend/{bookingId}")
    Call<List<HourlyRideModel>> endHourTripData(@Path("bookingId") String bookingId,
                                                @Field("rideStatus") String rideStatus,
                                                @Field("destinationLat") String destinationLat,
                                                @Field("destinationLon") String destinationLon,
                                                @Field("destinationPlace") String destinationPlace,
                                                @Field("endTime") String endTime,
                                                @Field("price") String price,
                                                @Field("discount") String discount,
                                                @Field("finalPrice") String updatedPrice,
                                                @Field("totalTime") String time);

    @GET("bookingrate?")
    Call<List<RidingRate>> getPrice(@Query("id") String car_type);

    @FormUrlEncoded
    @PUT("priceupdate/{bookingId}")
    Call<List<BookRegularModel>> priceUpdate(@Path("bookingId") String bookingId,
                                             @Field("price") String price,
                                             @Field("discount") String discount,
                                             @Field("finalPrice") String updatedPrice,
                                             @Field("totalDistance") String km,
                                             @Field("totalTime") String time);

    @FormUrlEncoded
    @PUT("hourlypriceupdate/{bookingId}")
    Call<List<HourlyRideModel>> hourpriceUpdate(@Path("bookingId") String bookingId,
                                                @Field("price") String price,
                                                @Field("discount") String discount,
                                                @Field("finalPrice") String updatedPrice,
                                                @Field("totalTime") String time);

    @FormUrlEncoded
    @PUT("rideCountUpdate/{driver_id}")
    Call<List<ProfileModel>> rideCountUpdate(@Path("driver_id") String driver_id,
                                             @Field("rideCount") int rideCount);

    @FormUrlEncoded
    @PUT("detailsupdate/{driver_id}")
    Call<List<ProfileModel>> updateBio(@Path("driver_id") String driver_id,
                                       @Field("details") String details);

    @GET("customerbookinglist?")
    Call<List<BookRegularModel>> rideHistory(@Query("id") String customer_id);

    @GET("car")
    Call<List<Car>> getCar();

    @GET("carmodelsearch?car_name=")
    Call<List<CarModel>> getCarModel(@Query("car_name") String car_name);

    @GET("carmodelyear")
    Call<List<CarModleYear>> getCarYear();

    @FormUrlEncoded
    @PUT("drivercarinfo/{driver_id}")
    Call<List<DriverInfo>> driverCarInfo(@Path("driver_id") String driver_id,
                                         @Field("car_name") String car_name,
                                         @Field("car_model") String car_model,
                                         @Field("production_year") String production_year,
                                         @Field("plate_number") String plate_number,
                                         @Field("car_owner") String car_owner,
                                         @Field("partner_name") String partner_name,
                                         @Field("partner_phone") String partner_phone,
                                         @Field("partner_address") String partner_address);

    @FormUrlEncoded
    @PUT("drivercarinfo/{driver_id}")
    Call<List<DriverInfo>> driverCarInfoWithOwner(@Path("driver_id") String driver_id,
                                                  @Field("car_name") String car_name,
                                                  @Field("car_model") String car_model,
                                                  @Field("production_year") String production_year,
                                                  @Field("plate_number") String plate_number,
                                                  @Field("car_owner") String car_owner);

    @FormUrlEncoded
    @PUT("drivercarinfo/{driver_id}")
    Call<List<DriverInfo>> partnerInfo(@Path("driver_id") String driver_id,
                                       @Field("partner_name") String partner_name,
                                       @Field("partner_phone") String partner_phone,
                                       @Field("partner_address") String partner_address);

    @Multipart
    @POST("drivernid")
    Call<List<DriverInfo>> driverNid1(@Part("driver_id") RequestBody driver_id,
                                      @Part MultipartBody.Part nid_front);

    @Multipart
    @POST("drivernid")
    Call<List<DriverInfo>> driverNid2(@Part("driver_id") RequestBody driver_id,
                                      @Part MultipartBody.Part nid_back);

    @Multipart
    @POST("drivernid")
    Call<List<DriverInfo>> driverLicense1(@Part("driver_id") RequestBody driver_id,
                                          @Part MultipartBody.Part driving_license_photosF);

    @Multipart
    @POST("drivernid")
    Call<List<DriverInfo>> driverLicense2(@Part("driver_id") RequestBody driver_id,
                                          @Part MultipartBody.Part driving_license_photosB);

    @Multipart
    @POST("drivernid")
    Call<List<DriverInfo>> selfie(@Part("driver_id") RequestBody driver_id,
                                  @Part MultipartBody.Part selfie);

    @FormUrlEncoded
    @POST("driverearningspost")
    Call<List<DriverEarnings>> DriverEarnings(@Field("ride_id") String ride_id,
                                              @Field("customer_id") String customer_id,
                                              @Field("driver_id") String driver_id,
                                              @Field("ride_type") String ride_type,
                                              @Field("total_amount") int total_amount,
                                              @Field("pay_type") String pay_type,
                                              @Field("discount_amount") int discount_amount,
                                              @Field("commission") int commission,
                                              @Field("update_price") int update_price);

    @GET("driverbookinglist?")
    Call<List<BookRegularModel>> driverRideHistory(@Query("id") String driver_id);

    @GET("driverearninghistory?")
    Call<List<DriverAllRidePrice>> driverAllRidePrice(@Query("driver_id") String driver_id);

    @GET("driverhourlylist?")
    Call<List<HourlyRideModel>> driverInsideHistory(@Query("id") String driver_id);

    @GET("paymentshow?")
    Call<List<PayHistoryModel>> driverPayHistory(@Query("id") String driver_id);

    @GET("couponshow?")
    Call<List<CouponShow>> getValidCoupon(@Query("customer_id") String customer_id);

    @FormUrlEncoded
    @PUT("walletupdate/{id}")
    Call<List<CustomerProfile>> walletValue(@Path("id") String customer_id,
                                            @Field("wallet") int wallet,
                                            @Field("e_wallet") int e_wallet);

    @FormUrlEncoded
    @PUT("walletupdate/{id}")
    Call<List<CustomerProfile>> updatewalletValue(@Path("id") String customer_id,
                                            @Field("wallet") int wallet);

    @FormUrlEncoded
    @PUT("hourlycashreceived/{bookingId}")
    Call<List<BookRegularModel>> hourlyCashReceived(@Path("bookingId") String bookingId,
                                                    @Field("cashReceived") String cashReceived);

    @FormUrlEncoded
    @PUT("bookingcashreceived/{bookingId}")
    Call<List<BookRegularModel>> BookingCashReceived(@Path("bookingId") String bookingId,
                                                     @Field("cashReceived") String cashReceived);

    @GET("driverearningstotal?")
    Call<List<EarningsShowModel>> getEarningsData(@Query("driver_id") String driver_id);

    @GET("driver-noti-list?")
    Call<List<NotificationModel>> getNotificationData(@Query("driver_id") String driver_id,
                                                      @Query("carType") String carType);

    @POST("driver_device_token")
    @FormUrlEncoded
    Call<List<ApiDeviceToken>> updateToken(@Field("driver_id") String driver_id,
                                           @Field("device_token") String token);

    @POST("driver_remove_token")
    @FormUrlEncoded
    Call<List<ApiDeviceToken>> deleteToken(@Field("driver_id") String driver_id);

}
