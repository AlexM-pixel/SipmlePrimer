package com.example.mysympleapplication.hw9.newDesign.ui.dialogues

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mysympleapplication.hw9.newDesign.di.builder.ViewModelFactory
import com.example.mysympleapplication.hw9.newDesign.ui.ARG_DATE_DETAIL
import com.example.mysympleapplication.hw9.newDesign.ui.ARG_NAME_DETAIL
import com.example.mysympleapplication.hw9.newDesign.utils.Config
import com.example.mysympleapplication.hw9.newDesign.viewmodels.DetailMonthlyViewModel
import dagger.android.support.DaggerDialogFragment
import javax.inject.Inject

class DeleteSpendDialog : DaggerDialogFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var detailViewModel: DetailMonthlyViewModel


    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        detailViewModel =
            ViewModelProvider(
                requireActivity(),
                viewModelFactory
            )[DetailMonthlyViewModel::class.java]
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val id: Long
        val name: String
        val month: String
        val arguments = arguments
        id = arguments?.getLong(Config.DEL_SPEND_DIALOG) ?: 0
        name = arguments?.getString(ARG_NAME_DETAIL) ?: "empty"
        month = arguments?.getString(ARG_DATE_DETAIL) ?: "empty"
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Удалить на веки вечные!")
            .setCancelable(true)
            .setPositiveButton(
                "ok"
            ) { dialog: DialogInterface?, _: Int ->
                detailViewModel.deleteSpend(id.toString(), name = name, month = month)
                dismiss()
            }
            .setNegativeButton("отмена") { dialog: DialogInterface?, _: Int ->
                dismiss()
            }
        return builder.create()
    }

}