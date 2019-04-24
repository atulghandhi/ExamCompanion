package com.ec16358.examcompanion;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/*
* This class shows a list of modules using a list view and populated by FlashCardsModulesAdapter.java.
*
* Modules here are the same as those that may be added in the Modules activity, accessed though the
* Pomodoro activity. New modules can be added. Clicking a module opens FlashCardDecks activity which
* shows a list of decks for that module. An intent is used to sent information about which module was
* selected to that class.
*
* */

public class FlashCards extends AppCompatActivity {
    //create tag, used to log date
    private static final String TAG = "DisplayModules";
    //date picker dialog so user can enter module exam date
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    //get reference to fireBase database and reference and eventListener
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference modulesDatabaseReference;
    private ChildEventListener childEventListener;

    //create listView, adapter and arrayList for modules
    private ListView modulesListView;
    private FlashcardModulesAdapter flashcardModulesAdapter;
    List<ModuleObject> list;

    //get reference to userId so modules will be stored in fireBase database for each individual user
    String userID = Home.getCurrentUser().getUserId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_cards);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Flashcard Modules");

        //initialise moduleObject list
        list = new ArrayList<>();

        //add modulesAdapter reference, bind it to ModuleObject list
        flashcardModulesAdapter = new FlashcardModulesAdapter(this, list);

        //get ref to listView that will show a list of modules
        modulesListView = findViewById(R.id.idDecksListview);
        modulesListView.setAdapter(flashcardModulesAdapter);

        //use fireBase database reference to access modules
        firebaseDatabase = FirebaseDatabase.getInstance();
        modulesDatabaseReference = firebaseDatabase.getReference().child(userID).child("modules");

        //create child event listener to add modules form the database to the listView
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //called for each module in list

                //deserialize ModuleObject from database to create temporary object
                ModuleObject m = dataSnapshot.getValue(ModuleObject.class);

                //get object date and time (make sure its not null)
                assert m != null;
                String moduleDate = m.getModuleExamDate();

                //get format of date
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);

                //get current date and format as above
                Date today = Calendar.getInstance().getTime();
                String dateNow = dateFormat.format(today);

                //check if module exam is past - if so, don't display module cards no longer relevant
                try {
                    //if the following returns true, module exam is before 'today'
                    if (dateFormat.parse(moduleDate).before(dateFormat.parse(dateNow))) {
                        return; //don't add event to adapter
                    }
                } catch (ParseException exception) {
                    exception.printStackTrace();
                }
                //otherwise, add event to listView via adapter.
                flashcardModulesAdapter.add(m);
                flashcardModulesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //if any module event is edited - notify adapter of change
                flashcardModulesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                //if any module is removed from list, notify adapter.
                flashcardModulesAdapter.notifyDataSetChanged();
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
        modulesDatabaseReference.addChildEventListener(childEventListener);

        //if any module in the list is clicked, go to list of decks for the module

        modulesListView.setOnItemClickListener((parent, view, position, id) -> {
            //get object from list
            ModuleObject moduleObject = list.get(position);
            //create intent, then add the values new activity will need to a Bundle
            Intent intent = new Intent(FlashCards.this, FlashCardDecks.class);
            Bundle extras = new Bundle();
            extras.putString("MODULE_ID", moduleObject.getModuleId());
            extras.putString("MODULE_NAME", moduleObject.getModuleName());
            //add values to intent, then start intent
            intent.putExtras(extras);
            startActivity(intent);

        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> addModuleDialogBuilder());

    }

    //method that creates a dialog box to add a new module
    public void addModuleDialogBuilder(){
        //build alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(FlashCards.this);
        //inflate relevant xml dialog layout
        View view1 = getLayoutInflater().inflate(R.layout.module_dialog, null);
        //get references to textView and editText in layout
        EditText moduleNameEdit = view1.findViewById(R.id.idModuleName_editText);
        TextView moduleDateEdit = view1.findViewById(R.id.idModuleDate_editText);

        //to add event, open date picker dialog
        moduleDateEdit.setOnClickListener(v -> {
            //open calender when textView is clicked.
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    FlashCards.this,
                    android.R.style.Theme_DeviceDefault_Light_Dialog,
                    mDateSetListener,
                    year,month,day);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.show();
        });

        //Display in textView the date selected in date picker
        mDateSetListener = (datePicker, year, month, day) -> {
            //months are from 0 to 11, so add 1 to month value when displaying
            month = month + 1;
            Log.d(TAG, "onDateSet: mm/dd/yy: " + month + "/" + day + "/" + year);

            String date = month + "/" + day + "/" + year;
            moduleDateEdit.setText(date);
        };

        //create dialog button to save module to database
        builder.setPositiveButton("           Save", (dialog, which) -> {
            //get input in strings
            String moduleName = moduleNameEdit.getText().toString().trim();
            String moduleDate = moduleDateEdit.getText().toString().trim();

            //check if input is valid
            if(checkIfValid(moduleName, moduleDate)){
                //if valid; add module to database
                String moduleId = modulesDatabaseReference.push().getKey(); //get unique key from database
                //save module in database using unique key
                modulesDatabaseReference.child(moduleId).setValue(new ModuleObject(moduleId, moduleName, moduleDate));
                Toast.makeText(this, "Module added", Toast.LENGTH_LONG).show(); //show toast to confirm module creation
            } else {
                //if input not valid, reopen dialog builder for user to re-enter event
                addModuleDialogBuilder();
            }
        });

        //create cancel dialog button to dismiss dialog
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.setView(view1);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //method to check if input is valid
    public boolean checkIfValid(String name, String date){
        //Check if module name is not entered
        if(name.equals("")) {
            Toast.makeText(this, "Please enter module name", Toast.LENGTH_SHORT).show();
            return false;
        }
        //check if exam date not selected
        if(date.equals("Exam Date:") || date.equals("")) {
            Toast.makeText(this, "Please enter module exam date", Toast.LENGTH_SHORT).show();
            return false;
        }

        //no check if exam date selected is in the past

        //get format to parse date to
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);
        //get current date
        Date today = Calendar.getInstance().getTime();
        String currentDate = dateFormat.format(today);

        try {
            //if the following returns true, the events end time is before 'now'
            if(dateFormat.parse(date).before(dateFormat.parse(currentDate))){
                Toast.makeText(this, "Exam is in the past, please change the exam date", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (ParseException e) {
            //handle the exceptions
        }
        //If the above code does not return false, then event is valid, thus we return true.
        return true;
    }

    //method to make sure back button takes user back to home page
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent intent = new Intent(FlashCards.this, Home.class);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
