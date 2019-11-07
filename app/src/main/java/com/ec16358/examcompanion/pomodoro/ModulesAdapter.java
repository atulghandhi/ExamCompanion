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

/*
* Modules adapter populates listview in Modules.java file with an ArrayList of moduleObjects.
*
* */

public class ModulesAdapter extends ArrayAdapter<ModuleObject> {

    //constructor: pass in list of ModuleObjects and bind constructor to xml layout 'custom_row_module'
    ModulesAdapter(@NonNull Context context, List<ModuleObject> modules) {
        super(context, R.layout.custom_row_module, modules);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //inflate custom row for listView using xml layout
        LayoutInflater inflaterA = LayoutInflater.from(getContext());
        View customView = inflaterA.inflate(R.layout.custom_row_module, parent, false);

        //get reference to each moduleObject item using position parameter
        ModuleObject moduleObject = getItem(position);
        //get reference to textViews in layout
        TextView moduleName = customView.findViewById(R.id.idModuleName);
        TextView moduleDate = customView.findViewById(R.id.idModuleDate);
        //reformat module date
        String dateString="";
        try {
            DateFormat format1 = new SimpleDateFormat("MM/dd/yy");
            Date date1 = format1.parse(moduleObject.getModuleExamDate());
            DateFormat format2 = new SimpleDateFormat("MMM dd, yyyy");
            dateString = "Exam: " + format2.format(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //get information about module object
        String moduleNameRef = moduleObject.getModuleName();
        String moduleDateRef = dateString;
        //set information into its respective textView
        moduleName.setText(moduleNameRef);
        moduleDate.setText(moduleDateRef);
        //return listView row
        return customView;
    }


}
