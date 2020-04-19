package com.example.mysympleapplication.hw4;

import com.example.mysympleapplication.R;

import java.util.Arrays;
import java.util.List;

public class Presidents {
    private String name;
    private String callAgo;
    private int idFoto;

    private Presidents(String name,String callAgo, int idFoto) {
        this.name = name;
        this.callAgo = callAgo;
        this.idFoto = idFoto;
    }

    public static List<Presidents> presidentsList = Arrays.asList(new Presidents("Рыгорыч","fife minutes ago", R.drawable.luk),
            new Presidents("Путин","seven minutes ago", R.drawable.putin),
            new Presidents("Ким","six minutes ago", R.drawable.kim), new Presidents("Эрдоган","two minutes ago", R.drawable.erdogan));

    public String getName() {
        return name;
    }

    public int getIdFoto() {
        return idFoto;
    }

    public  String getCallAgo() {
        return callAgo;
    }
}
