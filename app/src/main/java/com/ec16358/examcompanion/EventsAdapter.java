package com.ec16358.examcompanion;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

//create custom adapter class that extends an adapter of the type that you are working with
class EventsAdapter extends ArrayAdapter<EventObject> {

    private String adapterDateStorage = "";

    //constructor: pass in list of EventObjects and bind constructor to xml layout 'custom_row'
    EventsAdapter(@NonNull Context context, List<EventObject> events) {
        super(context, R.layout.custom_row, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflaterA = LayoutInflater.from(getContext());
        View customView;

        EventObject eventObject = getItem(position);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);
        //get current date-time and format
        Date nowDate = Calendar.getInstance().getTime();
        String dateToday = dateFormat.format(nowDate);

        if(eventObject.get_eventdate().equals(dateToday) || eventObject.get_eventdate().equals(adapterDateStorage)){
            customView = inflaterA.inflate(R.layout.custom_row, parent, false);

            //first, get a reference to each item in the array - use the position parameter
            //then we create a reference to relevant textView's of the event cards in the xml file.

            TextView eventName = customView.findViewById(R.id.idEventName);
            TextView eventType = customView.findViewById(R.id.idEventType);
            TextView eventTime = customView.findViewById(R.id.idEventTime);

            //format date using simple-date-format
            String date = eventObject.get_eventdate();
            String dateString="";
            try {
                DateFormat format1 = new SimpleDateFormat("MM/dd/yy");
                Date date1 = format1.parse(date);
                DateFormat format2 = new SimpleDateFormat("MMM dd, yyyy");
                dateString = format2.format(date1);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //set textViews in each row of listView
            String eventNameRef = eventObject.get_eventname();
            String eventTypeRef = eventObject.get_eventtype() + "  on  " + dateString;
            String eventStartTimeRef = "From " + eventObject.get_eventstarttime() + "  to " + eventObject.get_eventendtime();

            String eventColour = eventObject.get_eventcolour();

            //get current time, format it just like EventObject time variable
            Date nowTime = Calendar.getInstance().getTime();
            String currentTime = dateFormat.format(nowTime);

            //set colour of event showing in list
            if(currentTime.equals(eventObject.get_eventdate())){
                switch (eventColour) {
                    case "blue":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_blue_today));
                        break;
                    case "cyan":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_cyan_today));
                        break;
                    case "green":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_green_today));
                        break;
                    case "yellow":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_yellow_today));
                        break;
                    case "orange":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_orange_today));
                        break;
                    case "red":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_red_today));
                        break;
                    case "pink":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_pink_today));
                        break;
                    case "black":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_black_today));
                        break;
                    case "purple":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_black_today));
                        break;
                }
            }else{
                switch (eventColour) {
                    case "blue":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_blue));
                        break;
                    case "cyan":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_cyan));
                        break;
                    case "green":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_green));
                        break;
                    case "yellow":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_yellow));
                        break;
                    case "orange":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_orange));
                        break;
                    case "red":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_red));
                        break;
                    case "pink":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_pink));
                        break;
                    case "black":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_black));
                        break;
                    case "purple":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_black));
                        break;
                }
            }

            //then we set the text we want into the reference - getting the text from the EventObject
            eventName.setText(eventNameRef);
            eventType.setText(eventTypeRef);
            eventTime.setText(eventStartTimeRef);

            return customView;

        } else {
            customView = inflaterA.inflate(R.layout.custom_row_date, parent, false);

            //set adapter date storage to this events date
            adapterDateStorage=eventObject.get_eventdate();

            //format date using simple-date-format
            String longDate = "";
            try {
                DateFormat format1 = new SimpleDateFormat("MM/dd/yy"); //original format of date
                Date date1 = format1.parse(adapterDateStorage);
                DateFormat format2 = new SimpleDateFormat("EEEE d MMMM"); //desired format of date
                longDate = format2.format(date1);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            TextView date = customView.findViewById(R.id.idcustomDate); //get textView ref
            date.setText(longDate); //set textView's text as formatted date
            date.setPaintFlags(date.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG); //underline text

            TextView eventName = customView.findViewById(R.id.idEventName_date);
            TextView eventType = customView.findViewById(R.id.idEventType_date);
            TextView eventTime = customView.findViewById(R.id.idEventTime_date);

            //format date using simple-date-format
            String dateString="";
            try {
                DateFormat format1 = new SimpleDateFormat("MM/dd/yy");
                Date date1 = format1.parse(eventObject.get_eventdate());
                DateFormat format2 = new SimpleDateFormat("MMM dd, yyyy");
                dateString = format2.format(date1);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //set textViews in each row of listView
            String eventNameRef = eventObject.get_eventname();
            String eventTypeRef = eventObject.get_eventtype() + "  on  " + dateString;
            String eventStartTimeRef = "From " + eventObject.get_eventstarttime() + "  to " + eventObject.get_eventendtime();

            String eventColour = eventObject.get_eventcolour();

            //get current time, format it just like EventObject time variable
            Date nowTime = Calendar.getInstance().getTime();
            String currentTime = dateFormat.format(nowTime);

            //set colour of event showing in list
            if(currentTime.equals(eventObject.get_eventdate())){
                switch (eventColour) {
                    case "blue":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_blue_today));
                        break;
                    case "cyan":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_cyan_today));
                        break;
                    case "green":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_green_today));
                        break;
                    case "yellow":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_yellow_today));
                        break;
                    case "orange":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_orange_today));
                        break;
                    case "red":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_red_today));
                        break;
                    case "pink":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_pink_today));
                        break;
                    case "black":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_black_today));
                        break;
                    case "purple":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_black_today));
                        break;
                }
            }else{
                switch (eventColour) {
                    case "blue":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_blue));
                        break;
                    case "cyan":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_cyan));
                        break;
                    case "green":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_green));
                        break;
                    case "yellow":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_yellow));
                        break;
                    case "orange":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_orange));
                        break;
                    case "red":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_red));
                        break;
                    case "pink":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_pink));
                        break;
                    case "black":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_black));
                        break;
                    case "purple":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_black));
                        break;
                }
            }

            //then we set the text we want into the reference - getting the text from the EventObject
            eventName.setText(eventNameRef);
            eventType.setText(eventTypeRef);
            eventTime.setText(eventStartTimeRef);

            return customView;
        }
    }
}

