package com.zackmatthews.genericcomponents;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class LaunchActivity extends AppCompatActivity {

    static boolean isLaunchInterupted = false;
    Thread startActivityThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        findViewById(R.id.launcher_icon).setAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
        ((ImageView)findViewById(R.id.launcher_icon)).animate();
        startActivityThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1200);
                    if(!isLaunchInterupted){
                        startActivity(new Intent(LaunchActivity.this, MainActivity.class));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        startActivityThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
