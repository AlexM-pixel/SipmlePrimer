package com.example.mysympleapplication.hw9.newDesign.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.base.BaseFragment
import com.example.mysympleapplication.hw9.newDesign.di.builder.ViewModelFactory
import com.example.mysympleapplication.hw9.newDesign.domain.background.SmsReceiver
import com.example.mysympleapplication.hw9.newDesign.ui.adapters.MonthlySpendRvAdapter
import com.example.mysympleapplication.hw9.newDesign.viewmodels.HomeFragmentViewModel
import com.example.mysympleapplication.hw9.newDesign.viewmodels.MonthlySpendsViewModel
import java.util.regex.Pattern
import javax.inject.Inject

const val ARG_DATE = "date_arg"

class SpendsOfMonthFragment : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    val viewModel: MonthlySpendsViewModel by viewModels { viewModelFactory }
    private lateinit var myAdapter: MonthlySpendRvAdapter
    private var date: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            date = it?.getString(ARG_DATE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_spends_of_mounth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        viewModel.getMonthlySpends(date!!)
        viewModel.monthlySpendsLiveData.observe(viewLifecycleOwner) {
            myAdapter.setList(it)
        }
    }

    private fun init(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.monthlySpends_rv)
        myAdapter = MonthlySpendRvAdapter()
        recyclerView.apply {
            adapter = myAdapter
            layoutManager = LinearLayoutManager(context)
        }
        myAdapter.onItemClick = { name, date ->
            Toast.makeText(requireContext(), "$name, $date", Toast.LENGTH_SHORT).show()
        }
    }


}