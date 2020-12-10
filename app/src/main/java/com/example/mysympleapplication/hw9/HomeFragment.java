package com.example.mysympleapplication.hw9;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mysympleapplication.R;
import com.example.mysympleapplication.hw9.view.auth.EmailPasswordActivity;
import com.example.mysympleapplication.hw9.viewModel.MyViewModel;
import com.example.mysympleapplication.hw9.viewModel.ViewModelBalance;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private List<SumSpendsOfMonth> cardViewArrayList;
    public static final String DATE_MONTH = "choice_date";
    TextView balance;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        balance = view.findViewById(R.id.text_postupilo);
        //  getSummPostplenie();
        RecyclerView recyclerView = view.findViewById(R.id.recycler_for_mounth_spends);
        cardViewArrayList = new ArrayList<>();
        MyViewModel myViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(MyViewModel.class);
        ViewModelBalance viewModelBalance = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(ViewModelBalance.class);
        viewModelBalance.balanceLiveData.observe(getActivity(), input -> {
            if (input != null) {
                balance.setText("Баланс: " + input.getBalance());
            }
        });
        final SpendMonthAdapter adapter = new SpendMonthAdapter(cardViewArrayList);
        myViewModel.listLiveData.observe(getActivity(), sumSpendsOfMonths -> {
            adapter.setCardViewList(sumSpendsOfMonths);
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setSpendMonthListener(new SpendMonthAdapter.SpendMonthListener() {
            @Override
            public void onSpendMonthClickListener(String date) {
                Intent intent = new Intent(view.getContext(), DetailMonthActivity.class);
                intent.putExtra(DATE_MONTH, date);
                startActivity(intent);
            }
        });

        return view;
    }

}
