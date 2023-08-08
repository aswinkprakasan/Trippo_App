package com.example.trippoapp;

import static android.content.ContentValues.TAG;

import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    FirebaseFirestore fStore;
    ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        imageView = view.findViewById(R.id.imageView);
        fStore = FirebaseFirestore.getInstance();

        String fName = "season";
        String fValue = "winter";

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

                        Log.d(TAG, "Name: " + name);
                        Log.d(TAG, "District: " + district);
                        Log.d(TAG, "State: " + state);

                        String location = district + state;
//                        RecycleSeasonClass data = document.toObject(RecycleSeasonClass.class);
                        seasonClass.add(new RecycleSeasonClass(name,location,id));

                    }
                    RecyclerView recyclerView = view.findViewById(R.id.recycle_view);

                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
                    recyclerView.setAdapter(new RecycleSeasonAdapter(getActivity().getApplicationContext(),seasonClass));
                } else {

                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });


//        seasonClass.add(new RecycleSeasonClass("Kunnamkulam", "kunnamkulam, Thrissur"));
//        seasonClass.add(new RecycleSeasonClass("Kolukumalai", "marathamcode, Thrissur"));
//        seasonClass.add(new RecycleSeasonClass("Kuttanad", "pulinkunnu, Thrissur"));
//        seasonClass.add(new RecycleSeasonClass("Kunnamkulam", "kunnamkulam, Thrissur"));
//        seasonClass.add(new RecycleSeasonClass("Kolukumalai", "marathamcode, Thrissur"));
//        seasonClass.add(new RecycleSeasonClass("Kuttanad", "pulinkunnu, Thrissur"));



        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ManageAccountFragment()).commit();
            }
        });

        return view;
    }
}