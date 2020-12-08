package com.example.mysympleapplication.hw9.view.iu.dialogues;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;


import com.example.mysympleapplication.R;
import com.example.mysympleapplication.hw9.view.auth.EmailPasswordActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class RecoveryDialog extends DialogFragment {
    private FirebaseAuth mAuth;
    private String mail;
    String maill;
    EditText userInput;
    FragmentManager fm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        fm = getChildFragmentManager();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mail = arguments.getString(EmailPasswordActivity.MAIL);
        }
        LayoutInflater li = LayoutInflater.from(getActivity());
        View dialogView = li.inflate(R.layout.dialog_email, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        userInput = dialogView.findViewById(R.id.input_text);

        builder.setCancelable(false);
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        maill = String.valueOf(userInput.getText());
                        mAuth.sendPasswordResetEmail(maill)
                                .addOnSuccessListener(aVoid -> {                                      //  восстановление пароля
                                    Log.e(EmailPasswordActivity.TAG, aVoid.toString() + "  !!!!!!");

                                }).addOnFailureListener(e -> {

                            Log.e(EmailPasswordActivity.TAG, e.toString() + "  !!!!!!");
                            if (e.toString().contains("The email address is badly formatted")) {
//                               FragmentDialogAlert alertDialogFragment = new FragmentDialogAlert();
//                            Bundle bundle = new Bundle();
//                                bundle.putString(EmailPasswordActivity.ALERT_DIALOG, "не корректный email !");
//                                alertDialogFragment.onCreateDialog(bundle);
//                                alertDialogFragment.setArguments(bundle);
//                                alertDialogFragment.show(fm, "badly email");
                            }
                        });
                    }
                });
        builder.setNegativeButton("Отмена",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }
}
