package com.ec16358.examcompanion.flashcards;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ec16358.examcompanion.Home;
import com.ec16358.examcompanion.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/*
*
* This class is used to create flash cards and add them to the firebase database.
* As each card is created, the class also gives the user 2 points for creating the card.
* The page to create flash cards is intentionally simple to prevent distractions with pointless
* formatting. There are two textViews for the prompt and card content.
* When cards are viewed, the card content is automatically formatted with bullet-points to make reading easier.
*
* */

public class CreateFlashCard extends AppCompatActivity {

    //get database and databaseReference to save event
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference cardsDatabaseReference;

    String userID = Home.getCurrentUser().getUserId();

    EditText cardPrompt;
    EditText cardAnswer;
    Button saveCard;

    String deckId;
    String deckName;
    String moduleId;
    String moduleName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_flash_card);

        //access deck information for the deck selected in the last activity
        Bundle bundle = getIntent().getExtras();
        if (getIntent().getStringExtra("DECK_ID") != null && (getIntent().getStringExtra("DECK_NAME") != null) && (getIntent().getStringExtra("MODULE_ID") != null && (getIntent().getStringExtra("MODULE_NAME") != null))){
            //get deckId and moduleId so you can go back to previous activities
            deckId = bundle.getString("DECK_ID");
            deckName = bundle.getString("DECK_NAME");
            moduleId = bundle.getString("MODULE_ID");
            moduleName = bundle.getString("MODULE_NAME");
        }


        //initialise edittexts and save button
        cardPrompt = findViewById(R.id.idCardPrompt);
        cardAnswer = findViewById(R.id.idCardAnswer);
        saveCard = findViewById(R.id.isSaveCard);

        saveCard.setOnClickListener(v -> saveButtonClicked());

    }

    public void saveButtonClicked(){
        //initialise fireBase database variables
        firebaseDatabase = FirebaseDatabase.getInstance();
        cardsDatabaseReference = firebaseDatabase.getReference().child(userID).child("cards").child(deckId);
        //save card to database
        //get strings entered by user in edittexts
        String front = cardPrompt.getText().toString();
        String back = cardAnswer.getText().toString();
        //check if user input is empty - if so; return without saving card to database
        if(!checkIfValid(front, back)){
            return;
        }
        //if input is valid, save card to database
        FlashCardObject flashCardObject = new FlashCardObject();

        //if all the above code checks out then event is valid. Now we add it to database.
        String cardId = cardsDatabaseReference.push().getKey();

        //initialise card values
        flashCardObject.setId(cardId);
        flashCardObject.setModule(moduleId);
        flashCardObject.setDeck(deckId);
        flashCardObject.setPrompt(front);
        flashCardObject.setAnswer(back);
        flashCardObject.setCurrentLietnerDeck(1);

        //add card to database
        cardsDatabaseReference.child(cardId).setValue(flashCardObject);

        DatabaseReference pointsDatabaseReference = firebaseDatabase.getReference().child("users").child(userID).child("points");
        pointsDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    pointsDatabaseReference.setValue(dataSnapshot.getValue(Integer.class) + 2);
                } else {
                    pointsDatabaseReference.setValue(2);
                }
                Toast.makeText(CreateFlashCard.this, "You earned 2 points", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //create intent, then add the values new activity will need to a Bundle
        Intent intent = new Intent(CreateFlashCard.this, FlashCardsCardView.class);
        Bundle extras = new Bundle();
        extras.putString("DECK_ID", deckId);
        extras.putString("DECK_NAME", deckName);
        extras.putString("MODULE_ID", moduleId);
        extras.putString("MODULE_NAME", moduleName);
        //add values to intent, then start intent
        intent.putExtras(extras);
        startActivity(intent);
    }

    public boolean checkIfValid(String front, String back){
        if(front.equals("") || back.equals("")){
            Toast.makeText(this, "You cannot create a card with an empty prompt or answer", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //method to make sure back button takes user back to flash cards
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            //create intent, then add the values new activity will need to a Bundle
            Intent intent = new Intent(CreateFlashCard.this, FlashCardsCardView.class);
            Bundle extras = new Bundle();
            extras.putString("DECK_ID", deckId);
            extras.putString("DECK_NAME", deckName);
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
