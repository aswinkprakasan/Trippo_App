package com.example.trippoapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class AdminDetailedActivity extends AppCompatActivity {

    TextView placeName, seasonName;
    Button delete;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_detailed);

        placeName = findViewById(R.id.place_name);
        seasonName = findViewById(R.id.season_name);
        delete = findViewById(R.id.delete);

        db = FirebaseFirestore.getInstance();
        Bundle bundle = getIntent().getExtras();
        String placeId = null, name = null, season = null;
        if (bundle != null) {
            placeId = bundle.getString("id");
            name = bundle.getString("name");
            season = bundle.getString("season");
        }
        placeName.setText(name);
        seasonName.setText(season);

        String finalPlaceId = placeId;
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminDetailedActivity.this);
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure you want to proceed?");

// Add positive button
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        DocumentReference documentReference = db.collection("season").document(finalPlaceId);
                        documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Intent intent = new Intent(AdminDetailedActivity.this, AdminActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(AdminDetailedActivity.this, "deleted fstore", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AdminDetailedActivity.this, "could not deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Toast.makeText(AdminDetailedActivity.this, "deleted", Toast.LENGTH_SHORT).show();
                    }
                });

// Add negative button
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle the case when the user cancels the action
                        Toast.makeText(AdminDetailedActivity.this, "cancelled", Toast.LENGTH_SHORT).show();
                    }
                });

// Create and show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}