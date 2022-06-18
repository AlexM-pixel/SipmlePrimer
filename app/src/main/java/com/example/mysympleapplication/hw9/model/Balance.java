package com.example.mysympleapplication.hw9.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "balance")
public class Balance {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String balance;

    public Balance(Long id, String balance) {
        this.id = id;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
