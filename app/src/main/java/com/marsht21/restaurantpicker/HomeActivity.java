package com.marsht21.restaurantpicker;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.Place.Field;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private Button mLogout;
    private Button mSwipe;
    private Button mtest;
    private EditText mSearch;
    private FirebaseAuth mAuth;
    private TextView mResults;
    private PlacesClient placesClient;
    private List<Field> placeFields;
    private StringBuilder mResult;

    private void initializePlaces() {
        Places.initialize(getApplicationContext(), getString(R.string.places_api_key));
        placesClient = Places.createClient(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        placeFields = Arrays.asList(Place.Field.NAME, Place.Field.TYPES);
        mAuth = FirebaseAuth.getInstance();
        mLogout = findViewById(R.id.hlogout);
        mSwipe = findViewById(R.id.swipe);
        mSearch = findViewById(R.id.search);
        mtest = findViewById(R.id.test);
        mResults = findViewById(R.id.resultstest1);

        initializePlaces();

        mtest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(HomeActivity.this, new String[]{ACCESS_FINE_LOCATION}, 0);
                }
//                    FindCurrentPlaceRequest request = FindCurrentPlaceRequest.builder(placeFields).build();
//                    Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
//
//                placeResponse.addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        FindCurrentPlaceResponse response = task.getResult();
//                        for (PlaceLikelihood place : response.getPlaceLikelihoods()) {
//                            mResults.setText(place.getPlace().getName());
//                        }
//                    }
//                    else {
//                        mResults.setText("Search Unsucessful");
//                    }
//                }).addOnFailureListener(e -> {
//                    mResults.setText("Search Failure");
//                });
                Toast.makeText(HomeActivity.this, mSearch.getText().toString(), Toast.LENGTH_SHORT).show();
                AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
                RectangularBounds bounds = RectangularBounds.newInstance(
                        new LatLng(-33.880490, 151.184363), //dummy lat/lng
                        new LatLng(-33.858754, 151.229596));
                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        // Call either setLocationBias() OR setLocationRestriction().
                        .setLocationBias(bounds)
                        //.setLocationRestriction(bounds)
                        .setCountry("us")//Nigeria
                        .setTypeFilter(TypeFilter.ESTABLISHMENT)
                        .setSessionToken(token)
                        .setQuery(mSearch.getText().toString())
                        .build();

                placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
                    mResult = new StringBuilder();
                    for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                        mResult.append(" ").append(prediction.getFullText(null) + "\n");
                        Log.i(TAG, prediction.getPlaceId());
                        Log.i(TAG, prediction.getPrimaryText(null).toString());
                        Toast.makeText(HomeActivity.this, prediction.getPrimaryText(null) + "-" + prediction.getSecondaryText(null), Toast.LENGTH_SHORT).show();
                    }
                    mResults.setText(String.valueOf(mResult));
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                    }
                });
            }


        });

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



            }
        });
    }

}