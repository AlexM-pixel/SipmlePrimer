package com.example.mysympleapplication.hw9.view.iu.dialogues;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mysympleapplication.hw9.view.auth.EmailPasswordActivity;

public class FragmentDialogAlert extends DialogFragment {


    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String text = "";
        Bundle arguments = getArguments();
        if (arguments != null) {
            text = arguments.getString(EmailPasswordActivity.ALERT_DIALOG);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(text)
                .setCancelable(true)
                .setPositiveButton("ok", (dialog, id) -> {
                });
        return builder.create();
    }

}
