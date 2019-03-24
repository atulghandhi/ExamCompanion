package com.ec16358.examcompanion;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class CreateEvent extends AppCompatActivity {

    //create tag, will need later
    private static final String TAG = "SelectEventDate";
    //get instance variables for fireBase database
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference eventsDatabaseReference;

    //create references to widgets in activity
    private TextView eventDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        //Create datePicker dialog when select date is clicked.
        //bind textView reference above to textView in xml
        eventDate = findViewById(R.id.idSelectDate);
        //create set onClickListener for textView
        eventDate.setOnClickListener(view -> {
            //open calender when textView is clicked.
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    CreateEvent.this,
                    android.R.style.Theme_DeviceDefault_Light_Dialog,
                    mDateSetListener,
                    year,month,day);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.show();
        });

        //Display in textView the date selected.
        mDateSetListener = (datePicker, year, month, day) -> {
            //months are from 0 to 11, so add 1 to month value when displaying
            month = month + 1;
            Log.d(TAG, "onDateSet: mm/dd/yy: " + month + "/" + day + "/" + year);

            String date = month + "/" + day + "/" + year;
            eventDate.setText(date);
        };

        //get ref to time text views
        final TextView startTime = findViewById(R.id.idStartTime);
        final TextView endTime = findViewById(R.id.idEndTime);
        //TimePicker: create instance of timeListener object
        TimeListener timeListener = new TimeListener(this);
        //start instance of timeListener class when either enter time textView is clicked
        startTime.setOnClickListener(timeListener);
        endTime.setOnClickListener(timeListener);


    }

    //method to run what save button is clicked in CreateEvent
    public void saveButtonClicked(View view){
        //initialise fireBase database variables
        firebaseDatabase = FirebaseDatabase.getInstance();
        eventsDatabaseReference = firebaseDatabase.getReference().child("events");

        //First get a reference to all the fields in create event activity
        TextView eventTitle = findViewById(R.id.idEventTitle);
        Spinner eventType = findViewById(R.id.idEventType);
        //TextView for Date already referenced with name 'eventDate'
        TextView startTime = findViewById(R.id.idStartTime);
        TextView endTime = findViewById(R.id.idEndTime);
        TextView location = findViewById(R.id.idEventLocation);
        RadioGroup colour = findViewById(R.id.idEventRadioGroup);
        int radioButtonID = colour.getCheckedRadioButtonId();
        Spinner repeat = findViewById(R.id.idEventRepeat);
        TextView notes = findViewById(R.id.idEventNotes);

        //Next, use the references above to get string value entered for event
        String eventBegin = startTime.getText().toString().trim();
        String eventEnd = endTime.getText().toString().trim();
        String eventDateText = eventDate.getText().toString().trim();
        String eventName = eventTitle.getText().toString().trim();
        String eventKind = eventType.getSelectedItem().toString();
        String eventRepeat = repeat.getSelectedItem().toString();
        String eventLocation = location.getText().toString().trim();
        String eventColour = getResources().getResourceEntryName(radioButtonID);
        String eventNotes = notes.getText().toString().trim();

        //Check values entered are valid, if not, show toast and return to createEvent
        if(!checkIfEventValid(eventBegin, eventEnd, eventDateText, eventName, eventKind)){
            return;
        }

        Calendar c = Calendar.getInstance(); //get instance of java calender
        String eventDateFormatted=""; //this will contain the date the user selected

        //this is how our date is formatted
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);

        //this will format entered date using simpleDateFormat to allow comparisons for sorting and validation.
        try {
            c.setTime(dateFormat.parse(eventDateText));
            eventDateFormatted = dateFormat.format(c.getTime());
        } catch (java.text.ParseException e){
            //exception handling
        }

        //if all the above code checks out then event is valid. Now we add it to database.
        String eventId = eventsDatabaseReference.push().getKey();
        eventsDatabaseReference.child(eventId).setValue(new EventObject(eventId, eventName,
                eventKind, eventDateFormatted, eventBegin, eventEnd, eventRepeat, eventLocation,
                eventColour, eventNotes));


        //once initial event is added to database, we deal with possible repeat events
        if(!eventRepeat.equals("No repeat")){

            if(eventRepeat.equals("Weekly")){
                //for loop runs 13 times (event already added once) to have 14 weeks of event
                for(int i = 1; i<14; i++){
                    //event repeats each week for 14 weeks.
                    try {
                        c.setTime(dateFormat.parse(eventDateFormatted));
                    } catch (java.text.ParseException e){
                        //exception handling
                    }
                    c.add(Calendar.DAY_OF_MONTH, 7);
                    eventDateFormatted = dateFormat.format(c.getTime());

                    eventId = eventsDatabaseReference.push().getKey();
                    eventsDatabaseReference.child(eventId).setValue(new EventObject(eventId,
                            eventName, eventKind, eventDateFormatted, eventBegin, eventEnd,
                            eventRepeat, eventLocation, eventColour, eventNotes));
                }
                Toast.makeText(this, "Event will repeat each week for the next 14 weeks", Toast.LENGTH_LONG).show();
            }

            if(eventRepeat.equals("Every 2-weeks")){
                //loop 6 times to have a total of 7 iterations over 14 weeks
                for(int i = 1; i<7; i++){
                    //event repeats every 2 weeks 7 times.
                    try {
                        c.setTime(dateFormat.parse(eventDateFormatted));
                    } catch (java.text.ParseException e){
                        //exception handling
                    }
                    c.add(Calendar.DAY_OF_MONTH, 14);
                    eventDateFormatted = dateFormat.format(c.getTime());

                    eventId = eventsDatabaseReference.push().getKey();
                    eventsDatabaseReference.child(eventId).setValue(new EventObject(eventId,
                            eventName, eventKind, eventDateFormatted, eventBegin, eventEnd,
                            eventRepeat, eventLocation, eventColour, eventNotes));

                }
                Toast.makeText(this, "Event will repeat every 2-weeks for the next 14 weeks", Toast.LENGTH_LONG).show();
            }

            if(eventRepeat.equals("Monthly")){
                //loop 6 times to have a total of 6 iterations over 6 months
                for(int i = 1; i<6; i++){
                    //event repeats each month for 6 months.
                    try {
                        c.setTime(dateFormat.parse(eventDateFormatted));
                    } catch (java.text.ParseException e){
                        //exception handling
                    }
                    c.add(Calendar.MONTH, 1);
                    eventDateFormatted = dateFormat.format(c.getTime());

                    eventId = eventsDatabaseReference.push().getKey();
                    eventsDatabaseReference.child(eventId).setValue(new EventObject(eventId,
                            eventName, eventKind, eventDateFormatted, eventBegin, eventEnd,
                            eventRepeat, eventLocation, eventColour, eventNotes));

                }
                Toast.makeText(this, "Event will repeat each month for the next 6 months", Toast.LENGTH_LONG).show();
            }
        }

        //finally, go back to schedule activity
        Intent intent = new Intent(CreateEvent.this, Schedule.class);
        startActivity(intent);
    }

    public boolean checkIfEventValid(String eventBegin, String eventEnd, String eventDateText, String eventName, String eventKind){
        if(eventName.equals("")) {
            Toast.makeText(this, "Please enter event title", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(eventKind.equals("Select event type")) {
            Toast.makeText(this, "Please select event type", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(eventBegin.equals("Select start time")) {
            Toast.makeText(this, "Please enter event starting time", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(eventEnd.equals("Select end time")) {
            Toast.makeText(this, "Please enter event ending time", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(eventBegin.equals(eventEnd)) {
            Toast.makeText(this, "Your event ends and begins at the same time.", Toast.LENGTH_SHORT).show();
            return false;
        }

        //check if event end time is after 'now', and event ends after it starts
        //String eventBegin, eventEnd
        String eventBeginDateTime = eventDateText + " " + eventBegin;
        String eventEndDateTime = eventDateText + " " + eventEnd;

        //get format to parse to
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MM/dd/yy HH:mm", Locale.ENGLISH);

        //get current date-time
        Date nowTime = Calendar.getInstance().getTime();
        String currentTime = dateTimeFormat.format(nowTime);

        try {
            //if the following returns true, the events end time is before 'now'
            if(dateTimeFormat.parse(eventEndDateTime).before(dateTimeFormat.parse(currentTime))){
                Toast.makeText(this, "Event is in the past, please change the date/time", Toast.LENGTH_SHORT).show();
                return false;
            }
            if(dateTimeFormat.parse(eventEndDateTime).before(dateTimeFormat.parse(eventBeginDateTime))){
                Toast.makeText(this, "Event ends before it begins, please change the event time", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (ParseException e) {
            //handle the exceptions
        }
        return true;
    }

    public void cancelButtonClicked(View view){
        //if cancel button clicked, return to schedule page.
        Intent intent = new Intent(CreateEvent.this, Schedule.class);
        startActivity(intent);
    }

    //method to make sure back button takes user back to schedule page.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent intent = new Intent(CreateEvent.this, Schedule.class);
            startActivity(intent);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}