package com.example.mysympleapplication.hw9;

import androidx.room.ColumnInfo;

public class CalendarSpends {
    @ColumnInfo(name = "id")
    private String id;
@ColumnInfo(name = "totalValue")
    private String totalValue;
    @ColumnInfo(name = "date")
    private String date;
    @ColumnInfo(name = "spendName")
    private String spendName;

    CalendarSpends(String id,String spendName, String date, String totalValue) {
        this.id=id;
        this.date = date;
        this.totalValue = totalValue;
        this.spendName = spendName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTotalValue() {
        return totalValue;
    }

    public String getDate() {
        return date;
    }

    public void setTotalValue(String totalValue) {
        this.totalValue = totalValue;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSpendName() {
        return spendName;
    }

    public void setSpendName(String spendName) {
        this.spendName = spendName;
    }
}
