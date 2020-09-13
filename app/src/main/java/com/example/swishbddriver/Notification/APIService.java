package com.example.swishbddriver.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAbgLhnkE:APA91bGUG9RBWF5Q3OgosFfvcIoKuvCKhpbnvNjvuwaJpPoQZuvckY6ZPwICW5QEo0U89LLXIIYKpcIGflNH0bj-scpfdsqBz42lR1zNBYEsXxJk4-SkjDsHO_Mg7tDjGXLeidxguQxP"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
