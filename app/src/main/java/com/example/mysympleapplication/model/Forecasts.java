package com.example.mysympleapplication.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Forecasts implements Serializable {
    @SerializedName("date")
    String date;
    @SerializedName("temp_min")
    int tempMin;
    @SerializedName("temp_max")
    int tempMax;
}
