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
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.mysympleapplication.R;
import com.example.mysympleapplication.hw9.model.Friends;
import com.example.mysympleapplication.hw9.view.auth.EmailPasswordActivity;
import com.example.mysympleapplication.hw9.view.iu.BaseActivity;
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
    RadioGroup radioGroup;

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
        radioGroup = view.findViewById(R.id.radio_group_styleTheme);
        sPref = view.getContext().getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        final EditText textAdressat = view.findViewById(R.id.edit_text_BankName);
        EditText editText = view.findViewById(R.id.email_friends_id);
        Button signOut = view.findViewById(R.id.button_sign_Out);
        Button button = view.findViewById(R.id.button_add);
        initRadoiButton();
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
            //     SharedPreferences sPref = this.getActivity().getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);        // сделать отдельный класс по преференсам
            String friends_email = editText.getText().toString();                                          // добавить проверку на существование такой коллекции
           if (!friends_email.isEmpty()) {
               String email = sPref.getString(EmailPasswordActivity.EMAIL_USER, "empty");
               Friends friend = new Friends(false, email);
               FirebaseFirestore firestore = FirebaseFirestore.getInstance();
               firestore.collection(friends_email)
                       .document("fiends")
                       .collection("friends")
                       .document(email)
                       .set(friend);
               Log.e("rtrtr", "сработало");
           }else {
               editText.setHint("Пожалуйста напиши email друга");
           }
        });
        checkStyleTheme();
        return view;
    }

    private void checkStyleTheme() {
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radio_pink:
                    setStyleTheme(R.style.AppThemePink);
                    break;
                case R.id.radio_orange:
                    setStyleTheme(R.style.AppThemeOrange);
                    break;
                case R.id.radio_blue:
                    setStyleTheme(R.style.AppThemeBlue);
                    break;
                case R.id.radio_def:
                    setStyleTheme(R.style.AppTheme);
                    break;
            }
        });
    }

    private void setStyleTheme(int newStyle) {                                      // сохраняю выбранный стиль и перезапускаю активити
        int oldStyle = sPref.getInt(BaseActivity.THEME_STYLE, R.style.AppTheme);
        if (oldStyle != newStyle) {
            sPref.edit()
                    .putInt(BaseActivity.THEME_STYLE, newStyle)
                    .apply();
            getActivity().recreate();
        }
    }

    private void initRadoiButton() {                                                // отображает нажатую кнопку при открытии активити
        int stile = sPref.getInt(BaseActivity.THEME_STYLE, R.style.AppTheme);
        switch (stile) {
            case R.style.AppThemePink:
                radioGroup.check(R.id.radio_pink);
                break;
            case R.style.AppThemeOrange:
                radioGroup.check(R.id.radio_orange);
                break;
            case R.style.AppThemeBlue:
                radioGroup.check(R.id.radio_blue);
                break;
            case R.style.AppTheme:
                radioGroup.check(R.id.radio_def);
                break;
        }
    }

}