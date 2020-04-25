package com.example.mysympleapplication.hw2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.view.View;

import com.example.mysympleapplication.R;

public class Main2Activity extends AppCompatActivity implements Fragment1.OnButtonClickListener {
   private int idPosition;
   private View frameLayout;
   public static final String KEY_POSITION="key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        frameLayout = findViewById(R.id.frame_for_fragment2);
        FragmentManager fm=getSupportFragmentManager();
        if (fm.getBackStackEntryCount()>0 && frameLayout==null) {          //если наклацал фрагментов в бэкстек и телефон повернул обратно в портретный
            idPosition = savedInstanceState.getInt(KEY_POSITION);
            Intent intent = new Intent(this, DescriptionActivity.class);
            intent.putExtra(KEY_POSITION, idPosition);
            startActivity(intent);
        }
    }

    @Override
    public void idButton(int id) {
        frameLayout = findViewById(R.id.frame_for_fragment2);                   // при наличии второго фрагмениа в макете
        if (frameLayout != null) {
            idPosition = id;
            Fragment2 fragment2 = Fragment2.newInstance(id);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
           // fragment2.setCity(id);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.replace(R.id.frame_for_fragment2, fragment2);
            ft.addToBackStack(null).commit();

        } else {
            Intent intent = new Intent(this, DescriptionActivity.class);
            intent.putExtra(KEY_POSITION, id);
            startActivity(intent);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_POSITION, idPosition);
    }
}
