package com.ec16358.examcompanion;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/*
* THIS CLASS WAS NOT WRITTEN BY ME
*
* SOURCE: https://stackoverflow.com/questions/21475498/timepicker-displaying-time-incorrectly
*
* PURPOSE: used to display date picker dialog for modules and events so user can select date for those events.
*
* */

public class TimeListener implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {

    private int hour;
    private int minute;
    private Activity activity;
    private View touchedView;

    public TimeListener(Activity activity) {
        this.activity = activity;
        final Calendar c = Calendar.getInstance();
        this.hour = c.get(Calendar.HOUR_OF_DAY);
        this.minute = c.get(Calendar.MINUTE);
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }


    @Override
    public void onClick(View v) {
        touchedView = v;

        new TimePickerDialog(activity,
                this, this.getHour(), this.getMinute(), false).show();
    }

    private void updateDisplay() {
        ((TextView) touchedView).setText(
                new StringBuilder()
                        .append(pad(hour)).append(":")
                        .append(pad(minute)));
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.hour = hourOfDay;
        this.minute = minute;
        updateDisplay();
    }
}