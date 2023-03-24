package com.example.mysympleapplication.hw9.newDesign.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.base.BaseFragment
import com.example.mysympleapplication.hw9.newDesign.di.builder.ViewModelFactory
import com.example.mysympleapplication.hw9.newDesign.domain.ext.setImageByDrawable
import com.example.mysympleapplication.hw9.newDesign.domain.model.DetailsSpend
import com.example.mysympleapplication.hw9.newDesign.domain.model.Images
import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend
import com.example.mysympleapplication.hw9.newDesign.ui.adapters.MonthlyByNameSpendsRvAdapter
import com.example.mysympleapplication.hw9.newDesign.ui.dialogues.DeleteSpendDialog
import com.example.mysympleapplication.hw9.newDesign.utils.Config
import com.example.mysympleapplication.hw9.newDesign.viewmodels.DetailMonthlyViewModel
import javax.inject.Inject

const val ARG_NAME_DETAIL = "nameSpend"
const val ARG_VALUE_DETAIL = "value_for_editSpend"
const val ARG_ID_KEY_DETAIL = "idKeySpend"
const val ARG_DATE_DETAIL = "dateSpend"

class DetailMonthlyFragment : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    val viewModel: DetailMonthlyViewModel by viewModels { viewModelFactory }
    private lateinit var titleText: TextView
    private lateinit var imageTitle: ImageView
    private lateinit var recyclerView: RecyclerView
    private var listSpend = mutableListOf<Spend>()
    private var name: String? = null
    private var date: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            name = it.getString(ARG_NAME_DETAIL)
            date = it.getString(ARG_DATE_DETAIL)
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
        titleText.text = name
        imageTitle.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(ARG_NAME_DETAIL, name)
            findNavController().navigate(
                R.id.action_detailMonthlyFragment_to_imagesFragment,
                bundle
            )
        }
        viewModel.getCategoryToImage(name = name ?: "null")
        viewModel.imageNameLiveData.observe(viewLifecycleOwner) {
            val image = Images.getImageForItem(it)
            val drawable = ContextCompat.getDrawable(requireContext(), image)
            imageTitle.setImageDrawable(drawable)
        }
        viewModel.getMonthlySpends(name = name!!, month = date!!)
        viewModel.detailSpendsByNameLiveData.observe(viewLifecycleOwner) {
            listSpend = it as MutableList<Spend>
        }
        viewModel.detailLiveData.observe(viewLifecycleOwner) {
            initRecycler(it)
        }
    }

    private fun init(view: View) {
        titleText = view.findViewById(R.id.nameSpend)
        imageTitle = view.findViewById(R.id.detail_image_spends_nd)
        recyclerView = view.findViewById<RecyclerView>(R.id.monthly_detail_Spends_rv)

    }

    private fun initRecycler(list: List<DetailsSpend>) {
        val myAdapter = MonthlyByNameSpendsRvAdapter(list)
        recyclerView.apply {
            adapter = myAdapter
            layoutManager = LinearLayoutManager(context)
        }
        myAdapter.setList(listSpend)
        myAdapter.onEditClick = { idKey: Long ->
            val bundle = Bundle()
            bundle.putLong(ARG_ID_KEY_DETAIL, idKey)
            findNavController().navigate(
                R.id.action_detailMonthlyFragment_to_editSpendFragment,
                bundle
            )
        }
        myAdapter.onDeleteClick = { result ->
            val fragmentDialog = DeleteSpendDialog()
            if (!fragmentDialog.isAdded) {
                val bundle = Bundle()
                bundle.putLong(Config.DEL_SPEND_DIALOG, result)
                bundle.putString(ARG_NAME_DETAIL, name)
                bundle.putString(ARG_DATE_DETAIL, date)
                fragmentDialog.arguments = bundle
            }
            requireActivity().supportFragmentManager.let {
                if (!fragmentDialog.isAdded) {
                    fragmentDialog.show(it, "dialog_list")
                }
            }
        }
    }
}

