package com.example.trippoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar); //Ignore red line errors
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,
                R.string.close_nav);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        View header = navigationView.getHeaderView(0);
        ImageView nav_img = header.findViewById(R.id.head_img);
        TextView nav_name = header.findViewById(R.id.head_name);
        TextView nav_mail = header.findViewById(R.id.head_mail);

        SharedPreferences sp = getSharedPreferences("MyPref", MODE_PRIVATE);
        String val = sp.getString("id","");

        DBHelper myDB = new DBHelper(this);
        Cursor cursor = myDB.readData(val);

        if (cursor.getCount() == 0){
            Toast.makeText(this, "Not logged IN", Toast.LENGTH_SHORT).show();
        }
        else {
            while (cursor.moveToNext()){
                nav_name.setText("Hi "+cursor.getString(1));
                nav_mail.setText(cursor.getString(2));
//                byte[] imageByte = cursor.getBlob(5);
//
//                if (imageByte != null && imageByte.length > 0) {
//                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
//                    nav_img.setImageBitmap(bitmap);
//                }
            }
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                break;
            case R.id.nav_acc:
                Intent intent = new Intent(getApplicationContext(), ManageAccountActivity.class);
                startActivity(intent);
                break;
            case R.id.logout:
                Context context = this;
                SharedPreferences sp = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.remove("id");
                editor.apply();
                recreate();
                Toast.makeText(this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}