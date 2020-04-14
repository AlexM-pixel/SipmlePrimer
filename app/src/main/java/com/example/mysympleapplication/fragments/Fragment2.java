package com.example.mysympleapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mysympleapplication.Cities;
import com.example.mysympleapplication.R;

public class Fragment2 extends Fragment {
    private static final String KEY_TEXT = "text";
    private int idCity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_two, container, false);
    }

    public void setCity(int id) {
        this.idCity = id;
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            TextView tiile = view.findViewById(R.id.Title);
            TextView description = view.findViewById(R.id.text_description);
            tiile.setText( Cities.citiesList.get(idCity).getTitle());
            description.setText( Cities.citiesList.get(idCity).getDescription());
        }
    }
}
