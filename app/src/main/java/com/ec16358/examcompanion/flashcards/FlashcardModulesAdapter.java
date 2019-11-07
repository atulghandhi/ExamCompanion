package com.ec16358.examcompanion.flashcards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ec16358.examcompanion.pomodoro.ModuleObject;
import com.ec16358.examcompanion.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/*
*
* This simple adapter is used to populate the FlashCards.java ListView of modules. Each row is
* inflated from its xml layout and includes the module name which is added to its textView.
*
* */

public class FlashcardModulesAdapter extends ArrayAdapter<ModuleObject> {

    //constructor: pass in list of ModuleObjects and bind constructor to xml layout 'custom_row_module'
    FlashcardModulesAdapter(@NonNull Context context, List<ModuleObject> decks) {
        super(context, R.layout.custom_row_flashcard_modules, decks);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //inflate custom row for listView using xml layout
        LayoutInflater inflaterA = LayoutInflater.from(getContext());
        View customView = inflaterA.inflate(R.layout.custom_row_flashcard_modules, parent, false);

        //get reference to each moduleObject item using position parameter
        ModuleObject moduleObject = getItem(position);
        //get reference to textViews in layout
        TextView moduleName = customView.findViewById(R.id.idModuleName_flashcards);

        //get information about module object
        assert moduleObject != null;
        String moduleNameRef = moduleObject.getModuleName();

        //set information into its respective textView
        moduleName.setText(moduleNameRef);
        //return listView row
        return customView;
    }


}
