package com.example.trippoapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.trippoapp.model.MyDataModel;
import com.example.trippoapp.model.ReviewModel;

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

        List<ReviewModel> dataList = new ArrayList<>();
        Cursor cursor = db.readReview(val);
        if (cursor.moveToFirst()) {
            do {
                // Extract data from the cursor
                String name = cursor.getString(3);
                String review = cursor.getString(5);

                if (!review.isEmpty()){
                    dataList.add(new ReviewModel(name, review));
                }
                // Create a data model and add to the list


            } while (cursor.moveToNext());
        }
        ArrayAdapter<ReviewModel> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, dataList);

// Set the adapter to your ListView
        listView = view.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        return view;
    }
}