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
import java.util.Objects;

public class Schedule extends AppCompatActivity {
    //access eventObjects arrayList from home activity.
    private ArrayList<EventObject> list;
    //get ref to listView that will show a list of events
    private ListView eventsListView;
    //get ref to database handler to access events list
    private DBHandler dbHandler;
    //create custom adapter
    private CustomAdapter eventsArrayListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get required format of date time
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("EEEE MM MMMM", Locale.ENGLISH);
        //get current date-time and format correctly
        Date nowTime = Calendar.getInstance().getTime();
        String dateTimeNow = dateTimeFormat.format(nowTime);
        //set formatted date as activity title
        setTitle(dateTimeNow);

        //initialise dataBase Handler and arrayList
        dbHandler = new DBHandler(this, null, null, 1);
        list = Home.getList();

        //add custom adapter, apply custom adapter to listView created in xml.
        eventsArrayListAdapter = new CustomAdapter(this, list);
        eventsListView = findViewById(R.id.EventsListView);
        eventsListView.setAdapter(eventsArrayListAdapter);

        //create dialog to show up when each event item is clicked.
        eventsListView.setOnItemClickListener((parent, view, position, id) -> {
            dialogBuilder(list.get(position), position);
        });

        //add events floating button
        FloatingActionButton fab = findViewById(R.id.fab_schedule);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(Schedule.this, CreateEvent.class);
            startActivity(intent);
        });
    }

    //method that creates a dialog box to show event details.
    public void dialogBuilder(EventObject e1, int pos){
            //get correctly formatted date reference for selected event
            String dateString="";
            try {
                DateFormat format1 = new SimpleDateFormat("MM/dd/yy");
                Date date1 = format1.parse(e1.get_eventdate());
                DateFormat format2 = new SimpleDateFormat("MMM dd, yyyy");
                dateString = format2.format(date1);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //build alert dialog
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

            builder.setPositiveButton("           OK", (dialog, which) -> dialog.dismiss());

            builder.setNegativeButton("Delete", (dialog, which) -> {
                //delete selected event from dataBase
                dbHandler.deleteEvent(list.get(pos).get_eventid());
                Intent intent = new Intent(Schedule.this, Home.class);
                startActivity(intent);
            });

            builder.setView(view1);
            AlertDialog dialog = builder.create();

            String eventColour = e1.get_eventcolour();
            switch (eventColour) {
                case "blue":
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.event_row_blue);
                    break;
                case "cyan":
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.event_row_cyan);
                    break;
                case "green":
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.event_row_green);
                    break;
                case "yellow":
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.event_row_yellow);
                    break;
                case "orange":
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.event_row_orange);
                    break;
                case "red":
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.event_row_red);
                    break;
                case "pink":
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.event_row_pink);
                    break;
                case "purple":
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.event_row_purple);
                    break;
            }
            dialog.show();
    }

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
}
