package com.example.mysympleapplication.hw9.newDesign.base

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.mysympleapplication.hw9.newDesign.utils.Config
import dagger.android.support.DaggerFragment

abstract class BaseFragment : DaggerFragment() {
    protected fun showView(view: View) {
        if (view.visibility == View.GONE) {
            view.visibility = View.VISIBLE
        }
    }

    protected fun hideView(view: View) {
        if (view.visibility == View.VISIBLE) {
            view.visibility = View.GONE
        }
    }

    fun showMyDialog(result: String, fragmentDialog: DialogFragment) {
        if (result!="") {
            val bundle = Bundle()
            bundle.putString(Config.ALERT_DIALOG_ND, result)
                    fragmentDialog.arguments = bundle
        }
            requireActivity().supportFragmentManager.let {
                it.executePendingTransactions()
                if (!fragmentDialog.isAdded) {
                    fragmentDialog.show(it, "dialog_list")
                }
            }

    }
    protected fun hideKeyboard() {
        val view = activity?.getCurrentFocus()
        if (view != null) {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
    fun showMessage(text: String?) {
        val toast = Toast.makeText(
            requireContext(),
            text,
            Toast.LENGTH_LONG
        )
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

}