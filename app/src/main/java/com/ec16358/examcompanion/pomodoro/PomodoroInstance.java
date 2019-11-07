package com.ec16358.examcompanion.pomodoro;

/*
 * Pomodoro instance object. An instance is created when a pomodoro session begins and is saved to
 * the firebase database when a pomodoro session ends.
 *
 * PomodoroHistory adapter uses instances of this class to populate listview.
 *
 * */

public class PomodoroInstance {

    //allows objects to be sorted by date in list.
    public int compareTo(PomodoroInstance e){
        return getStartDateTime().compareTo(e.getStartDateTime());
    }

    private String id;
    //variable to hold module a pomodoro is completed for
    private String module;
    //target for pomodoro before pomodoro begins
    private String target;
    //summary of work completed during pomodoro once timer ends
    private String summary;
    //length of pomodoro
    private int length;
    //success of pomodoro
    private boolean success;

    //date of pomodoro instance
    private String startDateTime;

    //constructor initialising all pomodoroInstance variables
    public PomodoroInstance(String id, String module, String target, String summary, int length,
                            boolean success, String startDateTime) {
        this.id = id;
        this.module = module;
        this.target = target;
        this.summary = summary;
        this.length = length;
        this.success = success;
        this.startDateTime = startDateTime;
    }

    //default constructor, no parameters
    PomodoroInstance() {
    }

    //getters and setters for variables
    public String getModule() {
        return module;
    }

    void setModule(String module) {
        this.module = module;
    }

    public String getTarget() {
        return target;
    }

    void setTarget(String target) {
        this.target = target;
    }

    public String getSummary() {
        return summary;
    }

    void setSummary(String summary) {
        this.summary = summary;
    }

    public int getLength() {
        return length;
    }

    void setLength(int length) {
        this.length = length;
    }

    public boolean isSuccess() {
        return success;
    }

    void setSuccess(boolean success) {
        this.success = success;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }
}
