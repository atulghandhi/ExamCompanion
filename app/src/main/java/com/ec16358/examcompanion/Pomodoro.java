package com.ec16358.examcompanion;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.media.MediaPlayer;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Pomodoro extends AppCompatActivity {
    //get reference to userId to access user's storage in database
    String userID = Home.getCurrentUser().getUserId();

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
    //initialise textViews and buttons
    private TextView timerCountdownTextview;
    private TextView pomodoroCount;
    private Button startPauseButton;
    private Button resetTimerButton; //will skip remaining pomodoro (or break)
    private Button modulesButton;
    private Button historyButton;
    private Button resetPomodoroButton; //will restart current pomodoro (or break)
    private ProgressBar progressBar;
    private Spinner moduleSpinner;

    //create countDownTimer
    CountDownTimer countDownTimer;

    //variable to check if timer is running
    private boolean isTimerRunning;

    //variable to hold time remaining in timer - initial value equal to start_time in onStart method
    private long timeLeftInMillis = START_TIME_IN_MILLIS;

    //get reference to fireBase database and reference and eventListener
    private FirebaseDatabase firebaseDatabase;
    //reference to read modules from database and place them in modules spinner
    private DatabaseReference modulesDatabaseReference;
    //reference to add pomodoro instances to database
    private DatabaseReference pomodoroDatabaseReference;
    //PomodoroInstance object will hold all pomodoro information to be sent to database
    PomodoroInstance pomodoroInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialise textView and buttons and progressbar in layout
        timerCountdownTextview = findViewById(R.id.idPomodoroTimerCount);
        pomodoroCount = findViewById(R.id.idPomodorosCount);
        startPauseButton = findViewById(R.id.idPomodoroStartPauseButton);
        resetTimerButton = findViewById(R.id.idPomodoroResetButton);
        resetPomodoroButton = findViewById(R.id.idPomodoro_ResetButton);
        modulesButton = findViewById(R.id.idPomodoroModulesButton);
        historyButton = findViewById(R.id.idPomodoroHistoryButton);
        progressBar = findViewById(R.id.idPomodoroProgressBar);
        moduleSpinner = findViewById(R.id.idCurrentModuleSpinner);
        //add module values to spinner
        addItemsToModuleSpinner();
        //bind buttons to onClickListeners -> call separate methods when buttons clicked.
        startPauseButton.setOnClickListener(v -> startPauseButtonClicked());
        resetTimerButton.setOnClickListener(v -> resetTimerButtonClicked());
        resetPomodoroButton.setOnClickListener(v -> resetPomodoroButtonClicked());
        modulesButton.setOnClickListener(v -> startActivity(new Intent(Pomodoro.this, Modules.class)));
        historyButton.setOnClickListener(v -> startActivity(new Intent(Pomodoro.this, PomodoroHistory.class)));
        //set timer start time - initialise breakCount and breakOrWork
        breakOrWork = false;
        breakCount = 1;
        setStartTime();
        //update buttons and countdown text - set progress bar to full
        updateCountdownText();
        updateButtons();
        progressBar.setProgress(100);
    }

    public void addItemsToModuleSpinner(){
        //use fireBase database reference to access modules
        firebaseDatabase = FirebaseDatabase.getInstance();
        modulesDatabaseReference = firebaseDatabase.getReference().child(userID).child("modules");

        //Get dataSnapShot at the "module" root node
        modulesDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //show modules in spinner

                //Create simple spinner adapter and fill spinner with items from modules
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(Pomodoro.this, android.R.layout.simple_spinner_item, android.R.id.text1);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                moduleSpinner.setAdapter(spinnerAdapter);
                //read list of modules from database, add modules to spinner
                //create enhanced for loop to read module instances in data snapshot and deserialize moduleObjects
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ModuleObject m = ds.getValue(ModuleObject.class);
                    //add module name from each module object to spinner
                    spinnerAdapter.add(m.getModuleName());
                    spinnerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //code
            }
        });
    }

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
            if(timeLeftInMillis == START_TIME_IN_MILLIS) {
                if(!breakOrWork) {
                    //if beginning of work pomodoro ask for Pomodoro target and initialise pomodoroInstance object
                    pomodoroInstance = new PomodoroInstance();
                    enterPomodoroTargetDialog();
                } else {
                    startTimer();
                }
            } else {
                startTimer();
            }
        }
    }

    private void resetTimerButtonClicked(){
        //reset timer when clicked.
        if(isTimerRunning){
            pauseTimer();
        }
        resetTimer();
    }

    private void resetPomodoroButtonClicked(){
        //Reset current timer without changing breakOrWork
        //pause timer, reset timeLeft to startTime, update buttons, countDownText and progressBar
        if(isTimerRunning){
            pauseTimer();
        }
        timeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountdownText();
        updateButtons();
        progressBar.setProgress(100);
    }

    private void startTimer(){

        //if pomodoro starting and module not selected - tell user to add module
        if(moduleSpinner.getCount() == 0) {
            Toast.makeText(this, "Add a module in the modules page, then select it by clicking the text next to 'Current Module'", Toast.LENGTH_LONG).show();
            return;
        }

        //what to do if Non-Break pomodoro starting from beginning
        if(!breakOrWork && timeLeftInMillis == START_TIME_IN_MILLIS) {
            //remind user to select correct module
            Toast.makeText(this, "Make sure you've selected the correct module name", Toast.LENGTH_LONG).show();
            //add pomodoro length, in minutes, to instance
            int length = (int) START_TIME_IN_MILLIS/60000;
            pomodoroInstance.setLength(length);
        }

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
                //request pomodoroSummary
                enterPomodoroSummaryDialog();
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

    public void enterPomodoroTargetDialog(){
        //build alert dialog using xml layout
        AlertDialog.Builder builder = new AlertDialog.Builder(Pomodoro.this);
        View view1 = getLayoutInflater().inflate(R.layout.pomodoro_target_dialog, null);
        //set dialog title as name of module
        builder.setTitle("Pomodoro Target");
        //get ref to textView in dialog layout and show date of module
        EditText t1 = view1.findViewById(R.id.pomodoro_target_editText);

        //set "OK" button to dismiss dialog
        builder.setPositiveButton("           Save", (dialog, which) -> {
            String target = t1.getText().toString().trim();
            if(target.equals("")){
                Toast.makeText(this, "Enter a target for your Pomodoro or click 'skip'", Toast.LENGTH_SHORT).show();
                enterPomodoroTargetDialog();
            } else {
                pomodoroInstance.setTarget(target); //set 'target' text in pomodoro instance
                startTimer();
            }
        });

        //set delete button to remove module from database
        builder.setNegativeButton("Skip", (dialog, which) -> {
            startTimer();
            dialog.dismiss();
        });

        builder.setView(view1);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void enterPomodoroSummaryDialog(){
        //build alert dialog using xml layout
        AlertDialog.Builder builder = new AlertDialog.Builder(Pomodoro.this);
        View view1 = getLayoutInflater().inflate(R.layout.pomodoro_summary_dialog, null);
        //set dialog title as name of module
        builder.setTitle("Pomodoro summary");
        //get ref to textView in dialog layout and show date of module
        EditText t1 = view1.findViewById(R.id.pomodoro_summary_edittext);
        CheckBox c1 = view1.findViewById(R.id.pomodoro_target_achieved_checkbox);
        boolean checked = c1.isChecked();
        pomodoroInstance.setSuccess(checked);
        pomodoroInstance.setModule(moduleSpinner.getSelectedItem().toString());

        //set "OK" button to dismiss dialog
        builder.setPositiveButton("           Save", (dialog, which) -> {
            String summary = t1.getText().toString().trim();
            if(summary.equals("")){
                Toast.makeText(this, "Enter a Summary for your Pomodoro or click 'skip'", Toast.LENGTH_SHORT).show();
                enterPomodoroSummaryDialog();
            }
            pomodoroInstance.setSummary(summary);
            saveInstanceToDatabase();
        });

        //set delete button to remove module from database
        builder.setNegativeButton("Skip", (dialog, which) -> {
            saveInstanceToDatabase();
            dialog.dismiss();
        });

        builder.setView(view1);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void saveInstanceToDatabase(){
        //save pomodoro instance to database
        pomodoroDatabaseReference = firebaseDatabase.getReference().child(userID).child("pomodoros");
        String pomodoroId = pomodoroDatabaseReference.push().getKey();
        pomodoroInstance.setId(pomodoroId);
        assert pomodoroId != null;
        pomodoroDatabaseReference.child(pomodoroId).setValue(pomodoroInstance);
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

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
