package com.example.mysympleapplication.model.dataWeather;

import com.example.mysympleapplication.model.dataWeather.Fact;
import com.example.mysympleapplication.model.dataWeather.Forecasts;
import com.example.mysympleapplication.model.dataWeather.InfoObject;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SampleWeather implements Serializable {
    @SerializedName("now")
    private long nowTime;
    @SerializedName("now_dt")
    private String now_dt;
    @SerializedName("info")
    private InfoObject info;
    @SerializedName("fact")
    private Fact fact;
    @SerializedName("forecasts")
    private List<Forecasts> forecasts;

    public Fact getFact() {
        return fact;
    }

    public void setFact(Fact fact) {
        this.fact = fact;
    }
}
