package com.example.trippoapp;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AdminSeasonFragment extends Fragment {

    ImageView close;
    FirebaseFirestore fStore;
    EditText placeName, districtName, stateName;
    TextView search;
    Button submit;
    String placeID;
    ActivityResultLauncher<Intent> startAutocomplete;
    AutoCompleteTextView autoCompleteTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startAutocomplete = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        if (intent != null) {
                            Place place = Autocomplete.getPlaceFromIntent(intent);
                            String placeName1 = place.getName();

                            String placeID1 = place.getId();
                            placeID = placeID1;

                            search.setText(placeName1);
                            placeName.setText(placeName1);

                            LatLng latLng = getLatLngFromAddress(placeName1);

                            if (latLng != null){
                                Address address = getAddressFromLatLng(latLng);
                                if (address != null){
                                    Log.d("Address : ", "" + address.toString());
                                    Log.d("Address Line : ",""+address.getAddressLine(0));
                                    Log.d("Phone : ",""+address.getPhone());
                                    Log.d("Pin Code : ",""+address.getPostalCode());
                                    Log.d("Feature : ",""+address.getFeatureName());
                                    Log.d("More : ",""+address.getLocality());

                                    districtName.setText(address.getLocality());
                                    stateName.setText(address.getAdminArea());
                                }
                                else {
                                    Log.d("Adddress","Address Not Found");
                                }
                            }
                            else {
                                Log.d("Latlng","latlng Not Found");
                            }

                            Toast.makeText(requireContext(), "Selected " + placeName, Toast.LENGTH_SHORT).show();
                        }
                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        // The user canceled the operation.
                        Log.d(TAG, "User canceled autocomplete");
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_season, container, false);

        close = view.findViewById(R.id.close);
        placeName = view.findViewById(R.id.place_name);
        districtName = view.findViewById(R.id.district_name);
        stateName = view.findViewById(R.id.state_name);
        search = view.findViewById(R.id.search1);
        submit = view.findViewById(R.id.submit);
        autoCompleteTextView = view.findViewById(R.id.season_name);

        fStore = FirebaseFirestore.getInstance();

        Bundle metaData;
        try {
            metaData = getActivity().getPackageManager().getApplicationInfo(getActivity().getPackageName(), PackageManager.GET_META_DATA).metaData;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        String apiKey = metaData.getString("com.google.android.geo.API_KEY");

        if (!Places.isInitialized()){
            Places.initialize(getActivity().getApplicationContext(),apiKey);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.season_array));
        autoCompleteTextView.setAdapter(adapter);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(requireActivity() );
                startAutocomplete.launch(intent);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(AdminSeasonFragment.this);
                fragmentTransaction.commit();

                Intent intent = new Intent(getActivity(), AdminActivity.class);
                startActivity(intent);


            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String place, district, state, season, id;
                place = placeName.getText().toString();
                district = districtName.getText().toString();
                state = stateName.getText().toString();
                season = autoCompleteTextView.getText().toString();
                id = placeID;

                DocumentReference documentReference = fStore.collection("season").document();
                Map<String, Object> data = new HashMap<>();
                data.put("placeId",id);
                data.put("place", place);
                data.put("district", district);
                data.put("state", state);
                data.put("season", season);

                documentReference.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getActivity(), "Added successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return view;
    }

    private LatLng getLatLngFromAddress(String address){

        Geocoder geocoder=new Geocoder(getContext());
        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocationName(address, 1);
            if(addressList!=null){
                Address singleaddress=addressList.get(0);
                LatLng latLng=new LatLng(singleaddress.getLatitude(),singleaddress.getLongitude());
                return latLng;
            }
            else{
                return null;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    private Address getAddressFromLatLng(LatLng latLng){
        Geocoder geocoder=new Geocoder(getContext());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5);
            if(addresses!=null){
                Address address=addresses.get(0);
                return address;
            }
            else{
                return null;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
}