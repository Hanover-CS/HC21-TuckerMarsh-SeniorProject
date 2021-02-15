package com.marsht21.restaurantpicker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

/*
 * LoginActivity
 * Opening screen of app where user can login, reset password, or create an account
 */
public class LoginActivity extends AppCompatActivity {
    private Button mLogin;
    private EditText mEmail;
    private Button mForgot;
    private Button mRegister;
    private EditText mPassword;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = getInstance();
        mLogin = findViewById(R.id.login);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mForgot = findViewById(R.id.forgot_password);
        mRegister = findViewById(R.id.register);

        mLogin.setOnClickListener(v -> { //Take email and password from user and logs them into the app
            final String email = mEmail.getText().toString();
            final String password = mPassword.getText().toString();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, task -> {
                if(task.isSuccessful()) {  //Account created successfully and open app to home activity
                    Toast.makeText(LoginActivity.this, "Successfully Logged In", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);

                }else {
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                }
            })
        ;});

        mForgot.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        mRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            intent.putExtra("email", mEmail.getText().toString());
            startActivity(intent);
        });

    }

}