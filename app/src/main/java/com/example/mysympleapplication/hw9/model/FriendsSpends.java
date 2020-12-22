package com.example.mysympleapplication.hw9.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "friend_spends")
public class FriendsSpends {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String spendName;
    private String value;
    private String date;

    public FriendsSpends(Long id, String spendName, String value, String date) {
        this.id = id;
        this.spendName = spendName;
        this.value = value;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpendName() {
        return spendName;
    }

    public void setSpendName(String spendName) {
        this.spendName = spendName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
