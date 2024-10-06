package com.example.finalassignment_group5_topic1b2.Model;

public class Task {
    private int id;
    private String taskName;
    private int estimateDays;

    public Task(int id, String taskName, int estimateDays) {
        this.id = id;
        this.taskName = taskName;
        this.estimateDays = estimateDays;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEstimateDays() {
        return estimateDays;
    }

    public void setEstimateDays(int estimateDays) {
        this.estimateDays = estimateDays;
    }
}