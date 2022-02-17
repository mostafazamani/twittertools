package com.opteam.tools.Room;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ProgreesRepositoryFollower {

    private ProgressDaoFollower progressDao;
    private LiveData<List<ProgressStateFollower>> states;

    public ProgreesRepositoryFollower(Application application) {
        ProgressDatabaseFollower database = ProgressDatabaseFollower.getInstance(application);
        progressDao = database.progressDao();
        states = progressDao.getStates();
    }

    public void insert(ProgressStateFollower state) {
        new InsertAsyncTask(progressDao).execute(state);
    }

    public void update(ProgressStateFollower state) {
        new UpdateAsyncTask(progressDao).execute(state);
    }

    public void delete(ProgressStateFollower state) {
        new DeleteAsyncTask(progressDao).execute(state);
    }

    public LiveData<List<ProgressStateFollower>> getStates() {
        return states;
    }


    private static class InsertAsyncTask extends AsyncTask<ProgressStateFollower, Void, Void> {

        private ProgressDaoFollower progressDao;

        public InsertAsyncTask(ProgressDaoFollower progressDao) {
            this.progressDao = progressDao;
        }

        @Override
        protected Void doInBackground(ProgressStateFollower... progressStates) {
            progressDao.insert(progressStates[0]);

            return null;
        }
    }

    private static class UpdateAsyncTask extends AsyncTask<ProgressStateFollower, Void, Void> {

        private ProgressDaoFollower progressDao;

        public UpdateAsyncTask(ProgressDaoFollower progressDao) {
            this.progressDao = progressDao;
        }

        @Override
        protected Void doInBackground(ProgressStateFollower... progressStates) {
            progressDao.update(progressStates[0]);

            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<ProgressStateFollower, Void, Void> {

        private ProgressDaoFollower progressDao;

        public DeleteAsyncTask(ProgressDaoFollower progressDao) {
            this.progressDao = progressDao;
        }

        @Override
        protected Void doInBackground(ProgressStateFollower... progressStates) {
            progressDao.delete(progressStates[0]);

            return null;
        }
    }


}
