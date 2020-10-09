package com.example.mysympleapplication.model.dataWeather;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Forecasts implements Serializable {
    @SerializedName("date")
    private String date;
    @SerializedName("temp_min")
    private int tempMin;
    @SerializedName("temp_max")
    private int tempMax;
}
