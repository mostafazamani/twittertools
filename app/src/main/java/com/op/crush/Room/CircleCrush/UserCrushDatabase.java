package com.op.crush.Room.CircleCrush;


import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;


@Database(entities = UserCrush.class , version = 1,exportSchema = false)
public abstract class UserCrushDatabase extends RoomDatabase {

    public static UserCrushDatabase instance;

    public abstract UserCrushDao userCrushDao();

    public static synchronized UserCrushDatabase getInstance(Context context){

        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    UserCrushDatabase.class,
                    "UserCrush_database")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .addCallback(callback)
                    .build();
        }

        return instance;
    }

    private static RoomDatabase.Callback callback = new RoomDatabase.Callback(){

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new populateDb(instance).execute();
        }
    };
    private static class populateDb extends AsyncTask<Void,Void,Void> {

        private UserCrushDao userCrushDao;

        public populateDb(UserCrushDatabase userCrushDatabase) {
            this.userCrushDao = userCrushDatabase.userCrushDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            userCrushDao.insert(new UserCrush(0L));
            return null;
        }
    }

}
