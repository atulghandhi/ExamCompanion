package com.ec16358.examcompanion.util

import android.content.Context
//import android.content.Intent
import android.preference.PreferenceManager
//import android.provider.Settings
//import androidx.core.content.ContextCompat.startActivity
import com.ec16358.examcompanion.PomodoroTimer

//preferences are key value pairs saved to the disk that allow variables to be
//saved even when the app is killed
class PrefUtil {
    companion object {
        /*NotificationManager mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        // Check if the notification policy access has been granted for the app.
        if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            startActivity(intent)
        }*/
        //companion object hold the java equivalent of static members
        //function to get timer length in minute

        fun getTimerLength(context: Context): Int{
            //timer length in minutes
            return 25
        }

        private const val PREVIOUS_TIMER_LENGTH_SECONDS_ID = "com.ec16358.timer.previous_timer_length"

        //If timer length is changed while timer is running, this will keep current running timer
        //unchanged by storing previous timer lengths.
        fun getPreviousTimerLengthSeconds(context: Context): Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, 0)
        }

        fun setPreviousTimerLengthSeconds(seconds: Long, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, seconds)
            editor.apply()
        }

        private const val TIMER_STATE_ID = "com.ec16358.timer.timer_state"

        fun getTimerState(context: Context): PomodoroTimer.TimerState{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val ordinal = preferences.getInt(TIMER_STATE_ID, 0)
            return PomodoroTimer.TimerState.values()[ordinal]
        }

        fun setTimerState(state: PomodoroTimer.TimerState, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            val ordinal = state.ordinal
            editor.putInt(TIMER_STATE_ID, ordinal)
            editor.apply()
        }

        private const val SECONDS_REMAINING_ID = "com.ec16358.timer.previous_timer_length"

        //If timer length is changed while timer is running, this will keep current running timer
        //unchanged by storing previous timer lengths.
        fun getSecondsRemaining(context: Context): Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(SECONDS_REMAINING_ID, 0)
        }

        fun setSecondsRemaining(seconds: Long, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(SECONDS_REMAINING_ID, seconds)
            editor.apply()
        }
    }
}