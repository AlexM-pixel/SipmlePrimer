package com.example.mysympleapplication.hw9.newDesign.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.base.BaseFragment
import com.example.mysympleapplication.hw9.newDesign.di.builder.ViewModelFactory
import com.example.mysympleapplication.hw9.newDesign.domain.model.State
import com.example.mysympleapplication.hw9.newDesign.viewmodels.ManuallyAddSpendViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

const val ARG_BALANCE = "balanceM"

class AddManualSpendFragment : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    val viewModel: ManuallyAddSpendViewModel by viewModels { viewModelFactory }
    private var namesSpend: AutoCompleteTextView? = null
    private var valueSpend: EditText? = null
    private var balanceEdit: EditText? = null
    private var balanceTitle: TextView? = null
    private var dateTextView: TextView? = null
    private var datePicker: MaterialDatePicker<Long>? = null
    private var adapter: ArrayAdapter<String>? = null
    private var oldBalance: Float? = null
    private var newBalance: Float? = null
    private var getBalanceFromEdit: String? = null
    private var dateN: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { oldBalance = it.getFloat(ARG_BALANCE) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_manual_spend, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        balanceTitle?.text = oldBalance.toString()
        viewModel.getAllNamesSpendForAutoComplete()
        viewModel.namesCategorySpendLiveData.observe(viewLifecycleOwner) { list -> // список ruName from NameSpends
            adapter = ArrayAdapter<String>(
                requireContext(), android.R.layout.simple_dropdown_item_1line, list
            )
            namesSpend?.setAdapter(adapter)
        }
        viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                State.SUCCESS -> {
                    hideKeyboard()
                    Toast.makeText(requireContext(), "Ваши купки сохранены!", Toast.LENGTH_SHORT)
                        .show()
                    oldBalance = newBalance
                    namesSpend?.setText("")
                    valueSpend?.setText("")
                }
                State.ERROR -> {
                    hideKeyboard()
                    Toast.makeText(requireContext(), state.stateDescription, Toast.LENGTH_SHORT)
                        .show()
                }
                else -> {}
            }
        }
        dateN = SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date())
        dateTextView?.text = dateN
        Log.e("AddManualSpendFragment", "from DAte: $dateN")
    }

    private fun init(view: View) {
        namesSpend = view.findViewById(R.id.autoTextInput_nameSpend)
        valueSpend = view.findViewById(R.id.valuem_editText)
        balanceTitle = view.findViewById(R.id.balanceTitleM)
        balanceEdit = view.findViewById(R.id.balanceM_textInput)
        dateTextView = view.findViewById(R.id.textView_show_date)
        val btnDatePicker: FrameLayout = view.findViewById(R.id.frame_date)
        btnDatePicker.setOnClickListener { showDatePickerDialog() }
        val btnAdd = view.findViewById<Button>(R.id.btn_save_addSpendM)
        btnAdd.setOnClickListener { saveSpend() }
        val btnBack = view.findViewById<ImageButton>(R.id.btn_back_from_addM)
        btnBack.setOnClickListener { findNavController().navigateUp() }
        val checkSpend = view.findViewById<CheckBox>(R.id.chekSpend_box)
        checkSpend.setOnCheckedChangeListener { btnView, isChecked ->
            if (isChecked) {
                balanceCalculation(valueSpend?.text.toString())
            } else {
                balanceCalculation("0")
            }
        }
        valueSpend!!.addTextChangedListener { if (checkSpend.isChecked) balanceCalculation(it.toString()) }

    }

    private fun balanceCalculation(value: String?) {
        Log.e("balanceCalculation!!!!!", "value: $value")
        newBalance = oldBalance?.minus(value?.toFloatOrNull() ?: 0f)
        if (newBalance != null) {
            balanceTitle?.text = String.format("%.2f", newBalance)
        }
        Log.e("balanceCalculation!!!!!", "NewBalance = $newBalance , OldBalance = $oldBalance")
    }

    @SuppressLint("SimpleDateFormat")
    private fun showDatePickerDialog() {
        datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setTheme(R.style.MaterialCalendarTheme)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
        datePicker?.addOnPositiveButtonClickListener() {
            Log.e("AddManualSpendFragment", "from Picker: ${datePicker?.headerText}")
            val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            utc.timeInMillis = it
            val format = SimpleDateFormat("d MMM yyyy")
            dateN = format.format(utc.time)
            dateTextView?.text = dateN
            Log.e("AddManualSpendFragment", "from Picker FORMATTED : $dateN")

        }
        datePicker?.show(childFragmentManager, "addManuallyDate")
    }

    private fun saveSpend() {
        if (namesSpend?.text!!.isNotEmpty() && valueSpend?.text!!.isNotEmpty()) {
            viewModel.addNewSpend(
                name = namesSpend?.text.toString(),
                value = valueSpend?.text.toString(),
                date = dateTextView?.text.toString(),
                nameImage = null
            )
            if (newBalance != oldBalance && isEmptyBalanceEditView()) {  // если баланс изменился, сохраняю новое значение
                viewModel.changeBalance(newBalance ?: 0f)
            } else if (!isEmptyBalanceEditView()) {
                newBalance = getBalanceFromEdit?.toFloat()
                viewModel.changeBalance(
                    getBalanceFromEdit?.toFloat() ?: 0f
                ) // если пользователь вручную ввел баланс
            } else {
                viewModel.changeBalance(oldBalance ?: 0f)
            }
        } else Toast.makeText(requireContext(), "insert all members", Toast.LENGTH_SHORT).show()
    }

    private fun isEmptyBalanceEditView(): Boolean {
        getBalanceFromEdit = balanceEdit?.text.toString()
        Log.e("AddManualSpendFragment", "BALANCE FROM EditView : ${getBalanceFromEdit?.toFloatOrNull()}")
        return getBalanceFromEdit?.toFloatOrNull() == null
    }

}