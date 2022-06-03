package com.example.mysympleapplication.hw9.newDesign.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.base.BaseFragment
import com.example.mysympleapplication.hw9.newDesign.di.builder.ViewModelFactory
import com.example.mysympleapplication.hw9.newDesign.domain.model.State
import com.example.mysympleapplication.hw9.newDesign.ui.dialogues.ResetPassDialogFragment
import com.example.mysympleapplication.hw9.newDesign.ui.dialogues.ResultsDialogFragment
import com.example.mysympleapplication.hw9.newDesign.utils.Config
import com.example.mysympleapplication.hw9.newDesign.utils.Config.COUNT_ERRORS_PASSW
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs
import com.example.mysympleapplication.hw9.newDesign.viewmodels.LoginViewModel
import javax.inject.Inject


class LoginFragment : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    val viewModel: LoginViewModel by viewModels { viewModelFactory }
    private lateinit var emailEdit: EditText
    private lateinit var passEdit: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var btnLogin: Button
    private lateinit var btnGoToRegistrScreen: Button
    private lateinit var fragmentDialog: ResultsDialogFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                State.LOADING -> showView(progressBar)
                State.SUCCESS -> {
                    hideView(progressBar)
                    findNavController().navigate(R.id.action_loginFragment_to_bottomNavFragment)
                }
                State.ERROR -> hideView(progressBar)
                else -> hideView(progressBar)
            }
        }
        btnGoToRegistrScreen.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
        }
    }

    private fun initView(view: View) {
        val resetPassDialog = ResetPassDialogFragment()
        fragmentDialog = ResultsDialogFragment()
        progressBar = view.findViewById(R.id.progressBarAuth)
        btnLogin = view.findViewById<Button>(R.id.btn_login)
        btnGoToRegistrScreen = view.findViewById(R.id.btn_goTo_registrFragment)
        emailEdit = view.findViewById(R.id.edit_mail)
        passEdit = view.findViewById(R.id.edit_passw)
        btnLogin.setOnClickListener {
            viewModel.logInByEmail(emailEdit.text.toString(), passEdit.text.toString())
            if (viewModel.countErrors != COUNT_ERRORS_PASSW - 1) {                    // если уже три раза была ошибка по неправильному паролю, то не надо выводить опять об этом сообщение
                viewModel.liveDataResult.observe(viewLifecycleOwner) { msg ->
                    showMyDialog(msg, fragmentDialog)
                }
            }
        }
        viewModel.liveDataCount.observe(viewLifecycleOwner) { count ->
            if (count == COUNT_ERRORS_PASSW) {
                viewModel.countErrors = 0
                Log.e("createUser", "count = $count")
                showMyDialog("", resetPassDialog)       //показато диалог для сбороса пароля
            }
        }
    }
}