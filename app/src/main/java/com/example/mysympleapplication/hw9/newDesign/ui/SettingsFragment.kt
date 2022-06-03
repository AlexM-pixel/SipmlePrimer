package com.example.mysympleapplication.hw9.newDesign.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import androidx.fragment.app.viewModels
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.base.BaseFragment
import com.example.mysympleapplication.hw9.newDesign.di.builder.ViewModelFactory
import com.example.mysympleapplication.hw9.newDesign.utils.Config.FEEDBACK_EMAIL_ADDRESS
import com.example.mysympleapplication.hw9.newDesign.utils.Config.FEEDBACK_SEND_EMAIL
import com.example.mysympleapplication.hw9.newDesign.utils.Config.FEEDBACK_SUBJECT
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs
import com.example.mysympleapplication.hw9.newDesign.viewmodels.SettingsFragmentViewModel
import com.example.mysympleapplication.hw9.view.iu.BaseActivity
import java.util.*
import javax.inject.Inject

class SettingsFragment : BaseFragment() {
    var bankName: EditText? = null
    var friendsLogin: EditText? = null
    var btnBankName: Button? = null
    var btnSignOut: Button? = null
    var btnAddFriend: Button? = null
    var btnSendMsg: Button? = null
    var radioGroup: RadioGroup? = null

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModelSettings: SettingsFragmentViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings_nd, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        setListeners()
    }

    private fun setListeners() {
        btnBankName?.setOnClickListener { viewModelSettings.setNewBankName(bankName?.text.toString()) }
        btnSignOut?.setOnClickListener { viewModelSettings.logOut() }
        btnAddFriend?.setOnClickListener { viewModelSettings.addFriend(friendsLogin?.text.toString()) }
        btnSendMsg?.setOnClickListener { goToFeedback("helloWorld") }
        radioGroup?.setOnCheckedChangeListener { radioGroup, i -> checkStyleTheme(i) }
    }

    private fun init(view: View) {
        bankName = view.findViewById(R.id.edit_text_BankNameNd)
        friendsLogin = view.findViewById(R.id.email_friends_nd)
        btnBankName = view.findViewById(R.id.button_add)
        btnSignOut = view.findViewById(R.id.button_sign_Out)
        btnAddFriend = view.findViewById(R.id.button_add_Friends)
        btnSendMsg = view.findViewById(R.id.button_support_nd)
        radioGroup = view.findViewById(R.id.radio_group_styleTheme_nd)
        initRadioButton()
    }

    private fun goToFeedback(msg: String) {   // техт не передается в сообщение
        val emailIntent = Intent(
            Intent.ACTION_SENDTO, Uri.fromParts("mailto", FEEDBACK_EMAIL_ADDRESS, null)
        )
        //  emailIntent.data = Uri.parse("mailto:")
        //   emailIntent.putExtra(Intent.EXTRA_EMAIL, FEEDBACK_EMAIL_ADDRESS)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, FEEDBACK_SUBJECT)
        emailIntent.putExtra(Intent.EXTRA_TEXT, msg)
        startActivity(Intent.createChooser(emailIntent, FEEDBACK_SEND_EMAIL))
    }

    private fun checkStyleTheme(checkedId: Int) {
        when (checkedId) {
            R.id.radio_pink_nd -> setStyleTheme(R.style.AppThemePink)
            R.id.radio_orange_nd -> setStyleTheme(R.style.AppThemeOrange)
            R.id.radio_blue_nd -> setStyleTheme(R.style.AppThemeBlue)
            R.id.radio_def_nd -> setStyleTheme(R.style.AppTheme)
        }
    }

    private fun setStyleTheme(newStyle: Int) {                                      // сохраняю выбранный стиль и перезапускаю активити
        val oldStyle: Int = MainPrefs.stile
        if (oldStyle != newStyle) {
            MainPrefs.stile = newStyle
            requireActivity().recreate()
        }
    }

    private fun initRadioButton() {                                                // отображает нажатую кнопку при открытии активити
        val stile: Int = MainPrefs.stile
        when (stile) {
            R.style.AppThemePink -> radioGroup?.check(R.id.radio_pink_nd)
            R.style.AppThemeOrange -> radioGroup?.check(R.id.radio_orange_nd)
            R.style.AppThemeBlue -> radioGroup?.check(R.id.radio_blue_nd)
            R.style.AppTheme -> radioGroup?.check(R.id.radio_def_nd)
        }
    }
}