package com.opteam.tools.Room.CircleCrush;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

public class UserCrushViewModel extends AndroidViewModel {

    UserCrushRepository crushRepository;
    List<UserCrush> userCrushList;


    public UserCrushViewModel(@NonNull Application application) {
        super(application);
        crushRepository = new UserCrushRepository(application);
        userCrushList = crushRepository.getUserCrushList();
    }

    public void delete(UserCrush userCrush){new DeleteAT(crushRepository).execute(userCrush);}
    public List<UserCrush> getUserCrushList() {
        return userCrushList;
    }


    public static class DeleteAT extends AsyncTask<UserCrush,Void,Void> {

        UserCrushRepository crushRepository;

        public DeleteAT(UserCrushRepository crushRepository) {
            this.crushRepository = crushRepository;
        }

        @Override
        protected Void doInBackground(UserCrush... userCrushes) {
            crushRepository.delete(userCrushes[0]);
            return null;
        }
    }
}
