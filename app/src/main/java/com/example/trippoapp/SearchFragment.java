package com.example.trippoapp;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SearchFragment extends Fragment {

    TextView search;
    ActivityResultLauncher<Intent> startAutocomplete;
    SharedPreferences sp;
    ListView listView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);

        startAutocomplete = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        if (intent != null) {
                            Place place = Autocomplete.getPlaceFromIntent(intent);
                            String placeName1 = place.getName();
                            String id = place.getId();
                            search.setText(placeName1);



                            Set<String> valueSet = sp.getStringSet("places", new HashSet<>());
                            List<String> valuesList = new ArrayList<>(valueSet);

                            if (!placeName1.isEmpty()) {
                                valuesList.add(placeName1);
                            }

                            SharedPreferences.Editor editor = sp.edit();
                            editor.putStringSet("places", new HashSet<>(valuesList));
                            editor.apply();

                            Toast.makeText(requireContext(), "Selected " + placeName1, Toast.LENGTH_SHORT).show();

                            Intent intent1 = new Intent(getActivity(), PlaceDetailsActivity.class);
                            intent1.putExtra("Id", id);
                            intent1.putExtra("Name", placeName1);
                            startActivity(intent1);



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
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        search = view.findViewById(R.id.search);
        listView = view.findViewById(R.id.listView);

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

        Set<String> valueSet = sp.getStringSet("places", new HashSet<>());
        List<String> valuesList = new ArrayList<>(valueSet);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, valuesList);
        listView.setAdapter(adapter);

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {
//                String selectedItem = valuesList.get(position);
//                // Do something with the selected item, e.g., show a toast
//                Toast.makeText(getActivity(), "Selected Item: " + selectedItem, Toast.LENGTH_SHORT).show();
//            }
//        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(requireActivity() );
                startAutocomplete.launch(intent);
            }
        });

        return view;
    }
}