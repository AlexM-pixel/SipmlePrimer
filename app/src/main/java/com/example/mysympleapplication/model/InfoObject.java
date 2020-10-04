package com.example.mysympleapplication.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class InfoObject implements Serializable {
    @SerializedName("lat")
    String lat;
    @SerializedName("lon")
    String lon;
}
