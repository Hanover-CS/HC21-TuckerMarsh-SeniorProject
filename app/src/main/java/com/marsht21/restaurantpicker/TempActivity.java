package com.marsht21.restaurantpicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

public class TempActivity extends AppCompatActivity {

    private static final String TAG = TempActivity.class.getSimpleName();
    private Toolbar toolbar;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private TextView restaurantName;
    private RatingBar ratingBar;
    private RatingBar priceBar;
    private Button launchDirections;
    private String placeIdTemp;
    private String nameTemp;
    private StringBuilder url;
    private Button launchPhone;
    private String phoneTemp;
    private String websiteTemp;
    private Button launchWebsite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        toolbar = findViewById(R.id.toolbar_temp);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        restaurantName = findViewById(R.id.restaurantName);
        ratingBar = findViewById(R.id.ratingbar);
        priceBar = findViewById(R.id.pricebar);
        launchDirections = findViewById(R.id.button_directions);
        launchPhone = findViewById(R.id.button_phone);
        launchWebsite = findViewById(R.id.button_website);
        setToolbar();

        db.collection("restaurants")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Float r = Float.parseFloat(document.get("rating").toString());
                                Float p = Float.parseFloat(document.get("price level").toString());
                                restaurantName.setText(document.get("name").toString());
                                ratingBar.setRating(r);
                                priceBar.setRating(p);
                                placeIdTemp = document.get("place id").toString();
                                nameTemp = document.get("name").toString();
                                phoneTemp = document.get("phone number").toString();
                                websiteTemp = document.get("website").toString();

                                StringBuilder url = buildDirectionsUrl();  // Builds url that opens directions to restaurant in Google maps

                                launchDirections.setOnClickListener(v -> {  // Open directions in maps when button is pressed
                                    Uri uri = Uri.parse(String.valueOf(url));
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent);
                                });

                                launchPhone.setOnClickListener(v -> {  // Open phone number when button is pressed
                                    Uri uri = Uri.parse("tel:" + phoneTemp);
                                    Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                                    startActivity(intent);
                                });
                                
                                launchWebsite.setOnClickListener(v -> {  // Open phone number when button is pressed
                                    Uri uri = Uri.parse(websiteTemp);
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent);
                                });

                                break;
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }

    @NotNull
    private StringBuilder buildDirectionsUrl() {
        StringBuilder url = new StringBuilder();
        url.append("https://www.google.com/maps/dir/?api=1")
                .append("&destination=")
                .append(nameTemp)
                .append("&destination_place_id=")
                .append(placeIdTemp);
        return url;
    }

    @Override
    public void onBackPressed() {
        deleteRestaurantDocuments();

        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                deleteRestaurantDocuments();

                Intent intent = new Intent(TempActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }

    private void deleteRestaurantDocuments() {
        db.collection("restaurants")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                db.collection("restaurants").document(document.getId()).delete();
                            }
                        } else {
                            Log.d(TAG, "Error deleting documents: ", task.getException());
                        }
                    }
                });
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

}