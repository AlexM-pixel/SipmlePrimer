package com.example.mysympleapplication.hw9.newDesign.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.base.BaseFragment
import com.example.mysympleapplication.hw9.newDesign.di.builder.ViewModelFactory
import com.example.mysympleapplication.hw9.newDesign.domain.model.State
import com.example.mysympleapplication.hw9.newDesign.ui.adapters.ImagesRvAdapter
import com.example.mysympleapplication.hw9.newDesign.viewmodels.EditSpendViewModel
import com.example.mysympleapplication.hw9.newDesign.viewmodels.ImagesViewModel
import javax.inject.Inject


class ImagesFragment : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val imagesViewModel: ImagesViewModel by viewModels { viewModelFactory }
    private var btnOk: Button? = null
    private var btnCancel: Button? = null
    private var textTitle: TextView? = null
    private var btnBack: ImageButton? = null
    private var adapterRv: ImagesRvAdapter? = null
    private var nameSelected: String? = null
    private var ruName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            ruName = it.getString(ARG_NAME_DETAIL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_images, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        setListeners()
        textTitle?.text = ruName
        imagesViewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                State.SUCCESS -> {
                    back()
                }
                State.ERROR -> {
                    Toast.makeText(requireContext(), "ошибочка вышла!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun init(view: View) {
        textTitle = view.findViewById(R.id.nameSpend_imagesFragment)
        btnOk = view.findViewById(R.id.btn_okImage)
        btnCancel = view.findViewById(R.id.btn_cancelImage)
        btnBack = view.findViewById(R.id.btn_back_imageFragment)
        adapterRv = ImagesRvAdapter()
        val rvImages: RecyclerView = view.findViewById(R.id.rv_images)
        rvImages.apply {
            adapter = adapterRv
            layoutManager = GridLayoutManager(requireContext(), 3)
        }
        adapterRv?.onImageClick = {
            nameSelected = it
        }
    }

    private fun setListeners() {
        btnOk?.setOnClickListener {
            if (nameSelected != null) {
                Log.e("btnOk", "nameSelected= $nameSelected , ruName= $ruName")
                imagesViewModel.getCategoryToUpdate(
                    nameImage = nameSelected!!,
                    ruName = ruName ?: ""
                )
            }
        }
        btnCancel?.setOnClickListener { back() }
        btnBack?.setOnClickListener { back() }
    }

    private fun back() {
        findNavController().navigateUp()
    }
}
