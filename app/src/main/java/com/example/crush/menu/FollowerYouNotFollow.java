package com.example.crush.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.crush.R;

public class FollowerYouNotFollow  extends Fragment {

    ImageButton back_to_homefrag;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.follower_ynf_fragment, container, false);

        back_to_homefrag = view.findViewById(R.id.fynf_back);
        return view;
    }
    }
