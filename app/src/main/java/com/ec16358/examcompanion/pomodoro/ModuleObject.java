package com.ec16358.examcompanion.pomodoro;

/*
* Instances of this class are created in Modules activity accessed via the Pomodoro page and in
* FlashCards.java. The objects created are shared by the two activities. ModulesObjects are used to
* organise flash cards and assign Pomodoro's to.
*
*
* */

public class ModuleObject {

    //variables to hold module name and date
    private String moduleId;
    private String moduleName;
    private String moduleExamDate;

    //default constructor
    public ModuleObject() {
    }

    //constructor to initialise moduleObject values
    public ModuleObject(String moduleId, String moduleName, String moduleExamDate) {
        this.moduleName = moduleName;
        this.moduleExamDate = moduleExamDate;
        this.moduleId = moduleId;
    }

    //getters and setters for variables
    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleExamDate() {
        return moduleExamDate;
    }

    public void setModuleExamDate(String moduleExamDate) {
        this.moduleExamDate = moduleExamDate;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }
}
