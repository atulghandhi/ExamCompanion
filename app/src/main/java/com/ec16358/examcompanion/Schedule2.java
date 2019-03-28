package com.ec16358.examcompanion;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.KeyEvent;
import android.view.View;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class Schedule2 extends AppCompatActivity {

    private ArrayList<String> eventNames = new ArrayList<>();
    private ArrayList<String> eventTypes = new ArrayList<>();
    private ArrayList<String> eventTimes = new ArrayList<>();

    //get reference to fireBase database and reference and eventListener
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference eventsDatabaseReference;
    private ChildEventListener childEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get required format of date time
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("EEEE d MMMM", Locale.ENGLISH);
        //get current date-time and format correctly
        Date nowTime = Calendar.getInstance().getTime();
        String dateTimeNow = dateTimeFormat.format(nowTime);
        //set formatted date as activity title
        setTitle(dateTimeNow);

        //use fireBase database reference to access events
        firebaseDatabase = FirebaseDatabase.getInstance();
        eventsDatabaseReference = firebaseDatabase.getReference().child("events");

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //called for each eventObject
                //deserialize EventObject from database and add to adapter
                EventObject e = dataSnapshot.getValue(EventObject.class);

                //get object date and time.
                String listDateTime = e.get_eventdate() + " " + e.get_eventendtime();
                //get format of date time
                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MM/dd/yy HH:mm", Locale.ENGLISH);
                //get current date-time and format as above
                Date nowTime = Calendar.getInstance().getTime();
                String dateTimeNow = dateTimeFormat.format(nowTime);
                //check if event is after current. If so, don't display event
                try {
                    //if the following returns true, the events end time is before 'now'
                    if (dateTimeFormat.parse(listDateTime).before(dateTimeFormat.parse(dateTimeNow))) {
                        return; //don't add event to adapter
                    }
                } catch (ParseException exception) {
                    //handle the exceptions
                }

                //format date format to be shown using simple-date-format
                String date = e.get_eventdate();
                String dateString="";
                try {
                    DateFormat format1 = new SimpleDateFormat("MM/dd/yy");
                    Date date1 = format1.parse(date);
                    DateFormat format2 = new SimpleDateFormat("MMM dd, yyyy");
                    dateString = format2.format(date1);
                } catch (ParseException exception) {
                    exception.printStackTrace();
                }

                eventNames.add(e.get_eventname());
                eventTypes.add(e.get_eventtype() + "  on " + dateString);
                eventTimes.add("From " + e.get_eventstarttime() + "  to " + e.get_eventendtime());

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //called when an event is changed
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                //called when an event is removed
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //called if one of the messages change position in the list
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //some sort of error occurred (no permission to read data)
            }
        };
        //bind reference (what are we listening to?) to childEventListener (what should we do if something happens?)
        eventsDatabaseReference.addChildEventListener(childEventListener);

        initRecyclerView();

        //add events floating button
        FloatingActionButton fab = findViewById(R.id.fab_schedule2);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(Schedule2.this, CreateEvent.class);
            startActivity(intent);
        });
    }

    public void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.idScheduleRecyclerView);
        EventsRecyclerAdapter eventsRecyclerAdapter = new EventsRecyclerAdapter(this, eventNames, eventTypes, eventTimes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); //new GridLayoutManager(this, 3)
        recyclerView.setAdapter(eventsRecyclerAdapter);
    }

    //method to make sure back button takes user back to home page
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent intent = new Intent(Schedule2.this, Home.class);
            startActivity(intent);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
