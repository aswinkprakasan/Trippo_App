package com.example.trippoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.trippoapp.adapter.RecycleAdminAdapter;
import com.example.trippoapp.adapter.RecycleSeasonAdapter;
import com.example.trippoapp.model.AdminRecycleClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    Button season, logout;
    FrameLayout fragmnetContainer;
    FirebaseFirestore fStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        season = findViewById(R.id.button1);
        fragmnetContainer = findViewById(R.id.fragment_container1);
        logout = findViewById(R.id.logout);
        fStore = FirebaseFirestore.getInstance();

        SharedPreferences sp = getSharedPreferences("MyPref", MODE_PRIVATE);
        String val = sp.getString("id1","");

        if (val.isEmpty()){
            Intent intent = new Intent(this, SigninActivity.class);
            startActivity(intent);
        }

        List<AdminRecycleClass> adminRecycle = new ArrayList<AdminRecycleClass>();
        fStore.collection("season").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    for (DocumentSnapshot document : documents){
                        String name = document.getString("place");
                        String season = document.getString("season");
//                        String id = document.getString("placeID");

                        String docId = document.getId();
                        adminRecycle.add(new AdminRecycleClass(name, season, docId));

                    }

                    RecyclerView recyclerView = findViewById(R.id.recycle_view);

                    recyclerView.setLayoutManager(new LinearLayoutManager(AdminActivity.this, RecyclerView.VERTICAL, false));
                    recyclerView.setAdapter(new RecycleAdminAdapter(getApplicationContext(),adminRecycle));
                }else {

                }
            }
        });


        season.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container1, new AdminSeasonFragment());
                fragmentTransaction.commit();

                season.setVisibility(View.GONE);

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sp.edit();
                editor.remove("id1");
                editor.apply();

                Intent intent = new Intent(AdminActivity.this, SigninActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}