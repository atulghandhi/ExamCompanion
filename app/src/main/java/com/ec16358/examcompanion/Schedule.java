package com.ec16358.examcompanion;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Schedule extends AppCompatActivity {
    //create list of eventObjects
    List<EventObject> list;

    //get reference to fireBase database and reference and eventListener
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference eventsDatabaseReference;
    private ChildEventListener childEventListener;
    //create custom adapter object
    private EventsAdapter eventsAdapter;
    //create list view object
    private ListView eventsListView;
    //get id of current user
    String userID = Home.getCurrentUser().getUserId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get required format of date time
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("EEEE d MMMM", Locale.ENGLISH);
        //get current date-time and format correctly
        Date nowTime = Calendar.getInstance().getTime();
        String dateTimeNow = dateTimeFormat.format(nowTime);
        //set formatted date as activity title
        setTitle(dateTimeNow);

        //initialise list of eventObjects
        list = new ArrayList<>();

        //add EventsAdapter reference, bind it to EventsObject list
        eventsAdapter = new EventsAdapter(this, list);

        //get ref to listView that will show a list of events
        eventsListView = findViewById(R.id.EventsListView);
        eventsListView.setAdapter(eventsAdapter);


        //use fireBase database reference to access events
        firebaseDatabase = FirebaseDatabase.getInstance();
        eventsDatabaseReference = firebaseDatabase.getReference().child(userID).child("events");
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //Called for each event in list
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
                eventsAdapter.add(e);
                //sort arrayList by time
                Collections.sort(list, (o1, o2) -> {
                    try {
                        return new SimpleDateFormat("HH:mm").parse(o1.get_eventstarttime()).compareTo(new SimpleDateFormat("HH:mm").parse(o2.get_eventstarttime()));
                    } catch (ParseException exception) {
                        return 0;
                    }
                });
                //sort arrayList by date
                Collections.sort(list);
                eventsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //called when an event is changed
                eventsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                //called when an event is removed
                eventsAdapter.notifyDataSetChanged();
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

        //create dialog to show up when each event item is clicked.
        eventsListView.setOnItemClickListener((parent, view, position, id) -> dialogBuilder(position));

        //add events floating button
        FloatingActionButton fab = findViewById(R.id.fab_schedule);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(Schedule.this, CreateEvent.class);
            startActivity(intent);
        });

    }

    //method that creates a dialog box to show event details.
    public void dialogBuilder(int pos){
        EventObject e1 = list.get(pos);
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
                String eventsId = e1.get_eventid();
                //remove event from database
                eventsDatabaseReference.child(eventsId).removeValue();
                Intent intent = new Intent(Schedule.this, Schedule.class);
                startActivity(intent);
            });

            builder.setView(view1);
            AlertDialog dialog = builder.create();
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.event_row_primary);
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
