package com.example.mysympleapplication.hw8;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

class Forecasts implements Serializable {
    @SerializedName("date")
    String date;
    @SerializedName("temp_min")
    int tempMin;
    @SerializedName("temp_max")
    int tempMax;
}
