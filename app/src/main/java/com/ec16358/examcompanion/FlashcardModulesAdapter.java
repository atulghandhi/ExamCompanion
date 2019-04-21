package com.ec16358.examcompanion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
        TextView moduleCardsDue = customView.findViewById(R.id.idModuleCardsDue);

        //get information about module object
        assert moduleObject != null;
        String moduleNameRef = moduleObject.getModuleName();
        String moduleCardsDueRef = "Cards due for review : " + moduleNameRef.length();

        //set information into its respective textView
        moduleName.setText(moduleNameRef);
        moduleCardsDue.setText(moduleCardsDueRef);
        //return listView row
        return customView;
    }


}
