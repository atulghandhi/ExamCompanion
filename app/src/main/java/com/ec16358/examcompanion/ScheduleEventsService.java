/*package com.ec16358.examcompanion;

import android.app.IntentService;
import android.content.Intent;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.Nullable;

//this works but seems to take longer than just putting the method in schedule.

public class ScheduleEventsService extends IntentService{

    //ArrayList<EventObject> list;


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //Access eventList from database, then add events added to event list
        ArrayList<EventObject> list = new DBHandler(this, null, null, 1).getDbEventList();
        DBHandler dbHandler = new DBHandler(this, null, null, 1);

        for(int i = 0; i<list.size(); i++) {
            //check if event is after current. If so, delete event
            //get date-time of event by merging date and start time values
            String eventDateTime = list.get(i).get_eventdate() + " " + list.get(i).get_eventendtime();
            //get format to parse to
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm", Locale.ENGLISH);
            //get current date-time
            Date nowTime = Calendar.getInstance().getTime();
            String dateTimeNow = dateFormat.format(nowTime);
            try {
                //if the following returns true, the events end time is before 'now'
                if (dateFormat.parse(eventDateTime).before(dateFormat.parse(dateTimeNow))) {
                    dbHandler.deleteEvent(list.get(i).get_eventid());
                }
            } catch (ParseException e) {
                //handle the exceptions
            }
        }
    }

    public ScheduleEventsService() {
        super("ScheduleEventsService");
        //list = e;
    }
}
*/