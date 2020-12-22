package com.example.mysympleapplication.hw9.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "friend_postuplenie")
public class FriendsPostuplenie {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String value;
    private String date;

    public FriendsPostuplenie(Long id, String value, String date) {
        this.id = id;
        this.value = value;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
