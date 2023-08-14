package com.example.trippoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.trippoapp.model.PlacesApiService;
import com.example.trippoapp.model.PlacesResponse;
import com.example.trippoapp.model.Plase;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.trippoapp.databinding.ActivityMapsBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button restaurant, route, parking, nearby, clear, pump;
    LatLng latLng1;
    TextView service;
    String name1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        restaurant = findViewById(R.id.restaurant);
        route = findViewById(R.id.route);
        parking = findViewById(R.id.parking);
        nearby = findViewById(R.id.nearby);
        pump = findViewById(R.id.pump);
        clear = findViewById(R.id.clear);
        service = findViewById(R.id.services);

        Bundle bundle = getIntent().getExtras();
        String placeId = null, name = null;
        if (bundle != null) {
            placeId = bundle.getString("id");
            name = bundle.getString("name");
        }

        name1 = name;
        LatLng latLng = getfromName(name);
        latLng1 = latLng;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        service.setText(name1);
        restaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();

                service.setText("Restaurant");

                String type = "restaurant";
                int rad = 5000;
                assert latLng != null;
                fetchAndDisplayNearbyRestaurants(latLng, type, rad);

            }
        });

        route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();

                service.setText("Route");

                String type = "parking";
                int rad = 5000;
                assert latLng != null;
                fetchAndDisplayNearbyRestaurants(latLng, type, rad);
            }
        });

        parking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();

                service.setText("Parking");

                String type = "parking";
                int rad = 1500;
                assert latLng != null;
                fetchAndDisplayNearbyRestaurants(latLng, type, rad);
            }
        });

        pump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();

                service.setText("Gas Station");

                String type = "gas_station";
                int rad = 10000;
                assert latLng != null;
                fetchAndDisplayNearbyRestaurants(latLng, type, rad);
            }
        });

        nearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();

                service.setText("Nearby Attractions");

                String type = "tourist_attraction";
                int rad = 100000;
                assert latLng != null;
                fetchAndDisplayNearbyRestaurants(latLng, type, rad);
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();

                service.setText(name1);

                mMap.addMarker(new MarkerOptions().position(latLng).title(name1));
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(latLng1).title(name1));
        float zoomLevel = 10.0f;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng1, zoomLevel);
        mMap.animateCamera(cameraUpdate);
    }

    private LatLng getfromName(String placeName) {
        Geocoder geocoder = new Geocoder(MapsActivity.this);
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

    private void fetchAndDisplayNearbyRestaurants(LatLng location, String type, int rad) {

        String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PLACES_API_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PlacesApiService service = retrofit.create(PlacesApiService.class);
        Call<PlacesResponse> call = service.getNearbyRestaurants(
                location.latitude + "," + location.longitude,
                rad,
                type,
                BuildConfig.MAPS_API_KEY
        );

        call.enqueue(new Callback<PlacesResponse>() {

            @Override
            public void onResponse(@NonNull Call<PlacesResponse> call, @NonNull retrofit2.Response<PlacesResponse> response) {

                if (response.isSuccessful()) {
                    PlacesResponse placesResponse = response.body();
                    if (placesResponse != null) {
                        List<Plase> places = placesResponse.getResults();
                        if (places != null) {

                            List<String> restaurantNames = new ArrayList<>();
                            for (Plase place : places) {
                                String restaurant = place.getName();
                                restaurantNames.add(restaurant);

                                LatLng placeLocation = new LatLng(place.getGeometry().getLocation().getLat(), place.getGeometry().getLocation().getLng());
                                mMap.addMarker(new MarkerOptions().position(placeLocation).title(place.getName()));
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PlacesResponse> call, Throwable t) {
                // Handle failure here
            }
        });
    }

}