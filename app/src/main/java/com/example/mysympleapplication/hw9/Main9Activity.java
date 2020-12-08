package com.example.mysympleapplication.hw9;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
    private List<SumSpendsOfMonth> cardViewArrayList;
    private SpendMonthAdapter adapter;
    private boolean checkAdressat;
    private SharedPreferences sPref;
    //--------------------------------------------
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main9);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_for_fragments, HomeFragment.newInstance())
                    .commit();
        }
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        fragment = HomeFragment.newInstance();
                        break;
                    case R.id.navigation_balance:
                        fragment = BalanceFragment.newInstance();
                        break;
                    case R.id.navigation_settings:
                        fragment = SettingsFragment.newInstance();
                        break;
                }
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_for_fragments, fragment)
                        .commit();
                return true;
            }
        });
        sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        checkAdressat = sPref.getBoolean(PREFERENCES_CHECK, false);
        if (!checkAdressat) {
            showDialog();
        }

//        RecyclerView recyclerView = findViewById(R.id.recycler_for_mounth_spends);
//        cardViewArrayList = new ArrayList<>();
//        getItemForMonthSpendsSpisok();
//        adapter = new SpendMonthAdapter(cardViewArrayList);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setAdapter(adapter);
//        adapter.setSpendMonthListener(new SpendMonthAdapter.SpendMonthListener() {
//            @Override
//            public void onSpendMonthClickListener(String date) {
//                Intent intent = new Intent(Main9Activity.this, DetailMonthActivity.class);
//                intent.putExtra(DATE_MONTH, date);
//                startActivity(intent);
//            }
//        });

        Log.e("AScs", "onCreate() ");
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
                            //.putString(PREFERENCES_FROM, adressat)           // добавить в сервис getPreference  для получения адрессата а лучше сохранять массив
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
