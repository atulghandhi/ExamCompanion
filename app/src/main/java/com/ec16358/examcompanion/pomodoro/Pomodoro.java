package com.ec16358.examcompanion.pomodoro;
import com.ec16358.examcompanion.Home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.CountDownTimer;
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

import com.ec16358.examcompanion.Home;
import com.ec16358.examcompanion.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/*
* This Class builds a Pomodoro timer, which iterates between 25 minute work, 5 minute short break and
* 15 minute long break sessions. These intervals can be changed in PomodoroSettings activity accessed
* via a button in this activity.
*
* Each pomodoro can have a module, target, summary and success - and each completed pomodoro is saved to the
* firebase database. History and Modules of Pomodoro's can be accessed via buttons here.
*
* A Pomodoro iteration can be skipped (move to next break or work) or reset (restart current iteration)
* A skipped work pomodoro is void and not saved to history.
*
* The following tutorial was used to aid in the creation of a simple fixed-time timer. This was then
* heavily modified into a Pomodoro timer.
* https://www.youtube.com/watch?v=MDuGwI6P-X8&list=PLrnPJCHvNZuB8wxqXCwKw2_NkyEmFwcSd&index=1
* */

public class Pomodoro extends AppCompatActivity {

    //get reference to userId to access user's storage in database
    String userID = Home.getCurrentUser().getUserId();


    //timer length; default is 25 minutes - can be changed in settings
    private static long START_TIME_IN_MILLIS = 25*60*1000; //minutes * seconds * 1000

    //store the system time when the timer will end (timer more accurate using endTime-currentTime to calculate timeLeft)
    private long endTime;

    //Initialise timer iteration lengths with default start times
    private static long START_TIME_POMODORO = 25*60*1000;
    private static long START_TIME_SHORT_BREAK = 5*60*1000;
    private static long START_TIME_LONG_BREAK = 15*60*1000;

    //setters for length of timer iterations, will allow PomodoroSettings to change timer length
    public static void setStartTimePomodoro(long startTimePomodoro) {
        START_TIME_POMODORO = startTimePomodoro;
    }
    public static void setStartTimeShortBreak(long startTimeShortBreak) {
        START_TIME_SHORT_BREAK = startTimeShortBreak;
    }
    public static void setStartTimeLongBreak(long startTimeLongBreak) {
        START_TIME_LONG_BREAK = startTimeLongBreak;
    }

    //int to count which break iteration the timer is currently on; incremented at each break
    int breakCount;

    //keep count of points for current pomodoro
    int pointsFromDialog = 0;

    //boolean for current iteration type; true means break, false means work
    boolean breakOrWork;

    //create textViews and buttons used in layout
    private TextView timerCountdownTextview;
    private TextView pomodoroCount;
    private Button startPauseButton;
    private Button skipTimerButton; //will skip remaining time in current iteration and move on
    private Button modulesButton;
    private Button historyButton;
    private Button resetPomodoroButton; //will restart timer without changing timer iteration (breakOrWork)
    private ProgressBar progressBar;
    private Spinner moduleSpinner;
    CountDownTimer countDownTimer;

    //boolean to check if timer is currently running
    private boolean isTimerRunning;

    //variable to hold time remaining in timer - initialised in onStart method
    private long timeLeftInMillis;

    private FirebaseDatabase firebaseDatabase;

    //database reference to read modules from database and place them in modules spinner
    private DatabaseReference modulesDatabaseReference;

    //database reference to add pomodoro instances to database
    private DatabaseReference pomodoroDatabaseReference;

    //PomodoroInstance object will hold all pomodoro information to be sent to database
    PomodoroInstance pomodoroInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialise layout widgets
        timerCountdownTextview = findViewById(R.id.idPomodoroTimerCount);
        pomodoroCount = findViewById(R.id.idPomodorosCount);
        startPauseButton = findViewById(R.id.idPomodoroStartPauseButton);
        skipTimerButton = findViewById(R.id.idPomodoroResetButton);
        resetPomodoroButton = findViewById(R.id.idPomodoro_ResetButton);
        modulesButton = findViewById(R.id.idPomodoroModulesButton);
        historyButton = findViewById(R.id.idPomodoroHistoryButton);
        progressBar = findViewById(R.id.idPomodoroProgressBar);
        moduleSpinner = findViewById(R.id.idCurrentModuleSpinner);

        //add modules to spinner from database
        addItemsToModuleSpinner();

        //bind buttons to onClickListeners
        startPauseButton.setOnClickListener(v -> startPauseButtonClicked());
        skipTimerButton.setOnClickListener(v -> resetTimerButtonClicked());
        resetPomodoroButton.setOnClickListener(v -> resetPomodoroButtonClicked());
        modulesButton.setOnClickListener(v -> startActivity(new Intent(Pomodoro.this, Modules.class)));
        historyButton.setOnClickListener(v -> startActivity(new Intent(Pomodoro.this, PomodoroHistory.class)));

        //set timer start time - initialise breakCount and breakOrWork
        //setStartTime();
        breakOrWork = false;
        breakCount = 1;

        //set progress bar to full
        progressBar.setProgress(100);
    }

    //called before activity is destroyed
    @Override
    protected void onStop() {
        super.onStop();
        //save values when activity is stopped - so that timer is not reset when closed
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("timeLeft", timeLeftInMillis);
        editor.putBoolean("timerRunning", isTimerRunning);
        editor.putLong("systemEndTime", endTime);
        editor.putBoolean("isBreak", breakOrWork);

        if(!breakOrWork){
            //if pomodoro iteration active, save values of PomodoroInstance
            editor.putString("pomodoroTarget", pomodoroInstance.getTarget());
            editor.putString("pomodoroStart", pomodoroInstance.getStartDateTime());
            editor.putInt("pomodoroLength", pomodoroInstance.getLength());
            editor.putInt("breakNumber", breakCount);
            editor.putInt("points", pointsFromDialog);
        }
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //restore any saved values when activity reopens
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        //the default value for timeLeft if no value stored is START_TIME_IN_MILLIS
        timeLeftInMillis = prefs.getLong("timeLeft", START_TIME_IN_MILLIS);
        //the default for isTimerRunning is false
        isTimerRunning = prefs.getBoolean("timerRunning", false);

        pointsFromDialog = prefs.getInt("points", 0);
        breakOrWork = prefs.getBoolean("isBreak", false);

        //update countdownText and buttons after values restored or initialised
        updateCountdownText();
        updateButtons();

        if(!breakOrWork){
            //if timer running and not break, restore these values
            breakCount = prefs.getInt("breakNumber",1);
            pomodoroInstance = new PomodoroInstance();
            pomodoroInstance.setStartDateTime(prefs.getString("pomodoroStart", ""));
            pomodoroInstance.setTarget(prefs.getString("pomodoroTarget", ""));
            pomodoroInstance.setLength(prefs.getInt("pomodoroLength", 25));
        }

        //if timer is running when activity reopens
        if(isTimerRunning){
            //restore system endTime of timer and use to reset timeLeft
            endTime = prefs.getLong("systemEndTime", 0); //get saved endTime
            timeLeftInMillis = endTime - System.currentTimeMillis(); //set time left using endTime

            //find out if endTime is already passed (giving negative timeLeft)
            if(timeLeftInMillis<0){
                //if so, set timer running to false (as timer as already run its course) and timeLeft as 0
                timeLeftInMillis = 0;
                isTimerRunning = false;
                resetTimer(); //calls updateCountDownText and updateButtons
            } else {
                //if endTime is not yet passed, meaning timeLeft is positive - start timer from timeLeft.
                startTimer();
            }
            updateButtons();
        } else {
            //If timer isn't running then set the timer start time
            setStartTime();
        }

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
                //if break, pomodoro count should say work next
                String text = "Next: Pomodoro work (" + START_TIME_POMODORO/60000  + " mins)" ;
                pomodoroCount.setText(text);
            } else {
                //every other break should be short
                START_TIME_IN_MILLIS = START_TIME_SHORT_BREAK;
                //if break, pomodoro count should say work next
                String text = "Next: Pomodoro work (" + START_TIME_POMODORO/60000 + " mins)" ;
                pomodoroCount.setText(text);
            }
            //increase breakCount value
            breakCount++;
        } else {
            //if not break then set start time to pomodoro length
            START_TIME_IN_MILLIS = START_TIME_POMODORO;
            //if work, pomodoro count should say is short break or long break next
            String text;
            if(((breakCount-1)%4) == 3){
                text = "Next: Long break (" + START_TIME_LONG_BREAK/60000 + "mins)" ;
            } else {
                text = "Next: Short break (" + START_TIME_SHORT_BREAK/60000 + "mins)" ;
            }
            pomodoroCount.setText(text);
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
                    pointsFromDialog = 0;
                    enterPomodoroTargetDialog();
                } else {
                    startTimer();
                }
            } else {
                startTimer();
            }
        }
    }

    //skip current timer (break or work) and move on to next step. Voids Pomodoro
    private void resetTimerButtonClicked(){
        if(isTimerRunning){
            pauseTimer();
        }
        resetTimer();
    }

    //Reset current timer without changing breakOrWork
    private void resetPomodoroButtonClicked(){
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
        if(moduleSpinner.getCount() == 0 && timeLeftInMillis == START_TIME_IN_MILLIS) {
            Toast.makeText(this, "Add a module in the modules page, then select it by clicking the text next to 'Current Module'", Toast.LENGTH_LONG).show();
            return;
        }

        //what to do if Non-Break pomodoro starting from beginning
        if(!breakOrWork && timeLeftInMillis == START_TIME_IN_MILLIS) {
            //remind user to select correct module
            Toast.makeText(this, "Make sure you've selected the correct module name", Toast.LENGTH_SHORT).show();
            //add pomodoro length, in minutes, to instance
            int length = (int) START_TIME_IN_MILLIS/60000;
            //set length and start time of pomodoro instance as it begins
            pomodoroInstance.setLength(length);
            pomodoroInstance.setStartDateTime(getCurrentDateTime());
        }

        //set system time in millis when timer will end
        endTime = System.currentTimeMillis()+timeLeftInMillis;

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
                if(!breakOrWork){
                    //request pomodoroSummary if work pomodoro
                    enterPomodoroSummaryDialog();
                }
            }
        }.start();
        //timer is started - set boolean variable to true.
        isTimerRunning = true;
        //update buttons
        updateButtons();
        updateCountdownText();
    }

    private void pauseTimer(){
        //what to do what timer pause button is clicked.
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isTimerRunning = false;
        //update buttons
        updateButtons();
    }

    //goes to next iteration of timer
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

        if(!breakOrWork) {
            //if beginning of work pomodoro ask for Pomodoro target and initialise pomodoroInstance object
            pomodoroInstance = new PomodoroInstance();
            pointsFromDialog = 0;
            enterPomodoroTargetDialog();
        } else {
            startTimer();
        }
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
            if(target.trim().equals("")){
                Toast.makeText(this, "Enter a target for your Pomodoro or click 'skip'", Toast.LENGTH_SHORT).show();
                enterPomodoroTargetDialog();
            } else {
                pomodoroInstance.setTarget(target); //set 'target' text in pomodoro instance
                pointsFromDialog++;
                startTimer();
            }
        });

        //set delete button to remove module from database
        builder.setNegativeButton("Skip", (dialog, which) -> {
            pomodoroInstance.setTarget("");
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
        if(moduleSpinner.getSelectedItem() == null){
            return;
        }
        pomodoroInstance.setModule(moduleSpinner.getSelectedItem().toString());
        //set "OK" button to dismiss dialog
        builder.setPositiveButton("           Save", (dialog, which) -> {
            pomodoroInstance.setSuccess(c1.isChecked()); //set success of pomodoro. true = target achieved
            String summary = t1.getText().toString().trim();
            if(summary.trim().equals("")){
                Toast.makeText(this, "Enter a Summary for your Pomodoro or click 'skip'", Toast.LENGTH_SHORT).show();
                enterPomodoroSummaryDialog();
            }
            pomodoroInstance.setSummary(summary);
            pointsFromDialog++;
            saveInstanceToDatabase(); //calls resetTimer
        });

        //set delete button to remove module from database
        builder.setNegativeButton("Skip", (dialog, which) -> {
            pomodoroInstance.setSuccess(c1.isChecked()); //set success of pomodoro. true = target achieved
            pomodoroInstance.setSummary("");
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

        DatabaseReference pointsDatabaseReference = firebaseDatabase.getReference().child("users").child(userID).child("points");
        pointsDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            int pointsEarned = pointsFromDialog + pomodoroInstance.getLength()/5;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    pointsDatabaseReference.setValue(dataSnapshot.getValue(Integer.class) + pointsEarned);
                } else {
                    pointsDatabaseReference.setValue(pointsEarned);
                }

                Toast.makeText(Pomodoro.this, "You earned " + pointsEarned + " points", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        resetTimer();
    }

    public String getCurrentDateTime() {
        //Return date time of the moment this code is run - used to set start date time of PomodoroInstance
        //get format of date time
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MM/dd/yy HH:mm", Locale.ENGLISH);
        //get current date-time and format as above
        Date nowTime = Calendar.getInstance().getTime();
        return dateTimeFormat.format(nowTime);
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
