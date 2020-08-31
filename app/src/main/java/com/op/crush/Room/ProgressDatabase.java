package com.op.crush.Room;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;


@Database(entities = ProgressState.class , version = 1,exportSchema = false)
public abstract class ProgressDatabase extends RoomDatabase {

    public static ProgressDatabase instance;

    public abstract ProgressDao progressDao();

    public static synchronized ProgressDatabase getInstance(Context context){

        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    ProgressDatabase.class,
                    "progress_database")
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

    private static class populateDb extends AsyncTask<Void,Void,Void>{

        private ProgressDao progressDao;

        public populateDb(ProgressDatabase progressDatabase) {
            this.progressDao = progressDatabase.progressDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            progressDao.insert(new ProgressState(0));
            return null;
        }
    }

}
