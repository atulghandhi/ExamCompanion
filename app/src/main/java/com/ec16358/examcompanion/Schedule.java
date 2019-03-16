package com.ec16358.examcompanion;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Schedule extends AppCompatActivity {
    //method to make sure back button takes user back to home page
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent intent = new Intent(Schedule.this, Home.class);
            startActivity(intent);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Access eventList from database, then add events added to event list
        final ArrayList<EventObject> list = new DBHandler(this, null, null, 1).getDbEventList();

        final DBHandler dbHandler = new DBHandler(this, null, null, 1);

        //add custom adapter, apply custom adapter to listView created in xml.
        final CustomAdapter eventsArrayListAdapter = new CustomAdapter(this, list);
        final ListView eventsListView = findViewById(R.id.EventsListView);
        eventsListView.setAdapter(eventsArrayListAdapter);

        /*for(int i = 0; i<list.size(); i++) {
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
                    eventsArrayListAdapter.notifyDataSetChanged();
                    Intent intent = new Intent(Schedule.this, Schedule.class);
                    startActivity(intent);
                }
            } catch (ParseException e) {
                //handle the exceptions
            }
        }*/

        //create dialog to show up when each event item is clicked.
        eventsListView.setOnItemClickListener((parent, view, position, id) -> {
            final int pos = position;
            EventObject e1 = list.get(position);

            //get correctly formatted date reference
            String dateString="";
            try {
                DateFormat format1 = new SimpleDateFormat("MM/dd/yy");
                Date date1 = format1.parse(e1.get_eventdate());
                DateFormat format2 = new SimpleDateFormat("MMM dd, yyyy");
                dateString = format2.format(date1);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(Schedule.this);
            View view1 = getLayoutInflater().inflate(R.layout.event_dialog, null);
            builder.setTitle(e1.get_eventname());
            TextView t1 = view1.findViewById(R.id.dialogTitleType);
            t1.setText(e1.get_eventtype());
            TextView t2 = view1.findViewById(R.id.dialogLocation);
            t2.setText(e1.get_eventlocation());
            TextView t3 = view1.findViewById(R.id.dialogDateTime);
            t3.setText("On  " + dateString + "  from  " + e1.get_eventstarttime() + "  to  " + e1.get_eventendtime());
            TextView t4 = view1.findViewById(R.id.dialogRepeat);
            t4.setText("Repeat:  " + e1.get_eventrepeat());
            TextView t5 = view1.findViewById(R.id.dialogNotes);
            t5.setText(e1.get_eventnotes());

            builder.setPositiveButton("           OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EventObject e1 = list.get(pos);
                    dbHandler.deleteEvent(e1.get_eventid());
                    Intent intent = new Intent(Schedule.this, Schedule.class);
                    startActivity(intent);
                }
            });

            builder.setView(view1);
            AlertDialog dialog = builder.create();

            String eventColour = e1.get_eventcolour();
            switch (eventColour) {
                case "blue":
                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.event_row_blue);
                    break;
                case "cyan":
                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.event_row_cyan);
                    break;
                case "green":
                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.event_row_green);
                    break;
                case "yellow":
                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.event_row_yellow);
                    break;
                case "orange":
                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.event_row_orange);
                    break;
                case "red":
                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.event_row_red);
                    break;
                case "pink":
                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.event_row_pink);
                    break;
                case "purple":
                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.event_row_purple);
                    break;
            }

            dialog.show();
        });

        //add events floating button
        FloatingActionButton fab = findViewById(R.id.fab_schedule);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(Schedule.this, CreateEvent.class);
            startActivity(intent);
        });

    }



}
