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
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;

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
        if (getIntent().getStringExtra("EXTRA_MODULE_ID") != null && (getIntent().getStringExtra("EXTRA_MODULE_NAME") != null)){
            moduleId = bundle.getString("EXTRA_MODULE_ID");
            moduleName = bundle.getString("EXTRA_MODULE_NAME");
        }

        setTitle(moduleName + " Decks");

        //initialise deckObject list
        list = new ArrayList<>();

        //add deckAdapter reference, bind it to deckObject list
        deckAdapter = new DeckAdapter(this, list);

        //get ref to listView that will show a list of modules
        decksListView = findViewById(R.id.idDecksListView);
        decksListView.setAdapter(deckAdapter);

        //use fireBase database reference to access modules
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

        //set "OK" button to dismiss dialog
        builder.setPositiveButton("           Save", (dialog, which) -> {
            DeckObject deckObject = new DeckObject();
            //code to save dialog to firebase
            String deckId = decksDatabaseReference.push().getKey();
            deckObject.setId(deckId);
            deckObject.setName(deckName.getText().toString());
            deckObject.setCards(0);
            decksDatabaseReference.child(deckId).setValue(deckObject);
            //refresh activity to remove module from listview
            deckAdapter.notifyDataSetChanged();
            //startActivity(new Intent(FlashCardDecks.this, FlashCardDecks.class));
        });

        //set delete button to remove module from database
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

}
