package com.example.mysympleapplication;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mysympleapplication.fragments.Fragment2;


public class DescriptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);
        Fragment2 descriptionfragment = (Fragment2) getSupportFragmentManager().findFragmentById(R.id.fragment_description_portret); //получил ссылку на фрагмент
        int idButton_fromFragment1 = getIntent().getExtras().getInt(MainActivity.KEY_TITLE);
        descriptionfragment.setCity(idButton_fromFragment1);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
