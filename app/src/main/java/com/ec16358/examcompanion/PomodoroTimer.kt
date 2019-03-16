package com.ec16358.examcompanion
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.KeyEvent
//import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_pomodoro_timer.*
//import android.R
import android.view.Menu
import android.view.MenuItem
import com.ec16358.examcompanion.util.PrefUtil
import kotlinx.android.synthetic.main.content_pomodoro_timer.*
import java.util.*
import kotlin.system.exitProcess


class PomodoroTimer : AppCompatActivity() {

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val intent = Intent(this@PomodoroTimer, Home::class.java)
            startActivity(intent)
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    enum class TimerState{
        Stopped, Paused, Running
    }

    private lateinit var timer: CountDownTimer
    private var timerLengthSeconds = 0L //amount of time timer is on for
    private var timerState = TimerState.Stopped //state of timer stopped/paused etc

    private var secondsRemaining = 0L //seconds remaining in timer (L means long)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pomodoro_timer)
        setSupportActionBar(toolbar)
        supportActionBar?.setIcon(R.drawable.ic_timer)  //adding timer icon to top left of timer page
        supportActionBar?.title = "   Pomodoro Timer"  //adding title next to icon

        fab_play.setOnClickListener { v ->
            startTimer()
            timerState=TimerState.Running
            updateButtons()
        }
        fab_pause.setOnClickListener { v ->
            timer.cancel()
            timerState=TimerState.Paused
            updateButtons()
        }
        fab_stop.setOnClickListener { v ->
            timer.cancel()
            onTimerFinished()
        }
    }


    override fun onResume() {
        super.onResume()

        initTimer()   //initialise timer
        //TODO: remove background timer, hide notification
    }

    override fun onPause() {
        super.onPause()

        if(timerState == TimerState.Running){
            timer.cancel()
            //TODO: start background timer; notification
        }
        else if(timerState == TimerState.Paused){
            //TODO: show notification
        }

        //save state if activity is taken off screen
        PrefUtil.setPreviousTimerLengthSeconds(timerLengthSeconds, this)
        PrefUtil.setSecondsRemaining( secondsRemaining, this)
        PrefUtil.setTimerState(timerState, this)
    }

    //create function to initialise timer; used in onResume above
    private fun initTimer(){
        timerState = PrefUtil.getTimerState(this)

        if(timerState == TimerState.Stopped) {
            //timer length will change to new timer length if paused
            setNewTimerLength()
        } else {
            //timer length if running or paused should not change
            setPreviousTimerLength()
        }

        //set seconds remaining to previous seconds remaining if timer was running or paused or set
        //to a new value if timer was stopped
        secondsRemaining = if (timerState == TimerState.Running || timerState == TimerState.Paused){
            PrefUtil.getSecondsRemaining(this)
        } else {
            timerLengthSeconds
        }

        //TODO: change seconds remaining according to where background timer left off.

        //resume timer where it left off
        if (timerState == TimerState.Running){
            startTimer()
        }
        updateButtons() //play pause stop buttons
        updateCountdownUI() //this is the time text view and progress bar

    }

    private fun onTimerFinished(){
        timerState = TimerState.Stopped
        setNewTimerLength()//reset timer length
        progress_countdown.progress = 0 //reset progressbar to nought
        PrefUtil.setSecondsRemaining(timerLengthSeconds, this)
        secondsRemaining = timerLengthSeconds

        updateButtons()
        updateCountdownUI()
    }

    private fun startTimer(){
        timerState = TimerState.Running //change timer state to running

        //need to multiply the following by 1000 as its in milliseconds
        timer = object : CountDownTimer(secondsRemaining*1000, 1000){
            override fun onFinish() = onTimerFinished()
            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished/1000
                updateCountdownUI()
            }
        }.start() //start the timer
    }

    private fun setNewTimerLength(){
        val lengthInMinutes = PrefUtil.getTimerLength(this)
        timerLengthSeconds = lengthInMinutes*60L //seconds = minutes*60
        progress_countdown.max = timerLengthSeconds.toInt() //progress bar progress related to time remaining
    }

    private fun setPreviousTimerLength(){
        timerLengthSeconds = PrefUtil.getPreviousTimerLengthSeconds(this)
        progress_countdown.max = timerLengthSeconds.toInt()
    }

    private fun updateCountdownUI(){
        val minutesUntilFinished = secondsRemaining/60
        val secondsInMinuteUntilFinished = secondsRemaining - minutesUntilFinished*60
        val secondsStr = secondsInMinuteUntilFinished.toString()
        textView_countdown.text = "$minutesUntilFinished:${
        if (secondsStr.length == 2) secondsStr
        else "0"+secondsStr}"
        progress_countdown.progress = (timerLengthSeconds-secondsRemaining).toInt()
    }

    private fun updateButtons(){
        when(timerState){ //when is java equivalent of switch statement
            TimerState.Running ->{
                fab_play.isEnabled = false
                fab_pause.isEnabled = true
                fab_stop.isEnabled = true
            }
            TimerState.Stopped ->{
                fab_play.isEnabled = true
                fab_pause.isEnabled = false
                fab_stop.isEnabled = false
            }
            TimerState.Paused ->{
                fab_play.isEnabled = true
                fab_pause.isEnabled = false
                fab_stop.isEnabled = true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //inflating menu adds items to action bar if menu is present
        menuInflater.inflate(R.menu.menu_pomodoro_timer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //handles action bar item clicks

        return true
    }

}
