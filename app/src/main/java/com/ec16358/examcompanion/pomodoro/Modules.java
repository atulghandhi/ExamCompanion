package com.ec16358.examcompanion.pomodoro;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.ec16358.examcompanion.Home;
import com.ec16358.examcompanion.R;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/*
*
* Modules activity accessed from the pomodoro page shows a scrolling list of modules and their exam dates.
* New modules can be added via alert dialog. Clicking a module will give option to delete it via alert
* dialog.
*
* checkIfValid checks if user's new module is valid (name is not null or "", exam date is in the future)
*
* uses ModuleObject to create a new module. uploads to firebase database.
*
* Uses ModulesAdapter.java to populate its listview of modules. enters listview of ModuleObject as argument
* to ModulesAdapter. Listview is filled from firebase database.
* If a module in database has an exam date that is passed; that module is not shown in the list (user
* no longer needs to revise for it)
*
*
* */

public class Modules extends AppCompatActivity {
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
    private ModulesAdapter modulesAdapter;
    List<ModuleObject> list;

    //get reference to userId so modules will be stored in fireBase database for each individual user
    String userID = Home.getCurrentUser().getUserId();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modules);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialise moduleObject list
        list = new ArrayList<>();

        //add modulesAdapter reference, bind it to ModuleObject list
        modulesAdapter = new ModulesAdapter(this, list);

        //get ref to listView that will show a list of modules
        modulesListView = findViewById(R.id.idModulesListView);
        modulesListView.setAdapter(modulesAdapter);

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

                //check if module exam is past - if so, don't display module (still stored in database for statistics/analytics)
                try {
                    //if the following returns true, module exam is before 'today'
                    if (dateFormat.parse(moduleDate).before(dateFormat.parse(dateNow))) {
                        return; //don't add event to adapter
                    }
                } catch (ParseException exception) {
                    exception.printStackTrace();
                }
                //otherwise, add event to listView via adapter.
                modulesAdapter.add(m);
                modulesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //if any module event is edited - notify adapter of change
                modulesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                //if any module is removed from list, notify adapter.
                modulesAdapter.notifyDataSetChanged();
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

        //if any module in the list is clicked, show dialog that will allow user to delete that module
        modulesListView.setOnItemClickListener((parent, view, position, id) -> onItemClickDialogBuilder(position));

        //button with '+' sign, clicking it will open dialog to allow user to add new module to list.
        FloatingActionButton addModuleButton = findViewById(R.id.idAddModuleButton);
        addModuleButton.setOnClickListener(view -> addModuleDialogBuilder());
    }

    //method that creates a dialog box to add a new module
    public void addModuleDialogBuilder(){
        //build alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(Modules.this);
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
                    Modules.this,
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

    //method that creates a dialog box that allows user to delete a module clicked on.
    public void onItemClickDialogBuilder(int pos){
        //first get a reference to the moduleObject to be deleted
        ModuleObject m1 = list.get(pos);

        //format date of the module to display in dialog alongside module name
        String dateString="";
        try {
            DateFormat format1 = new SimpleDateFormat("MM/dd/yy");
            Date date1 = format1.parse(m1.getModuleExamDate());
            DateFormat format2 = new SimpleDateFormat("MMM dd, yyyy");
            dateString = "Exam: " + format2.format(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //build alert dialog using xml layout
        AlertDialog.Builder builder = new AlertDialog.Builder(Modules.this);
        View view1 = getLayoutInflater().inflate(R.layout.module_item_dialog, null);
        //set dialog title as name of module
        builder.setTitle(m1.getModuleName());
        //get ref to textview in dialog layout and show date of module
        TextView t1 = view1.findViewById(R.id.module_item_date);
        t1.setText(dateString);

        //set "OK" button to dismiss dialog
        builder.setPositiveButton("           OK", (dialog, which) -> dialog.dismiss());

        //set delete button to remove module from database
        builder.setNegativeButton("Delete", (dialog, which) -> {
            //get id of module selected
            String moduleId = m1.getModuleId();
            //remove module from database
            modulesDatabaseReference.child(moduleId).removeValue();
            //refresh activity to remove module from listview
            Intent intent = new Intent(Modules.this, Modules.class);
            startActivity(intent);
        });
        builder.setView(view1);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //method to check if input is valid
    public boolean checkIfValid(String name, String date){
        //Check if module name is not entered
        if(name.equals("")) {
            Toast.makeText(this, "Enter module name", Toast.LENGTH_SHORT).show();
            return false;
        }
        //check if exam date not selected
        if(date.equals("Exam Date:") || date.equals("")) {
            Toast.makeText(this, "Enter module exam date", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "Exam is in the past, change the exam date", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (ParseException e) {
            //handle the exceptions
        }
        //If the above code does not return false, then event is valid, thus we return true.
        return true;
    }

    //method to make sure back button takes user back to Pomodoro
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent intent = new Intent(Modules.this, Pomodoro.class);
            startActivity(intent);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
