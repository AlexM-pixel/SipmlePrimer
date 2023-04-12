package com.example.mysympleapplication.hw9.newDesign.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.base.BaseFragment
import com.example.mysympleapplication.hw9.newDesign.di.builder.ViewModelFactory
import com.example.mysympleapplication.hw9.newDesign.ui.adapters.SumMonthSpendsRvAdapter
import com.example.mysympleapplication.hw9.newDesign.ui.adapters.ViewPagerAdapter
import com.example.mysympleapplication.hw9.newDesign.utils.Config.REQUEST_CODE
import com.example.mysympleapplication.hw9.newDesign.viewmodels.HomeFragmentViewModel
import javax.inject.Inject

class HomeFragment : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    val viewModel: HomeFragmentViewModel by viewModels { viewModelFactory }
    private lateinit var myAdapter: SumMonthSpendsRvAdapter
    private var isPermissionGranted = false
    private var balanceTitle: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_nd, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        setViewPager(view)
        if (!isPermissionGranted) {
            showRequestPermission()
        }
        viewModel.getMonthlyExpenses()
        viewModel.getBalance()
        viewModel.sumSpendsLiveData.observe(viewLifecycleOwner) {
            myAdapter.setMonthList(it)
        }
        viewModel.balanceLiveData.observe(viewLifecycleOwner) {
            balanceTitle?.text = it?.balance
        }
    }

    private fun setViewPager(view: View) {
        val viewPager2 = view.findViewById<ViewPager2>(R.id.viewPager_home)
        val viewPagerAdapter = ViewPagerAdapter()
        viewPager2.adapter = viewPagerAdapter
        val transformerSideMargin =
            pixelToDp(requireActivity(), resources.getDimension(R.dimen.cardView_margin) * 2)
        viewPager2.setShowSideItems(transformerSideMargin, transformerSideMargin)
        viewPager2.setCurrentItem(1, false)
        viewPagerAdapter.onButtonClick = { position ->
            when (position) {
                0 -> {
                    Toast.makeText(
                        requireContext(),
                        "Посмотрите отчёт за эту неделю",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                1 -> {
                    startAddingManualFragment()
                }
                2 -> {
                    Toast.makeText(
                        requireContext(),
                        "Воспользуйтесь нашим калькулятором с удобным ковертором",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                3 -> {
                    Toast.makeText(requireContext(), "Всего вы потратили", Toast.LENGTH_SHORT)
                        .show()
                }
                else -> Toast.makeText(requireContext(), "Error item position", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun initView(view: View) {
        val rv = view.findViewById<RecyclerView>(R.id.rv_home_spends)
        balanceTitle = view.findViewById(R.id.textView_balanceValue)
        myAdapter = SumMonthSpendsRvAdapter()
        rv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = myAdapter
        }
        myAdapter.onItemClick = { date ->
            val bundle = Bundle()
            bundle.putString(ARG_DATE, date)
            findNavController().navigate(
                R.id.action_bottomNavFragment_to_spendsOfMonthFragment,
                bundle
            )
        }
    }

    fun showRequestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS
            ), REQUEST_CODE
        )

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            isPermissionGranted = true
        }
    }

    fun ViewPager2.setShowSideItems(pageMarginPx: Int, offsetPx: Int) {
        clipToPadding = false
        clipChildren = false
        offscreenPageLimit = 4

        setPageTransformer { page, position ->

            val offset = position * -(2 * offsetPx + pageMarginPx)
            if (this.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                    page.translationX = -offset
                } else {
                    page.translationX = offset
                }
            } else {
                page.translationY = offset
            }
        }
    }

    fun pixelToDp(context: Context, pixelValue: Float): Int {
        val scale: Float = context.resources.displayMetrics.density
        return (pixelValue / scale + 0.5f).toInt()
    }

    private fun startAddingManualFragment() {
        val bundle = Bundle()
        val balance = balanceTitle?.text.toString()
        bundle.putFloat(ARG_BALANCE, balance.toFloatOrNull() ?: 0f)
        findNavController().navigate(R.id.action_global_addManualSpendFragment, bundle)
    }
}