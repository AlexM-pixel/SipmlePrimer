package com.example.mysympleapplication.hw1;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mysympleapplication.R;
import com.example.mysympleapplication.hw1.fragments.Fragment1;
import com.example.mysympleapplication.hw1.fragments.Fragment2;
import com.example.mysympleapplication.hw1.fragments.Fragment3;

public class Statistic extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
    }

    public void choiceingFragment(View view) {
        Fragment fragment = null;
        switch (view.getId()) {
            case R.id.first:
                fragment = new Fragment1();
                break;
            case R.id.second:
                fragment = new Fragment2();
                break;
            case R.id.third:
                fragment = new Fragment3();
                break;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction frTransaction = fragmentManager.beginTransaction().replace(R.id.frame_for_fragment, fragment).addToBackStack(null);
        frTransaction.commit();
    }
}
