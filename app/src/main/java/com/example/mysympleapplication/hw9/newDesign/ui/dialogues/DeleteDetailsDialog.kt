package com.example.mysympleapplication.hw9.newDesign.ui.dialogues

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.mysympleapplication.hw9.newDesign.di.builder.ViewModelFactory
import com.example.mysympleapplication.hw9.newDesign.ui.ARG_DATE_DETAIL
import com.example.mysympleapplication.hw9.newDesign.ui.ARG_NAME_DETAIL
import com.example.mysympleapplication.hw9.newDesign.utils.Config
import com.example.mysympleapplication.hw9.newDesign.viewmodels.EditSpendViewModel
import dagger.android.support.DaggerDialogFragment
import javax.inject.Inject

class DeleteDetailsDialog : DaggerDialogFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var editSpendViewModel: EditSpendViewModel


    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        editSpendViewModel =
            ViewModelProvider(
                requireActivity(),
                viewModelFactory
            )[EditSpendViewModel::class.java]
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val id: Long
        val arguments = arguments
        id = arguments?.getLong(Config.DEL_DETAILS_DIALOG) ?: 0
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Удалить !")
            .setCancelable(true)
            .setPositiveButton(
                "ok"
            ) { dialog: DialogInterface?, _: Int ->
                editSpendViewModel.deleteDetails(id)
                dismiss()
            }
            .setNegativeButton("отмена") { dialog: DialogInterface?, _: Int ->
                dismiss()
            }
        return builder.create()
    }

}