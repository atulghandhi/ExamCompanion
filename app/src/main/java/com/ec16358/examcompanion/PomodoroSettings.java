package com.ec16358.examcompanion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class PomodoroSettings extends AppCompatActivity {
    SeekBar pomodoroIntervalBar;
    SeekBar breakIntervalBar;
    SeekBar longBreakIntervalBar;

    TextView pomodoroInterval;
    TextView breakInterval;
    TextView longBreakInterval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro_settings);

        setTitle("Pomodoro Timer Settings");

        //initialise seekBars and textViews
        pomodoroInterval = findViewById(R.id.idPomodoroSettingsWorkIntervalValue);
        breakInterval = findViewById(R.id.idPomodoroSettingsBreakIntervalValue);
        longBreakInterval = findViewById(R.id.idPomodoroSettingsLongBreakValue);

        pomodoroIntervalBar = findViewById(R.id.idPomodoroSettingsWorkIntervalSeekbar);
        breakIntervalBar = findViewById(R.id.idPomodoroSettingsBreakIntervalSeekbar);
        longBreakIntervalBar = findViewById(R.id.idPomodoroSettingsLongBreakSeekbar);

        //create onSeekBarChangeListeners for the seekBars.
        pomodoroIntervalBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            String a = "";
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                a = Integer.toString(progress);
                pomodoroInterval.setText(a);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Pomodoro.setStartTimeInMillis(Integer.parseInt(a)*60000);
            }
        });

        breakIntervalBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String a = Integer.toString(progress);
                breakInterval.setText(a);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        longBreakIntervalBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String a = Integer.toString(progress);
                longBreakInterval.setText(a);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void saveButtonClicked(View v){
        startActivity(new Intent(PomodoroSettings.this, Pomodoro.class));
    }


}