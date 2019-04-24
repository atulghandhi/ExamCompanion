package com.ec16358.examcompanion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/*
 * This Class is used to view instances of FlashCardObject's. When an object is clicked, the
 * CardRecyclerViewAdapter directs the app to this activity (each card has an onClickListener) and
 * uses an intent to pass the data held in each card (text in card prompt and card content)
 *
 * This class uses 4 lines of code from the following source to add bullet points to each line of
 * text before displaying it:
 *
 * https://stackoverflow.com/questions/35562333/android-creating-bullet-point-list-in-an-edit-text
 * */

public class ViewCardItem extends AppCompatActivity {

    //create text views to hold two parts of the flashcard
    TextView cardFront;
    TextView cardBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_card_item);

        //initialise textViews within the view_card_jtem layout
        cardFront = findViewById(R.id.id_card_item_front);
        cardBack = findViewById(R.id.id_card_item_back);

        //recieve data from CardRecyclerViewAdapter onClickListener
        Intent intent = getIntent();
        String front = intent.getExtras().getString("CARD_FRONT");
        String back = intent.getExtras().getString("CARD_BACK");

        //Add bullet point to each new line in the cards content for easier reading of notes.
        String pad = "";
        String[] lines = back.split("\n");              //these 4 lines from source cited above
        for (int i = 0; i < lines.length; i++) {
            pad = pad + ("\n" + "\u2022" + "  " + lines[i]);
        }

        //set text in textViews
        cardFront.setText(front);
        cardBack.setText(pad);

        //tell user to click prompt to make card content visible
        Toast.makeText(this, "Click card prompt to show content", Toast.LENGTH_SHORT).show();

        //set onClick listener; when the card prompt is clicked then show the rest of the card.
        cardFront.setOnClickListener(v -> {
            cardBack.setVisibility(View.VISIBLE);
        });
    }
}
