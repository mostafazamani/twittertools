package com.op.crush.Room;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.loader.content.AsyncTaskLoader;

import java.util.List;

public class ProgreesRepository {

    private ProgressDao progressDao;
    private LiveData<List<ProgressState>> states;

    public ProgreesRepository(Application application) {
        ProgressDatabase database = ProgressDatabase.getInstance(application);
        progressDao = database.progressDao();
        states = progressDao.getStates();
    }

    public void insert(ProgressState state) {
        new InsertAsyncTask(progressDao).execute(state);
    }

    public void update(ProgressState state) {
        new UpdateAsyncTask(progressDao).execute(state);
    }

    public void delete(ProgressState state) {
        new DeleteAsyncTask(progressDao).execute(state);
    }

    public LiveData<List<ProgressState>> getStates() {
        return states;
    }


    private static class InsertAsyncTask extends AsyncTask<ProgressState, Void, Void> {

        private ProgressDao progressDao;

        public InsertAsyncTask(ProgressDao progressDao) {
            this.progressDao = progressDao;
        }

        @Override
        protected Void doInBackground(ProgressState... progressStates) {
            progressDao.insert(progressStates[0]);

            return null;
        }
    }

    private static class UpdateAsyncTask extends AsyncTask<ProgressState, Void, Void> {

        private ProgressDao progressDao;

        public UpdateAsyncTask(ProgressDao progressDao) {
            this.progressDao = progressDao;
        }

        @Override
        protected Void doInBackground(ProgressState... progressStates) {
            progressDao.update(progressStates[0]);

            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<ProgressState, Void, Void> {

        private ProgressDao progressDao;

        public DeleteAsyncTask(ProgressDao progressDao) {
            this.progressDao = progressDao;
        }

        @Override
        protected Void doInBackground(ProgressState... progressStates) {
            progressDao.delete(progressStates[0]);

            return null;
        }
    }


}
