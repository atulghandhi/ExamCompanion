package com.ec16358.examcompanion;

public class PomodoroInstance {

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

    //constructor initialising all pomodoroInstance variables
    public PomodoroInstance(String id, String module, String target, String summary, int length, boolean success) {
        this.id = id;
        this.module = module;
        this.target = target;
        this.summary = summary;
        this.length = length;
        this.success = success;
    }

    //default constructor, no parameters
    public PomodoroInstance() {
    }

    //getters and setters for variables
    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
