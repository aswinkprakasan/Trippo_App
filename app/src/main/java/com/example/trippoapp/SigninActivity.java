package com.example.trippoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SigninActivity extends AppCompatActivity {

    EditText email, password;
    Button btnlogin, btnregister;
    DBHelper myDB;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_pass);
        btnlogin = findViewById(R.id.btnLogin);
        btnregister = findViewById(R.id.btnRegister);

        myDB = new DBHelper(this);


        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = email.getText().toString();
                String pass = password.getText().toString();

                if (mail.equals("") || pass.equals("")){
                    Toast.makeText(SigninActivity.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
                }
                else {
                    ModelClass modelClass = new ModelClass("", mail, "", pass, null);
                    Boolean result = myDB.checkusernamepass(modelClass);
                    if (mail.equals("admin@gmail.com") || pass.equals("admin123")){
                        sp = getSharedPreferences("MyPref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("id1",mail);
                        editor.apply();

                        Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                        startActivity(intent);
                    }
                    else {
                        if (result){
                            sp = getSharedPreferences("MyPref", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("id",mail);
                            editor.apply();

                            Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(SigninActivity.this, "Invalid Credential", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }
        });

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}