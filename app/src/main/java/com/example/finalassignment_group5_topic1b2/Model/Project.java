package com.example.finalassignment_group5_topic1b2.Model;

public class Project {
    private int id;
    private String dev_name;
    private int taskId;
    private String startDate;
    private String endDate;

    public Project(int id, String name, int taskId, String startDate, String endDate) {
        this.id = id;
        this.dev_name = name;
        this.taskId = taskId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDevName() {
        return dev_name;
    }

    public void setDevName(String name) {
        this.dev_name = name;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
