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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view2 = inflater.inflate(R.layout.fragment_two, container, false);
        TextView textView = view2.findViewById(R.id.text_description);
           Bundle bundle = getArguments();
           if (bundle !=null) {
               String description = bundle.getString(KEY_TEXT);
               textView.setText(description);
           }
        return view2;
    }

    public static Fragment2 newInstance(String text) {
        Bundle arguments = new Bundle();
        arguments.putString(KEY_TEXT, text);
        Fragment2 fragment2 = new Fragment2();
        fragment2.setArguments(arguments);
        return fragment2;
    }
}
