package com.marsht21.restaurantpicker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.jetbrains.annotations.NotNull;
/*
 * Screen of the app that displays information for selected restaurant
 */
public class TempActivity extends AppCompatActivity {

    private static final String TAG = TempActivity.class.getSimpleName();
    private Toolbar toolbar;
    private FirebaseFirestore firestore;
    private TextView restaurantName;
    private RatingBar ratingBar;
    private ProgressBar priceBar;
    private Button launchDirections;
    private String placeIdTemp;
    private String nameTemp;
    private Button launchPhone;
    private String phoneTemp;
    private String websiteTemp;
    private Button launchWebsite;
    private TextView totalRatings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        toolbar = findViewById(R.id.toolbar_temp);
        firestore = FirebaseFirestore.getInstance();
        restaurantName = findViewById(R.id.restaurantName);
        ratingBar = findViewById(R.id.ratingbar);
        priceBar = findViewById(R.id.progressBar);
        launchDirections = findViewById(R.id.button_directions);
        launchPhone = findViewById(R.id.button_phone);
        launchWebsite = findViewById(R.id.button_website);
        totalRatings = findViewById(R.id.userRatingTotal);

        setToolbar();

        firestore.collection("restaurants")
                .get() // Gets all documents in collection Restaurants
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            getFields(document);

                            StringBuilder url = buildDirectionsUrl();

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
                });
    }

    /*
     * Get chosen fields from Firestore to be displayed
     * @param document A document holding information for the restaurant
     */
    private void getFields(QueryDocumentSnapshot document) {
        float r = Float.parseFloat(document.get("rating").toString());
        int p = Integer.parseInt(document.get("price level").toString());
        restaurantName.setText(document.get("name").toString());
        StringBuilder ratings = new StringBuilder();
        ratings.append("Reviews ").append("(").append(document.get("total ratings").toString()).append(")");
        totalRatings.setText(ratings);
        ratingBar.setRating(r);
        priceBar.setProgress(p);
        priceBar.setScaleY(7f);
        placeIdTemp = document.get("place id").toString();
        nameTemp = document.get("name").toString();
        phoneTemp = document.get("phone number").toString();
        websiteTemp = document.get("website").toString();
    }

    /*
     * Deletes all documents in Restaurant collection so only current search documents will
     * be stored in Firestore.
     */
    private void deleteRestaurantDocuments() {
        firestore.collection("restaurants")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (!document.getId().equals("restaurantID")) {
                                firestore.collection("restaurants").document(document.getId()).delete();
                            }
                        }
                    } else {
                        Log.d(TAG, "Error deleting documents: ", task.getException());
                    }
                });
    }

    /*
     *  Builds url that opens directions to restaurant in Google maps
     */
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

    /*
     * Override back button to delete documents
     */
    @Override
    public void onBackPressed() {
        deleteRestaurantDocuments();

        super.onBackPressed();
    }

    /*
     * Override back button in toolbar to delete documents
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            deleteRestaurantDocuments();

            Intent intent = new Intent(TempActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }


    /*
     * Set toolbar for back functionality
     */
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