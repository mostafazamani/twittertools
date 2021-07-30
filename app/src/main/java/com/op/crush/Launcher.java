package com.op.crush;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;

public class Launcher extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher_activity);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Launcher.this, MainActivity.class);

                startActivity(intent);

                finish();
            }
        },2500);


//        Thread myThread = new Thread()
//        {
//            @Override
//            public void run() {
//                try {
//                    sleep(800);
//                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
//
//
//                    startActivity(intent);
//                    //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                    finish();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        myThread.start();
    }
}
