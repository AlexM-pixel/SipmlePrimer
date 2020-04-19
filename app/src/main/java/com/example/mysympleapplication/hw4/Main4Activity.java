package com.example.mysympleapplication.hw4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.mysympleapplication.R;

public class Main4Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        RecyclerView recyclerView = findViewById(R.id.recycler_for_mainActivity);
        String[] presidName = new String[Presidents.presidentsList.size()];                //получил массив имен
        for (int i = 0; i < presidName.length; i++) {
            presidName[i] = Presidents.presidentsList.get(i).getName();
        }
        String[] presidCall = new String[Presidents.presidentsList.size()];                  //получил массив звонков
        for (int i = 0; i < presidCall.length; i++) {
            presidCall[i] = Presidents.presidentsList.get(i).getCallAgo();
        }
        int[] presidFoto = new int[Presidents.presidentsList.size()];                        //получил массив фото
        for (int i = 0; i < presidFoto.length; i++) {
            presidFoto[i] = Presidents.presidentsList.get(i).getIdFoto();
        }
        PresidentsAdapter adapter = new PresidentsAdapter(presidName,presidCall,presidFoto);
        recyclerView.setAdapter(adapter);
        //GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }
}
