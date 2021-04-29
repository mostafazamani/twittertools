package com.op.crush;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.op.crush.models.SuggestUser;

import java.util.ArrayList;
import java.util.List;

public class DbSuggest extends SQLiteOpenHelper {
    Context context;
    private static final String DBname = "su";
    private static final String TB_NAME = "suggest";


    private static final String CMD = "CREATE TABLE " + TB_NAME + " ("
            + SuggestUser.Key_ID + " LONG PRIMARY KEY NOT NULL, "
            + SuggestUser.KEY_NAME + " TEXT, "
            + SuggestUser.KEY_SCREEN + " TEXT, "
            + SuggestUser.KEY_IMAGE + " TEXT " +
            ");";

    public DbSuggest(@Nullable Context context) {
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
        db.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
        onCreate(db);

    }


    public void AddItem(SuggestUser items) {


        SQLiteDatabase sd = this.getWritableDatabase();
        if (!CheckItem(items.getId())) {
            long insertId = sd.insert(TB_NAME, null, items.getContentValues());
//            Toast.makeText(context, "added", Toast.LENGTH_SHORT).show();
        }
        if (sd.isOpen()) sd.close();

    }

    public boolean CheckItem(long i) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TB_NAME + " WHERE " + SuggestUser.Key_ID + "='" + i + "'", null);

        if (cursor.moveToFirst()) {

            return true;
        } else {
            return false;
        }
    }


    public List<SuggestUser> getItem() {

        SQLiteDatabase db = getReadableDatabase();
        List<SuggestUser> lsl = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TB_NAME, null);


        if (cursor.moveToFirst()) {
            do {
                SuggestUser user = new SuggestUser();
                user.setId(cursor.getLong(cursor.getColumnIndex(SuggestUser.Key_ID)));
                user.setName(cursor.getString(cursor.getColumnIndex(SuggestUser.KEY_NAME)));
                user.setScreenName(cursor.getString(cursor.getColumnIndex(SuggestUser.KEY_SCREEN)));
                user.setProfilePictureUrl(cursor.getString(cursor.getColumnIndex(SuggestUser.KEY_IMAGE)));
                lsl.add(user);

            } while (cursor.moveToNext());
        }
        cursor.close();
        if (db.isOpen()) db.close();
        return lsl;

    }

}