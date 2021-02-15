package com.marsht21.restaurantpicker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

/*
 * RegistrationActivity
 * Registers a new user using Google Firebase
 */
public class RegistrationActivity extends AppCompatActivity {
    private Button mRegister;
    private EditText mEmail;
    private EditText mPassword;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        mRegister = findViewById(R.id.rregister);
        mEmail = findViewById(R.id.remail);
        mPassword = findViewById(R.id.rpasword);
        mToolbar = findViewById(R.id.toolbar_register);

        setToolbar();
        setEmail();

        mRegister.setOnClickListener(v -> {  //Take user email and password and creates account in firebase
            final String email = mEmail.getText().toString();
            final String password = mPassword.getText().toString();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegistrationActivity.this, task -> {
                if (task.isSuccessful()) { //Account created successfully and open app to home activity
                    Toast.makeText(RegistrationActivity.this, "Successfully Registered", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegistrationActivity.this, "Registration Failed", Toast.LENGTH_LONG).show();
                }
            })
            ;});
    }

    /*
     * Set email and password if value was entered on login screen
     */
    private void setEmail() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        mEmail.setText(bundle.getString("email"));
    }

    /*
     * Set toolbar for back functionality
     */
    private void setToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle("Register");
        } else {
            throw new NullPointerException("Something went wrong");
        }
    }



}