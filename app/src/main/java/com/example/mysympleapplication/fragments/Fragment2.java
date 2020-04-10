package com.example.mysympleapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mysympleapplication.R;

public class Fragment2 extends Fragment {
    private static final String KEY_TEXT = "text";
    private long idCity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_two, container, false);
    }

//    public static Fragment2 newInstance(String text) {
//        Bundle arguments = new Bundle();
//        arguments.putString(KEY_TEXT, text);
//        Fragment2 fragment2 = new Fragment2();
//        fragment2.setArguments(arguments);
//        return fragment2;
//    }

    public void setCity(long id) {
        this.idCity = id;
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            TextView tiile= view.findViewById(R.id.Title);

        }
    }
}
