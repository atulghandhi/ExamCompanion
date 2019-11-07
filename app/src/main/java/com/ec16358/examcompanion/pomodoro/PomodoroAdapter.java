package com.ec16358.examcompanion.pomodoro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ec16358.examcompanion.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/*
 * Adapter for PomodoroHistory class; populates ListView in PomodoroHistory with information in
 * PomodoroInstance objects. Sets background of each row depending on if pomodoro was successful or not.
 *
 * */

class PomodoroAdapter extends ArrayAdapter<PomodoroInstance> {
    //constructor: pass in list of PomodoroInstances and bind constructor to xml layout 'custom_row_pomodoro_history'
    PomodoroAdapter(@NonNull Context context, List<PomodoroInstance> pomodoros) {
        super(context, R.layout.custom_row_pomodoro_history, pomodoros);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflaterA = LayoutInflater.from(getContext());
        View customView = inflaterA.inflate(R.layout.custom_row_pomodoro_history, parent, false);

        //get reference to pomodoroInstance item
        PomodoroInstance pomodoroInstance = getItem(position);

        //set references to the textViews in listView custom row
        TextView instanceDate = customView.findViewById(R.id.pomodoro_instance_date);
        TextView instanceSuccess = customView.findViewById(R.id.pomodoro_instance_success_textView);
        TextView instanceTarget = customView.findViewById(R.id.pomodoro_instance_target);
        TextView instanceSummary = customView.findViewById(R.id.pomodoro_instance_summary);

        //format instance date using simple-date-format
        assert pomodoroInstance != null;
        String date = pomodoroInstance.getStartDateTime();
        String dateString="";
        try {
            DateFormat format1 = new SimpleDateFormat("MM/dd/yy HH:mm");
            Date date1 = format1.parse(date);
            DateFormat format2 = new SimpleDateFormat("EEEE d MMMM, HH:mm");
            dateString = format2.format(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Create strings of the text we want to set in textViews
        String instanceSuccessRef = pomodoroInstance.isSuccess() ? "SUCCESS":"FAILURE";
        String instanceTargetRef = "TARGET: " + pomodoroInstance.getTarget();
        String instanceSummaryRef = "SUMMARY: " + pomodoroInstance.getSummary();
        String instanceModuleRef = "MODULE: " + pomodoroInstance.getModule();

        //set background colour of pomodoro - green for successful and red for failed
        switch (instanceSuccessRef) {
            case "SUCCESS":
                customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_green_today));
                break;
            case "FAILURE":
                customView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.event_row_red_today));
                break;
        }

        //set the strings created above into relevant textViews
        instanceDate.setText(dateString);
        instanceSuccess.setText(instanceModuleRef);
        instanceTarget.setText(instanceTargetRef);
        instanceSummary.setText(instanceSummaryRef);

        //return listView row
        return customView;
    }
}
