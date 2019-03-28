package com.ec16358.examcompanion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

public class EventsRecyclerAdapter extends RecyclerView.Adapter<EventsRecyclerAdapter.ViewHolder> {

    private ArrayList<String> eventNames;
    private ArrayList<String> eventTypes;
    private ArrayList<String> eventTimes;
    private Context mContext;

    public EventsRecyclerAdapter(Context mContext, ArrayList<String> eventNames, ArrayList<String> eventTypes, ArrayList<String> eventTimes) {
        this.eventNames = eventNames;
        this.eventTypes = eventTypes;
        this.eventTimes = eventTimes;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate recyclerView row with custom row layout created in xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_row, parent, false);
        //set viewHolder to recycle the 'views' for each row.
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.eventName.setText(eventNames.get(position));
        holder.eventType.setText(eventTypes.get(position));
        holder.eventTime.setText(eventTimes.get(position));

        holder.parentLayout.setOnClickListener(v -> Toast.makeText(mContext, eventNames.get(position), Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return eventNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView eventName;
        TextView eventType;
        TextView eventTime;
        NestedScrollView parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.idEventName);
            eventType = itemView.findViewById(R.id.idEventType);
            eventTime = itemView.findViewById(R.id.idEventTime);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }

    }

}