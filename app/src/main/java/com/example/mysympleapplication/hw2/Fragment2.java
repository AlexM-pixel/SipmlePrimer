package com.example.mysympleapplication.hw2;

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
    private static final String ID_BUTTON = "idB";
    private int idCity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            idCity = savedInstanceState.getInt(ID_BUTTON);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_two, container, false);
    }

    void setCity(int id) {
        this.idCity = id;
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            TextView tiile = view.findViewById(R.id.Title);
            TextView description = view.findViewById(R.id.text_description);
            tiile.setText(Cities.citiesList.get(idCity).getTitle());
            description.setText(Cities.citiesList.get(idCity).getDescription());
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(ID_BUTTON, idCity);
    }
}