package com.ec16358.examcompanion;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class PomodoroSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro_settings);

        setTitle("Pomodoro Settings");
    }
}
