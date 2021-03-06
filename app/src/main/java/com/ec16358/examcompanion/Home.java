package com.ec16358.examcompanion;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.ec16358.examcompanion.events.Schedule;
import com.ec16358.examcompanion.flashcards.FlashCards;
import com.ec16358.examcompanion.pomodoro.Pomodoro;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

/*
*
* Home activity displays the user with 4 buttons leading to the 4 main pages of this application.
* The activity also ensures the user is signed in using firebase authorisation class.
*
* This activity also creates a user object and uses firebaseAuth.getCurrentUser() to get information
* about that user (photoURL, name, id). This object is then uploaded to the fireBase database to be
* used to create the Leaderboard (showing a list of all users username and points earned).
*
* FirebaseAuth and AuthUI is used to sign the user in. It takes care of log-in pages and security.
* It allows users to log-in using google account.
*
* Implementing firebase authorisation was learned using the following video tutorials : classroom.udacity.com/courses/ud0352
* This information is, however, freely available on many websites.
*
* */

public class Home extends AppCompatActivity {
    //create instance variables to authenticate user with fireBase login
    private FirebaseAuth mfirebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private static int RC_SIGN_IN = 1;

    //get reference to fireBase database and reference and eventListener
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userDatabaseReference;

    private static UserObject currentUser;
    public static UserObject getCurrentUser() {
        return currentUser;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialise FireBase auth created above.
        mfirebaseAuth = FirebaseAuth.getInstance();

        //Set activity title
        setTitle("Home");

        //create buttons and bind to activities to be opened when buttons are clicked.
        Button HomePomodoroButton = findViewById(R.id.idHomePomodoroButton);
        HomePomodoroButton.setOnClickListener(
                v -> startActivity(new Intent(Home.this, Pomodoro.class))
        );

        Button HomeScheduleButton = findViewById(R.id.idHomeScheduleButton);
        HomeScheduleButton.setOnClickListener(
                v -> startActivity(new Intent(Home.this, Schedule.class))
        );

        Button HomeFlashcardButton = findViewById(R.id.idHomeFlashcardsButton);
        HomeFlashcardButton.setOnClickListener(
                v -> startActivity(new Intent(Home.this, FlashCards.class))
        );

        Button HomePointsButton = findViewById(R.id.idHomePointsButton);
        HomePointsButton.setOnClickListener(
                v -> startActivity(new Intent(Home.this, Leaderboard.class))
        );

        //initialise firesBase authStateListener
        authStateListener = firebaseAuth -> {
            //check if user is logged in using firebaseAuth provided
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                //user is signed in
                onSignedIn(user.getUid(), user.getDisplayName(), user.getPhotoUrl());
            } else {
                //user is signed out - show log-in screen
                onSignedOut();
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)//saves users credentials to keep them logged in
                                .setAvailableProviders(Arrays.asList(
                                        new AuthUI.IdpConfig.EmailBuilder().build(),
                                        new AuthUI.IdpConfig.GoogleBuilder().build()))
                                .build(),
                        RC_SIGN_IN);
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(requestCode == RESULT_OK){
                //user successfully signed in
                Toast.makeText(this, "Signed in successfully", Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RESULT_CANCELED){
                //user not signed in
                Toast.makeText(this, "Signed in cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out_menu:
                //sign out
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //bind fireBase auth object to auth state listener. This will detect if user is authorised (logged in) when app opened
        mfirebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //remove authListener from auth object when app is paused
        mfirebaseAuth.removeAuthStateListener(authStateListener);
    }

    public void onSignedIn(String Uid, String username, Uri photoURL){
        //place any Home activity code using database here
        //initialise currentUser object
        firebaseDatabase = FirebaseDatabase.getInstance();
        userDatabaseReference = firebaseDatabase.getReference().child("users").child(Uid);

        currentUser = new UserObject();
        currentUser.setUserId(Uid);
        currentUser.setUsername(username);
        if(photoURL != null) {
            currentUser.setPhotoURL(photoURL.toString());
            //TODO :: deal with any other part of code that uses user photos; does code crash is no user photo?
        }

        userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    //if this user is already there do nothing
                } else {
                    //if this is a new user, save their information in database to be used for Leaderboard
                    userDatabaseReference.setValue(currentUser);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void onSignedOut(){
        //cleanup when user logs out
    }

    /*
     *
     * Close app when back button pressed from home activity
     *
     * */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}
