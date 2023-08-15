package com.example.trippoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.trippoapp.model.PlacesApiService;
import com.example.trippoapp.model.PlacesResponse;
import com.example.trippoapp.model.Plase;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.trippoapp.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, ActivityCompat.OnRequestPermissionsResultCallback, RoutingListener {

    private GoogleMap mMap;
    Button restaurant, route, parking, nearby, clear, pump;
    LatLng latLng1;
    TextView service;
    String name1, apiK;
    private boolean permissionDenied = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    LatLng currentLoc, start, end;
    private FusedLocationProviderClient fusedLocationProviderClient;


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
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        Bundle bundle = getIntent().getExtras();
        String placeId = null, name = null;
        if (bundle != null) {
            placeId = bundle.getString("id");
            name = bundle.getString("name");
        }

        Bundle metaData;
        try {
            metaData = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        String apiKey = metaData.getString("com.google.android.geo.API_KEY");
        apiK = apiKey;

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

                Findroutes(currentLoc,latLng1);
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
                float zoomLevel = 10.0f;
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng1, zoomLevel);
                mMap.animateCamera(cameraUpdate);
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
        enableMyLocation();
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


    public void Findroutes(LatLng Start, LatLng End) {
        if(Start==null || End==null) {
            start = Start;
            end = End;
            Toast.makeText(MapsActivity.this,"Unable to get location", Toast.LENGTH_LONG).show();
        }
        else
        {
            start = Start;
            end = End;
            // Add Marker on start location
            MarkerOptions startMarker = new MarkerOptions();
            startMarker.position(Start);
            startMarker.title("Start Location");
            mMap.addMarker(startMarker);

            // Add Marker on end location
            MarkerOptions endMarker = new MarkerOptions();
            endMarker.position(End);
            endMarker.title("End Location");
            mMap.addMarker(endMarker);

            // Add Marker for waypoints

            Routing.Builder routingBuilder = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(false)
                    .waypoints(Start, End)
                    .key(apiK);  //also define your api key here.

            Routing routing =routingBuilder.build();
            routing.execute();
        }
    }
    @Override
    public void onRoutingFailure(RouteException e) {

        Findroutes(start,end);
    }

    @Override
    public void onRoutingStart() {

        Toast.makeText(MapsActivity.this,"Finding Route...",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        List<Polyline> polylines = new ArrayList<>();



        // Add route(s) to the map using polyline
        for (int i = 0; i < route.size(); i++) {
            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(ContextCompat.getColor(this, R.color.teal_700));
            polyOptions.width(15);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);
        }

        // Move camera to show the entire route and markers
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(start); // Include start marker
        builder.include(end); // Include end marker

        // Include waypoints if available

        LatLngBounds bounds = builder.build();
        int padding = 100; // Padding around the route (in pixels)
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cameraUpdate);

    }

    @Override
    public void onRoutingCancelled() {

    }

    private void enableMyLocation() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            getCurrentLocationAndShowOnMap();
            return;
        }
        requestLocationPermissions();
    }

    private void getCurrentLocationAndShowOnMap() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000); // Update location every 5 seconds

        // Check location settings and request location updates
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }
    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) return;

            // Get the most recent location from the location result
            android.location.Location location = locationResult.getLastLocation();
            if (location != null) {
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                currentLoc = currentLatLng;
//                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                Toast.makeText(MapsActivity.this, "current location using fused", Toast.LENGTH_SHORT).show();
                fusedLocationProviderClient.removeLocationUpdates(locationCallback);

                LatLng End = new LatLng(10.530345, 76.214729);
//                Findroutes(currentLatLng, End);

            }
        }
    };

    private void requestLocationPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);

    }
    @Override
    public boolean onMyLocationButtonClick() {
        if (currentLoc != null) {
            double lat = currentLoc.latitude;
            double lng = currentLoc.longitude;

            String latLngString = String.format("%.6f, %.6f", lat, lng);
            Toast.makeText(this, "Current location: " + latLngString, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Current location is not available", Toast.LENGTH_SHORT).show();
        }

        // Return false so that the default behavior still occurs (camera animates to user's location).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

        double lat = location.getLatitude();
        double lng = location.getLongitude();

//        currentLoc = new LatLng(lat, lng);
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length > 0 &&
                (grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                        grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            // Permission granted, enable location
            enableMyLocation();
        }else {
            // Permission was denied. Display an error message
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            permissionDenied = false;
        }
    }

    private void showMissingPermissionError() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Denied")
                .setMessage("Location permission is required to show your current location on the map.")
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}