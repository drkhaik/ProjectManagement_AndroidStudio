package com.example.finalassignment_group5_topic1b2.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.finalassignment_group5_topic1b2.Model.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProjectRepository {
    private DatabaseHelper dbHelper;

    public ProjectRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Add Project
    public void addProject(Project project) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_DEV_NAME, project.getDevName());
        values.put(DatabaseHelper.COLUMN_TASK_FOREIGN_ID, project.getTaskId()); // Lưu ID của task
        values.put(DatabaseHelper.COLUMN_START_DATE, project.getStartDate());
        values.put(DatabaseHelper.COLUMN_END_DATE, project.getEndDate());
        db.insert(DatabaseHelper.TABLE_PROJECT, null, values);
        db.close();
    }

    // Get all
    public List<Project> getAllProjects() {
        List<Project> projectList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PROJECT, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String devName = cursor.getString(1);
                int taskId = cursor.getInt(2);
                String startDate = cursor.getString(3);
                String endDate = cursor.getString(4);
                projectList.add(new Project(id, devName, taskId, startDate, endDate));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return projectList;
    }

    // Update
    public void updateProject(Project project) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_DEV_NAME, project.getDevName());
        values.put(DatabaseHelper.COLUMN_TASK_FOREIGN_ID, project.getTaskId());
        values.put(DatabaseHelper.COLUMN_START_DATE, project.getStartDate());
        values.put(DatabaseHelper.COLUMN_END_DATE, project.getEndDate());
        db.update(DatabaseHelper.TABLE_PROJECT, values, DatabaseHelper.COLUMN_PROJECT_ID + " = ?", new String[]{String.valueOf(project.getId())});
        db.close();
    }

    // Delete
    public boolean deleteProject(int projectId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = db.delete(DatabaseHelper.TABLE_PROJECT,
                DatabaseHelper.COLUMN_PROJECT_ID + " = ?",
                new String[]{String.valueOf(projectId)});
        db.close();
        return rowsAffected > 0;
    }

    // Delete many
    public void deleteManyProjects(Set<Integer> projectIds) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (int projectId : projectIds) {
            db.delete(DatabaseHelper.TABLE_PROJECT, DatabaseHelper.COLUMN_PROJECT_ID + " = ?", new String[]{String.valueOf(projectId)});
        }
        db.close();
    }

    // Search projects by assignee
    public List<Project> searchProjectsByAssignee(String assignee) {
        List<Project> projectList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PROJECT +
                " WHERE " + DatabaseHelper.COLUMN_DEV_NAME + " LIKE ?", new String[]{"%" + assignee + "%"});
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String devName = cursor.getString(1);
                int taskId = cursor.getInt(2);
                String startDate = cursor.getString(3);
                String endDate = cursor.getString(4);
                projectList.add(new Project(id, devName, taskId, startDate, endDate));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return projectList;
    }
}