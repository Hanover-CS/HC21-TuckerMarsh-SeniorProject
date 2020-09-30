package com.marsht21.restaurantpicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class HomeActivity extends AppCompatActivity {
    private Button mLogout;
    private Button mSwipe;
    private EditText mSearch;
    private FirebaseAuth mAuth;
    private PlacesClient placesClient;

    private void initializePlaces () {
        Places.initialize(getApplicationContext(), getString(R.string.places_api_key));
        placesClient = Places.createClient(this);
    }

    private void checkPermissions(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 0);
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        this.checkPermissions();

        mAuth = FirebaseAuth.getInstance();
        mLogout = findViewById(R.id.hlogout);
        mSwipe = findViewById(R.id.swipe);
        mSearch = findViewById(R.id.search);

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mSwipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SwipeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String search = mSearch.getText().toString();

                String request = "https://api.foursquare.com/v2/venues/explore";


            }
        });
    }
}