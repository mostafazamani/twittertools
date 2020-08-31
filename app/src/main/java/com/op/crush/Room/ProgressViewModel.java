package com.op.crush.Room;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;


public class ProgressViewModel extends AndroidViewModel {

    private ProgreesRepository repository;
    private LiveData<List<ProgressState>> data;

    public ProgressViewModel(@NonNull Application application) {
        super(application);
        repository = new ProgreesRepository(application);
        data = repository.getStates();
    }

    public void insert(ProgressState state){
        new InsertAsyncTask(repository).execute(state);
    }
    public void update(ProgressState state){
        repository.update(state);
    }
    public void delete(ProgressState state){
        repository.delete(state);
    }

    public LiveData<List<ProgressState>> getState(){
        return data;
    }

    private static class InsertAsyncTask extends AsyncTask<ProgressState, Void, Void> {

        private ProgreesRepository progressDao;

        public InsertAsyncTask(ProgreesRepository progressDao) {
            this.progressDao = progressDao;
        }

        @Override
        protected Void doInBackground(ProgressState... progressStates) {
            progressDao.insert(progressStates[0]);

            return null;
        }
    }


}
