package com.example.trippoapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;

public class ManageAccountActivity extends AppCompatActivity {

    ImageView imageView;
    FloatingActionButton fabEdit;
    EditText uploadName, uploadEmail, uploadNum;
    Button btnSave;
    private Uri imageUri;
    private Bitmap bitmapImage;
    DBHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);

        Toolbar toolbar = findViewById(R.id.toolbar); // Replace `toolbar` with the ID of your Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView = findViewById(R.id.upload_image);
        fabEdit = findViewById(R.id.edit_fab);
        uploadName = findViewById(R.id.upload_name);
        uploadEmail = findViewById(R.id.upload_email);
        uploadNum = findViewById(R.id.upload_num);
        btnSave = findViewById(R.id.btn_upload);
        myDB = new DBHelper(this);

        imageView.setEnabled(false);
        uploadName.setEnabled(false);
        uploadEmail.setEnabled(false);
        uploadNum.setEnabled(false);
        btnSave.setEnabled(false);
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setEnabled(true);
                uploadName.setEnabled(true);
                uploadNum.setEnabled(true);
                btnSave.setEnabled(true);
            }
        });

        ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            assert data != null;
                            imageUri = data.getData();
                            try {
                                bitmapImage = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            } catch (IOException e) {
                                Toast.makeText(ManageAccountActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            imageView.setImageBitmap(bitmapImage);
                        }
                        else {
                            Toast.makeText(ManageAccountActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    resultLauncher.launch(intent);
                }
                catch (Exception e){
                    Toast.makeText(ManageAccountActivity.this, "e.getMessage()", Toast.LENGTH_SHORT).show();
                }
            }
        });

        showData();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name, number, mail;
                name = uploadName.getText().toString();
                number = uploadNum.getText().toString();
                mail = uploadEmail.getText().toString();
                ModelClass modelClass = new ModelClass(name,mail,number, "",bitmapImage);
                Boolean result = myDB.updateData(modelClass);
                if (result){
                    Toast.makeText(ManageAccountActivity.this, "Saved successfully", Toast.LENGTH_SHORT).show();
                    recreate();
                }
                else {
                    Toast.makeText(ManageAccountActivity.this, "Error occured", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showData() {

        SharedPreferences sp = getSharedPreferences("MyPref", MODE_PRIVATE);
        String val = sp.getString("id","");

        DBHelper myDB = new DBHelper(this);
        Cursor cursor = myDB.readData(val);

        if (cursor.getCount() == 0){
            Toast.makeText(this, "Not logged IN", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, SigninActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            while (cursor.moveToNext()){
                uploadName.setText(cursor.getString(1));
                uploadEmail.setText(cursor.getString(2));
                uploadNum.setText(cursor.getString(4));
//                byte[] imageByte = cursor.getBlob(5);
//
//                if (imageByte != null && imageByte.length > 0) {
//                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
//                    imageView.setImageBitmap(bitmap);
//                }
                cursor.close();
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            Intent intent = new Intent(this,HomeActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}