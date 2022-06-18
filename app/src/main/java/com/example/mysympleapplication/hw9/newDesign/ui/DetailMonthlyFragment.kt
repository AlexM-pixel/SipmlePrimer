package com.example.mysympleapplication.hw9.newDesign.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.base.BaseFragment
import com.example.mysympleapplication.hw9.newDesign.di.builder.ViewModelFactory
import com.example.mysympleapplication.hw9.newDesign.domain.ext.setImageByDrawable
import com.example.mysympleapplication.hw9.newDesign.domain.model.Images
import com.example.mysympleapplication.hw9.newDesign.ui.adapters.DetailMonthlySpendsRvAdapter
import com.example.mysympleapplication.hw9.newDesign.ui.adapters.MonthlySpendRvAdapter
import com.example.mysympleapplication.hw9.newDesign.viewmodels.DetailMonthlyViewModel
import com.example.mysympleapplication.hw9.newDesign.viewmodels.MonthlySpendsViewModel
import javax.inject.Inject

const val ARG_NAME_DETAIL = "nameSpend"
const val ARG_DATE_DETAIL = "dateSpend"
const val ARG_IMAGE_DETAIL = "imageName"

class DetailMonthlyFragment : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    val viewModel: DetailMonthlyViewModel by viewModels { viewModelFactory }
    private lateinit var myAdapter: DetailMonthlySpendsRvAdapter
    private lateinit var titleText: TextView
    private lateinit var imageTitle: ImageView
    private var name: String? = null
    private var date: String? = null
    private var imageName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            name = it.getString(ARG_NAME_DETAIL)
            date = it.getString(ARG_DATE_DETAIL)
            imageName = it.getString(ARG_IMAGE_DETAIL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_monthly, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        val image = Images.getImageForItem(imageName)
        titleText.text = name
        imageTitle.setImageByDrawable(image)
        viewModel.getMonthlySpends(name = name!!, month = date!!)
        viewModel.detailSpendsByNameLiveData.observe(viewLifecycleOwner) {
            myAdapter.setList(it)
        }
    }

    private fun init(view: View) {
        titleText = view.findViewById(R.id.nameSpend)
        imageTitle=view.findViewById(R.id.detail_image_spends_nd)
        val recyclerView = view.findViewById<RecyclerView>(R.id.monthly_detail_Spends_rv)
        myAdapter = DetailMonthlySpendsRvAdapter()
        recyclerView.apply {
            adapter = myAdapter
            layoutManager = LinearLayoutManager(context)
        }
        myAdapter.onItemClick = { name, date ->
            Toast.makeText(requireContext(), "name=$name", Toast.LENGTH_SHORT).show()
        }
    }

}