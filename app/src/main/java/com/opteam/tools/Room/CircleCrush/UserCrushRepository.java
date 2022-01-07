package com.opteam.tools.Room.CircleCrush;


import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

public class UserCrushRepository {

    private UserCrushDao userCrushDao;
    List<UserCrush> userCrushList;
    public UserCrushRepository(Application application) {

        UserCrushDatabase userCrushDatabase = UserCrushDatabase.getInstance(application);
        userCrushDao = userCrushDatabase.userCrushDao();
    }


    public List<UserCrush> getUserCrushList() {
        return new Li(userCrushDao).doInBackground();
    }

    public void insert(UserCrush userCrush){new InsertAT(userCrushDao).execute(userCrush);}
    public void update(UserCrush userCrush){new UpdateAT(userCrushDao).execute(userCrush);}
    public void delete(UserCrush userCrush){new DeleteAT(userCrushDao).execute(userCrush);}


    public static class InsertAT extends AsyncTask<UserCrush,Void,Void>{

        UserCrushDao userCrushDao;

        public InsertAT(UserCrushDao userCrushDao) {
            this.userCrushDao = userCrushDao;
        }

        @Override
        protected Void doInBackground(UserCrush... userCrushes) {
            userCrushDao.insert(userCrushes[0]);
            return null;
        }
    }
    public static class UpdateAT extends AsyncTask<UserCrush,Void,Void>{

        UserCrushDao userCrushDao;

        public UpdateAT(UserCrushDao userCrushDao) {
            this.userCrushDao = userCrushDao;
        }

        @Override
        protected Void doInBackground(UserCrush... userCrushes) {
            userCrushDao.update(userCrushes[0]);
            return null;
        }
    }
    public static class DeleteAT extends AsyncTask<UserCrush,Void,Void>{

        UserCrushDao userCrushDao;

        public DeleteAT(UserCrushDao userCrushDao) {
            this.userCrushDao = userCrushDao;
        }

        @Override
        protected Void doInBackground(UserCrush... userCrushes) {
            userCrushDao.delete(userCrushes[0]);
            Log.i("removeItem","Removed");
            return null;
        }
    }
    public static class DeleteAll extends AsyncTask<Void,Void,Void>{
        UserCrushDao userCrushDao;

        public DeleteAll(UserCrushDao userCrushDao) {
            this.userCrushDao = userCrushDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            userCrushDao.deleteAll();
            return null;
        }
    }

    public static class Li extends AsyncTask<Void,Void,List<UserCrush>>{
        UserCrushDao userCrushDao;
        List<UserCrush> userCrushes;
        public Li(UserCrushDao userCrushDao) {
            this.userCrushDao = userCrushDao;
        }

        @Override
        protected List<UserCrush> doInBackground(Void... voids) {
           userCrushes =userCrushDao.getUserCrush();
            return userCrushes;
        }
    }
}
