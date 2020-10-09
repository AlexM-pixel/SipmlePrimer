package com.example.mysympleapplication.model.dataWeather;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Fact implements Serializable {
    @SerializedName("temp")
    private int temp;
    @SerializedName("feels_like")
    private int feels_like;
    @SerializedName("temp_water")
    private int temp_water;
    @SerializedName("icon")
    private String icon;
    @SerializedName("condition")
    private String condition;
    @SerializedName("wind_speed")
    private float wind_speed;
    @SerializedName("wind_gust")
    private float wind_gust;
    @SerializedName("wind_dir")
    private String wind_dir;
    @SerializedName("pressure_mm")
    private int pressure_mm;
    @SerializedName("pressure_pa")
    private int pressure_pa;

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }
}
