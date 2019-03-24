package com.ec16358.examcompanion;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.Cursor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.Nullable;


//this class is to handle all things related to the central database holding user information
public class DBHandler extends SQLiteOpenHelper{
    String ip = "92.2.114.152";
    String pw = "examCompanionDB";

    //database version - update version whenever changing database structure
    private static final int DATABASE_VERSION = 2;

    //name of file storing data
    private static final String DATABASE_NAME = "scheduleEvents.db";

    //table name
    private static final String TABLE_EVENTS = "events";

    //columns in table
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_EVENTNAME = "_eventname";
    private static final String COLUMN_EVENTDATE = "_eventdate";
    private static final String COLUMN_EVENTTYPE = "_eventtype";
    private static final String COLUMN_EVENTSTARTTIME = "_eventstarttime";
    private static final String COLUMN_EVENTENDTIME = "_eventendtime";
    private static final String COLUMN_EVENTREPEAT = "_eventrepeat";
    private static final String COLUMN_EVENTLOCATION = "_eventlocation";
    private static final String COLUMN_EVENTCOLOUR = "_eventcolour";
    private static final String COLUMN_EVENTNOTES = "_eventnotes";

    //constructor - to feed in name and version information
    public DBHandler(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //what should program do when database is created for the first time
        //first we want to create our table using sql
        //auto increment on id will give each event a unique id beginning with 1 incrementing
        String query = "CREATE TABLE "  + TABLE_EVENTS + "(" +
                COLUMN_ID + " TEXT PRIMARY KEY, " +
                COLUMN_EVENTNAME + " TEXT, " +
                COLUMN_EVENTDATE + " TEXT, " +
                COLUMN_EVENTTYPE + " TEXT, " +
                COLUMN_EVENTSTARTTIME + " TEXT, " +
                COLUMN_EVENTENDTIME + " TEXT, " +
                COLUMN_EVENTREPEAT + " TEXT, " +
                COLUMN_EVENTLOCATION + " TEXT, " +
                COLUMN_EVENTCOLOUR + " TEXT, " +
                COLUMN_EVENTNOTES + " TEXT " +
                ");" ;
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //what should program do when database is upgraded (version number is incremented)
        //first delete old table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        //then recreate new table
        onCreate(db);
    }

    //Create method to add row to database
    public void addEvent(EventObject eventObject){
        //use contentValues to add event values to database
        ContentValues values = new ContentValues();
        values.put(COLUMN_EVENTNAME, eventObject.get_eventname());
        values.put(COLUMN_EVENTDATE, eventObject.get_eventdate());
        values.put(COLUMN_EVENTTYPE, eventObject.get_eventtype());
        values.put(COLUMN_EVENTSTARTTIME, eventObject.get_eventstarttime());
        values.put(COLUMN_EVENTENDTIME, eventObject.get_eventendtime());
        values.put(COLUMN_EVENTREPEAT, eventObject.get_eventrepeat());
        values.put(COLUMN_EVENTLOCATION, eventObject.get_eventlocation());
        values.put(COLUMN_EVENTCOLOUR, eventObject.get_eventcolour());
        values.put(COLUMN_EVENTNOTES, eventObject.get_eventnotes());
        //create database instance to write to
        SQLiteDatabase db = getWritableDatabase();
        //add events from contentValues to table
        db.insert(TABLE_EVENTS, null, values);
        //close database once finished.
        db.close();
    }

    //Create method to delete row from database
    public void deleteEvent(String id){
        //create database instance to write to
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_EVENTS + " WHERE " + COLUMN_ID + "=\"" + id + "\";");
    }

    /*
    //Get Row Count
    public int getCount() {
        String countQuery = "SELECT  * FROM " + TABLE_EVENTS;
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        if(cursor != null && !cursor.isClosed()){
            count = cursor.getCount();
            cursor.close();
        }
        return count;
    }
    */

    //Returns arrayList of eventObjects created from database
    public ArrayList<EventObject> getDbEventList() {
        //first select entire table
        String selectQuery = "SELECT  * FROM " + TABLE_EVENTS;

        //get reference to database
        SQLiteDatabase db = this.getWritableDatabase();

        //get cursor to go through database
        Cursor cursor = db.rawQuery(selectQuery, null);

        //make arrayList to store event objects and return them.
        ArrayList<EventObject> eventObjectArrayList = new ArrayList<EventObject>();

        //loop through each loop of database, creating event objects and adding them to list
        if (cursor.moveToFirst()) {
            //a do while loop to run through each item in the database, the while part moves cursor to next item.
            do {
                //get object date and time.
                String listDateTime = cursor.getString(2) + " " + cursor.getString(5);
                //get format of date time
                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MM/dd/yy HH:mm", Locale.ENGLISH);
                //get current date-time and format correctly
                Date nowTime = Calendar.getInstance().getTime();
                String dateTimeNow = dateTimeFormat.format(nowTime);
                //check if event is after current. If so, delete event
                try {
                    //if the following returns true, the events end time is before 'now'
                    if (dateTimeFormat.parse(listDateTime).before(dateTimeFormat.parse(dateTimeNow))) {
                        deleteEvent(cursor.getString(0));
                        //continue skips the rest of this loop iteration.
                        continue;
                    }
                } catch (ParseException e) {
                    //handle the exceptions
                }

                //If event is not after current time, create event object using empty constructor
                EventObject list = new EventObject();
                //add variables to event object from database rows.
                list.set_eventid(cursor.getString(0));
                list.set_eventname(cursor.getString(1));
                list.set_eventdate(cursor.getString(2));
                list.set_eventtype(cursor.getString(3));
                list.set_eventstarttime(cursor.getString(4));
                list.set_eventendtime(cursor.getString(5));
                list.set_eventrepeat(cursor.getString(6));
                list.set_eventlocation(cursor.getString(7));
                list.set_eventcolour(cursor.getString(8));
                list.set_eventnotes(cursor.getString(9));

                //add event object to list
                eventObjectArrayList.add(list);
            } while (cursor.moveToNext());
        }
        //sort arrayList by time
        Collections.sort(eventObjectArrayList, (o1, o2) -> {
            try {
                return new SimpleDateFormat("HH:mm").parse(o1.get_eventstarttime()).compareTo(new SimpleDateFormat("HH:mm").parse(o2.get_eventstarttime()));
            } catch (ParseException e) {
                return 0;
            }
        });
        //sort arrayList by date
        Collections.sort(eventObjectArrayList);
        return eventObjectArrayList;
    }

}

