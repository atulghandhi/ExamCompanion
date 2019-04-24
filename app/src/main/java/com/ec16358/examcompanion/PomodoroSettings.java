package com.ec16358.examcompanion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/*
 * Used to changed length of pomodoro timer intervals. Uses setters in the Pomodoro class
 *
 * */

public class PomodoroSettings extends AppCompatActivity {
    SeekBar pomodoroIntervalBar;
    SeekBar breakIntervalBar;
    SeekBar longBreakIntervalBar;

    TextView pomodoroInterval;
    TextView breakInterval;
    TextView longBreakInterval;
    TextView pomodoroPointInfo;

    String pomodoroDuration;
    String pomodoroShortBreak;
    String pomodoroLongBreak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro_settings);

        setTitle("Pomodoro Timer Settings");

        //initialise seekBars and textViews
        pomodoroInterval = findViewById(R.id.idPomodoroSettingsWorkIntervalValue);
        breakInterval = findViewById(R.id.idPomodoroSettingsBreakIntervalValue);
        longBreakInterval = findViewById(R.id.idPomodoroSettingsLongBreakValue);
        pomodoroPointInfo = findViewById(R.id.idPomodoroPointsInfo);

        pomodoroIntervalBar = findViewById(R.id.idPomodoroSettingsWorkIntervalSeekbar);
        breakIntervalBar = findViewById(R.id.idPomodoroSettingsBreakIntervalSeekbar);
        longBreakIntervalBar = findViewById(R.id.idPomodoroSettingsLongBreakSeekbar);

        pomodoroDuration = pomodoroInterval.getText().toString();
        pomodoroShortBreak = breakInterval.getText().toString();
        pomodoroLongBreak = longBreakInterval.getText().toString();

        //create onSeekBarChangeListeners for the seekBars.
        pomodoroIntervalBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //set progress of bar as text to textView
                pomodoroDuration = Integer.toString(progress);
                pomodoroInterval.setText(pomodoroDuration);
                int points = progress/5;
                pomodoroPointInfo.setText("You will earn 1 point for every 5 minutes of a completed Pomodoro session. \n\n The current Pomodoro interval will earn you " + points + " points.");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //notify user that 0 is not acceptable
                if(Integer.parseInt(pomodoroDuration) == 0) {
                    Toast.makeText(PomodoroSettings.this, "You cannot set timer duration to 0", Toast.LENGTH_SHORT).show();
                }
            }
        });

        breakIntervalBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //set progress of bar to textView
                pomodoroShortBreak = Integer.toString(progress);
                breakInterval.setText(pomodoroShortBreak);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //notify user that 0 is not acceptable
                if(Integer.parseInt(pomodoroShortBreak) == 0) {
                    Toast.makeText(PomodoroSettings.this, "You cannot set timer duration to 0", Toast.LENGTH_SHORT).show();
                }
            }
        });

        longBreakIntervalBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //set progress of bar to textView
                pomodoroLongBreak = Integer.toString(progress);
                longBreakInterval.setText(pomodoroLongBreak);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //notify user that 0 is not acceptable
                if(Integer.parseInt(pomodoroLongBreak) == 0) {
                    Toast.makeText(PomodoroSettings.this, "You cannot set timer duration to 0", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void saveButtonClicked(View v){

        if(Integer.parseInt(pomodoroDuration) != 0) {
            Pomodoro.setStartTimePomodoro(Integer.parseInt(pomodoroDuration)*60000);
        } else {
            Toast.makeText(PomodoroSettings.this, "You cannot set timer duration to 0", Toast.LENGTH_SHORT).show();
            return;
        }

        if(Integer.parseInt(pomodoroShortBreak) != 0) {
            Pomodoro.setStartTimeShortBreak(Integer.parseInt(pomodoroShortBreak)*60000);
        } else {
            Toast.makeText(PomodoroSettings.this, "You cannot set timer duration to 0", Toast.LENGTH_SHORT).show();
            return;
        }

        if(Integer.parseInt(pomodoroLongBreak) != 0) {
            Pomodoro.setStartTimeLongBreak(Integer.parseInt(pomodoroLongBreak) * 60000);
        } else {
            Toast.makeText(PomodoroSettings.this, "You cannot set timer duration to 0", Toast.LENGTH_SHORT).show();
            return;
        }

        startActivity(new Intent(PomodoroSettings.this, Pomodoro.class));
    }

}