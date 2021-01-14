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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mysympleapplication.R;
import com.example.mysympleapplication.hw9.Months;
import com.example.mysympleapplication.hw9.MyAppDataBase;
import com.example.mysympleapplication.hw9.SumSpendsOfMonth;
import com.example.mysympleapplication.hw9.view.iu.adapters.GeneralMonthAdapter;
import com.example.mysympleapplication.hw9.viewModel.ViewModelBalance;
import com.example.mysympleapplication.hw9.viewModel.ViewModelFriendsData;

import java.util.ArrayList;
import java.util.List;

public class GeneralExpensesFragment extends Fragment {
    private List<SumSpendsOfMonth> cardViewArrayList;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView bynText;
    private TextView monthText;
    private TextView userValue;
    private TextView friendValue;
    ProgressBar userBar;
    ProgressBar friendbar;
    ProgressBar innerbar;
    ViewModelFriendsData viewModelFriendsData;
    ViewModelBalance viewModelBalance;
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
        viewModelBalance = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(ViewModelBalance.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_general_expenses, container, false);
        bynText = view.findViewById(R.id.text_byn);
        monthText = view.findViewById(R.id.name_month);
        userValue = view.findViewById(R.id.value_user_text);
        friendValue = view.findViewById(R.id.value_friend_text);
        friendbar = view.findViewById(R.id.background_progressbar);
        innerbar = view.findViewById(R.id.stats_inner);
        userBar = view.findViewById(R.id.stats_progressbar);
        TextView balanceUser = view.findViewById(R.id.valueBalanceUser);
        TextView balanceFriend = view.findViewById(R.id.valueBalanceFriend);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_month);
        cardViewArrayList = new ArrayList<>();
        GeneralMonthAdapter adapter = new GeneralMonthAdapter(cardViewArrayList);
        viewModelFriendsData.generalListLiveData.observe(getActivity(), input -> {
            adapter.setCardViewList(input);
            setValues(input.get(0));
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setGeneralSpendMonthListener(input -> {
            setValues(input);
        });
        viewModelBalance.balanceLiveData.observe(getActivity(), input -> balanceUser.setText(input.getBalance()));
        viewModelFriendsData.balanceFriendLiveData.observe(getActivity(), input->{
            if (input!=null) {
                balanceFriend.setText(input.getBalance());
            }
        });
        viewModelFriendsData.friendSpendsFromRoom.observe(getActivity(), input -> {
            // Log.e("AScs", "generalExpenses, list.size()= " + input.get(0).getId());
            //  text_spend.setText(input.get(0).getSpendName() + "  " + input.get(0).getValue());

        });
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void setValues(SumSpendsOfMonth date) {
        bynText.setText(date.getValue_spends() + " BYN");
        monthText.setText(Months.getMonth(date.getDateM()).getNameMonth());
        float friend_value = viewModelFriendsData.getFriendSumOfMonthSpends(date.getDateM()).getValue_spends();
        friendValue.setText(String.valueOf(friend_value));
        float user_value = viewModelBalance.getSumOfMonthSpends(date.getDateM()).getValue_spends();
        userValue.setText(String.valueOf(user_value));
        setPercents(user_value, friend_value);

    }

    private void setPercents(float valueUser, float valueFriend) {
        double percent;
        if (valueUser > valueFriend) {
            percent = valueFriend / (double) (valueUser + valueFriend) * 100;
            userBar.setProgress(100);
            friendbar.setProgress((int) percent);
            innerbar.setVisibility(View.GONE);
        } else {
            percent = valueUser / (double) (valueFriend + valueUser) * 100;
            userBar.setProgress((int) percent);
            friendbar.setProgress(100);
            innerbar.setVisibility(View.VISIBLE);
        }
    }
}