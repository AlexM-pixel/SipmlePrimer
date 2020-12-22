package com.example.mysympleapplication.hw9.model;


import androidx.room.ColumnInfo;

public class FriendsSumValue {
    @ColumnInfo(name = "dateM")
    private String dateM;
    @ColumnInfo(name = "value_post")
    private float value_spends;

    public FriendsSumValue (String dateM, float value_spends) {
        this.dateM= dateM;
        this.value_spends= value_spends;
    }

    public String getDateM() {
        return dateM;
    }

    public void setDateM(String dateM) {
        this.dateM = dateM;
    }

    public float getValue_spends() {
        return value_spends;
    }

    public void setValue_spends(float value_spends) {
        this.value_spends = value_spends;
    }
}
