package com.example.finalassignment_group5_topic1b2.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.widget.SwitchCompat;

import com.example.finalassignment_group5_topic1b2.R;
import com.example.finalassignment_group5_topic1b2.Service.MusicService;

public class SettingsFragment extends Fragment {

    private SwitchCompat switchHideEstimate;
    private SwitchCompat switchPlayMusic;
    private SharedPreferences sharedPreferences;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences("AppSettings", 0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        switchHideEstimate = view.findViewById(R.id.switch_hide_estimate);
        switchPlayMusic = view.findViewById(R.id.switch_play_music);

        boolean hideEstimate = sharedPreferences.getBoolean("hide_estimate", false);
        boolean playMusic = sharedPreferences.getBoolean("play_music", true);

        switchHideEstimate.setChecked(hideEstimate);
        switchPlayMusic.setChecked(playMusic);

        switchHideEstimate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("hide_estimate", isChecked);
            editor.apply();
        });

        switchPlayMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("play_music", isChecked);
            editor.apply();

            if (isChecked) {
                Intent serviceIntent = new Intent(getActivity(), MusicService.class);
                getActivity().startService(serviceIntent);
                Log.d("SettingsFragment", "MusicService started");
            } else {
                Intent serviceIntent = new Intent(getActivity(), MusicService.class);
                getActivity().stopService(serviceIntent);
                Log.d("SettingsFragment", "MusicService stopped");
            }

        });

        return view;
    }
}