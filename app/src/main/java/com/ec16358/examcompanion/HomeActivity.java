package com.ec16358.examcompanion;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.*;

import java.sql.SQLException;


public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button HomePomodoroButton = findViewById(R.id.HomePomodoroButton);
        HomePomodoroButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        startActivity(new Intent(HomeActivity.this, PomodoroTimer.class));
                    }
                }
        );

        Button HomeScheduleButton = findViewById(R.id.HomeScheduleButton);
        HomeScheduleButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        startActivity(new Intent(HomeActivity.this, Schedule.class));
                    }
                }
        );

        Button HomeFlashcardButton = findViewById(R.id.HomeFlashCardButton);
        HomeFlashcardButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        //startActivity(new Intent(HomeActivity.this, Schedule.class));
                    }
                }
        );

        Button HomePointsButton = findViewById(R.id.HomePointsButton);
        HomePointsButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        //startActivity(new Intent(HomeActivity.this, Schedule.class));
                    }
                }
        );

    }


}

//pomodoro points increase exponentially per pomodoro
//