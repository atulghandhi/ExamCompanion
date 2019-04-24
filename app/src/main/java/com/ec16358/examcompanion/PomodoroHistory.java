package com.ec16358.examcompanion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/*
 * This Class builds a Pomodoro history, accessed from the pomodoro timer.
 *
 * Each complete pomodoro instance is saved to the firebase database.
 *
 * This class reads that data and displays it in a scrollable listview so users can see their revision history.
 *
 * */


public class PomodoroHistory extends AppCompatActivity {
    //create list of eventObjects
    List<PomodoroInstance> list;

    //get reference to fireBase database and reference and eventListener
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference pomodoroDatabaseReference;
    private ChildEventListener childEventListener;
    //create custom adapter object
    private PomodoroAdapter pomodoroAdapter;
    //create list view object
    private ListView pomodoroListView;
    //get id of current user
    String userID = Home.getCurrentUser().getUserId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro_history);
        setTitle("Pomodoro History");

        //initialise list of PomodoroInstances
        list = new ArrayList<>();

        //add pomodoroAdapter and bind to pomodoroInstances list
        pomodoroAdapter = new PomodoroAdapter(this, list);

        pomodoroListView = findViewById(R.id.idPomodoro_HistoryListView);
        pomodoroListView.setAdapter(pomodoroAdapter);

        //use fireBase database reference to access events
        firebaseDatabase = FirebaseDatabase.getInstance();
        pomodoroDatabaseReference = firebaseDatabase.getReference().child(userID).child("pomodoros");
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //called for each instance in the list

                //first, deserialize instance from database
                PomodoroInstance p = dataSnapshot.getValue(PomodoroInstance.class);
                pomodoroAdapter.add(p);
                Collections.reverse(list);
                pomodoroAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //called if pomodoro instance changed
                pomodoroAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                //called if pomodoro instance deleted from list
                pomodoroAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //called if one of the instances change position in the list
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //called if some sort of error occurs in accessing data
            }
        };
        //bind reference (what are we listening to?) to childEventListener (what should we do if something happens?)
        pomodoroDatabaseReference.addChildEventListener(childEventListener);

        //create dialog to show up when each event item is clicked.
        pomodoroListView.setOnItemClickListener((parent, view, position, id) -> dialogBuilder(position));
    }

    //method that creates a dialog box to show pomodoro details.
    public void dialogBuilder(int pos){

        PomodoroInstance p1 = list.get(pos);
        //get correctly formatted date reference for selected event
        String dateString="";
        try {
            DateFormat format1 = new SimpleDateFormat("MM/dd/yy HH:mm");
            Date date1 = format1.parse(p1.getStartDateTime());
            DateFormat format2 = new SimpleDateFormat("EEEE d MMMM, HH:mm");
            dateString = format2.format(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String isSuccessful = p1.isSuccess() ? "SUCCESS":"FAILURE";

        //build alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(PomodoroHistory.this);
        View view1 = getLayoutInflater().inflate(R.layout.pomodoro_instance_dialog, null);
        builder.setTitle("Pomodoro Instance : " + isSuccessful);

        TextView t1 = view1.findViewById(R.id.pomodoro_history_dialog_dateTime);
        t1.setText(dateString);

        TextView t2 = view1.findViewById(R.id.pomodoro_history_dialog_durationPoints);
        String pomLength = "Length (in minutes): " + Integer.toString(p1.getLength());
        t2.setText(pomLength);

        TextView t3 = view1.findViewById(R.id.pomodoro_history_dialog_target);
        String pomTarget = "Target: " + p1.getTarget();
        t3.setText(pomTarget);

        TextView t4 = view1.findViewById(R.id.pomodoro_history_dialog_summary);
        String pomSummary = "Summary: " + p1.getSummary();
        t4.setText(pomSummary);


        builder.setPositiveButton("           OK", (dialog, which) -> dialog.dismiss());

        builder.setNegativeButton("Delete", (dialog, which) -> {
            //delete selected event from database
            String pomodoroId = p1.getId();
            //remove module from database
            pomodoroDatabaseReference.child(pomodoroId).removeValue();
            Intent intent = new Intent(PomodoroHistory.this, PomodoroHistory.class);
            startActivity(intent);
        });

        builder.setView(view1);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //method to make sure back button takes user back to Pomodoro
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent intent = new Intent(PomodoroHistory.this, Pomodoro.class);
            startActivity(intent);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
