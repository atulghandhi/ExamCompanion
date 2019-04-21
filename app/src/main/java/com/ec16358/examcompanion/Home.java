package com.ec16358.examcompanion;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Arrays;

public class Home extends AppCompatActivity {
    //create instance variables to authenticate user with fireBase login
    private FirebaseAuth mfirebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private static int RC_SIGN_IN = 1;

    //get reference to fireBase database and reference and eventListener
    /*private FirebaseDatabase firebaseDatabase;
    private DatabaseReference eventsDatabaseReference;
    private ChildEventListener childEventListener;*/

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
                v -> startActivity(new Intent(Home.this, Dashboard.class))
        );

        //initialise firesBase authStateListener
        authStateListener = firebaseAuth -> {
            //check if user is logged in using firebaseAuth provided
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                //user is signed in
                onSignedIn(user.getUid(), user.getDisplayName());
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

    public void onSignedIn(String Uid, String username){
        //place any Home activity code using database here
        //initialise currentUser object
        currentUser = new UserObject(Uid, username);

        //use fireBase database reference to access events
        /*firebaseDatabase = FirebaseDatabase.getInstance();
        eventsDatabaseReference = firebaseDatabase.getReference().child("events");
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //called when user is added
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //called when user is changed
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                //called when user is removed
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //called if user changes position in the list
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //some sort of error occurred (no permission to read data)
            }
        };*/
    }

    public void onSignedOut(){
        //cleanup when user logs out
    }

    /*
     * Close app when back button pressed from home activity
     * */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}
