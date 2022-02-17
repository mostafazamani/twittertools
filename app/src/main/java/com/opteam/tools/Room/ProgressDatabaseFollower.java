package com.opteam.tools.Room;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;


@Database(entities = ProgressStateFollower.class , version = 1,exportSchema = false)
public abstract class ProgressDatabaseFollower extends RoomDatabase {

    public static ProgressDatabaseFollower instance;

    public abstract ProgressDaoFollower progressDao();

    public static synchronized ProgressDatabaseFollower getInstance(Context context){

        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    ProgressDatabaseFollower.class,
                    "progress_database_follower")
                    .fallbackToDestructiveMigration()
                    .addCallback(callback)
                    .build();
        }

        return instance;
    }

    private static Callback callback = new Callback(){

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new populateDb(instance).execute();
        }
    };

    private static class populateDb extends AsyncTask<Void,Void,Void>{

        private ProgressDaoFollower progressDao;

        public populateDb(ProgressDatabaseFollower progressDatabase) {
            this.progressDao = progressDatabase.progressDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            progressDao.insert(new ProgressStateFollower(0));
            return null;
        }
    }

}
