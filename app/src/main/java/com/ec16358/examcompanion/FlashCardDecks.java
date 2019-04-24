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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;

/*
*
* This class is creates a scolling list of decks for a given module. It is called when from the
* FlashCards activity which displays a similar scrolling list of modules. Each module has a set of
* decks - so when it is called, FlashCards.java uses an intent to send forward data about which
* module the user selected.
*
* Clicking any card sends the user to FlashCardsCardView.java where they are shown a scrolling list
* of cards in that deck. An intent is used to send data to that activity about which deck was chosen.
*
* New decks are added via alert dialog and uploaded to firebase database
* Existing decks can be deleted via alert dialog by long clicking them.
* */

public class FlashCardDecks extends AppCompatActivity {

    //get reference to fireBase database and reference and eventListener
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference decksDatabaseReference;
    private ChildEventListener childEventListener;

    //create listView, adapter and arrayList for decks
    private ListView decksListView;
    private DeckAdapter deckAdapter;
    List<DeckObject> list;

    //get reference to userId
    String userID = Home.getCurrentUser().getUserId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_card_decks);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //access module information for the module selected in the last activity
        String moduleId = "";
        String moduleName = "";
        Bundle bundle = getIntent().getExtras();
        if (getIntent().getStringExtra("MODULE_ID") != null && (getIntent().getStringExtra("MODULE_NAME") != null)){
            moduleId = bundle.getString("MODULE_ID");
            moduleName = bundle.getString("MODULE_NAME");
        }

        setTitle(moduleName + " Decks");

        //initialise deckObject list
        list = new ArrayList<>();

        //add deckAdapter reference, bind it to deckObject list
        deckAdapter = new DeckAdapter(this, list);

        //get ref to listView that will show a list of decks
        decksListView = findViewById(R.id.idDecksListView);
        decksListView.setAdapter(deckAdapter);

        //use fireBase database reference to access decks
        firebaseDatabase = FirebaseDatabase.getInstance();
        assert moduleId != null;
        decksDatabaseReference = firebaseDatabase.getReference().child(userID).child("decks").child(moduleId);

        //create child event listener to add modules form the database to the listView
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //called for each deck in list

                //deserialize ModuleObject from database to create temporary object
                DeckObject d = dataSnapshot.getValue(DeckObject.class);

                //add event to listView via adapter.
                deckAdapter.add(d);
                deckAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //if any module event is edited - notify adapter of change
                deckAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                //if any module is removed from list, notify adapter.
                deckAdapter.notifyDataSetChanged();
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
        decksDatabaseReference.addChildEventListener(childEventListener);

        //if any module in the list is clicked, show dialog that will allow user to delete that module
        decksListView.setOnItemClickListener((parent, view, position, id) -> {
            //what to do when deck is selected?
        });

        final String moduleIdFinal = moduleId;
        final String moduleNameFinal = moduleName;

        //onItemClick listener will take user to cardView of all cards in that deck
        decksListView.setOnItemClickListener((parent, view, position, id) -> {
            //get object from list
            DeckObject deckObject = list.get(position);
            //create intent, then add the values new activity will need to a Bundle
            Intent intent = new Intent(FlashCardDecks.this, FlashCardsCardView.class);
            Bundle extras = new Bundle();
            //send deck info to next activity so that it knows which deck cards to show
            extras.putString("DECK_ID", deckObject.getId());
            extras.putString("DECK_NAME", deckObject.getName());
            //send module info to next activity so it can return to this one
            extras.putString("MODULE_ID", moduleIdFinal);
            extras.putString("MODULE_NAME", moduleNameFinal);
            //add values to intent, then start intent
            intent.putExtras(extras);
            startActivity(intent);
        });

        //longClicking a deckItem will give the user the option to delete it
        decksListView.setOnItemLongClickListener((parent, view, position, id) -> deleteDeckDialog(position));

        //button with '+' sign, clicking it will open dialog to allow user to add new deck to list.
        FloatingActionButton addDecksButton = findViewById(R.id.idAddDecksButton);
        addDecksButton.setOnClickListener(view -> addDeckToList());
    }

    public void addDeckToList(){
        //build alert dialog using xml layout
        AlertDialog.Builder builder = new AlertDialog.Builder(FlashCardDecks.this);
        View view1 = getLayoutInflater().inflate(R.layout.decks_dialog, null);
        //set dialog title as name of module
        builder.setTitle("Add a flash-card deck");
        //get ref to textView in dialog layout and show date of module
        EditText deckName = view1.findViewById(R.id.idDeckNameDialog);

        //set "Save" button to save deck
        builder.setPositiveButton("           Save", (dialog, which) -> {
            DeckObject deckObject = new DeckObject();
            //code to save deck to firebase
            String deckId = decksDatabaseReference.push().getKey();
            deckObject.setId(deckId);
            deckObject.setName(deckName.getText().toString());
            deckObject.setCards(0);
            decksDatabaseReference.child(deckId).setValue(deckObject);
            //notify deck adapter to add deck to listView
            deckAdapter.notifyDataSetChanged();
        });

        //set cancel button to dismiss dialog
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.setView(view1);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public boolean deleteDeckDialog(int pos){
        //build alert dialog using xml layout
        AlertDialog.Builder builder = new AlertDialog.Builder(FlashCardDecks.this);
        View view1 = getLayoutInflater().inflate(R.layout.delete_deck_item, null);

        //get selected deckObject
        DeckObject d1 = list.get(pos);

        //set dialog title
        builder.setTitle("Delete flash-card deck");


        //set delete button to remove deck from database
        builder.setPositiveButton("           Delete", (dialog, which) -> {
            //delete selected deck from dataBase
            String deckId = d1.getId();
            //remove event from database
            decksDatabaseReference.child(deckId).removeValue();
            //since deck item is deleted; refresh activity
            finish();
            startActivity(getIntent());
        });

        //set cancel button to dismiss dialog
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.setView(view1);
        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    //method to make sure back button takes user back to Flash cards modules page
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent intent = new Intent(FlashCardDecks.this, FlashCards.class);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
