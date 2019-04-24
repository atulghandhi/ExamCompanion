package com.ec16358.examcompanion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.auth.data.model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/*
* This class creates a scrollable list of all users of the ExamCompanion application; ordered by
* how many points they have.
* This UserObject information is read from the firebase database.
*
* */

public class Leaderboard extends AppCompatActivity {
    //get reference to fireBase database and reference and eventListener
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference leaderboardDatabaseReference;
    private ChildEventListener childEventListener;

    //create listView, adapter and arrayList for users
    private ListView leaderboardListview;
    private LeaderboardAdapter leaderboardAdapter;
    List<UserObject> list;

    //get reference to userId so modules will be stored in fireBase database for each individual user
    String userID = Home.getCurrentUser().getUserId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        //initialise userObject list
        list = new ArrayList<>();

        //add leaderboard adapter reference, bind it to UserObject list
        leaderboardAdapter = new LeaderboardAdapter(this, list);

        //get ref to listView that will show a list of users
        leaderboardListview = findViewById(R.id.idLeaderboardListview);
        leaderboardListview.setAdapter(leaderboardAdapter);

        //use fireBase database reference to access users
        firebaseDatabase = FirebaseDatabase.getInstance();
        leaderboardDatabaseReference = firebaseDatabase.getReference().child("users");

        //create child event listener to add modules form the database to the listView
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //called for each object in list

                //deserialize ModuleObject from database to create temporary object
                UserObject m = dataSnapshot.getValue(UserObject.class);

                //otherwise, add event to listView via adapter.
                leaderboardAdapter.add(m);
                Collections.sort(list, new UserComparator());
                leaderboardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //if any module event is edited - notify adapter of change
                leaderboardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                //if any module is removed from list, notify adapter.
                leaderboardAdapter.notifyDataSetChanged();
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

        //bind database reference (what are we listening to?) to childEventListener (what should we do if something happens?)
        leaderboardDatabaseReference.addChildEventListener(childEventListener);

    }
}
