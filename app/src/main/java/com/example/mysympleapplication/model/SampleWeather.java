package com.example.mysympleapplication.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SampleWeather implements Serializable {
    @SerializedName("now")
    long nowTime;
    @SerializedName("now_dt")
    String now_dt;
    @SerializedName("info")
    InfoObject info;
    @SerializedName("fact")
   public Fact fact;
    @SerializedName("forecasts")
    List<Forecasts> forecasts;
}
