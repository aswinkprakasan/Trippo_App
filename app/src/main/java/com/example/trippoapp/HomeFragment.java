package com.example.trippoapp;

import static android.content.ContentValues.TAG;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.trippoapp.adapter.HigherRatingAdapter;
import com.example.trippoapp.adapter.RecycleSeasonAdapter;
import com.example.trippoapp.model.HigherRatingClass;
import com.example.trippoapp.model.RecycleSeasonClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {

    FirebaseFirestore fStore;
    ImageView imageView;
    PlacesClient placesClient;
    TextView seasonName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        imageView = view.findViewById(R.id.imageView);
        fStore = FirebaseFirestore.getInstance();
        seasonName = view.findViewById(R.id.season);

        Bundle metaData;
        try {
            metaData = getActivity().getPackageManager().getApplicationInfo(getActivity().getPackageName(), PackageManager.GET_META_DATA).metaData;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        String apiKey = metaData.getString("com.google.android.geo.API_KEY");

        if (!Places.isInitialized()) {
            Places.initialize(getActivity().getApplicationContext(), apiKey);
        }
        placesClient = Places.createClient(getActivity());


        int month = getMonth();

        String fName = "season";

        String fValue = "";
        if (month == 11 || month == 12 || month == 1 || month == 2){
            fValue = "winter";
        } else if (month >= 3 && month <= 5) {
            fValue = "summer";
        } else if (month >= 6 && month <= 10) {
            fValue = "monsoon";
        }

        String season1 = "Places to visit this "+fValue+" !!";
        seasonName.setText(season1);
        Query query = fStore.collection("season").whereEqualTo(fName, fValue);

        List<RecycleSeasonClass> seasonClass = new ArrayList<RecycleSeasonClass>();
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();

                    for (DocumentSnapshot document : documents) {

                        String name = document.getString("place");
                        String district = document.getString("district");
                        String state = document.getString("state");
                        String id = document.getString("placeId");

                        int pic = R.drawable.winter;

                        Log.d(TAG, "Name: " + name);
                        Log.d(TAG, "District: " + district);
                        Log.d(TAG, "State: " + state);

                        String location = district +", "+state;
                        seasonClass.add(new RecycleSeasonClass(name,location,id,pic));

                    }
                    RecyclerView recyclerView = view.findViewById(R.id.recycle_view);

                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
                    recyclerView.setAdapter(new RecycleSeasonAdapter(getActivity().getApplicationContext(),seasonClass));
                } else {

                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });


        Query query1 = fStore.collection("ratings").whereGreaterThan("rating", 4.0);
        Query query2 = fStore.collection("ratings").whereGreaterThan("count", 2.0);

        Task<QuerySnapshot> ratingTask = query1.get();
        Task<QuerySnapshot> countTask = query2.get();

        List<HigherRatingClass> ratingClass = new ArrayList<HigherRatingClass>();

        Tasks.whenAllSuccess(ratingTask, countTask).addOnCompleteListener(new OnCompleteListener<List<Object>>() {
            @Override
            public void onComplete(@NonNull Task<List<Object>> task) {

                if (task.isSuccessful()){
                    List<DocumentSnapshot> ratingDocuments = ratingTask.getResult().getDocuments();
                    List<DocumentSnapshot> countDocuments = countTask.getResult().getDocuments();

                    for (DocumentSnapshot ratingDoc : ratingDocuments) {
                        for (DocumentSnapshot countDoc : countDocuments) {
                            if (ratingDoc.getString("placeID").equals(countDoc.getString("placeID"))) {
                                // You have a match for both rating and count conditions
                                String name = ratingDoc.getString("placeName");
                                String id = ratingDoc.getString("placeID");
                                Double rating = ratingDoc.getDouble("rating");
                                String rat = String.format("%.1f", rating);
                                int pic = R.drawable.summer;

                                ratingClass.add(new HigherRatingClass(name, id, rat, pic));
                            }
                        }
                    }
                    RecyclerView recyclerView = view.findViewById(R.id.recycle_view1);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
                    recyclerView.setAdapter(new HigherRatingAdapter(getActivity().getApplicationContext(),ratingClass));
                }
                else {
                    Log.d(TAG, "Error getting documents in rating: ", task.getException());
                }
            }
        });



        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ManageAccountFragment()).commit();
            }
        });

        return view;
    }

    private int getMonth() {
        Calendar calendar = Calendar.getInstance();
        int currentMonth1 = calendar.get(Calendar.MONTH);
        int currentMonth = currentMonth1+1;

        return currentMonth;
    }


}