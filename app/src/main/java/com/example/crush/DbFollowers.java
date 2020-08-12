package com.example.crush;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.crush.models.following;

import java.util.ArrayList;
import java.util.List;

public class DbFollowers extends SQLiteOpenHelper {
    Context context;
    private static final String DBname = "usr";
    private static final String TB_NAME = "follow";


    private static final String CMD = "CREATE TABLE " + TB_NAME + " ("
            + following.Key_ID + " long PRIMARY KEY NOT NULL, "
            + following.KEY_NAME + " TEXT, "
            + following.KEY_IMAGE + " TEXT " +
            ");";

    public DbFollowers(@Nullable Context context) {
        super(context, DBname, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CMD);
        Toast.makeText(context, "created", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF NOT EXISTS " + TB_NAME);
        onCreate(db);

    }


    public void AddItem(following items) {


        SQLiteDatabase sd = this.getWritableDatabase();
        if (!CheckItem(items.getId())) {
            long insertId = sd.insert(TB_NAME, null, items.getContentValues());
//            Toast.makeText(context, "added", Toast.LENGTH_SHORT).show();
        }
        if (sd.isOpen()) sd.close();

    }

    public boolean CheckItem(long i) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TB_NAME + " WHERE " + following.Key_ID + "='" + i + "'", null);

        if (cursor.moveToFirst()) {

            return true;
        } else {
            return false;
        }
    }


    public List<Long> getItem() {

        SQLiteDatabase db = getReadableDatabase();
        List<Long> lsl = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TB_NAME, null);


        if (cursor.moveToFirst()) {
            do {

                lsl.add(cursor.getLong(cursor.getColumnIndex(following.Key_ID)));

            } while (cursor.moveToNext());
        }
        cursor.close();
        if (db.isOpen()) db.close();
        return lsl;

    }

}