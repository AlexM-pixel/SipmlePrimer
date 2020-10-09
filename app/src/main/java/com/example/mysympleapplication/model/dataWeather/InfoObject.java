package com.example.mysympleapplication.model.dataWeather;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class InfoObject implements Serializable {
    @SerializedName("lat")
    private String lat;
    @SerializedName("lon")
    private String lon;
}
