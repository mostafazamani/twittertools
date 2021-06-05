package com.op.crush;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;

public class IntroActivityApp extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance("","",R.drawable.slide1, Color.BLUE));
        addSlide(AppIntroFragment.newInstance("","",R.drawable.slide2,Color.GREEN));
        addSlide(AppIntroFragment.newInstance("","",R.drawable.slide3,Color.YELLOW));
        addSlide(AppIntroFragment.newInstance("","",R.drawable.slide4,Color.RED));
        setColorTransitionsEnabled(true);
        setSkipButtonEnabled(false);

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        SharedPreferences.Editor editor = getSharedPreferences("intro", Context.MODE_PRIVATE).edit();
        editor.putBoolean("AppIntro", false);
        editor.apply();
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
    }

}
