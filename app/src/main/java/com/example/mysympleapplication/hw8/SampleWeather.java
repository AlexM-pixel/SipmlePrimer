package com.example.mysympleapplication.hw8;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class SampleWeather {
    @SerializedName("now")
    long nowTime;
    @SerializedName("now_dt")
    String now_dt;
    @SerializedName("info")
    InfoObject info;
    @SerializedName("fact")
    Fact fact;
    @SerializedName("forecasts")
    List<Forecasts> forecasts;

}
