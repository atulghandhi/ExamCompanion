package com.ec16358.examcompanion;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.Button;
import java.util.ArrayList;

public class Home extends AppCompatActivity {
    /*
    * To make sure schedule page doesn't lag when opening, we load the arraylist it needs from database
    * here in another thread and pass it to other classes with a better method. As the list has to be
    * a static variable, and DBHandler needs non-static context, we will use another method to run the
    * second thread.
    * */
    private static ArrayList<EventObject> list;

    public void loadArrayList(){
        Runnable r = new Runnable() {
            @Override
            public void run() {
                synchronized (this){
                    list = new DBHandler(Home.this, null, null, 1).getDbEventList();
                }
            }
        };
        Thread thread = new Thread(r);
        thread.start();
    }

    public static ArrayList<EventObject> getList(){
        return list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadArrayList();

        setTitle("Home");

        Button HomePomodoroButton = findViewById(R.id.idHomePomodoroButton);
        HomePomodoroButton.setOnClickListener(
                v -> startActivity(new Intent(Home.this, PomodoroTimer.class))
        );

        Button HomeScheduleButton = findViewById(R.id.idHomeScheduleButton);
        HomeScheduleButton.setOnClickListener(
                v -> startActivity(new Intent(Home.this, Schedule.class))
        );

        Button HomeFlashcardButton = findViewById(R.id.idHomeFlashcardsButton);
        HomeFlashcardButton.setOnClickListener(
                v -> {
                    //startActivity(new Intent(HomeActivity.this, FlashCards.class));
                }
        );

        Button HomePointsButton = findViewById(R.id.idHomePointsButton);
        HomePointsButton.setOnClickListener(
                v -> {
                    //startActivity(new Intent(HomeActivity.this, Points.class));
                }
        );

    }

}
