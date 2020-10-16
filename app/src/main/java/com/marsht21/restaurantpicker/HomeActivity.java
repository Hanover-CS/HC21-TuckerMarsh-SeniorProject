package com.marsht21.restaurantpicker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
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
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.Place.Field;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.BreakIterator;
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
    private double lat;
    private double lon;
    private TextView txtLat;
    private TextView txtLon;
    private FusedLocationProviderClient mFusedLocationClient;

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
        txtLat = findViewById(R.id.txtlat);
        txtLon = findViewById(R.id.txtlon);
        initializePlaces();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{ACCESS_FINE_LOCATION}, 0);
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        mtest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(HomeActivity.this, new String[]{ACCESS_FINE_LOCATION}, 0);
                }
                txtLon.setText(Double.toString(lon));
                txtLat.setText(Double.toString(lat));
                Toast.makeText(HomeActivity.this, mSearch.getText().toString(), Toast.LENGTH_SHORT).show();
                AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
                RectangularBounds bounds = RectangularBounds.newInstance(
                        new LatLng(lat + (10/69), lon + (10/69)), //10 mile box around device location needs fixed
                        new LatLng(lat - (10/69), lon - (10/69)));
                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        // Call either setLocationBias() OR setLocationRestriction().
                        .setLocationRestriction(bounds)
                        //.setLocationRestriction(bounds)
                        .setOrigin(new LatLng(lat,lon))
                        .setCountry("us")//Nigeria
                        .setTypeFilter(TypeFilter.ESTABLISHMENT)
                        .setSessionToken(token)
                        .setQuery(mSearch.getText().toString())
                        .build();

                placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
                    mResult = new StringBuilder();
                    for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                        for (Place.Type type : prediction.getPlaceTypes()){
                            if (type == Place.Type.RESTAURANT){
                                mResult.append(" ").append(prediction.getFullText(null) + "\n");
                                Log.i(TAG, prediction.getPlaceId());
                                Log.i(TAG, prediction.getPrimaryText(null).toString());
                                Toast.makeText(HomeActivity.this, prediction.getPrimaryText(null) + "-" + prediction.getSecondaryText(null), Toast.LENGTH_SHORT).show();
                            }
                        }
//                        mResult.append(" ").append(prediction.getFullText(null) + "\n");
//                        Log.i(TAG, prediction.getPlaceId());
//                        Log.i(TAG, prediction.getPrimaryText(null).toString());
//                        Toast.makeText(HomeActivity.this, prediction.getPrimaryText(null) + "-" + prediction.getSecondaryText(null), Toast.LENGTH_SHORT).show();
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
    }


}