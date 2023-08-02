package com.example.mysympleapplication.hw9.newDesign.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.base.BaseFragment
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs.isHasAccess
import me.ibrahimsn.lib.SmoothBottomBar


class BottomNavFragment : BaseFragment() {
    private var fm: FragmentManager? = null
    lateinit var fragment: Fragment
    private lateinit var bottomNavigationView: SmoothBottomBar
    private lateinit var homeFragment: HomeFragment
    private lateinit var statisticSoloFragment: StatisticSoloFragment
    private lateinit var statisticFragment: StatisticFragment
    private lateinit var settingsFragment: SettingsFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_nav, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVal(view)
        setBottomNav()
        fragment = homeFragment
        fm
            ?.beginTransaction()
            ?.replace(R.id.frame_container, fragment)
            ?.commit()
    }

    private fun setBottomNav() {
        bottomNavigationView.onItemSelected = { i ->
            Log.e("homeFragment", "bottomNavigationView: i = $i ")
            fragment = when (i) {
                0 -> homeFragment
                1 -> {
                    if (isHasAccess) statisticSoloFragment
                    else statisticFragment
                }
                2 -> settingsFragment
                else -> homeFragment
            }
            fm
                ?.beginTransaction()
                ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                ?.replace(R.id.frame_container, fragment)
                ?.commit()
        }
    }

    private fun initVal(view: View) {
        homeFragment = HomeFragment()
        statisticSoloFragment = StatisticSoloFragment()
        statisticFragment = StatisticFragment()
        settingsFragment = SettingsFragment()
        fm = requireActivity().supportFragmentManager
        bottomNavigationView = view.findViewById(R.id.bottom_nav)
    }


}