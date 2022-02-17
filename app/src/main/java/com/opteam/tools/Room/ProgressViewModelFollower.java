package com.opteam.tools.Room;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;


public class ProgressViewModelFollower extends AndroidViewModel {

    private ProgreesRepositoryFollower repository;
    private LiveData<List<ProgressStateFollower>> data;

    public ProgressViewModelFollower(@NonNull Application application) {
        super(application);
        repository = new ProgreesRepositoryFollower(application);
        data = repository.getStates();
    }

    public void insert(ProgressStateFollower state){
        new InsertAsyncTask(repository).execute(state);
    }
    public void update(ProgressStateFollower state){
        repository.update(state);
    }
    public void delete(ProgressStateFollower state){
        repository.delete(state);
    }

    public LiveData<List<ProgressStateFollower>> getState(){
        return data;
    }

    private static class InsertAsyncTask extends AsyncTask<ProgressStateFollower, Void, Void> {

        private ProgreesRepositoryFollower progressDao;

        public InsertAsyncTask(ProgreesRepositoryFollower progressDao) {
            this.progressDao = progressDao;
        }

        @Override
        protected Void doInBackground(ProgressStateFollower... progressStates) {
            progressDao.insert(progressStates[0]);

            return null;
        }
    }


}
