package com.ec16358.examcompanion.flashcards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ec16358.examcompanion.Home;
import com.ec16358.examcompanion.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/*
*
* The deck adapter class populates a listview in FlashCardsDecks activity with a list of decks for a
* given module. FlashCardsDecks enters an ArrayList of DeckObjects which this adapter then uses to
* populate rows of the listview.
*
* */


public class DeckAdapter extends ArrayAdapter<DeckObject> {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference decksDatabaseReference;

    String userID = Home.getCurrentUser().getUserId();

    //constructor: pass in list of ModuleObjects and bind constructor to xml layout 'custom_row_module'
    DeckAdapter(@NonNull Context context, List<DeckObject> modules) {
        super(context, R.layout.custom_row_deck, modules);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //inflate custom row for listView using xml layout
        LayoutInflater inflaterA = LayoutInflater.from(getContext());
        View customView = inflaterA.inflate(R.layout.custom_row_deck, parent, false);

        //get reference to each moduleObject item using position parameter
        DeckObject deckObject = getItem(position);

        //get reference to textViews in layout
        TextView deckName = customView.findViewById(R.id.idFlashcardDeckName);
        TextView cardCount = customView.findViewById(R.id.idFlashcardNumber);

        //get number of card nodes for this deck - first get reference to database with right path
        firebaseDatabase = FirebaseDatabase.getInstance();
        decksDatabaseReference = firebaseDatabase.getReference().child(userID).child("cards").child(deckObject.getId());
        //then add value listener
        decksDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            //get children from the database snapshot and set textview
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String cardsNumberRef = "Cards : " + String.valueOf(dataSnapshot.getChildrenCount());
                cardCount.setText(cardsNumberRef);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //some sort of error occurred (no permission to read data)
            }
        });

        //get information about module object
        String deckNameRef = deckObject.getName();
        //set information into its respective textView
        deckName.setText(deckNameRef);
        //return listView row
        return customView;
    }


}
