package com.marsht21.restaurantpicker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private Button mLogout;
    private Button mSwipe;
    private Button mSearchButton;
    private FirebaseAuth mAuth;
    private PlacesClient placesClient;
    private double lat;
    private double lon;
    private double lat1;
    private double lat2;
    private double lon1;
    private double lon2;
    private RectangularBounds bounds;
    private FusedLocationProviderClient mFusedLocationClient;
    private Toolbar toolbar;
    private FirebaseFirestore mFirestore;
    private EditText mSearch;
    private FindAutocompletePredictionsRequest request;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        mLogout = findViewById(R.id.hlogout);
        mSwipe = findViewById(R.id.swipe);
        mSearchButton = findViewById(R.id.searchbutton);
        toolbar = findViewById(R.id.toolbar_home);
        mSearch = findViewById(R.id.search);


        initializePlaces();
        initializeFirestore();
        setToolbar();
        getLocation();

        mSearchButton.setOnClickListener(v -> {  //Searches google maps for restaurants near users location
            if (ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{ACCESS_FINE_LOCATION}, 0);
            }

            setBounds();
            buildPlacesAutocompleteRequest();

            placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {  //Gets place predictions
                for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                    for (Place.Type type : prediction.getPlaceTypes()) {
                        if (type == Place.Type.RESTAURANT) {  //If the place is a restaurant, the selected fields are sent to Firestore

                            fetchPlaceSwipeFields(prediction);
                        }
                    }
                }
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                }
            });
            // Opens TempActivity to display restaurant information
            Intent intent = new Intent(HomeActivity.this, TempActivity.class);
            startActivity(intent);
            finish();
        });


        mLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        mSwipe.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SwipeActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void buildPlacesAutocompleteRequest() { // Builds search request for google places autocomplete
        request = FindAutocompletePredictionsRequest.builder()
                .setLocationRestriction(bounds)
                .setOrigin(new LatLng(lat, lon))
                .setCountry("us")
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setSessionToken(AutocompleteSessionToken.newInstance())
                .setQuery(mSearch.getText().toString())
                .build();
    }

    private void fetchPlaceSwipeFields(AutocompletePrediction prediction) {  // Retrieves selected fields from places and adds them to firebase
        final String placeId = prediction.getPlaceId();
        final List<Place.Field> swipeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.PRICE_LEVEL, Place.Field.RATING, Place.Field.PHOTO_METADATAS, Place.Field.ADDRESS, Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI, Place.Field.USER_RATINGS_TOTAL);
        final FetchPlaceRequest swipeFieldsRequest = FetchPlaceRequest.newInstance(placeId, swipeFields);
        placesClient.fetchPlace(swipeFieldsRequest).addOnSuccessListener((swipeFieldsResponse) -> {
            Place place = swipeFieldsResponse.getPlace();

            // creates new document of the restaurant id and adds swipe data
            Map<String, Object> id = new HashMap<>();
            id.put("name", place.getName());
            id.put("place id", place.getId());
            id.put("price level", place.getPriceLevel());
            id.put("rating", place.getRating());
            id.put("photo", place.getPhotoMetadatas().get(0));
            id.put("distance", prediction.getDistanceMeters());
            id.put("address", place.getAddress());
            id.put("phone number", place.getPhoneNumber());
            id.put("website", place.getWebsiteUri().toString());
            id.put("total ratings", place.getUserRatingsTotal().toString());

            mFirestore.collection("restaurants").document(place.getName()).set(id);

            Log.i(TAG, "Place found: " + place.getName());
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                Log.e(TAG, "Place not found: " + exception.getMessage());
            }
        });
    }

    private void getLocation() {  //Get users latitude and longitude
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{ACCESS_FINE_LOCATION}, 0);
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            lat = location.getLatitude();
            lon = location.getLongitude();
        });
    }

    private void setBounds(){  //Set search bounds to mile radius around user
        double var = 10.0/69.0;
        lat1 = round(lat - var);
        lon1 = round(lon - var);
        lat2 = round(lat + var);
        lon2 = round(lon + var);
        bounds = RectangularBounds.newInstance(new LatLng(lat1, lon1), new LatLng(lat2, lon2));
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        } else {
            throw new NullPointerException("Something went wrong");
        }
    }

    private void initializePlaces() {
        Places.initialize(getApplicationContext(), getString(R.string.places_api_key));
        placesClient = Places.createClient(this);
    }

    private static double round(double value) {
        if (7 < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(7, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private void initializeFirestore(){
        mFirestore = FirebaseFirestore.getInstance();
    }
}