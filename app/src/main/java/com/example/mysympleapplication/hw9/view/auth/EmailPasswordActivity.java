package com.example.mysympleapplication.hw9.view.auth;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.example.mysympleapplication.R;
import com.example.mysympleapplication.hw9.Main9Activity;
import com.example.mysympleapplication.hw9.MyAppDataBase;
import com.example.mysympleapplication.hw9.Spend;
import com.example.mysympleapplication.hw9.model.Balance;
import com.example.mysympleapplication.hw9.model.Friends;
import com.example.mysympleapplication.hw9.view.iu.BaseActivity;
import com.example.mysympleapplication.hw9.view.iu.dialogues.FragmentDialogAlert;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EmailPasswordActivity extends BaseActivity implements View.OnClickListener {

    public static final String MAIL = "email";
    public static final String TAG = "EmailPassword";
    private int count = 0;
    private FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    CollectionReference cRef;
    ExecutorService service;
    private FragmentManager fm;
    private EditText emailText;
    private EditText passwordText;
    public static final int AUTH_REQUEST = 15;
    private FragmentDialogAlert alertDialogFragment;
    public static final String ALERT_DIALOG = "alert_dialog";
    public static final String EMAIL_USER = "Firebase_auth_email";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailpassword);
        fm = getFragmentManager();
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        firestore = FirebaseFirestore.getInstance();
        emailText = findViewById(R.id.field_email);
        passwordText = findViewById(R.id.field_password);
        //Buttons
        Button emailSignInButton = findViewById(R.id.email_sign_in_button);
        Button emailCreateAccountButton = findViewById(R.id.email_create_account_button);
        emailCreateAccountButton.setOnClickListener(this);
        alertDialogFragment = new FragmentDialogAlert();
        emailSignInButton.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setProgressBar(R.id.progressBar);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    private void signIn(final String email, String password) {
        if (!validateForm()) {
            return;
        }
        Bundle bundle = new Bundle();
        showProgressBar();
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.e(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            startServiceCheckData(user);
                            saveEmailCurrentUser(user);
                        }
                        updateUI(user);
                        count = 0;
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e(TAG, "signInWithEmail:failure", task.getException());
                        if (task.getException().toString().contains("There is no user record corresponding to this identifier.")) {
                            if (!alertDialogFragment.isAdded()) {
                                bundle.putString(EmailPasswordActivity.ALERT_DIALOG, "не зарегистрированный email ");
                                alertDialogFragment.setArguments(bundle);
                                alertDialogFragment.show(fm, "badly email");
                            }
                        }
                        if (task.getException().toString().contains("The password is invalid or the user does not have a password")) {
                            bundle.putString(EmailPasswordActivity.ALERT_DIALOG, "не верный пароль !");
                            alertDialogFragment.setArguments(bundle);
                            if (!alertDialogFragment.isAdded()) {
                                alertDialogFragment.show(fm, "badly email");
                            }
                        }
                        if (task.getException().toString().contains("You can immediately restore it by resetting your password or you can try again later.")) {
                            bundle.putString(EmailPasswordActivity.ALERT_DIALOG, "Попробуйте позже или зайдите на почту для сброса пароля !");
                            alertDialogFragment.setArguments(bundle);
                            alertDialogFragment.show(fm, "badly email");
                        }
                        count++;
                        if (count == 3) {                                                        //если пароль или почта введены не правильно 3 раза подряд
                            resetPasswordUser(bundle, email);
                        }
                    }
                    hideProgressBar();
                });
        // [END sign_in_with_email]
    }

    private void resetPasswordUser(Bundle bundle, String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid -> {                                      //  восстановление пароля
                    bundle.putString(EmailPasswordActivity.ALERT_DIALOG, "На вашу почту отправлено письмо для восстановления пароля");   // cюда добавил диалог о отправке на почту
                    alertDialogFragment.setArguments(bundle);
                    if (!alertDialogFragment.isAdded()) {
                        alertDialogFragment.show(fm, "badly email");
                    } else {
                        alertDialogFragment.dismiss();
                        alertDialogFragment.show(fm, "badly email");
                    }
                }).addOnFailureListener(e -> {

            Log.e(EmailPasswordActivity.TAG, e.toString() + "  !!!!!!");
            if (e.toString().contains("The email address is badly formatted")) {
                bundle.putString(EmailPasswordActivity.ALERT_DIALOG, "не корректный email !");
                alertDialogFragment.setArguments(bundle);
                alertDialogFragment.show(fm, "badly email");
            }
        });
        count = 0;
    }

    private void resetPasswordUser(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid -> {                                      //  восстановление пароля
                    Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "email===" + email);
                }).addOnFailureListener(e -> {

            Log.e(EmailPasswordActivity.TAG, e.toString() + "  !!!!!!");

        });
        count = 0;
    }

    private void startServiceCheckData(FirebaseUser user) {
        cRef = firestore
                .collection(user.getEmail());
        // start new Thread:
        service = Executors.newSingleThreadExecutor();
        Runnable run = this::geAllSpendsFromFireStore;
        service.submit(run);
        service.shutdown();
    }


    private void createAccount(String email, String password) {
        Log.e(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }
        showProgressBar();
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            startServiceCheckData(user);
                            saveEmailCurrentUser(user);
                            createCollectionFriends(user);
                            updateUI(user);
                        } else {
                            Log.e(TAG, "createUserWithEmail:failure", task.getException());
                            Bundle bundle = new Bundle();
                            // If sign in fails, display a message to the user.
                            if (task.getException().toString().contains("Password should be at least 6 characters")) {
                                bundle.putString(EmailPasswordActivity.ALERT_DIALOG, "Пароль должен содержать минимум 6 символов");
                                alertDialogFragment.setArguments(bundle);
                                alertDialogFragment.show(fm, "badly email");
                            }
                            if (task.getException().toString().contains("The email address is already in use by another account")) {
                                bundle.putString(EmailPasswordActivity.ALERT_DIALOG, "Данный email уже зарегистрирован");
                                alertDialogFragment.setArguments(bundle);
                                alertDialogFragment.show(fm, "badly email");
                            }
                            // updateUI(null);
                        }
                        // [START_EXCLUDE]
                        hideProgressBar();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private void createCollectionFriends(FirebaseUser user) {
        Friends friend = new Friends(false, "first_init");
        firestore.collection(user.getEmail())
                .document("fiends")
                .collection("friends")
                .document("first_init")
                .set(friend);
        Log.e("rtrtr", "set first init to friends collection");
    }

    private void saveEmailCurrentUser(FirebaseUser user) {
        SharedPreferences sPref = getSharedPreferences(Main9Activity.APP_PREFERENCES, MODE_PRIVATE);
        sPref.edit()
                .putString(EMAIL_USER, user.getEmail())
                .apply();
    }

    private boolean validateForm() {
        boolean valid = true;
        String validEmail = "^[a-zA-Z0-9.]{1,}[@]{1}[a-z]{2,7}[.]{1}+[a-z]{2,6}";

        String email = emailText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailText.setHintTextColor(Color.RED);
            emailText.setHint("write email !");
            valid = false;
            return valid;
        }

        if (!email.matches(validEmail)) {
            emailText.setText("");
            emailText.setHintTextColor(Color.RED);
            emailText.setHint("не корректный email !");
            valid = false;
        }

        String password = passwordText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordText.setHintTextColor(Color.RED);
            passwordText.setHint("write password !");
            valid = false;
        }
        return valid;
    }

    private void updateUI(FirebaseUser user) {
        hideProgressBar();
        if (user != null) {

            Intent intent = new Intent(this, Main9Activity.class);
            startActivityForResult(intent, AUTH_REQUEST);
        } else {
            //   Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkDbFirestore(List<Spend> spendListFirestore) {
        List<Spend> spendListRoom = MyAppDataBase.getInstance().spendDao().getAllSpends();
        Balance balance = MyAppDataBase.getInstance().balanceDao().getBalanceSignIn();
        if (spendListFirestore.size() > 0 && spendListRoom != null && spendListRoom.size() == 0) {        // если в firestore по этому аккаунту что-то есть а в room пусто добавляю все в room db
            Log.e(TAG, spendListFirestore.size() + ":  колличество элементов в firestore");
            savingToRoom(spendListFirestore);
        }
        if (spendListFirestore.size() == 0 && spendListRoom != null && spendListRoom.size() > 0) {
            saveSpendFireStore(spendListRoom, balance);
        }
        if (spendListFirestore.size() == 0 && spendListRoom != null && spendListRoom.size() == 0) {
            Log.e(TAG, "Здесь запустить метод создания коллекций в fireStore");                  // tuta =============================//=====================//==//==//=======

        }
        Log.e(TAG, spendListFirestore.size() + ":  колличество элементов в firestore");
        Log.e(TAG, spendListRoom.size() + ":  колличество элементов в room");
    }


    // запихнуть все в onCreate()

    @WorkerThread
    public void geAllSpendsFromFireStore() {                                                        // получаю все затраты из коллекции firestore и заношу их в список
        List<Spend> allItems = new ArrayList<>();
        cRef.document("spends")
                .collection("spends")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            long id = document.getLong("id");
                            String name = String.valueOf(document.get("spendName"));
                            String value = String.valueOf(document.get("value"));
                            String date = String.valueOf(document.get("date"));
                            Spend spend = new Spend(id, name, value, date);
                            Log.e(TAG, spend.getId() + " " + spend.getSpendName() + " " + spend.getValue());
                            allItems.add(spend);
                            Log.e(TAG, allItems.get(0).getId() + " " + allItems.size());
                        }
                        Log.e(TAG, Thread.currentThread().getName() + " allItemsSize= " + allItems.size());
                    } else {
                        Log.e(TAG, "Error getting documents.", task.getException());
                    }
                    if (task.isComplete()) {
                        Log.e(TAG, Thread.currentThread().getName() + " allItemsSize= " + allItems.size());
                        checkDbFirestore(allItems);

                    }
                });
    }

    private void savingToRoom(List<Spend> spendListFirestore) {
        MyAppDataBase.getInstance().spendDao().inserAll(spendListFirestore);
        cRef.document("balance")
                .collection("balance")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Long id = document.getLong("id");
                            String balance = String.valueOf(document.get("balance"));
                            Balance balanceObj = new Balance(id, balance);
                            Log.e(TAG, "получил значение баланса= " + balanceObj.getBalance());
                            MyAppDataBase.getInstance().balanceDao().insertB(balanceObj);
                        }
                    } else {
                        Log.e(TAG, "Error getting balance.", task.getException());
                    }
                });

    }

    private void saveSpendFireStore(List<Spend> spendsList, Balance balance) {
        for (Spend spend : spendsList) {
            cRef.document("spends")
                    .collection("spends")
                    .document(spend.getId().toString())
                    .set(spend)
                    .addOnSuccessListener(v -> {
                        Log.e(TAG, "saved spend in firestore from room");
                    })
                    .addOnFailureListener(f -> {
                        Log.e(TAG, "Save Failed");
                    });
        }
        if (balance != null) {
            cRef.document("balance")
                    .collection("balance")
                    .document(balance.getId().toString())
                    .set(balance)
                    .addOnSuccessListener(b -> {
                        Log.e(TAG, "saved balance in firestore from room");
                    })
                    .addOnFailureListener(f -> {
                        Log.e(TAG, "Save balance Failed");
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTH_REQUEST && resultCode == RESULT_OK) {                                           // чтобы опять не заходило в это Активити если пользователь уже залогинелся
            finish();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.email_create_account_button:
                createAccount(emailText.getText().toString(), passwordText.getText().toString());
                break;
            case R.id.email_sign_in_button:
                String email = emailText.getText().toString();
                signIn(email, passwordText.getText().toString());
              //  resetPasswordUser(email);
                break;
        }
    }
}
