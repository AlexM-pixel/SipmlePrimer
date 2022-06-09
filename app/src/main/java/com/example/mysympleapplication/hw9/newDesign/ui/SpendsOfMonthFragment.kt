package com.example.mysympleapplication.hw9.newDesign.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.domain.background.SmsReceiver
import java.util.regex.Pattern

const val ARG_DATE = "date_arg"

class SpendsOfMonthFragment : Fragment() {
    private var date: String? = null
    private lateinit var textDate: TextView
    private lateinit var editBodySms: EditText
    private lateinit var editRegex: EditText
    private lateinit var textResult: TextView
    private lateinit var buttonStart: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            date = it?.getString(ARG_DATE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_spends_of_mounth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        textDate.text = date
    }

    private fun init(view: View) {
        textDate = view.findViewById(R.id.date_nd)
        editBodySms = view.findViewById(R.id.insertBodySms)
        editRegex = view.findViewById(R.id.validFormRegexInsert)
        textResult = view.findViewById(R.id.result_nd)
        buttonStart = view.findViewById(R.id.btn_start_nd)
    }


}