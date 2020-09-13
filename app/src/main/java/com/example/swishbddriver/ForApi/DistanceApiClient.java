package com.example.swishbddriver.ForApi;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface DistanceApiClient {
    @GET("distancematrix/json")
    Call<DistanceResponse> getDistanceInfo(@QueryMap Map<String, String> parameters);
}
