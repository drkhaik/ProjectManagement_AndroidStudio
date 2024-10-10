package com.example.finalassignment_group5_topic1b2;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.finalassignment_group5_topic1b2.Service.MusicService;
import com.example.finalassignment_group5_topic1b2.UI.ChartFragment;
import com.example.finalassignment_group5_topic1b2.UI.HomeFragment;
import com.example.finalassignment_group5_topic1b2.UI.SearchFragment;
import com.example.finalassignment_group5_topic1b2.UI.SettingsFragment;
import com.example.finalassignment_group5_topic1b2.UI.TaskFragment;
import com.example.finalassignment_group5_topic1b2.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        // start background music
        sharedPreferences = getSharedPreferences("AppSettings", 0);
        boolean playMusic = sharedPreferences.getBoolean("play_music", false);
        if (playMusic) {
            Intent serviceIntent = new Intent(this, MusicService.class);
            startService(serviceIntent);
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.task) {
                replaceFragment(new TaskFragment());
            } else if (itemId == R.id.search) {
                replaceFragment(new SearchFragment());
            } else if (itemId == R.id.chart) {
                replaceFragment(new ChartFragment());
            } else if (itemId == R.id.settings) {
                replaceFragment(new SettingsFragment());
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}