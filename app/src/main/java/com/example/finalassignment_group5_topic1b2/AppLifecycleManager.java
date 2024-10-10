package com.example.finalassignment_group5_topic1b2;

import android.app.Application;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.finalassignment_group5_topic1b2.Service.MusicService;

public class AppLifecycleManager extends Application implements Application.ActivityLifecycleCallbacks {

    private boolean isInBackground = false;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (isInBackground) {
            isInBackground = false;
            SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
            boolean playMusic = sharedPreferences.getBoolean("play_music", true);
            if (playMusic) {
                Intent serviceIntent = new Intent(this, MusicService.class);
                this.startService(serviceIntent);
            }
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        isInBackground = true;
        Intent serviceIntent = new Intent(this, MusicService.class);
        this.stopService(serviceIntent);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}
    @Override
    public void onActivityResumed(Activity activity) {}
    @Override
    public void onActivityPaused(Activity activity) {}
    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}