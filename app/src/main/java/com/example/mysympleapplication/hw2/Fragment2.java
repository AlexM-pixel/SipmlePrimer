package com.example.mysympleapplication.hw2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            idCity = bundle.getInt(ID_BUTTON);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_two, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            TextView tiile = view.findViewById(R.id.Title);
            TextView description = view.findViewById(R.id.text_description);
            tiile.setText(City.cityList.get(idCity).getTitle());
            description.setText(City.cityList.get(idCity).getDescription());
        }
    }

    void setCity(int id) {
        this.idCity = id;
    }

    static Fragment2 newInstance(int setId) {
        Bundle arguments = new Bundle();
        arguments.putInt(ID_BUTTON, setId);
        Fragment2 fragment2 = new Fragment2();
        fragment2.setArguments(arguments);
        return fragment2;
    }
}