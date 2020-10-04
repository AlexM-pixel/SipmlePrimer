package com.example.mysympleapplication.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Fact implements Serializable {
    @SerializedName("temp")
   public int temp;
    @SerializedName("feels_like")
  public   int feels_like;
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
