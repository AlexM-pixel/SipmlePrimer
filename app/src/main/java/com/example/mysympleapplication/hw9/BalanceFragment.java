package com.example.mysympleapplication.hw9;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.mysympleapplication.R;
import com.example.mysympleapplication.hw9.view.iu.ZoomOutPageTransformer;
import com.example.mysympleapplication.hw9.view.iu.adapters.ViewPagerFragmentAdapter;
import com.example.mysympleapplication.hw9.viewModel.MyViewModel;
import com.example.mysympleapplication.hw9.viewModel.ViewModelBalance;
import com.example.mysympleapplication.hw9.viewModel.ViewModelFriendsData;
import com.google.firebase.firestore.FirebaseFirestore;

public class BalanceFragment extends Fragment {

    private static final String ARG_ACCESS = "argument_access";
    private static final String ARG_OBSERVE_USER = "users_email";
    public static final int NUM_PAGES = 3;
    private ViewPager2 viewPager;
    private ViewPagerFragmentAdapter pagerAdapter;
    private boolean booleanAccess;
    private String emailObserveUser;

    public static BalanceFragment newInstance(boolean param1, String param2) {
        BalanceFragment fragment = new BalanceFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_ACCESS, param1);
        args.putString(ARG_OBSERVE_USER, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static BalanceFragment newInstance() {
        return new BalanceFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            booleanAccess = getArguments().getBoolean(ARG_ACCESS);
            emailObserveUser = getArguments().getString(ARG_OBSERVE_USER);
        }
        pagerAdapter = new ViewPagerFragmentAdapter(getChildFragmentManager(), getLifecycle(), booleanAccess);
        // pagerAdapter.setAccess(booleanAccess);
        if (booleanAccess) {
            // тута скачать все спенды с прослушиваемого юсера. если болеан переменная = фолс то вывести свои спенды
            ViewModelFriendsData myViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(ViewModelFriendsData.class);
            myViewModel.getLiveDataFriendSpend(emailObserveUser).observe(this, input -> {
                MyAppDataBase.getInstance().friendsDao().insertAllFriendSpends(input);
                Log.e("AScs", "balanceFragment, list.size()= " + input.get(0).getValue());
            });
            myViewModel.getBalanceFriendLiveData(emailObserveUser).observe(this, input -> {
                MyAppDataBase.getInstance().friendsDao().insertBF(input);
            });
        }
        Log.e("AScs", "balanceFragment, booleanAccess= " + booleanAccess);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_balance, container, false);
        viewPager = view.findViewById(R.id.pager);
        viewPager.setPageTransformer(new ZoomOutPageTransformer());
        viewPager.setAdapter(pagerAdapter);

        return view;
    }


}