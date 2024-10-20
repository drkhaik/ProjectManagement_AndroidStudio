package com.example.finalassignment_group5_topic1b2.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.example.finalassignment_group5_topic1b2.Model.Project;

import java.util.ArrayList;
import java.util.HashSet;
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

    // Search projects
    public List<Project> searchProjects(String query) {
        List<Project> results = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Tìm kiếm trong bảng Project theo dev_name
        String projectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_PROJECT +
                " WHERE " + DatabaseHelper.COLUMN_DEV_NAME + " LIKE ?";

        Cursor cursor = db.rawQuery(projectQuery, new String[]{"%" + query + "%"});

        while (cursor.moveToNext()) {
            Project project = new Project();
            project.setId(cursor.getInt(0)); // Lấy ID
            project.setDevName(cursor.getString(1));
            project.setTaskId(cursor.getInt(2));
            project.setStartDate(cursor.getString(3));
            project.setEndDate(cursor.getString(4));
            results.add(project);
        }
        cursor.close();

        Log.d("Check results", "Results size: " + results.size());
        for (Project project : results) {
            Log.d("Check results", "Project ID: " + project.getId() + ", Dev Name: " + project.getDevName());
        }

        String taskQuery = "SELECT " + DatabaseHelper.COLUMN_TASK_ID +
                " FROM " + DatabaseHelper.TABLE_TASK +
                " WHERE " + DatabaseHelper.COLUMN_TASK_NAME + " LIKE ?";

        Cursor taskCursor = db.rawQuery(taskQuery, new String[]{"%" + query + "%"});

        Set<Integer> foundTaskIds = new HashSet<>();
        while (taskCursor.moveToNext()) {
            int taskId = taskCursor.getInt(0);
            foundTaskIds.add(taskId);
        }
        taskCursor.close();

        if (!foundTaskIds.isEmpty()) {
            String foundProjectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_PROJECT +
                    " WHERE " + DatabaseHelper.COLUMN_TASK_FOREIGN_ID + " IN (" +
                    TextUtils.join(",", foundTaskIds) + ")";

            Cursor projectCursor = db.rawQuery(foundProjectQuery, null);

            while (projectCursor.moveToNext()) {
                Project project = new Project();
                project.setId(projectCursor.getInt(0)); // Lấy ID
                project.setDevName(projectCursor.getString(1));
                project.setTaskId(projectCursor.getInt(2));
                project.setStartDate(projectCursor.getString(3));
                project.setEndDate(projectCursor.getString(4));
                results.add(project);
            }
            cursor.close();
        }

        return results;
    }

    // Search projects by assignee
    public List<Project> getProjectsByDevName(String assignee) {
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

    // Phương thức để lấy task ID dựa trên project ID
    public Integer getTaskIdByProjectId(int projectId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Integer taskId = null;
        String query = "SELECT " + DatabaseHelper.COLUMN_TASK_FOREIGN_ID +
                " FROM " + DatabaseHelper.TABLE_PROJECT +
                " WHERE " + DatabaseHelper.COLUMN_PROJECT_ID + " = ?";
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(projectId)});
            if (cursor.moveToFirst()) {
                taskId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_TASK_FOREIGN_ID));
            }
        } catch (Exception e) {
            Log.e("ProjectRepository", "Error getting task ID by project ID", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return taskId;
    }

}