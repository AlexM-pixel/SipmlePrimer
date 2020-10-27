package com.example.mysympleapplication.hw8;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyRetrofit {
    private static Retrofit retrofit;
    public static Retrofit getInstance() {
        if (retrofit == null) {
            Gson gson = new GsonBuilder().create();
             retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .baseUrl("https://api.weather.yandex.ru")
                    .build();
        }
        return retrofit;
    }
}
