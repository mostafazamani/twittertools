package com.op.crush;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.op.crush.models.follow;

import java.util.ArrayList;
import java.util.List;

public class DbFollowings extends SQLiteOpenHelper {
    Context context;
    private static final String DBname = "flr";
    private static final String TB_NAME = "following";
    private static DbFollowings mInstance = null;


    private static final String CMD = "CREATE TABLE " + TB_NAME + " ("
            + follow.Key_ID + " long PRIMARY KEY NOT NULL, "
            + follow.KEY_NAME + " TEXT, "
            + follow.KEY_IMAGE + " TEXT " +
            ");";


    public static DbFollowings getInstance(Context context){
        if (mInstance == null) {
            mInstance = new DbFollowings(context.getApplicationContext());
        }
        return mInstance;
    }
    public DbFollowings(@Nullable Context context) {
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


    public void AddItem(follow items) {


        SQLiteDatabase sd = this.getWritableDatabase();
        sd.beginTransaction();
        if (!CheckItem(items.getId())) {
            long insertId = sd.insert(TB_NAME, null, items.getContentValues());
//            Toast.makeText(context, "added", Toast.LENGTH_SHORT).show();
        }
        if (sd.isOpen()){
            sd.setTransactionSuccessful();
            sd.endTransaction();
            sd.close();
        }

    }

    public boolean CheckItem(long i) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM " + TB_NAME + " WHERE " + follow.Key_ID + "='" + i + "'", null);

        if (c.moveToFirst()) {
            return true;
        } else {
            return false;
        }

    }
    public follow getOneItem(long i) {
        SQLiteDatabase db = getReadableDatabase();


        Cursor cursor = db.rawQuery("SELECT * FROM " + TB_NAME + " WHERE " + follow.Key_ID + "='" + i + "'", null);
        follow f = new follow();
        if (cursor.moveToFirst()) {
            do {

                f.setId(cursor.getLong(cursor.getColumnIndex(follow.Key_ID)));
                f.setName(cursor.getString(cursor.getColumnIndex(follow.KEY_NAME)));
                f.setProfilePictureUrl(cursor.getString(cursor.getColumnIndex(follow.KEY_IMAGE)));


            } while (cursor.moveToNext());

        }
        cursor.close();
        if (db.isOpen()) db.close();
        return f;
    }


    public List<follow> getItem() {

        SQLiteDatabase db = getReadableDatabase();
        List<follow> lsl = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TB_NAME, null);


        if (cursor.moveToFirst()) {
            do {
                follow f = new follow();

                f.setId(cursor.getLong(cursor.getColumnIndex(follow.Key_ID)));
                f.setName(cursor.getString(cursor.getColumnIndex(follow.KEY_NAME)));
                f.setProfilePictureUrl(cursor.getString(cursor.getColumnIndex(follow.KEY_IMAGE)));

                lsl.add(f);

            } while (cursor.moveToNext());
        }
        cursor.close();
        if (db.isOpen()) db.close();
        return lsl;

    }

}