package com.example.mysympleapplication.hw9.view.iu.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mysympleapplication.R;
import com.example.mysympleapplication.hw9.SumSpendsOfMonth;
import com.example.mysympleapplication.hw9.view.iu.adapters.GeneralMonthAdapter;
import com.example.mysympleapplication.hw9.viewModel.ViewModelFriendsData;

import java.util.ArrayList;
import java.util.List;

public class GeneralExpensesFragment extends Fragment {
    private List<SumSpendsOfMonth> cardViewArrayList;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ViewModelFriendsData viewModelFriendsData;
    private String mParam1;
    private String mParam2;

    public static GeneralExpensesFragment newInstance(String param1, String param2) {
        GeneralExpensesFragment fragment = new GeneralExpensesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static GeneralExpensesFragment newInstance() {
        GeneralExpensesFragment fragment = new GeneralExpensesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        viewModelFriendsData = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(ViewModelFriendsData.class);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_general_expenses, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_month);
        cardViewArrayList = new ArrayList<>();
        GeneralMonthAdapter adapter = new GeneralMonthAdapter(cardViewArrayList);
        viewModelFriendsData.generalListLiveData.observe(getActivity(), input -> {
            adapter.setCardViewList(input);
        });
        LinearLayoutManager layoutManager= new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setGeneralSpendMonthListener(date -> {
            Toast.makeText(getActivity().getApplicationContext(),date,Toast.LENGTH_SHORT).show();
            //сетать тута методами из вьюМодели

        });
        TextView text_spend = view.findViewById(R.id.user_spend_text);
        viewModelFriendsData.friendSpendsFromRoom.observe(getActivity(), input -> {
            Log.e("AScs", "generalExpenses, list.size()= " + input.get(0).getId());
          //  text_spend.setText(input.get(0).getSpendName() + "  " + input.get(0).getValue());
        });
        return view;
    }
}