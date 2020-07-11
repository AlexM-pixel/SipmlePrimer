package com.example.mysympleapplication.hw9;

import androidx.room.ColumnInfo;

public class SumSpendsOfMonth {
    @ColumnInfo(name = "dateM")
    private String dateM;
    @ColumnInfo(name = "value_spends")
    private float value_spends;

    SumSpendsOfMonth(String dateM, float value_spends) {
        this.dateM = dateM;
        this.value_spends = value_spends;
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
