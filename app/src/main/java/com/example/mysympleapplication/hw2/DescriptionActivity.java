package com.example.mysympleapplication.hw2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.mysympleapplication.R;

public class DescriptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        Fragment2 descriptionfragment = (Fragment2) getSupportFragmentManager().findFragmentById(R.id.fragment_description_portret); //получил ссылку на фрагмент
        int idButton_fromFragment1 = getIntent().getExtras().getInt(Main2Activity.KEY_POSITION);
        descriptionfragment.setCity(idButton_fromFragment1);
    }
}
