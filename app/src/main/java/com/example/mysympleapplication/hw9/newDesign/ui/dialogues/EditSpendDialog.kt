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
import com.example.mysympleapplication.hw9.newDesign.viewmodels.EditSpendViewModel
import dagger.android.support.DaggerDialogFragment
import javax.inject.Inject

class EditSpendDialog:DaggerDialogFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var editViewModel: EditSpendViewModel
    var btnOk: Button? = null
    var btnCancel: Button? = null

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        editViewModel =
            ViewModelProvider(
                requireActivity(),
                viewModelFactory
            )[EditSpendViewModel::class.java]
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
        val userInputName = view.findViewById<EditText>(R.id.input_valueX)
        val userInputValue = view.findViewById<EditText>(R.id.input_valueY)
        btnCancel = view.findViewById(R.id.btn_cancel_reset_pass)
        btnOk = view.findViewById(R.id.btn_ok_reset_pass)
        setListeners()
    }

    private fun setListeners() {
        btnCancel?.setOnClickListener { dismiss() }
        btnOk?.setOnClickListener {

        }
    }
}