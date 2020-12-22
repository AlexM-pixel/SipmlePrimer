package com.example.mysympleapplication.hw9;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mysympleapplication.R;
import com.example.mysympleapplication.hw9.model.Friends;
import com.example.mysympleapplication.hw9.view.auth.EmailPasswordActivity;
import com.example.mysympleapplication.hw9.view.iu.dialogues.FriendRequestDialog;
import com.example.mysympleapplication.hw9.view.iu.dialogues.RecoveryDialog;
import com.example.mysympleapplication.hw9.viewModel.MyViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main9Activity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_SMS = 99;
    public static final String DATE_MONTH = "choice_date";
    public static final String APP_PREFERENCES = "sPref";
    public static final String PREFERENCES_FROM = "from";
    public static final String PREFERENCES_CHECK = "prefCheck";
    public static final String PREFERENCES_CHECK_FRIEND = "check_added_friend";
    public static final String ADDED_USER = "email_userFrom_firestore";
    private boolean checkAddedFriend;
    private SharedPreferences sPref;
    Friends observeFriends;
    FragmentManager fm;

    //--------------------------------------------
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main9);
        fm = getSupportFragmentManager();
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_for_fragments, HomeFragment.newInstance())
                    .commit();
        }
        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        String email = sPref.getString(EmailPasswordActivity.EMAIL_USER, "empty");
        Log.e("AScs", "Main9Activity, email= " + email);
        MyViewModel myViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(MyViewModel.class);
        myViewModel.getFriendsData(email).observe(this, input -> {
            checkAddedFriend = sPref.getBoolean(PREFERENCES_CHECK_FRIEND, false);
            if (input.size() > 1 && !checkAddedFriend) {
                // добавить болеан переменную для проверкм может я уже добалил кого
                Toast.makeText(this, " размер списка= " + input.size() + " последний злемент= " + input.get(input.size() - 1), Toast.LENGTH_SHORT).show();
                FriendRequestDialog friendRequestDialog = new FriendRequestDialog();
                Bundle bundle = new Bundle();
                bundle.putString(ADDED_USER, input.get(input.size() - 1).getEmail_user());
                friendRequestDialog.setArguments(bundle);
                friendRequestDialog.show(fm, "requestFriendDialog");
            }
        });
        myViewModel.getObserveFriendsData(email).observe(this, input -> {
            Log.e("AScs", "отработал getObserveFriends");
            Log.e("AScs", "input.size= "+input.size());
            if (input.size() > 0) {
                Log.e("AScs", "input.size= "+input.size());
                // здесь создать обьект френда(можно строку с мылом) который буду закидывать в бандл фрагмента баланс там при создании будет проверка и если будет чейта мэйл то качаю и отбражаю его данные
                observeFriends = new Friends(input.get(input.size() - 1).getAccess(), input.get(input.size() - 1).getEmail_user());
            }
        });
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = HomeFragment.newInstance();
                    break;
                case R.id.navigation_balance:
                    if (observeFriends != null) {
                        fragment = BalanceFragment.newInstance(observeFriends.getAccess(), observeFriends.getEmail_user());
                    } else {
                        fragment = BalanceFragment.newInstance();
                    }
                    break;
                case R.id.navigation_settings:
                    fragment = SettingsFragment.newInstance();
                    break;
            }
            fm
                    .beginTransaction()
                    .replace(R.id.frame_for_fragments, fragment)
                    .commit();
            return true;
        });

        boolean checkAdressat = sPref.getBoolean(PREFERENCES_CHECK, false);
        if (!checkAdressat) {
            showDialog();
        }
        if (!isSmsPermissionGranted()) {
            ActivityCompat.requestPermissions(Main9Activity.this,
                    new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS}, PERMISSION_REQUEST_SMS);
        }

    }


    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    public boolean isSmsPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    void showDialog() {

        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") View saveLayoutView = inflater.inflate(R.layout.save_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(saveLayoutView);
        final EditText textAdressat = saveLayoutView.findViewById(R.id.input_adressat);
        builder.setCancelable(false);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String adressat = textAdressat.getText().toString();
                Set<String> stringSet = sPref.getStringSet(PREFERENCES_FROM, new HashSet<String>());
                Set<String> setCurrent = new HashSet<>(stringSet);
                if (!adressat.isEmpty()) {
                    setCurrent.add(adressat);
                    sPref.edit()
                            .putStringSet(PREFERENCES_FROM, setCurrent)
                            .putBoolean(PREFERENCES_CHECK, true)
                            .apply();
                    Toast.makeText(Main9Activity.this, "Молодец молодец!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Main9Activity.this, "ОЙ!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
