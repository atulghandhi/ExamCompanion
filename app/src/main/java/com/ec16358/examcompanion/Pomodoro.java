package com.ec16358.examcompanion;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.media.MediaPlayer;
import java.util.Locale;

public class Pomodoro extends AppCompatActivity {
    //create variable to hold timer length required
    private static long START_TIME_IN_MILLIS = 25*60*1000; //minutes * seconds * 1000

    private static long START_TIME_POMODORO = 25*60*1000;
    private static long START_TIME_SHORT_BREAK = 5*60*1000;
    private static long START_TIME_LONG_BREAK = 15*60*1000;

    //setters for Pomodoro_Settings to change timer lengths
    public static void setStartTimePomodoro(long startTimePomodoro) {
        START_TIME_POMODORO = startTimePomodoro;
    }
    public static void setStartTimeShortBreak(long startTimeShortBreak) {
        START_TIME_SHORT_BREAK = startTimeShortBreak;
    }
    public static void setStartTimeLongBreak(long startTimeLongBreak) {
        START_TIME_LONG_BREAK = startTimeLongBreak;
    }

    //int to count which break iteration timer is currently on
    int breakCount;
    //false means work, true means break
    boolean breakOrWork;

    public void setStartTime(){
        if(breakOrWork){
            //if break, find out if long break or short break
            if(breakCount%4 == 0){
                //every 4th break should be long
                START_TIME_IN_MILLIS = START_TIME_LONG_BREAK;
                String text = "Pomodoro count: " + String.valueOf(breakCount+1);
                pomodoroCount.setText(text);
            } else {
                //every other break should be short
                START_TIME_IN_MILLIS = START_TIME_SHORT_BREAK;
                String text = "Pomodoro count: " + String.valueOf(breakCount+1);
                pomodoroCount.setText(text);
            }
            //increase breakCount value
            breakCount++;
        } else {
            //if not break then set start time to pomodoro length
            START_TIME_IN_MILLIS = START_TIME_POMODORO;
        }
    }

    private TextView timerCountdownTextview;
    private TextView currentModuleTextview;
    private TextView pomodoroCount;
    private Button startPauseButton;
    private Button resetTimerButton;
    private Button modulesButton;
    private Button historyButton;
    private ProgressBar progressBar;

    //create countDownTimer
    CountDownTimer countDownTimer;

    //variable to check if timer is running
    private boolean isTimerRunning;

    //variable to hold time remaining in timer - initial value equal to start_time in onStart method
    private long timeLeftInMillis = START_TIME_IN_MILLIS;

    //variable to hold the system time when timer is supposed to end.
    private long endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialise textView and buttons and progressbar in layout
        timerCountdownTextview = findViewById(R.id.idPomodoroTimerCount);
        currentModuleTextview = findViewById(R.id.idPomodoroCurrentModule);
        pomodoroCount = findViewById(R.id.idPomodorosCount);
        startPauseButton = findViewById(R.id.idPomodoroStartPauseButton);
        resetTimerButton = findViewById(R.id.idPomodoroResetButton);
        modulesButton = findViewById(R.id.idPomodoroModulesButton);
        historyButton = findViewById(R.id.idPomodoroHistoryButton);
        progressBar = findViewById(R.id.idPomodoroProgressBar);
        //bind buttons to onClickListeners -> call separate methods when buttons clicked.
        startPauseButton.setOnClickListener(v -> startPauseButtonClicked());
        resetTimerButton.setOnClickListener(v -> resetTimerButtonClicked());
        modulesButton.setOnClickListener(v -> startActivity(new Intent(Pomodoro.this, Modules.class)));
        historyButton.setOnClickListener(v -> startActivity(new Intent(Pomodoro.this, PomodoroHistory.class)));
        //set timer start time - initialise breakCount and breakOrWork
        breakOrWork = false;
        breakCount = 1;
        setStartTime();
        //update buttons and countdown text
        updateCountdownText();
        updateButtons();
        progressBar.setProgress(100);
    }


    private void updateCountdownText(){
        //minutes left
        int minutes = (int) timeLeftInMillis/1000/60;
        //seconds left (use modulus of minutes)
        int seconds = (int) timeLeftInMillis/1000%60;

        String time_left = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        //set textView text to time left.
        timerCountdownTextview.setText(time_left);
    }

    private void updateButtons(){
        //this method will update start/pause/reset buttons depending on the current state of the timer
        if(isTimerRunning){
            //what to do with buttons when timer is running

            //we want our start/pause button to say pause as timer is running
            startPauseButton.setText("Pause");
        } else {
            //what to do with buttons when timer isn't running

            //first we want our start/pause button to say start as timer isn't yet running (thus can't be paused)
            startPauseButton.setText("Start");

            if(timeLeftInMillis < 1000){
                //what to do if timer has finished (time left is less than 1 second) - make start button invisible until timer reset
                startPauseButton.setVisibility(View.INVISIBLE);
            } else {
                //after reset button clicked we want start/pause to be visible again
                startPauseButton.setVisibility(View.VISIBLE);
            }

            if(timeLeftInMillis < START_TIME_IN_MILLIS) {
                //if timer isn't running but is not yet finished (therefore paused)
            }
        }
    }

    private void startPauseButtonClicked(){
        if(isTimerRunning){
            //if timer is running when clicked; pause timer
            pauseTimer();
            startPauseButton.setText("Continue");
        } else {
            //if timer not running when clicked; start timer
            startTimer();
        }
    }

    private void resetTimerButtonClicked(){
        //reset timer when clicked.
        if(isTimerRunning){
            pauseTimer();
        }
        resetTimer();
    }

    private void startTimer(){
        //set endTime as current system time + timer length
        endTime = System.currentTimeMillis() + timeLeftInMillis;

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //method on what to do on each tick of the clock (tick=1000ms as defined above)
                //set timeLeft variable as milliseconds left until timer finished.
                timeLeftInMillis = millisUntilFinished;
                //update textView containing countdown text
                updateCountdownText();

                double progress = ((double)timeLeftInMillis/(double)START_TIME_IN_MILLIS)*100;

                progressBar.setProgress((int)progress);

            }

            @Override
            public void onFinish() {
                //what to do when timer is finished; first play timer ended sound
                final MediaPlayer timerEndSound = MediaPlayer.create(Pomodoro.this, R.raw.timer_ding);
                timerEndSound.start();
                //set timer running to false as timer is ended
                isTimerRunning = false;
                //update buttons
                updateButtons();
            }
        }.start();
        //timer is started - set boolean variable to true.
        isTimerRunning = true;
        //update buttons
        updateButtons();
    }

    private void pauseTimer(){
        //what to do what timer pause button is clicked.
        countDownTimer.cancel();
        isTimerRunning = false;
        //update buttons
        updateButtons();
    }

    private void resetTimer(){
        //reverse breakOrWork value
        breakOrWork = !breakOrWork;
        //set start time for next interval
        setStartTime();
        //what to do when reset timer button is clicked.
        timeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountdownText();
        //update buttons
        updateButtons();
        progressBar.setProgress(100);
    }

    //what to do when app is closed by system or user for any reason
    @Override
    protected void onStop() {
        super.onStop();
    /*
        //instantiate shared preferences and editor
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        //save values we want saved when app is suddenly
        editor.putLong("timeLeftInMillisKey", timeLeftInMillis );
        editor.putBoolean("isTimerRunningKey", isTimerRunning);
        editor.putLong("endTimeKey", endTime);
        editor.apply();
    */
    }

    //on start will be called after onCreate
    @Override
    protected void onStart() {
        super.onStart();
    /*
        //instantiate shared preferences
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        //get back values saved in onStop (if applicable).
        //if no saved values from onStop, add default values as second argument.
        timeLeftInMillis = prefs.getLong("timeLeftInMillisKey", START_TIME_IN_MILLIS);
        isTimerRunning = prefs.getBoolean("isTimerRunningKey", false);
        //update buttons and countdown text
        updateCountdownText();
        updateButtons();
        //if timer is running reset timeLeftInMillis with reference to endTime for accuracy
        if(isTimerRunning){
            endTime = prefs.getLong("endTimeKey", 0);
            timeLeftInMillis = endTime - System.currentTimeMillis();
            //if timer is already done then the above will make timeLeft negative - if so...
            if(timeLeftInMillis<0){
                //make timer back to 0, stop timer running, update buttons/textView
                timeLeftInMillis = 0;
                isTimerRunning = false;
                updateCountdownText();
                updateButtons();
            }
        } else {
            //if time left is above 0, and timer is running, we start timer
            startTimer();
        }
    */
    }

    //what to do when timer is running and user goes off app
    @Override
    protected void onPause() {
        super.onPause();
    /*
        if(isTimerRunning){
            //if timer is running when activity pauses then start background timer

        } else {
            //if timer is already paused you don't need to start background timer - just show notification

        }
    */
    }

    //what to do when timer is running and user comes back to app
    @Override
    protected void onResume() {
        super.onResume();
        //TODO: remove background timer started at onPause and hide active notification
    }

    //add menu to Pomodoro page toolbar (settings icon top right)
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_pomodoro, menu);
        return true;

    }

    //what to do when settings button clicked.
    public boolean onOptionsItemSelected(MenuItem item) {
        //respond to menu item selection
        startActivity(new Intent(this, PomodoroSettings.class));
        return true;
    }

    //method to make sure back button takes user back to home page
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent intent = new Intent(Pomodoro.this, Home.class);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
