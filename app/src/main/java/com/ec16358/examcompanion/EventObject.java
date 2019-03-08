package com.ec16358.examcompanion;

import java.time.LocalDate;
import java.util.*;

//EventObject class will be used to create instances of events
public class EventObject implements Comparable<EventObject> {

    //allows objects to be sorted by date in list.
    public int compareTo(EventObject e){
        return get_eventdate().compareTo(e.get_eventdate());
    }

    //Attributes of event objects
    private int _eventid;

    private String _eventname;
    private String _eventtype;
    private String _eventdate;
    private String _eventstarttime;
    private String _eventendtime;

    private String _eventrepeat;
    private String _eventlocation;
    private String _eventcolour;
    private String _eventnotes;

    //constructor with no parameters used read items from database (in customAdapter)
    public EventObject(){
        _eventname = "";
        _eventtype = "";
        _eventdate = "";
        _eventstarttime = "";
        _eventendtime = "";
        _eventrepeat = "";
        _eventlocation = "";
        _eventcolour = "";
        _eventnotes= "";
    }

    //constructor with ALL parameters used to add items to database (in createEvent)
    public EventObject(String name, String type, String date, String startTime, String endTime, String repeat, String location, String colour, String notes) {
        //constructor that will actually be used - variables initialised
        _eventname = name;
        _eventtype = type;
        _eventdate = date;
        _eventstarttime = startTime;
        _eventendtime = endTime;
        _eventrepeat = repeat;
        _eventlocation = location;
        _eventcolour = colour;
        _eventnotes = notes;
    }

    public int get_eventid() {
        return _eventid;
    }

    public String get_eventname() {
        return this._eventname;
    }

    public String get_eventtype() {
        return this._eventtype;
    }

    public String get_eventdate() {
        return this._eventdate;
    }

    public String get_eventstarttime() {
        return this._eventstarttime;
    }

    public String get_eventendtime() {
        return this._eventendtime;
    }

    public String get_eventrepeat() {
        return _eventrepeat;
    }

    public String get_eventlocation() {
        return _eventlocation;
    }

    public String get_eventcolour() {
        return _eventcolour;
    }

    public String get_eventnotes() {
        return _eventnotes;
    }

    public void set_eventnotes(String _eventnotes) {
        this._eventnotes = _eventnotes;
    }

    public void set_eventcolour(String _eventcolour) {
        this._eventcolour = _eventcolour;
    }

    public void set_eventlocation(String _eventlocation) {
        this._eventlocation = _eventlocation;
    }

    public void set_eventrepeat(String _eventrepeat) {
        this._eventrepeat = _eventrepeat;
    }

    public void set_eventid(int _eventid) {
        this._eventid = _eventid;
    }

    public void set_eventname(String eventname) {
        this._eventname = eventname;
    }

    public void set_eventtype(String eventtype) {
        this._eventtype = eventtype;
    }

    public void set_eventdate(String eventdate) {
        this._eventdate = eventdate;
    }

    public void set_eventstarttime(String eventstarttime) {
        this._eventstarttime = eventstarttime;
    }

    public void set_eventendtime(String eventendtime) {
        this._eventendtime = eventendtime;
    }

}
