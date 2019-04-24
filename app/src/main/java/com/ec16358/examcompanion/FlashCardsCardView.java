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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/*
*
* This class uses a RecyclerView and the CardRecyclerViewAdapter.java class to create a scrollable
* grid of flashcards a user can browse. Clicking a card leads user to ViewCardItem.
*
* Flash card data is read from the firebase database and added to an ArrayList of FlashCardObject's.
* The adapter is given that list as argument. After each new card is added, the adapter is notified
* of a dataset change.
*
* */

public class FlashCardsCardView extends AppCompatActivity {
    //create listview of card objects, a recyclerView to hold them and an adapter to fill the recyclerView
    List<FlashCardObject> flashCardObjectsList;
    RecyclerView recyclerView;
    CardRecyclerViewAdapter cardRecyclerViewAdapter;

    //get reference to fireBase database and reference and eventListener
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference cardsDatabaseReference;
    private ChildEventListener childEventListener;

    //get id of current user
    String userID = Home.getCurrentUser().getUserId();

    //create variables to put information from last activity into
    String deckId = "";
    String deckName = "";
    String moduleId = "";
    String moduleName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_cards_card_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //access deck information for the deck selected in the last activity
        Bundle bundle = getIntent().getExtras();
        if (getIntent().getStringExtra("DECK_ID") != null && (getIntent().getStringExtra("DECK_NAME") != null) && (getIntent().getStringExtra("MODULE_ID") != null && (getIntent().getStringExtra("MODULE_NAME") != null))){
            deckId = bundle.getString("DECK_ID");
            deckName = bundle.getString("DECK_NAME");

            moduleId = bundle.getString("MODULE_ID");
            moduleName = bundle.getString("MODULE_NAME");
        }

        setTitle(deckName + " Cards");

        flashCardObjectsList = new ArrayList<>();

        //initialise recycle view adapter
        cardRecyclerViewAdapter = new CardRecyclerViewAdapter(this, flashCardObjectsList);

        //get reference to cards RecyclerView in layout
        recyclerView = findViewById(R.id.idCardsRecyclerView);

        //use fireBase database reference to access cards
        firebaseDatabase = FirebaseDatabase.getInstance();
        cardsDatabaseReference = firebaseDatabase.getReference().child(userID).child("cards").child(deckId);

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //Called for each card in list

                //deserialize EventObject from database
                FlashCardObject c = dataSnapshot.getValue(FlashCardObject.class);

                //add card to adapter
                flashCardObjectsList.add(c);
                cardRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //called when items are changed
                cardRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                //called when a card is removed
                cardRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //called if one of the items change position in the list
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //some sort of error occurred (no permission to read data)
            }
        };

        //bind reference (what are we listening to?) to childEventListener (what should we do if something happens?)
        cardsDatabaseReference.addChildEventListener(childEventListener);

        //set grid layout manager on recycler view
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        //bind recycler view to adapter
        recyclerView.setAdapter(cardRecyclerViewAdapter);

        //create final string variables to pass into inner method next
        final String deckIdFinal = deckId;
        final String deckNameFinal = deckName;
        final String moduleIdFinal = moduleId;
        final String moduleNameFinal = moduleName;

        FloatingActionButton addCardsButton = findViewById(R.id.idAddCardsFloatingButton);
        addCardsButton.setOnClickListener(view -> {
            //create intent, then add the values new activity will need to a Bundle
            Intent intent = new Intent(FlashCardsCardView.this, CreateFlashCard.class);
            Bundle extras = new Bundle();
            extras.putString("DECK_ID", deckIdFinal);
            extras.putString("DECK_NAME", deckNameFinal);
            //send module info to next activity so it can return to this one
            extras.putString("MODULE_ID", moduleIdFinal);
            extras.putString("MODULE_NAME", moduleNameFinal);
            //add values to intent, then start intent
            intent.putExtras(extras);
            startActivity(intent);
        });
    }


    //method to make sure back button takes user back to flash cards decks
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            //create intent, then add the values new activity will need to a Bundle
            Intent intent = new Intent(FlashCardsCardView.this, FlashCardDecks.class);
            Bundle extras = new Bundle();
            extras.putString("MODULE_ID", moduleId);
            extras.putString("MODULE_NAME", moduleName);
            //add values to intent, then start intent
            intent.putExtras(extras);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
