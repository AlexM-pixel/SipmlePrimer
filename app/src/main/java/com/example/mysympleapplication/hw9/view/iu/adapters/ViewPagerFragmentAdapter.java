package com.example.mysympleapplication.hw9.view.iu.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.mysympleapplication.hw9.BalanceFragment;
import com.example.mysympleapplication.hw9.view.iu.fragments.GeneralBalanceFragment;
import com.example.mysympleapplication.hw9.view.iu.fragments.GeneralExpensesFragment;
import com.example.mysympleapplication.hw9.view.iu.fragments.PersonalBalanceFragment;
import com.example.mysympleapplication.hw9.view.iu.fragments.PersonalExpensesFragment;

import java.util.ArrayList;

public class ViewPagerFragmentAdapter extends FragmentStateAdapter {
    private ArrayList<Fragment> arrayList = new ArrayList<>();
    private boolean access;

    public ViewPagerFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, boolean access) {
        super(fragmentManager, lifecycle);
        this.access = access;
    }

    public void setAccess(boolean access) {
        this.access = access;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                if (access) {
                    return GeneralExpensesFragment.newInstance();
                } else {
                    return PersonalExpensesFragment.newInstance();
                }
            case 1:
                if (access) {
                    return PersonalExpensesFragment.newInstance();
                } else {
                    return PersonalBalanceFragment.newInstance();
                }
            case 2:
                if (access) {
                    return GeneralBalanceFragment.newInstance();
                } else {
                    return PersonalBalanceFragment.newInstance("detalnoe", "opisanie");
                }
        }
        return PersonalExpensesFragment.newInstance();
    }

    @Override
    public int getItemCount() {
        return BalanceFragment.NUM_PAGES;
    }
}
