package com.example.trippoapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaceDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    TextView name;
    RatingBar ratingBar;
    ImageView imageView;
    PlacesClient placesClient;
    GoogleMap mMap;
    Button getLoc, reviewBtn;
    EditText review;
    String plceId,nme;
    LatLng latLng;
    DBHelper db;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        name = findViewById(R.id.name1);
        imageView = findViewById(R.id.image);
        getLoc = findViewById(R.id.get_location);
        ratingBar = findViewById(R.id.ratingBar);
        reviewBtn = findViewById(R.id.submit);
        review = findViewById(R.id.upload_review);

        db = new DBHelper(this);

        fStore = FirebaseFirestore.getInstance();

        SharedPreferences sp = getSharedPreferences("MyPref", MODE_PRIVATE);
        String val = sp.getString("id","");

        Bundle metaData;
        try {
            metaData = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        String apiKey = metaData.getString("com.google.android.geo.API_KEY");

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        placesClient = Places.createClient(this);

        Bundle bundle = getIntent().getExtras();
        String placeId = null, name1 = null;
        if (bundle != null) {
            placeId = bundle.getString("Id");
            name1 = bundle.getString("Name");
            name.setText(bundle.getString("Name"));
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getPhoto(placeId);
        plceId = placeId;
        nme = name1;

        latLng = getfromAddress(name1);

        getLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlaceDetailsActivity.this, MapsActivity.class);
                intent.putExtra("id", plceId);
                intent.putExtra("name", nme);
                startActivity(intent);
            }
        });

        Cursor cursor = db.readRating(plceId, val);

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "rating is empty", Toast.LENGTH_SHORT).show();

        } else {
            while (cursor.moveToNext()) {

                ratingBar.setRating(cursor.getFloat(4));
                review.setText(cursor.getString(5));
                cursor.close();
            }
        }
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                // Do something with the new rating value
                Boolean resultCheck = db.checkRating(plceId, val);
                if (resultCheck){

                    Boolean resultUpdate = db.updateRating(rating, plceId, val);
                    if (resultUpdate){
                        Toast.makeText(PlaceDetailsActivity.this, "rating updated", Toast.LENGTH_SHORT).show();

                    }
                    else {
                        Toast.makeText(PlaceDetailsActivity.this, "rating failed", Toast.LENGTH_SHORT).show();
                    }

                }
                else {

                    checkFStore(plceId, nme, rating);

                    Boolean result = db.insertRating(val, plceId, nme, rating);
                    if (result){
                        Toast.makeText(PlaceDetailsActivity.this, "rating added", Toast.LENGTH_SHORT).show();

                    }
                    else {
                        Toast.makeText(PlaceDetailsActivity.this, "rating failed", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });


        reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reviewValue = review.getText().toString();
                Toast.makeText(PlaceDetailsActivity.this, reviewValue, Toast.LENGTH_SHORT).show();
                if (!reviewValue.isEmpty()){
                    Boolean result = db.updateReview(reviewValue, plceId, val);
                    if (result){
                        Toast.makeText(PlaceDetailsActivity.this, "review updated", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(PlaceDetailsActivity.this, "review failed", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(PlaceDetailsActivity.this, "empty review", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ToggleButton favoriteButton = findViewById(R.id.favoriteButton);
//        favoriteButton.setChecked(true);

        Cursor cursor1 = db.readItinerary(plceId, val);
        if (cursor1.getCount() == 0) {
            Toast.makeText(this, "not checked", Toast.LENGTH_SHORT).show();
            favoriteButton.setChecked(false);

        } else {
            while (cursor1.moveToNext()) {
                int heart = cursor1.getInt(4);
                if (heart == 1){
                    favoriteButton.setChecked(true);
                }else {
                    favoriteButton.setChecked(false);
                }

                cursor1.close();
            }
        }

        favoriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int heart = 0;
                if (b){
//                    Toast.makeText(PlaceDetailsActivity.this, "checked", Toast.LENGTH_SHORT).show();
                    heart = 1;
                }
                else {
//                    Toast.makeText(PlaceDetailsActivity.this, "not checked", Toast.LENGTH_SHORT).show();
                    heart = 0;
                }
                Cursor cursor2 = db.readItinerary(plceId, val);
                if (cursor2.getCount() == 0){
                    Boolean result1 = db.insertItinerary(val,plceId,nme,heart);
                    if (result1){
                        Toast.makeText(PlaceDetailsActivity.this, "added to bucket list", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(PlaceDetailsActivity.this, "falied", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Boolean result2 = db.updateItinerary(val,plceId,heart);
                    if (result2){
                        Toast.makeText(PlaceDetailsActivity.this, "updated to bucketlist", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(PlaceDetailsActivity.this, "failed updation", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void checkFStore(String id, String name, float rating) {

        DocumentReference documentReference = fStore.collection("ratings").document(id);
        documentReference.get().addOnCompleteListener(task -> {
           if (task.isSuccessful()){
               DocumentSnapshot documentSnapshot = task.getResult();
               if (documentSnapshot.exists()){
                   Double sum = documentSnapshot.getDouble("sum");
                   Double count = documentSnapshot.getDouble("count");
                   updateFstore(id, rating, sum, count);
               }
               else {
                   addRatingToFStore(id, name, rating);
               }
           }
           else {
               Log.d(TAG, "checkFStore: error");
           }
        });
    }

    private void updateFstore(String id, float rating, Double sum, Double count) {

        sum += rating;
        count++;

        Double avg = sum/count;

        DocumentReference documentReference = fStore.collection("ratings").document(id);
        Map<String, Object> updates = new HashMap<>();
        updates.put("rating", avg);
        updates.put("sum", sum);
        updates.put("count", count);

        documentReference.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(PlaceDetailsActivity.this, "successfully updated firestore", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PlaceDetailsActivity.this, "updation firestore failed", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void addRatingToFStore(String placeId, String name, float rating) {
        DocumentReference documentReference = fStore.collection("ratings").document(placeId);
        Map<String, Object> data = new HashMap<>();
        data.put("placeID", placeId);
        data.put("placeName", name);
        data.put("rating", rating);
        data.put("count", 1);
        data.put("sum", rating);

        documentReference.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(PlaceDetailsActivity.this, "Added to firestore successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PlaceDetailsActivity.this, "firestore addition failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private LatLng getfromAddress(String placeName) {
        Geocoder geocoder = new Geocoder(PlaceDetailsActivity.this);
        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocationName(placeName, 1);
            if (addressList != null) {
                Address singleaddress = addressList.get(0);
                LatLng latLng = new LatLng(singleaddress.getLatitude(), singleaddress.getLongitude());
                return latLng;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void getPhoto(String placeId) {
        final List<Place.Field> fields = Collections.singletonList(Place.Field.PHOTO_METADATAS);

// Get a Place object (this example uses fetchPlace(), but you can also use findCurrentPlace())
        final FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(placeId, fields);

        placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {
            final Place place = response.getPlace();

            // Get the photo metadata.
            final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
            if (metadata == null || metadata.isEmpty()) {
                Log.w(TAG, "No photo metadata.");
                return;
            }
            final PhotoMetadata photoMetadata = metadata.get(0);

            // Get the attribution text.
            final String attributions = photoMetadata.getAttributions();

            // Create a FetchPhotoRequest.
            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxWidth(500) // Optional.
                    .setMaxHeight(300) // Optional.
                    .build();
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                imageView.setImageBitmap(bitmap);
                Log.d(TAG, "getPhoto: "+bitmap);
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    final ApiException apiException = (ApiException) exception;
                    Log.e(TAG, "Place not found: " + exception.getMessage());
                    final int statusCode = apiException.getStatusCode();
                    // TODO: Handle error with given status code.
                }
            });
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(latLng).title(nme));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

    }
}