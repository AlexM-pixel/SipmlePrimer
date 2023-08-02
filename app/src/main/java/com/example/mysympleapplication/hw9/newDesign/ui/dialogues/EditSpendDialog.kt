package com.example.mysympleapplication.hw9.newDesign.ui.dialogues

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.di.builder.ViewModelFactory
import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend
import com.example.mysympleapplication.hw9.newDesign.viewmodels.EditSpendViewModel
import dagger.android.support.DaggerDialogFragment
import javax.inject.Inject

const val SPEND_DIALOG_VALUE = "value_for_editName"
const val SPEND_DIALOG_NAME = "dialogToEditSpendName"
const val SPEND_ID_ARG = "idSpendToEdit"

//  диалог изменения ,доделать(по нажатию кнопки ок должны меняться значения во фрагменте)
    //    при добавлении новой спенды баланс обнуляется
class EditSpendDialog : DaggerDialogFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var editViewModel: EditSpendViewModel
    private var userInputName: EditText? = null
    private var userInputValue: EditText? = null
    private var btnOk: Button? = null
    private var btnCancel: Button? = null
    private var idSpend: Long? = null

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        editViewModel =
            ViewModelProvider(
                requireActivity(),
                viewModelFactory
            )[EditSpendViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idSpend = it.getLong(SPEND_ID_ARG)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.save_values, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        editViewModel.getSpend(idSpend.toString())
        editViewModel.spendLiveData.observe(viewLifecycleOwner) { spend ->
            userInputName!!.setText(spend?.spendName)
            userInputValue!!.setText(spend?.value)
            btnOk?.setOnClickListener { updateSpend(spend!!) }
        }
        btnCancel?.setOnClickListener { dismiss() }
    }

    private fun updateSpend(spend: Spend) {
        spend.spendName = userInputName?.text.toString()
        spend.value = userInputValue?.text.toString()
        editViewModel.updateEditSpend(spend = spend)
        dismiss()
    }

    fun init(view: View) {
        userInputName = view.findViewById<EditText>(R.id.input_name)
        userInputValue = view.findViewById<EditText>(R.id.input_value)
        btnCancel = view.findViewById(R.id.btn_cancelEdit)
        btnOk = view.findViewById(R.id.btn_ok_editSpend)
    }

}