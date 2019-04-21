package com.ec16358.examcompanion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DeckAdapter extends ArrayAdapter<DeckObject> {

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
        TextView cardNumber = customView.findViewById(R.id.idFlashcardNumber);


        //get information about module object
        String deckNameRef = deckObject.getName();
        String cardsNumberRef = "Cards : " + Integer.toString(deckObject.getCards());
        //set information into its respective textView
        deckName.setText(deckNameRef);
        cardNumber.setText(cardsNumberRef);
        //return listView row
        return customView;
    }


}
