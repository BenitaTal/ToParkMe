package com.example.toparkme.ToParkMe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.toparkme.R;
import com.example.toparkme.ToParkMe.classes.AppManager;
import com.example.toparkme.ToParkMe.classes.Person;
import com.example.toparkme.ToParkMe.classes.User;
//import com.example.toparkme.ToParkMe.utils.RealTimeDataManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class Activity_Log extends AppCompatActivity {

    private static final String BACKGROUND_URL = "https://pixabay.com/get/gdfef60270a5665c070066c788bc292cafa2d67e92c00caccc92058adbc2e5aa60bfae945a1dbf1b8b5d41a8bc6fec55dad3fc0f20e249cec8df4154f8989ff4f00692f34c40754ffe3c565f45b6c2fb6_1920.jpg";

    private MaterialButton submitLog;
    private MaterialButton signUp;
    private MaterialButton forgotPassword;
    private EditText emailLog;
    private EditText passwordLog;
    private ImageView main_IMG_back1;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        setViews();
        mAuth = FirebaseAuth.getInstance();

        submitLog.setOnClickListener(submitLogBtn);
        signUp.setOnClickListener(signUpBtn);
        forgotPassword.setOnClickListener(forgotPasswordLogBtn);

    }

    private View.OnClickListener submitLogBtn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            hideKeyboard(view);
            userLogin();
        }
    };


    private View.OnClickListener signUpBtn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            hideKeyboard(view);
            registerUser();
        }
    };

    private View.OnClickListener forgotPasswordLogBtn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            hideKeyboard(view);
            resetPassword();
        }
    };

    private void resetPassword() {
        String email = emailLog.getText().toString().trim();

        if (email.isEmpty()){
            emailLog.setError("Email is required!");
            emailLog.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailLog.setError("Please enter a valid email!");
            emailLog.requestFocus();
            return;
        }

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(Activity_Log.this, "Check your email to reset your password!", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(Activity_Log.this, "Try again! Something wrong happened!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void userLogin(){
        String email = emailLog.getText().toString();
        String password = passwordLog.getText().toString();

        if (email.isEmpty()){
            emailLog.setError("Email is required!");
            emailLog.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailLog.setError("Please enter a valid email!");
            emailLog.requestFocus();
            return;
        }
        if (password.isEmpty()){
            passwordLog.setError("Password is required!");
            passwordLog.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user.isEmailVerified()){
                                openActivity();
                            }else{
                                user.sendEmailVerification();
                                Toast.makeText(Activity_Log.this, "Check your email to verify your account!", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(Activity_Log.this, "Failed to login! Please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }



    private void registerUser(){
        String email = emailLog.getText().toString();
        String password = passwordLog.getText().toString();

        if (email.isEmpty()){
            emailLog.setError("Email is required!");
            emailLog.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailLog.setError("Please enter a valid email!");
            emailLog.requestFocus();
            return;
        }
        if (password.isEmpty()){
            passwordLog.setError("Password is required!");
            passwordLog.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            AppManager appManager = new AppManager(new Person());
                            appManager.getPerson().getUser().setUser(new User(email, password));

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(appManager).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Activity_Log.this, "The User has registered successfully! \nClose the app and login", Toast.LENGTH_LONG).show();
                                        finish();
                                    } else{
                                        Toast.makeText(Activity_Log.this, "Failed to registered! Try again", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(Activity_Log.this, "Failed to registered! Try again", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private void setViews() {
        setGlide();
        findViews();

    }

    private void setGlide(){
        main_IMG_back1 = findViewById(R.id.park_IMG_background);
        Glide
                .with(this)
                .load(BACKGROUND_URL)
                .centerCrop()
                .into(main_IMG_back1);
    }

    private void openActivity() {
        Intent newIntent = new Intent(this, Activity_Home.class);
        startActivity(newIntent);
        finish();
    }

    private void hideKeyboard(@NonNull View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
    }

    public void findViews(){
        emailLog = findViewById(R.id.park_log_email);
        passwordLog = findViewById(R.id.park_log_password);
        submitLog = findViewById(R.id.park_Btn_log_submit);
        signUp = findViewById(R.id.park_Btn_auth_sign_up);
        forgotPassword = findViewById(R.id.park_Btn_auth_change_password);

    }


}
