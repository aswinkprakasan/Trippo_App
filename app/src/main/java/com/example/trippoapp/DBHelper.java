package com.example.trippoapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "Tripp.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase myDB) {
        myDB.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, email TEXT, password TEXT, number TEXT)");
        myDB.execSQL("CREATE TABLE ratings (id INTEGER PRIMARY KEY AUTOINCREMENT,email TEXT, placeId TEXT, placeName TEXT, rating INTEGER)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase myDB, int i, int i1) {
        myDB.execSQL("drop Table if exists users");
    }

    public Boolean insertData(ModelClass modelClass){
        SQLiteDatabase myDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", modelClass.getEmail());
        contentValues.put("password", modelClass.getPass());
        contentValues.put("username",modelClass.getName());
        long result = myDB.insert("users", null, contentValues);
        if (result == -1){
            return false;
        }
        else {
            return true;
        }
    }

    public Boolean checkuser(ModelClass modelClass){
        SQLiteDatabase myDB = this.getWritableDatabase();
        String email = modelClass.getEmail();
        Cursor cursor = myDB.rawQuery("select * from users where email = ?", new String[] {email});
        if(cursor.getCount()>0){
            cursor.close();
            return true;
        }
        else{
            cursor.close();
            return false;
        }
    }

    public Boolean checkusernamepass(ModelClass modelClass){
        SQLiteDatabase myDB = this.getWritableDatabase();
        String email = modelClass.getEmail();
        String pass = modelClass.getPass();
        Cursor cursor = myDB.rawQuery("select * from users where email = ? and password = ?", new String[] {email, pass});
        if(cursor.getCount()>0){
            return true;
        }
        else{
            return false;
        }
    }

    public Boolean updateData(ModelClass modelClass){
        SQLiteDatabase myDB = this.getWritableDatabase();



        ContentValues contentValues = new ContentValues();
        contentValues.put("username",modelClass.getName());
        contentValues.put("number",modelClass.getNum());


        String whereClause = "email=?";
        String[] whereArgs = {modelClass.getEmail()};

        long result = myDB.update("users", contentValues, whereClause, whereArgs);

        if (result == -1){
            return false;
        }
        else {
            return true;
        }
    }

    public Cursor readData(String email){
        SQLiteDatabase myDB = this.getReadableDatabase();
        Cursor cursor = myDB.rawQuery("select * from users where email = ?", new String[] {email});
        return cursor;
    }
}
