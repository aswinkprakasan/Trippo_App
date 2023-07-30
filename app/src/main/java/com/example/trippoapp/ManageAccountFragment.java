package com.example.trippoapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class ManageAccountFragment extends Fragment {

    FloatingActionButton fabEdit;
    EditText uploadName, uploadEmail, uploadNum;
    Button btnSave, logout;
    DBHelper myDB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage_account, container, false);

        fabEdit = view.findViewById(R.id.edit_fab);
        uploadName = view.findViewById(R.id.upload_name);
        uploadEmail = view.findViewById(R.id.upload_email);
        uploadNum = view.findViewById(R.id.upload_num);
        btnSave = view.findViewById(R.id.btn_upload);
        logout = view.findViewById(R.id.logout);
        myDB = new DBHelper(getActivity());

        uploadName.setEnabled(false);
        uploadEmail.setEnabled(false);
        uploadNum.setEnabled(false);
        btnSave.setEnabled(false);

        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadName.setEnabled(true);
                uploadNum.setEnabled(true);
                btnSave.setEnabled(true);
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
                ModelClass modelClass = new ModelClass(name,mail,number, "",null);
                Boolean result = myDB.updateData(modelClass);
                if (result){
                    Toast.makeText(getActivity(), "Saved successfully", Toast.LENGTH_SHORT).show();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.remove(ManageAccountFragment.this);
                    fragmentTransaction.replace(R.id.fragment_container, new ManageAccountFragment());
                    fragmentTransaction.commit();
                }
                else {
                    Toast.makeText(getActivity(), "Error occured", Toast.LENGTH_SHORT).show();
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getActivity();
                SharedPreferences sp = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.remove("id");
                editor.apply();

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(ManageAccountFragment.this);
                fragmentTransaction.commit();

                Intent intent = new Intent(getActivity(), SigninActivity.class);
                startActivity(intent);

//                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ManageAccountFragment()).commit();
                Toast.makeText(getActivity(), "Logout Successfully", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void showData() {
        SharedPreferences sp = requireActivity().getSharedPreferences("MyPref", MODE_PRIVATE);
        String val = sp.getString("id", "");

        myDB = new DBHelper(getActivity());
        Cursor cursor = myDB.readData(val);

        if (cursor.getCount() == 0) {
            Toast.makeText(getActivity(), "Not logged IN", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), SigninActivity.class);
            startActivity(intent);
        } else {
            while (cursor.moveToNext()) {
                uploadName.setText(cursor.getString(1));
                uploadEmail.setText(cursor.getString(2));
                uploadNum.setText(cursor.getString(4));

                cursor.close();
            }
        }
    }

}