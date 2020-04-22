package com.example.mysympleapplication.hw2;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mysympleapplication.R;

public class Fragment1 extends Fragment {

    public  interface OnButtonClickListener {
        void idButton(int id);
    }

    private OnButtonClickListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_one, container, false);
        final Button button1 = view.findViewById(R.id.buttom1);
        final Button button2 = view.findViewById(R.id.buttom2);
        final int[] itemPosition = new int[]{0,1};

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.idButton(itemPosition[0]);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.idButton(itemPosition[1]);
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.listener = (OnButtonClickListener) context;
    }
}