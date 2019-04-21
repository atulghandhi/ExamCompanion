package com.ec16358.examcompanion;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

public class FlashCardsCardView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_cards_card_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.idAddCardsFloatingButton);
        fab.setOnClickListener(view -> Snackbar.make(view, "Open 'Create Card' activity", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
    }

}
