package com.example.mysympleapplication.hw9.newDesign.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.base.BaseFragment
import com.example.mysympleapplication.hw9.newDesign.di.builder.ViewModelFactory
import com.example.mysympleapplication.hw9.newDesign.domain.model.State
import com.example.mysympleapplication.hw9.newDesign.ui.dialogues.ResultsDialogFragment
import com.example.mysympleapplication.hw9.newDesign.viewmodels.CreateUserByEmailViewModel
import javax.inject.Inject


class RegistrationFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var userNameEdit: EditText
    private lateinit var emailEdit: EditText
    private lateinit var passEdit: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var viewModel: CreateUserByEmailViewModel
    private lateinit var fragmentDialog: ResultsDialogFragment
    private lateinit var btnGoToSignInScreen: Button
    private lateinit var buttonNext: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProvider(this, viewModelFactory)[CreateUserByEmailViewModel::class.java]
        initView(view)
        initListeners()

        viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                State.LOADING -> showView(progressBar)
                State.SUCCESS -> {
                    hideView(progressBar)
                    findNavController().navigate(R.id.action_registrationFragment_to_bottomNavFragment)
                }
                State.ERROR -> hideView(progressBar)
                else -> hideView(progressBar)
            }
        }
    }

    private fun initListeners() {
        buttonNext.setOnClickListener {
            viewModel.createUserByEmail(
                userName = userNameEdit.text.toString(),
                mail = emailEdit.text.toString(),
                pass = passEdit.text.toString()
            )
            viewModel.liveDataResult.observe(viewLifecycleOwner) {
                if (!fragmentDialog.isAdded) {
                    showMyDialog(it, fragmentDialog)
                }
            }
        }
        btnGoToSignInScreen.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initView(view: View) {
        fragmentDialog = ResultsDialogFragment()
        progressBar = view.findViewById(R.id.progressBarRegistration)
        btnGoToSignInScreen = view.findViewById(R.id.btn_goTo_loginFragment)
        buttonNext = view.findViewById(R.id.btn_registration)
        userNameEdit = view.findViewById(R.id.edit_username_registration)
        emailEdit = view.findViewById(R.id.edit_mail_registration)
        passEdit = view.findViewById(R.id.edit_passw_registration)
    }

}