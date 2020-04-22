package com.example.mysympleapplication.hw4;

import com.example.mysympleapplication.R;

import java.util.Arrays;
import java.util.List;

public class User {
    private String name;
    private String callAgo;
    private int idFoto;
    private int idKey;

    public User() {
    }

    private User(String name, String callAgo, int idFoto, int idKey) {
        this.name = name;
        this.callAgo = callAgo;
        this.idFoto = idFoto;
        this.idKey = idKey;
    }

//    public static List<User> userList = Arrays.asList(new User("Рыгорыч", "fife minutes ago", R.drawable.luk, 0),
//            new User("Путин", "seven minutes ago", R.drawable.putin, 1),
//            new User("Ким", "six minutes ago", R.drawable.kim, 12), new User("Эрдоган", "two minutes ago", R.drawable.erdogan, 3));

    public String getName() {
        return name;
    }

    public int getIdFoto() {
        return idFoto;
    }

    public int getIdKey() {
        return idKey;
    }

    public String getCallAgo() {
        return callAgo;
    }
}
