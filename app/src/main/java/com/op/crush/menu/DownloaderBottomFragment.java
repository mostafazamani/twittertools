package com.op.crush.menu;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.op.crush.R;
import com.op.crush.downloadvideo.ClipboardMonitor;
import com.op.crush.downloadvideo.TwitterVideoDownloader;

public class DownloaderBottomFragment   extends Fragment {

    public static String STARTFOREGROUND_ACTION = "startforeground";
    public static String STOPFOREGROUND_ACTION = "stopforeground";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.downloader_fragment, container, false);

        Button button = view.findViewById(R.id.btn_download);
        EditText editText = view.findViewById(R.id.text_download);
        Switch aSwitch = view.findViewById(R.id.switch_download);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TwitterVideoDownloader downloader = new TwitterVideoDownloader(view.getContext(), editText.getText().toString());
                downloader.DownloadVideo();
            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b)
                            startClipboardMonitor();
                        else
                            stopClipboardMonitor();
            }
        });


        return view;
    }

    public void startClipboardMonitor() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireActivity().startForegroundService(
                    new Intent(
                            requireContext(),
                            ClipboardMonitor.class
                ).setAction(STARTFOREGROUND_ACTION)
            );
        } else {
            requireActivity().startService(
                    new Intent(
                            requireContext(),
                            ClipboardMonitor.class
                )
            );
        }

    }

    public void stopClipboardMonitor() {


        requireActivity().stopService(
               new Intent(
                        requireContext(),
                        ClipboardMonitor.class
            ).setAction(STOPFOREGROUND_ACTION)
        );


    }

}