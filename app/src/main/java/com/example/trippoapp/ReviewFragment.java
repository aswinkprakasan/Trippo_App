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
import android.widget.ListView;

import com.example.trippoapp.adapter.RecycleAdminAdapter;
import com.example.trippoapp.adapter.RecycleReviewAdapter;
import com.example.trippoapp.model.AdminRecycleClass;

import java.util.ArrayList;
import java.util.List;


public class ReviewFragment extends Fragment {

    DBHelper db;
    ListView listView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review, container, false);

        db = new DBHelper(getContext());

        SharedPreferences sp = getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String val = sp.getString("id","");

        List<AdminRecycleClass> dataList = new ArrayList<>();
        Cursor cursor = db.readReview(val);
        if (cursor.moveToFirst()) {
            do {
                // Extract data from the cursor
                String name = cursor.getString(3);
                String review = cursor.getString(5);
                String plId = cursor.getString(2);

                if (!review.isEmpty()){
                    dataList.add(new AdminRecycleClass(name, review, plId));
                }
                // Create a data model and add to the list


            } while (cursor.moveToNext());
        }
        RecyclerView recyclerView = view.findViewById(R.id.recycle_view);
        recyclerView.setEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(new RecycleReviewAdapter(getActivity().getApplicationContext(),dataList));
        return view;
    }
}