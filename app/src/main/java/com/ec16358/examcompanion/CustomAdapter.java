package com.ec16358.examcompanion;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

//create custom adapter class that extends an adapter of the type that you are working with
class CustomAdapter extends ArrayAdapter<EventObject> {

    //context just means background info - and replace 'int resource' with the string array with event info
    CustomAdapter(@NonNull Context context, ArrayList<EventObject> events) {
        super(context, R.layout.custom_row, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflaterA = LayoutInflater.from(getContext());
        View customView = inflaterA.inflate(R.layout.custom_row, parent, false);

        //Put the information we want into custom row so its not static
        //first, get a reference to each item in the array - use the position parameter

        //then we create a reference to relevant textView's of the event cards in the xml file.
        TextView eventName     = customView.findViewById(R.id.idEventName);
        TextView eventType     = customView.findViewById(R.id.idEventType);
        TextView eventTime     = customView.findViewById(R.id.idEventTime);

        //now we need to get references to the information in our EventObject's so we can use those
        //Access event list, then add events added to event list
        ArrayList<EventObject> list1;
        list1 = new DBHandler(getContext(), null, null, 1).getDbEventList();

        int listSize = list1.size();

        if(listSize>0) {
            //format date using simple-date-format
            String date = list1.get(position).get_eventdate();
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
            String eventNameRef = list1.get(position).get_eventname();
            String eventTypeRef = list1.get(position).get_eventtype() + "  on  " + dateString;
            String eventStartTimeRef = "From " + list1.get(position).get_eventstarttime() + "  to " + list1.get(position).get_eventendtime();

            String eventColour = list1.get(position).get_eventcolour();

            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);
            Date nowTime = Calendar.getInstance().getTime();
            String currentTime = dateTimeFormat.format(nowTime);

            if(currentTime.equals(list1.get(position).get_eventdate())){
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
                    case "purple":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_purple_today));
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
                    case "purple":
                        customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_purple));
                        break;
                }
            }

            //then we set the text we want into the reference - getting the text from the EventObject
            eventName.setText(eventNameRef);
            eventType.setText(eventTypeRef);
            eventTime.setText(eventStartTimeRef);

        }//end if statement applying to each event in list

        return customView;
    }

}

