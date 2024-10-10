package com.example.finalassignment_group5_topic1b2.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.finalassignment_group5_topic1b2.Model.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskRepository {
    private DatabaseHelper dbHelper;

    public TaskRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Add task
    public void addTask(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TASK_NAME, task.getTaskName());
        values.put(DatabaseHelper.COLUMN_ESTIMATE_DAY, task.getEstimateDays());
        db.insert(DatabaseHelper.TABLE_TASK, null, values);
        db.close();
    }

    // Update Task
    public void updateTask(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TASK_NAME, task.getTaskName());
        values.put(DatabaseHelper.COLUMN_ESTIMATE_DAY, task.getEstimateDays());
        db.update(DatabaseHelper.TABLE_TASK, values, DatabaseHelper.COLUMN_TASK_ID + " = ?", new String[]{String.valueOf(task.getId())});
        db.close();
    }

    // Delete Task
    public void deleteTask(int taskId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_TASK, DatabaseHelper.COLUMN_TASK_ID + " = ?", new String[]{String.valueOf(taskId)});
        db.close();
    }

    // Get all Task
    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_TASK, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String taskName = cursor.getString(1);
                int estimateDays = cursor.getInt(2);
                taskList.add(new Task(id, taskName, estimateDays));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return taskList;
    }

    // Search task by name
    public Task getTaskByName(String taskName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_TASK +
                " WHERE " + DatabaseHelper.COLUMN_TASK_NAME + " = ?", new String[]{taskName});

        Task task = null;
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            int estimateDays = cursor.getInt(2);
            task = new Task(id, name, estimateDays);
        }

        cursor.close();
        db.close();

        return task;
    }

    // get task by id
    public Task getTaskById(int taskId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Task task = null;

        Cursor cursor = db.query(DatabaseHelper.TABLE_TASK,
                null, // Lấy tất cả các cột
                DatabaseHelper.COLUMN_TASK_ID + " = ?",
                new String[]{String.valueOf(taskId)},
                null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(0);
                String taskName = cursor.getString(1);
                int estimateDays = cursor.getInt(2);

                task = new Task(id, taskName, estimateDays);
            }
            cursor.close();
        }

        db.close();
        return task;
    }


}
