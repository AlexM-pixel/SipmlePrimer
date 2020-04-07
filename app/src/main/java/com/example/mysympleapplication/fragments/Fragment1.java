package com.example.mysympleapplication.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.mysympleapplication.R;
import com.example.mysympleapplication.SecondActivity;

public class Fragment1 extends Fragment {
    public static final String KEY_TITLE = "Title";
    private Button button1;
    private Button button2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_one, container, false);
        button1 = view.findViewById(R.id.buttom1);
        button2 = view.findViewById(R.id.buttom2);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textTitle = button1.getText().toString();
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    sendDescription(textTitle);
                } else {
                    Intent intent = new Intent(getActivity(), SecondActivity.class);
                    intent.putExtra(KEY_TITLE, textTitle);
                    startActivity(intent);
                }
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textTitle = button2.getText().toString();
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    sendDescription(textTitle);
                } else {
                    Intent intent = new Intent(getActivity(), SecondActivity.class);
                    intent.putExtra(KEY_TITLE, textTitle);
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    private void sendDescription(String text) {
        FragmentManager fm =getFragmentManager();
        assert fm != null;
        if (fm.findFragmentByTag("liner")==null) {
        fm.beginTransaction().replace(R.id.second_frame,Fragment2.newInstance(text)).commit();
        }
    }
}
