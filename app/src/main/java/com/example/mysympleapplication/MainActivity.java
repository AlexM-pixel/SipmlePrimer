package com.example.mysympleapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mysympleapplication.fragments.Fragment1;

public class MainActivity extends AppCompatActivity implements Fragment1.Listener {
public static final String KEY_TITLE="id";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void idButton(int id) {
        Intent intent = new Intent(this, DescriptionActivity.class);
                    intent.putExtra(KEY_TITLE, id);
                    startActivity(intent);
    }
}
