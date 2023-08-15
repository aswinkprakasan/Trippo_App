package com.example.trippoapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trippoapp.adapter.ItineraryAdapter;
import com.example.trippoapp.adapter.RecycleReviewAdapter;
import com.example.trippoapp.model.AdminRecycleClass;

import java.util.ArrayList;
import java.util.List;

public class ItineraryFragment extends Fragment {

    DBHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_itinerary, container, false);

        db = new DBHelper(getContext());
        SharedPreferences sp = getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String val = sp.getString("id","");

        List<AdminRecycleClass> dataList = new ArrayList<>();
        Cursor cursor = db.readStatus(val);

        if (cursor.moveToFirst()) {
            do {
                // Extract data from the cursor
                String name = cursor.getString(3);
                String email = cursor.getString(1);
                String plId = cursor.getString(2);

                dataList.add(new AdminRecycleClass(name, email, plId));

                // Create a data model and add to the list


            } while (cursor.moveToNext());
        }

        RecyclerView recyclerView = view.findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(new ItineraryAdapter(getActivity().getApplicationContext(),dataList));

        return view;
    }
}