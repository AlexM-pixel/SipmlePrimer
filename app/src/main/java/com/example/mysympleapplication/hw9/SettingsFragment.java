package com.example.mysympleapplication.hw9;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mysympleapplication.R;
import com.example.mysympleapplication.hw9.model.Friends;
import com.example.mysympleapplication.hw9.view.auth.EmailPasswordActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashSet;
import java.util.Set;

import static android.app.Activity.RESULT_CANCELED;
import static android.content.Context.MODE_PRIVATE;
import static com.example.mysympleapplication.hw9.Main9Activity.APP_PREFERENCES;


public class SettingsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private SharedPreferences sPref;

    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_settings, container, false);
        final EditText textAdressat = view.findViewById(R.id.edit_text_BankName);
        EditText editText = view.findViewById(R.id.email_friends_id);
        Button signOut = view.findViewById(R.id.button_sign_Out);
        sPref = view.getContext().getSharedPreferences(Main9Activity.APP_PREFERENCES, MODE_PRIVATE);
        Button button = view.findViewById(R.id.button_add);
        button.setOnClickListener(v -> {
            String adressat = textAdressat.getText().toString();
            Set<String> stringSet = sPref.getStringSet(Main9Activity.PREFERENCES_FROM, new HashSet<String>());
            Set<String> setCurrent = new HashSet<>(stringSet);
            if (!adressat.isEmpty()) {
                setCurrent.add(adressat);
                sPref.edit()
                        .putStringSet(Main9Activity.PREFERENCES_FROM, setCurrent)
                        .putBoolean(Main9Activity.PREFERENCES_CHECK, true)
                        .apply();
                Toast.makeText(view.getContext(), "Saved!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(view.getContext(), "Введите имя банка!", Toast.LENGTH_SHORT).show();
            }

        });
        signOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            getActivity().setResult(RESULT_CANCELED);
            getActivity().finish();
        });
        Button button_addFriends = view.findViewById(R.id.button_add_Friends);
        button_addFriends.setOnClickListener(v -> {
            SharedPreferences sPref = this.getActivity().getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);        // сделать отдельный класс по преференсам
            String email = sPref.getString(EmailPasswordActivity.EMAIL_USER, "empty");
            String friends_email = editText.getText().toString();                                          // добавить проверку на существование такой коллекции
            Friends friend = new Friends(false, email);
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection(friends_email)
                    .document("fiends")
                    .collection("friends")
                    .document(email)
                    .set(friend);
            Log.e("rtrtr", "сработало");
        });
        return view;
    }

}