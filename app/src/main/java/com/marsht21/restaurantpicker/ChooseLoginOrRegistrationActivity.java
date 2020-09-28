package com.marsht21.restaurantpicker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import androidx.appcompat.app.AppCompatActivity;

public class ChooseLoginOrRegistrationActivity extends AppCompatActivity {
    private Button mLogin;
    private Button mRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_login_or_registration);

        mLogin = findViewById(R.id.login);
        mRegister = findViewById(R.id.register);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseLoginOrRegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseLoginOrRegistrationActivity.this, RegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}