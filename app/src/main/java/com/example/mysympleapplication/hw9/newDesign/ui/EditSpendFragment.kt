package com.example.mysympleapplication.hw9.newDesign.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.base.BaseFragment
import com.example.mysympleapplication.hw9.newDesign.di.builder.ViewModelFactory
import com.example.mysympleapplication.hw9.newDesign.domain.model.Images
import com.example.mysympleapplication.hw9.newDesign.domain.model.State
import com.example.mysympleapplication.hw9.newDesign.ui.adapters.EditRvAdapter
import com.example.mysympleapplication.hw9.newDesign.ui.dialogues.*
import com.example.mysympleapplication.hw9.newDesign.utils.Config
import com.example.mysympleapplication.hw9.newDesign.viewmodels.EditSpendViewModel
import javax.inject.Inject
import kotlin.math.roundToInt


class EditSpendFragment : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val editViewModel: EditSpendViewModel by viewModels { viewModelFactory }
    private lateinit var editAdapter: EditRvAdapter
    private var titleFragment: Button? = null
    private var parentValueTextView: TextView? = null
    private var btnSave: Button? = null
    private var btnBack: ImageButton? = null
    private var nameEditText: EditText? = null
    private var valueEditText: EditText? = null
    private var imageTitle: ImageView? = null
    private var progressByValue: TextView? = null
    private var sumValue: TextView? = null
    private var progressBar: ProgressBar? = null
    private var mHandler: Handler? = null
    private var name: String? = "null"
    private var value: String? = "Empty"
    private var idKey: Long? = null
    var progr = 0
    var detailsValue = 0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idKey = it.getLong(ARG_ID_KEY_DETAIL)
            Log.e("idKey EditSpendFragment", "ID = $idKey")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_spend, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var sumCount: Float
        editViewModel.getSpend(id = idKey.toString())
        editViewModel.spendLiveData.observe(viewLifecycleOwner) { spend ->
            name = spend?.spendName
            titleFragment?.text = name
            value =
                spend?.value   // может после получения значения запускать подщет процентов и сетить прогрессбар
            parentValueTextView?.text = "$value"
        }
        init(view)
        setListeners()
        editViewModel.getDetailsList(id = idKey ?: 0)
        editViewModel.detailsLiveData.observe(viewLifecycleOwner) { list ->
            sumCount = 0f
            editAdapter.setList(list)
            list.forEach {
                sumCount += it.value.toFloat()
            }
            sumValue?.text = "$sumCount BYN"
            setProgressBar(sumCount)
        }
        editViewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                State.SUCCESS -> {
                    hideKeyboard()
                    clearEditTexts()
                }
                State.ERROR -> {
                    Toast.makeText(requireContext(), "ошибочка вышла!", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
        editViewModel.errorLiveData.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT)
                .show()  //Прочитать ощибку с телефона
        }
        editViewModel.getCategoryToImage(name = name ?: "null")
        editViewModel.imageNameLiveData.observe(viewLifecycleOwner) {
            val image = Images.getImageForItem(it)
            val drawable = ContextCompat.getDrawable(requireContext(), image)
            imageTitle?.setImageDrawable(drawable)
        }
    }

    private fun clearEditTexts() {
        nameEditText?.setText("")
        valueEditText?.setText("")
    }

    @SuppressLint("SetTextI18n")
    private fun init(view: View) {
        titleFragment = view.findViewById(R.id.title_editSpend)
        titleFragment?.setOnClickListener { showDialogSetSpend()}
        imageTitle = view.findViewById(R.id.edit_image_spends_nd)
        parentValueTextView = view.findViewById<TextView>(R.id.value_editFragment)
        btnBack = view.findViewById(R.id.btn_back_from_edit)
        progressBar = view.findViewById(R.id.progressBar_editFragment)
        progressByValue = view.findViewById(R.id.text_percents_progressBar)
        sumValue = view.findViewById(R.id.title_text_editSpend)
        btnSave = view.findViewById(R.id.btn_save_editFragment)
        nameEditText = view.findViewById(R.id.textInput_EditText_name)
        valueEditText = view.findViewById(R.id.textInput_EditText_value)
        val rvEdit = view.findViewById<RecyclerView>(R.id.rv_edit_fragment)
        editAdapter = EditRvAdapter()
        rvEdit.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = editAdapter
        }
        editAdapter.onDeleteClick = { idDetails ->
            val fragmentDialog = DeleteDetailsDialog()
            if (!fragmentDialog.isAdded) {
                val bundle = Bundle()
                bundle.putLong(Config.DEL_DETAILS_DIALOG, idDetails)
                fragmentDialog.arguments = bundle
            }
            requireActivity().supportFragmentManager.let {
                if (!fragmentDialog.isAdded) {
                    fragmentDialog.show(it, "dialog_list")
                }
            }
        }
    }

    private fun setListeners() {
        btnSave?.setOnClickListener { saveDetails() }
        btnBack?.setOnClickListener { findNavController().navigateUp() }
        imageTitle?.setOnClickListener { setNewImage() }
    }

    private fun setNewImage() {
        val bundle = Bundle()
        bundle.putString(ARG_NAME_DETAIL, name)
        findNavController().navigate(R.id.action_editSpendFragment_to_imagesFragment, bundle)
    }

    private fun saveDetails() {
        val name = nameEditText?.text.toString()
        val value = valueEditText?.text.toString()
        if (name.isNotEmpty() && value.isNotEmpty()) {
            editViewModel.saveDetailsSpend(
                name = name,
                value = value,
                fKey = idKey ?: 0
            )
        } else {
            Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStop() {
        super.onStop()
        (mHandler as Handler).removeMessages(0)

    }

    private fun setProgressBar(actualValue: Float) {
        val percents: Int = (actualValue / (value?.toFloat() ?: 0f) * 100).roundToInt()
        progressBar?.max = 100
        if (actualValue < detailsValue) {
            mHandler =
                object : Handler(Looper.getMainLooper()) {
                    @SuppressLint("SetTextI18n")
                    override fun handleMessage(msg: Message) {
                        super.handleMessage(msg)
                        if (progr > percents) {
                            progr -= 1
                            progressBar?.progress = progr
                            progressByValue?.text = "$progr%"
                            mHandler?.sendEmptyMessageDelayed(0, 3)
                        }
                    }
                }
            (mHandler as Handler).sendEmptyMessage(0)
        } else {
            mHandler =
                object : Handler(Looper.getMainLooper()) {
                    @SuppressLint("SetTextI18n")
                    override fun handleMessage(msg: Message) {
                        super.handleMessage(msg)
                        if (progr < percents) {
                            progr += 1
                            progressBar?.progress = progr
                            progressByValue?.text = "$progr%"
                            mHandler?.sendEmptyMessageDelayed(0, 3)
                        }
                    }
                }
            (mHandler as Handler).sendEmptyMessage(0)
        }
        detailsValue = actualValue
    }
    private fun showDialogSetSpend(){
        val fragmentDialog = EditSpendDialog()
        if (!fragmentDialog.isAdded) {
            val bundle = Bundle()
            bundle.putString(SPEND_DIALOG_NAME, name)
            bundle.putString(SPEND_DIALOG_VALUE, value)
            bundle.putLong(SPEND_ID_ARG,idKey!!)
            fragmentDialog.arguments = bundle
        }
        requireActivity().supportFragmentManager.let {
            if (!fragmentDialog.isAdded) {
                fragmentDialog.show(it, "dialog_list")
            }
        }
    }
}
