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

import com.example.trippoapp.model.MyDataModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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




                            String serializedData = sp.getString("searched_place", "");

                            JSONArray jsonArray;
                            try {
                                if (!serializedData.isEmpty()) {
                                    jsonArray = new JSONArray(serializedData);
                                } else {
                                    jsonArray = new JSONArray();
                                }

                                // Create a new JSON object for the new data item
                                JSONObject newDataItem = new JSONObject();
                                newDataItem.put("name", placeName1);
                                newDataItem.put("id", id);

                                // Add the new data item to the existing JSON array
                                jsonArray.put(newDataItem);

                                // Convert the updated JSON array to a string
                                String updatedSerializedData = jsonArray.toString();

                                // Save the updated serialized string back to SharedPreferences
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("searched_place", updatedSerializedData);
                                editor.apply();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }



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




        String serializedData = sp.getString("searched_place", "");
        List<MyDataModel> dataList = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(serializedData);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                String id = jsonObject.getString("id");

                MyDataModel dataItem = new MyDataModel(name, id); // Create a data model
                dataList.add(dataItem);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

// Create an ArrayAdapter or custom adapter for your ListView
        ArrayAdapter<MyDataModel> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, dataList);

// Set the adapter to your ListView
        listView = view.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyDataModel selectedItem = (MyDataModel) parent.getItemAtPosition(position);

                // Get the values from the selected item
                String name1 = selectedItem.getName();
                String id1 = selectedItem.getId();

                Intent intent2 = new Intent(getActivity(), PlaceDetailsActivity.class);
                intent2.putExtra("Id", id1);
                intent2.putExtra("Name", name1);
                startActivity(intent2);

                // Display a toast with the values
                String toastMessage = "Value1: " + name1 + ", Value2: " + id1;
                Toast.makeText(getActivity().getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();

            }
        });
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