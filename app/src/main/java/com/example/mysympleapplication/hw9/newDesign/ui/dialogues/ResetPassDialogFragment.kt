package com.example.mysympleapplication.hw9.newDesign.ui.dialogues

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.mysympleapplication.hw9.newDesign.domain.model.State
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.di.builder.ViewModelFactory
import com.example.mysympleapplication.hw9.newDesign.viewmodels.LoginViewModel
import dagger.android.support.DaggerDialogFragment
import javax.inject.Inject

class ResetPassDialogFragment : DaggerDialogFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    //val viewModel: ResetPassByMailViewModel by viewModels { viewModelFactory }
    private lateinit var viewModel: LoginViewModel

    var editPass: EditText? = null
    var btnOk: Button? = null
    var btnCancel: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_email, container, false)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        viewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)[LoginViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editPass = view.findViewById(R.id.input_textMailByReset)
        btnCancel = view.findViewById(R.id.btn_cancel_reset_pass)
        btnOk = view.findViewById(R.id.btn_ok_reset_pass)
        setListeners()
        viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                State.SUCCESS -> {
                    Toast.makeText(requireContext(), "проверьте почту!", Toast.LENGTH_LONG).show()
                    dismiss()
                }
                State.ERROR -> {
                    Toast.makeText(requireContext(), "Ошибка!", Toast.LENGTH_SHORT).show()
                }
                State.LOADING -> {
                    Log.e("createUser", "resetPassword:--   State.LOADING")

                }
                else -> dismiss()
            }
        }
        viewModel.liveDataResult.observe(viewLifecycleOwner) {
           editPass?.hint=it
        }
    }

    private fun setListeners() {
        btnCancel?.setOnClickListener { dismiss() }
        btnOk?.setOnClickListener {
            val email = editPass?.text.toString()
            editPass?.setText("")
            viewModel.resetPasswByEmail(email)
        }
    }
}