package com.example.mysympleapplication.hw9.view.auth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.example.mysympleapplication.R;
import com.example.mysympleapplication.hw9.Main9Activity;
import com.example.mysympleapplication.hw9.view.iu.BaseActivity;
import com.example.mysympleapplication.hw9.view.iu.dialogues.FragmentDialogAlert;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailPasswordActivity extends BaseActivity implements View.OnClickListener {

    public static final String MAIL = "email";
    public static final String TAG = "EmailPassword";
    private int count = 0;
    private FirebaseAuth mAuth;
    private FragmentManager fm;
    private Button emailSignInButton;
    private Button emailCreateAccountButton;
    private EditText emailText;
    private EditText passwordText;
    public static final int AUTH_REQUEST = 15;
    private FragmentDialogAlert alertDialogFragment;
    public static final String ALERT_DIALOG = "alert_dialog";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailpassword);
        fm = getSupportFragmentManager();
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        emailText = findViewById(R.id.field_email);
        passwordText = findViewById(R.id.field_password);
        //Buttons
        emailSignInButton = findViewById(R.id.email_sign_in_button);
        emailCreateAccountButton = findViewById(R.id.email_create_account_button);
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
                            mAuth.sendPasswordResetEmail(email)
                                    .addOnSuccessListener(aVoid -> {                                      //  восстановление пароля
                                        bundle.putString(EmailPasswordActivity.ALERT_DIALOG, "На вашу почту отправлено письмо для восстановления пароля");   // cюда добавил диалог о отправке на почту
                                        alertDialogFragment.setArguments(bundle);
                                        if (!alertDialogFragment.isAdded()) {
                                            alertDialogFragment.show(fm, "badly email");
                                        }else {
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
                    }
                    hideProgressBar();
                });
        // [END sign_in_with_email]
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTH_REQUEST && resultCode == RESULT_OK) {                                                // чтобы опять не заходило в это Активити если пользователь уже залогинелся
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.email_create_account_button:
                createAccount(emailText.getText().toString(), passwordText.getText().toString());
                break;
            case R.id.email_sign_in_button:
                signIn(emailText.getText().toString(), passwordText.getText().toString());
                break;
        }
    }

    private void savingUserProfile() {
        String email;
        String token;
        Log.e(TAG, "savingUserProfile");
    }
}
