package com.example.mysympleapplication.hw8;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

class InfoObject implements Serializable {
    @SerializedName("lat")
    String lat;
    @SerializedName("lon")
    String lon;

}
