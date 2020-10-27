package com.example.mysympleapplication.hw8;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface WeatherService {           // 0b322771-4cf4-46a0-a6b8-6fbd89e2693b
    @Headers({"X-Yandex-API-Key:0b322771-4cf4-46a0-a6b8-6fbd89e2693b"})
    @GET("/v1/forecast?")
    Call<SampleWeather> getWheatherNow(@Query("lat") String lat, @Query("lon") String lon);

}
