package com.example.mysympleapplication.hw8;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

class Fact implements Serializable {
    @SerializedName("temp")
    int temp;
    @SerializedName("feels_like")
    int feels_like;
    @SerializedName("temp_water")
    int temp_water;
    @SerializedName("icon")
    String icon;
    @SerializedName("condition")
    String condition;
    @SerializedName("wind_speed")
    float wind_speed;
    @SerializedName("wind_gust")
    float wind_gust;
    @SerializedName("wind_dir")
    String wind_dir;
    @SerializedName("pressure_mm")
    int pressure_mm;
    @SerializedName("pressure_pa")
    int pressure_pa;
}
