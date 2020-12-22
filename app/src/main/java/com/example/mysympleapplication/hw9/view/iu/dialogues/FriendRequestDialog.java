package com.example.mysympleapplication.hw9.view.iu.dialogues;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mysympleapplication.hw9.Main9Activity;
import com.example.mysympleapplication.hw9.model.Friends;
import com.example.mysympleapplication.hw9.view.auth.EmailPasswordActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.example.mysympleapplication.hw9.Main9Activity.APP_PREFERENCES;

public class FriendRequestDialog extends DialogFragment {
    private FirebaseFirestore firestore;
    SharedPreferences sPref;
    private String email;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            email = arguments.getString(Main9Activity.ADDED_USER);
        }
        sPref = this.getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        String myEmail = sPref.getString(EmailPasswordActivity.EMAIL_USER, "emty");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Поступил запрос на добавение к вам человечка под логином: " + email)
                .setCancelable(false)
                .setPositiveButton("ok", (dialog, id) -> {
                    // засетить к добавляющему мой почтовый адрес и занести у себя в коллекции значение true
                    Friends friend = new Friends(true, email);
                    firestore.collection(myEmail)
                            .document("fiends")
                            .collection("friends")
                            .document(email)
                            .set(friend);
                    friend.setEmail_user(myEmail);                                             // заменил на свое мыло чтобы создать у добавляющего документ с моим почтовым адресом
                    firestore.collection(email)
                            .document("Observefiends")
                            .collection("Observefriends")
                            .document(myEmail)
                            .set(friend);
                    sPref.edit()
                            .putBoolean(Main9Activity.PREFERENCES_CHECK_FRIEND, true)
                            .apply();
                });
        builder.setNegativeButton("Отказ",
                ((dialog, which) -> {
                    firestore.collection(myEmail)
                            .document("fiends")
                            .collection("friends")
                            .document(email)
                            .delete();
                    dialog.cancel();
                }));
        return builder.create();
    }
}
