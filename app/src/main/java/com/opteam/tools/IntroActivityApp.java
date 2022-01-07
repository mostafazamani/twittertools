package com.opteam.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.appintro.AppIntro2;
import com.github.appintro.AppIntroCustomLayoutFragment;

public class IntroActivityApp extends AppIntro2 {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.intro1));
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.intro2));
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.intro3));
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.intro4));
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.intro5));
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
