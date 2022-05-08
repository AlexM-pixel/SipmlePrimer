package com.example.mysympleapplication.hw9.newDesign.ui.dialogues

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.mysympleapplication.hw9.newDesign.utils.Config.ALERT_DIALOG_ND


class ResultsDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var text = ""
        val arguments = arguments
        if (arguments != null) {
            text = arguments.getString(ALERT_DIALOG_ND) ?: "null"
        }
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(text)
            .setCancelable(true)
            .setPositiveButton(
                "ok"
            ) { dialog: DialogInterface?, id: Int ->
               dismiss()
            }
        return builder.create()
    }
}