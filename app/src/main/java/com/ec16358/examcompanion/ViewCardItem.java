package com.ec16358.examcompanion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ViewCardItem extends AppCompatActivity {

    TextView cardFront;
    TextView cardBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_card_item);

        cardFront = findViewById(R.id.id_card_item_front);
        cardBack = findViewById(R.id.id_card_item_back);

        //recieve data from adaper onClickListener
        Intent intent = getIntent();
        String front = intent.getExtras().getString("CARD_FRONT");
        String back = intent.getExtras().getString("CARD_BACK");

        cardFront.setText(front);
        cardBack.setText(back);

        Toast.makeText(this, "Click card prompt to show content", Toast.LENGTH_SHORT).show();

        cardFront.setOnClickListener(v -> {
            cardBack.setVisibility(View.VISIBLE);
        });
    }
}
