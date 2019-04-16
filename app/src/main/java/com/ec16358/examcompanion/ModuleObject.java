package com.ec16358.examcompanion;

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
