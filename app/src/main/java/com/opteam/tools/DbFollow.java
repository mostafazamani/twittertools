package com.opteam.tools;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.opteam.tools.models.follow;

import java.util.ArrayList;
import java.util.List;

public class DbFollow extends SQLiteOpenHelper {
    Context context;
    private static final String DBname = "user";
    public static final String TB_FOLLOWER = "follower";
    public static final String TB_FOLLOWING = "following";
    private static DbFollow mInstance = null;

    private static final String CMD1 = "CREATE TABLE " + TB_FOLLOWER + " ("
            + follow.Key_ID + " INTEGER, "
            + follow.KEY_NAME + " TEXT, "
            + follow.KEY_IMAGE + " TEXT " +
            ");";
    private static final String CMD2 = "CREATE TABLE " + TB_FOLLOWING + " ("
            + follow.Key_ID + " INTEGER, "
            + follow.KEY_NAME + " TEXT, "
            + follow.KEY_IMAGE + " TEXT " +
            ");";

    private static final String CMD1k = "CREATE TABLE IF NOT EXISTS " + TB_FOLLOWER + " ("
            + follow.Key_ID + " INTEGER, "
            + follow.KEY_NAME + " TEXT, "
            + follow.KEY_IMAGE + " TEXT " +
            ");";

    private static final String CMD2k = "CREATE TABLE IF NOT EXISTS " + TB_FOLLOWING + " ("
            + follow.Key_ID + " INTEGER, "
            + follow.KEY_NAME + " TEXT, "
            + follow.KEY_IMAGE + " TEXT " +
            ");";


    public static DbFollow getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DbFollow(context.getApplicationContext());
        }
        return mInstance;
    }

    public DbFollow(@Nullable Context context) {
        super(context, DBname, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CMD1);
        db.execSQL(CMD2);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TB_FOLLOWER);
        db.execSQL("DROP TABLE IF EXISTS " + TB_FOLLOWING);
        onCreate(db);

    }


    public void AddItem(follow items, String tb_name) {


        SQLiteDatabase sd = DbFollow.getInstance(context).getWritableDatabase();
        if (tb_name == DbFollow.TB_FOLLOWING)
            sd.execSQL(CMD2k);
        if (tb_name == DbFollow.TB_FOLLOWER)
            sd.execSQL(CMD1k);
//        sd.beginTransaction();
        if (!CheckItem(items.getId(), tb_name)) {

            sd.insert(tb_name, null, items.getContentValues());

        }
//        sd.setTransactionSuccessful();
//        sd.endTransaction();
//        if (sd.isOpen()) sd.close();


    }

    public boolean CheckItem(long i, String tb_name) {
        SQLiteDatabase db = getReadableDatabase();
        boolean ch = false;
//        db.beginTransaction();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tb_name + " WHERE " + follow.Key_ID + " = '" + i + "'", null);
        if (cursor.getCount() != 0) {

            ch = true;
        }
        cursor.close();
        if (db.isOpen()) {
//            db.setTransactionSuccessful();
//            db.endTransaction();
//            db.close();
//            cursor.close();
        }
        return ch;
    }

    public follow getOneItem(long i, String tb_name) {
        SQLiteDatabase db = getInstance(context).getReadableDatabase();

//        db.beginTransaction();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tb_name + " WHERE " + follow.Key_ID + "='" + i + "'", null);
        follow f = new follow();
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {

                f.setId(cursor.getLong(cursor.getColumnIndex(follow.Key_ID)));
                f.setName(cursor.getString(cursor.getColumnIndex(follow.KEY_NAME)));
                f.setProfilePictureUrl(cursor.getString(cursor.getColumnIndex(follow.KEY_IMAGE)));


            } while (cursor.moveToNext());

        }
        cursor.close();
//        if (db.isOpen()) {
//            db.setTransactionSuccessful();
//            db.endTransaction();
//            db.close();
//            cursor.close();
//        }


        return f;
    }


    public List<follow> getItem(String tb_name) {

        SQLiteDatabase db = DbFollow.getInstance(context).getReadableDatabase();
        List<follow> lsl = new ArrayList<>();
//        db.beginTransaction();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tb_name, null);


        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                follow f = new follow();

                f.setId(cursor.getLong(cursor.getColumnIndex(follow.Key_ID)));
                f.setName(cursor.getString(cursor.getColumnIndex(follow.KEY_NAME)));
                f.setProfilePictureUrl(cursor.getString(cursor.getColumnIndex(follow.KEY_IMAGE)));

                lsl.add(f);

            }
        }
        cursor.close();
//        if (db.isOpen()) {
//            db.setTransactionSuccessful();
//            db.endTransaction();
//            db.close();
//            cursor.close();
//        }
        return lsl;

    }

    public List<follow> getExpectItem(String tb_name1, String tb_name2) {

        SQLiteDatabase db = DbFollow.getInstance(context).getReadableDatabase();
        List<follow> lsl = new ArrayList<>();
//        db.beginTransaction();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tb_name1 + " EXCEPT SELECT * FROM " + tb_name2, null);


        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                follow f = new follow();

                f.setId(cursor.getLong(cursor.getColumnIndex(follow.Key_ID)));
                f.setName(cursor.getString(cursor.getColumnIndex(follow.KEY_NAME)));
                f.setProfilePictureUrl(cursor.getString(cursor.getColumnIndex(follow.KEY_IMAGE)));

                lsl.add(f);

            }
        }
        cursor.close();
//        if (db.isOpen()) {
//            db.setTransactionSuccessful();
//            db.endTransaction();
//            db.close();
//            cursor.close();
//        }
        return lsl;

    }

    public void DeleteItem(long id, String tb_name) {

        SQLiteDatabase db = DbFollow.getInstance(context).getReadableDatabase();

        db.delete(tb_name, follow.Key_ID + " = '" + id + "';", null);


        if (db.isOpen()) db.close();


    }


    public void DropTable(String tbname) {
        SQLiteDatabase db = DbFollow.getInstance(context).getReadableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + tbname);


    }
}